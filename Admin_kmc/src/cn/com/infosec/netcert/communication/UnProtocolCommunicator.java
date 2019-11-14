package cn.com.infosec.netcert.communication;

import java.io.IOException;
import java.net.Socket;

import cn.com.infosec.netcert.base.Request;
import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.exceptions.NetCertProtocolException;


/**
 * ͨѶ�����ṩ�ߣ���װͨѶ����
 * <p>Title: ͨѶ�����ṩ��</p>
 * <p>Description: ���Է�װHello/Challenge����</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Infosec</p>
 * @author lixiangfeng
 * @version 2.0
 */
public class UnProtocolCommunicator extends BasicCommunicator {

	public UnProtocolCommunicator(Socket s) {
		super( s );
		//sslSocket = s;
	}


	/**
	 * @deprecated
	 * ��������
	 * @return ����
	 * @throws IOException IO��д����
	 * @throws NetCertProtocolException ������NetCert2.0��ͨ��Э��
	 */
	public Request recv() throws IOException, NetCertProtocolException {
		return null;
	}

	/**
	 * �������󣬽�����Ӧ
	 * @param req ����
	 * @return ��Ӧ
	 * @throws IOException
	 * @throws NetCertProtocolException
	 */
	public Response sendAndReceive(Request req)
		throws IOException, NetCertProtocolException {
		
		//req.setIp( this.sslSocket.getLocalAddress().getHostAddress() );
		Response res = null;
		send( req.toString() );

		int len = recvLength();
		if ((len <= 0) || (len > Communicator.maxRequestLength))
			throw new NetCertProtocolException("EOF of response");

		String sRes = recvData( len );
		res = new Response(sRes);
		return res;
	}

	/**
	 * @deprecated
	 * ���������ַ�������������Ӧ�ַ���
	 * @param sReq �������ݵ��ַ���
	 * @return ��Ӧ�ַ���
	 * @throws IOException
	 * @throws NetCertProtocolException
	 */
	public String sendAndReceive(String sReq) throws IOException {

		return null;
	}
}