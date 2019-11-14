package cn.com.infosec.netcert.kmcAdmin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc TODO
 * @Author Infosec_jy
 * @Date 2019��2��25�� ����11:18:10
 */
public class Utils {
	public static final String NameConnector = new String(new byte[] { 10 });
	public static final String separator = System.getProperty("line.separator");

	/**
	 * @Desc TODO   IP ��ַ��֤
	 * @Authod ����
	 * @Date 2019��2��21�� ����2:17:00
	 */
	public static boolean checkIp(String ip){
		
		if(ip == null || ip.length() == 0)
			return false;
		String reg = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(ip);
        if (m.matches()) {
            return true;
		} else {
            return false;
        }
	}
	/**
	 * �˿ں�У��
	 * @Description (�˿ںŷ�Χ�� 1 �� 65535֮��)
	 * @param     (String)
	 * @return    (String) 1 ��ʽ�쳣   2 0--65535  0 �ɹ� 
	 * @Author ���� 
	 * @Time   2019-06-05 10:20
	 * @version 1.0
	 */
	public static int checkPort(String port) {  
		Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isPort = pattern.matcher(port);
        if (!isPort.matches()) {
            return 1;
        }
        if(Integer.valueOf(port) < 1 || Integer.valueOf(port) > 65535)
        	return 2;
        return 0;
	 }
}
