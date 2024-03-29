package cn.com.infosec.netcert.kmcAdmin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeySKFImpl;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelConf;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelSecure;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelSign;

/**
 * 客户端的核心配置信息
 * @Author 江岩    
 * @Time 2019-06-10 18:02
 */
public class Env {
	public static String separator = System.getProperty("line.separator");
	public static final char Role_SA = 'A', Role_BA = 'B', Role_BO = 'C', Role_AA = 'E', Role_AO = 'D';
	public static final String BO_Policy = "(LOGIN,GENPREKEY,COUNTKEYS,RECOVERBYCATCHPOLL,VIEWCURKEYS,VIEWCASTATE,VIEWOLDKEYS)",
			CatchPoll_Policy = "(CATCHPOLL)";
	public static final String keyConfigFile = "key.cfg", ConfigFile = "kmcadmin.cfg";
	public static Map<String, UsbKeySKFImpl> map = new HashMap<String, UsbKeySKFImpl>();
	public static String host, trustCerFile, issuerDN, loginID = "";// 登录用户的用户名，登录成功后由服务端返回
	public static int port, timeout;
	public static boolean isSSL = false;
	public static KMClient client;
	public static enum ALG {
		RSA, SM2
	};
	public static ALG alg;
	private static ChannelConf channelConf;
	private static ChannelSecure channelSecure;
	private static boolean inited = false;
	private static long lastOperationTime = new Date().getTime();
	private static long validTime = 30 * 60 * 1000L;
	private static Locale locale = Locale.CHINA;

	/**
	 * 初始化 kmcAdmin  
	 * @Author 江岩 
	 * @Time   2019-06-10 17:59
	 * @version 1.0
	 */
	public static void initConfig() throws Exception {
		File f = new File(ConfigFile);
		if (!f.exists()) {
			f.createNewFile();
			Properties p = new Properties();
			p.setProperty("host", "");
			p.setProperty("port", "");
			p.setProperty("timeout", "");
			p.setProperty("useSSL", "false");
			p.setProperty("caCer", "");
			FileOutputStream os = new FileOutputStream(f);
			p.store(os, null);
			os.close();
			Env.alg = ALG.SM2;
		} else {
			FileInputStream fin = new FileInputStream(f);
			Properties p = new Properties();
			p.load(fin);
			fin.close();
			host = p.getProperty("host");
			if(host == null || host.length() == 0) {
				Env.alg = ALG.SM2;
				return;
			}
			port = Integer.parseInt(p.getProperty("port"));
			timeout = Integer.parseInt(p.getProperty("timeout"));
			String trustCer = p.getProperty("caCer");
			if (trustCer != null && trustCer.length() > 0) {
				File fca = new File(trustCer);
				if (fca.exists()) {
					fin = new FileInputStream(trustCer);
					CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
					X509Certificate trustCert = (X509Certificate) cf.generateCertificate(fin);
					fin.close();
					trustCerFile = trustCer;
					if ("RSA".equalsIgnoreCase(trustCert.getPublicKey().getAlgorithm())) {
						alg = ALG.RSA;
					} else {
						alg = ALG.SM2;
					}
					isSSL = Boolean.valueOf(p.getProperty("useSSL"));
					if (isSSL) {
						channelSecure = new ChannelSecure("ssl");
						channelSecure.setConf(ChannelSecure.ALG, alg.name());
						channelSecure.setConf(ChannelSecure.SSL_TrustStore, trustCerFile);
					} else {
						channelSecure = new ChannelSecure("plain");
					}
					channelConf = new ChannelConf(host, port, 0, 10, timeout);
					issuerDN = trustCert.getIssuerDN().getName();
					inited = true;
				}
			}
		}
	}
	
	/**
	 * 获取语言资源包  
	 * @return (ResourceBundle)
	 * @Author 江岩 
	 * @Time   2019-06-10 17:57
	 * @version 1.0
	 */
	public static ResourceBundle getLanguage() {
		return ResourceBundle.getBundle("cn.com.infosec.netcert.kmcAdmin.ui.resource.language", locale);
	}

	/**
	 * 获取所有使用的 skf 对象
	 * @return   (Map)     
	 * @Author 江岩 
	 * @Time   2019-06-05 09:52
	 * @version 1.0
	 */
	public static Map<String, UsbKeySKFImpl> getMap() {
		return map;
	}

	/**
	 * 判断是否是 windows
	 * @return  boolean      
	 * @Author 江岩 
	 * @Time   2019-06-10 17:58
	 * @version 1.0
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	/**
	 * 创建一个 KMC Client   
	 * @Author 江岩 
	 * @Time   2019-06-10 18:00
	 * @version 1.0
	 */
	public static KMClient getClient(ChannelSign chSign) throws Exception {
		client = new KMClient(chSign, channelSecure, channelConf);
		return client;
	}

	/**
	 * 标记 完成初始化
	 * @return  (boolean)      
	 * @Author 江岩 
	 * @Time   2019-06-10 18:00
	 * @version 1.0
	 */
	public static boolean isInited() {
		return inited;
	}

	/**
	 *  记录按钮的最后操作时间
	 * @Author 江岩 
	 * @Time   2019-06-05 10:04
	 * @version 1.0
	 */
	public static void setLastOperationTime() {
		lastOperationTime = new Date().getTime();
	}

	/**
	 * 判断session 是否失效 
	 * @Description  30分钟失效
	 * @return   (boolean)     
	 * @Author 江岩 
	 * @Time   2019-06-05 10:05
	 * @version 1.0
	 */
	public static boolean validSession() {
		if (new Date().getTime() - lastOperationTime > validTime) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 加载 key.Properties 
	 * @Author 江岩 
	 * @Time   2019-08-01 20:52
	 * @version 1.0
	 */
	public static Properties getProperties(File file) throws IOException {
		Properties pro = new Properties();
		FileInputStream in = new FileInputStream(file);
		pro.load(in);
		in.close();
		return pro;
	}
}
