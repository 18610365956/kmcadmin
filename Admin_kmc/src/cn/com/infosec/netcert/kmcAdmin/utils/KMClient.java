package cn.com.infosec.netcert.kmcAdmin.utils;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import cn.com.infosec.gm.tls.SSLContextImpl;
import cn.com.infosec.gm.tls.SSLSocketFactoryImpl;
import cn.com.infosec.netcert.base.Request;
import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.communication.Communicator;
import cn.com.infosec.netcert.communication.UnProtocolCommunicator;
import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.crypto.IHSM;
import cn.com.infosec.netcert.framework.crypto.IKeyStore;
import cn.com.infosec.netcert.framework.crypto.SM2Id;
import cn.com.infosec.netcert.framework.crypto.impl.SoftCryptoAndStore;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.NeedPinException;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeyCSPImpl;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeySKFImpl;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelConf;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelSecure;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelSign;
import cn.com.infosec.netcert.kmcAdmin.ui.admin.Panel_VerifyPin;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import cn.com.infosec.netcert.parser.RequestParser;
import cn.com.infosec.util.Base64;

/**
 * 与KMC通讯的工具类
 */
public class KMClient {
	private ChannelSign chSign;
	private ChannelConf chConf;
	private ChannelSecure chSec;
	private IHSM hsm;
	private SSLSocketFactory fac;
	private X509Certificate trustCer;
	private Certificate signCert; // 签名证书

	private Communicator comm = null;

	
	private boolean isSSL = false;
	private String keyIdx, signAlgName; // 签名密钥标识，签名算法

	private static ResourceBundle l = Env.getLanguage();

	public KMClient(ChannelSign chSign, ChannelSecure chSec, ChannelConf chConf) throws Exception {
		this.chSign = chSign;
		this.chSec = chSec;
		this.chConf = chConf;

		if (ALG.SM2 == Env.alg) {
			String SKF_sn = chSign.getKeyIdx().split(Utils.NameConnector)[0];
			this.hsm = new UsbKeySKFImpl(chSign.getHsm(), SKF_sn);
		} else {
			this.hsm = new UsbKeyCSPImpl(chSign.getHsm());
		}
		this.keyIdx = chSign.getKeyIdx();
		this.signAlgName = chSign.getSignAlgName();
		this.signCert = chSign.getSignCert();

		if ("ssl".equalsIgnoreCase(chSec.getName())) {
			//CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
			//String trustStore_temp = this.chSec.getConf(ChannelSecure.SSL_TrustStore);
			//FileInputStream fin = new FileInputStream(trustStore_temp);
			//this.trustCer = (X509Certificate) cf.generateCertificate(fin);
			//fin.close();
			this.trustCer = this.chSec.getSSL_TrustStore()[0];
			this.isSSL = true;

			if ("RSA".equalsIgnoreCase(this.chSec.getConf(ChannelSecure.ALG))) {
				SSLContext context = SSLContext.getInstance("TLS");
				String alg = TrustManagerFactory.getDefaultAlgorithm();
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(alg);

				KeyStore trustStore = KeyStore.getInstance("JKS");
				trustStore.load(null, null);
				trustStore.setCertificateEntry("trust", this.trustCer);
				tmf.init(trustStore);

				context.init(null, tmf.getTrustManagers(), null);
				this.fac = (SSLSocketFactory) context.getSocketFactory();
			} else { // sm2 TLS
				IKeyStore trustStore = new cn.com.infosec.netcert.framework.crypto.impl.KeyStore(
						new X509Certificate[] { this.trustCer });
				SSLContextImpl ctx = new SSLContextImpl();
				ctx.setTrustStore(trustStore);
				this.fac = new SSLSocketFactoryImpl(ctx);
			}
		}
		
		Socket ss;
		if (this.isSSL) {
			ss = this.fac.createSocket();
			((SSLSocket) ss).setUseClientMode(true);
		} else {
			ss = new Socket();
		}
		ss.connect(new InetSocketAddress(this.chConf.getTransIp(), this.chConf.getTransPort()),
				this.chConf.getConnectTimeout() * 1000);
		if (this.chConf.getTimeout() > 0) {
			ss.setSoTimeout(this.chConf.getTimeout() * 1000);
		}
		// 向kmc发请求
		comm = new UnProtocolCommunicator(ss);
		
	}

	public Response sendRequest(String type, Properties p) throws Exception {
		return send(makeRequest(type, p));
	}

	/**
	 * 发送请求到服务器
	 * @Description 发送type值 和 Properties 到服务器，服务器根据type 调用相对的 处理接口
	 * @param  (String,Properties)   
	 * @return  (Response)    
	 * @throws   (Exception) 
	 * @Author 江岩 
	 * @Time   2019-07-18 11:19
	 * @version 1.0
	 */
	public Request makeRequest(String type, Properties p) throws Exception {
		Request req = new Request();
		req.setType(type);
		req.setChannel("ADMIN");
		req.setId(Env.loginID);
		req.setLocalTime(String.valueOf(System.currentTimeMillis()));
		req.setReqData(p);

		String resource = req.toString();
		byte[] data = RequestParser.getSourceText(resource).getBytes("UTF-8");
		String containerName = this.keyIdx.split(Utils.NameConnector)[1];

		if (ALG.SM2 == Env.alg) { // SM2
			req.setX509Certificate((X509Certificate) this.chSign.getSignCert());
			byte[] hash = SoftCryptoAndStore.SM3WithId(data, SM2Id.getSignId("ADMIN").getBytes(),
					signCert.getPublicKey());
			try {
				byte[] signature_byte = this.hsm.sign(containerName, 0, null, hash, signAlgName, SM2Id.getSignId("ADMIN"));
				String signature = Base64.encode(signature_byte);
				req.setSignatureValue(signature);
			} catch (NeedPinException e) {
				Panel_VerifyPin verifyPin = new Panel_VerifyPin();
				verifyPin.setBlockOnOpen(true);
				int w = verifyPin.open();
				if (w == 0) {
					UsbKeySKFImpl skf = (UsbKeySKFImpl) this.hsm;
					int result = skf.verifyPIN(verifyPin.pin);
					if (result == 0) { // 检测 pin的值
						byte[] signature_byte = this.hsm.sign(containerName, 0, null, hash, signAlgName,
								SM2Id.getSignId("ADMIN"));
						String signature = Base64.encode(signature_byte);
						req.setSignatureValue(signature);
					} else {
						throw new Exception(l.getString("Notice_error_PIN"));
					}
				} else {
					throw new Exception(l.getString("Notice_null_PIN"));
				}
			}
		}
		if (ALG.RSA == Env.alg) { // RSA 
			UsbKeyCSPImpl csp = (UsbKeyCSPImpl)this.hsm;
			String signCerStr = csp.exportCertBase64(IHSM.SIGN, containerName);
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
			X509Certificate signCer = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signCerStr)));
			req.setX509Certificate(signCer);
			byte[] signature_byte = this.hsm.sign(containerName, 0, null, data, signAlgName, null);
			String signature = Base64.encode(signature_byte);
			req.setSignatureValue(signature);
		}
		req.setAlgorithm(signAlgName);
		return req;
	}

	/**
	 * 请求 kmc Server
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public Response send(Request req) throws Exception {
			Response resKMC = comm.sendAndReceive(req);

			if (resKMC.getErrNo() != 0) {
				throw new ServerException(String.valueOf(resKMC.getErrNo()), resKMC.getErrMsg());
			}
			return resKMC;
//		} finally {
//			if (comm != null) {
//				comm.close();
//			}
//		}
	}
}
