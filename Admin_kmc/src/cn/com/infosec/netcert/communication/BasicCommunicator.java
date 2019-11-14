
package cn.com.infosec.netcert.communication;

import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * ����ͨ�Ż�����, �������������շ�����.
 * 
 * @author lixiangfeng
 *
 */
public abstract class BasicCommunicator implements Communicator {

	protected Socket sslSocket;
	private DataOutputStream out;
	private DataInputStream input = null;

	/**
	 * ���캯��
	 * 
	 * @param s ��������ͻ��˵�Socket����
	 */
	public BasicCommunicator(Socket s) {
		sslSocket = s;
		try {
			out = new DataOutputStream(sslSocket.getOutputStream());
			input = new DataInputStream(sslSocket.getInputStream());
		} catch (IOException e) {
		}
	}
	
	/**
	 * �����ַ�������
	 */
	public void send(String data) throws IOException {
		DecimalFormat dc = new DecimalFormat("0000000000000000");
		byte[] b = data.getBytes("UTF-8");
		out.write(dc.format(b.length).getBytes());
		out.flush();
		out.write(b);
		out.flush();
	}

	/**
	 * ��ȡ���ݿ�ͷ16���ֽ�,ȡ�ý������ݵĳ���. 
	 * 
	 * @return ���ݳ���
	 * @throws IOException
	 */
	public int recvLength() throws IOException {

		byte[] allData = new byte[DATALENGTH_BLOCK_LENGTH];
		byte[] data = null;
		int rlen = DATALENGTH_BLOCK_LENGTH;

		while (rlen > 0) {
			if (rlen > DATALENGTH_BLOCK_LENGTH)
				data = new byte[DATALENGTH_BLOCK_LENGTH];
			else
				data = new byte[rlen];

			int alen = input.read(data);
			
			if(alen<0)throw new IOException( "read length (len:" + alen + ")");
			System.arraycopy(
				data,
				0,
				allData,
				DATALENGTH_BLOCK_LENGTH - rlen,
				alen);
			rlen -= alen;
		}

		String sLen = new String(allData);
		int len = Integer.parseInt(sLen);
		return len;

	}

	/**
	 * ��ȡָ�����ȵ�����
	 * 
	 * @param len ���ݳ���
	 * @return �ַ�������
	 * @throws IOException
	 */
	public String recvData(int len) throws IOException {
		byte[] allData = new byte[len];
		byte[] data = null;
		int rlen = len;

		while (rlen > 0) {
			if (rlen > READ_BLOCK_LENGTH)
				data = new byte[READ_BLOCK_LENGTH];
			else
				data = new byte[rlen];

			int alen = input.read(data);
			if(alen<0)throw new IOException( "read data (len:" + alen + ")" );
			System.arraycopy(data, 0, allData, len - rlen, alen);
			rlen -= alen;
		}
		return new String(allData, "UTF-8");
	}

	public void close() {

		try {
			sslSocket.close();
		} catch (Throwable e) {

		}
		
	}
}
