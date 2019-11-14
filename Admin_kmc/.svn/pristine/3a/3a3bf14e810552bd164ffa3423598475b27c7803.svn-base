package cn.com.infosec.netcert.communication;

/**
 * <p>Title: 通讯器接口</p>
 * <p>Description: 封装系统各部件之间的通信</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Infosec</p>
 * @author lixiangfeng
 * @version 2.0
 */

import java.io.*;
import cn.com.infosec.netcert.base.Request;
import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.exceptions.*;

public interface Communicator {
    
    static final long maxRequestLength = (40 * 1024) ;
    static final long maxResponseLength = (5*100 * 1024) ;

	static final int READ_BLOCK_LENGTH = 1024;
	static final int DATALENGTH_BLOCK_LENGTH = 16;

	/**
     * 发送字符串
     * @param data 要发送的数据
     * @throws IOException
     */	
    void send( String data )
        throws IOException;

    /**
     * 从网络接收一个请求
     * @return 接收到的请求
     * @throws IOException IO读写错误
     * @throws NetCertProtocolException 不符合NetCert2.0的通信协议
     */
    Request recv()
        throws IOException, NetCertProtocolException;

    /**
     * 发送请求并接收响应
     * @param req 请求
     * @return 接收到的响应
     * @throws IOException IO读写错误
     * @throws NetCertProtocolException 不符合NetCert2.0的通信协议
     */
    Response sendAndReceive( Request req )
        throws IOException,
        NetCertProtocolException;

    /**
     * 接收一个字符串数据，该字符串数据必须是一个合法的请求
     * @param sReq 请求的字符串
     * @return 响应字符串
     * @throws IOException IO读写错误
     * @throws NetCertProtocolException  不符合NetCert2.0的通信协议
     */
    String sendAndReceive( String sReq )
        throws IOException,
        NetCertProtocolException;
    
    void close();
}




