package cn.com.infosec.netcert.communication;

import java.io.IOException;
import java.net.Socket;

import cn.com.infosec.netcert.base.Request;
import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.exceptions.NetCertProtocolException;


/**
 * 通讯服务提供者，封装通讯操作
 * <p>Title: 通讯服务提供者</p>
 * <p>Description: 用以封装Hello/Challenge机制</p>
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
	 * 接收请求
	 * @return 请求
	 * @throws IOException IO读写错误
	 * @throws NetCertProtocolException 不符合NetCert2.0的通信协议
	 */
	public Request recv() throws IOException, NetCertProtocolException {
		return null;
	}

	/**
	 * 发送请求，接收响应
	 * @param req 请求
	 * @return 响应
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
	 * 发送请求字符串，并接收响应字符串
	 * @param sReq 请求数据的字符串
	 * @return 响应字符串
	 * @throws IOException
	 * @throws NetCertProtocolException
	 */
	public String sendAndReceive(String sReq) throws IOException {

		return null;
	}
}