package cn.com.infosec.netcert.kmcAdmin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc TODO
 * @Author Infosec_jy
 * @Date 2019年2月25日 上午11:18:10
 */
public class Utils {
	public static final String NameConnector = new String(new byte[] { 10 });
	public static final String separator = System.getProperty("line.separator");

	/**
	 * @Desc TODO   IP 地址验证
	 * @Authod 江岩
	 * @Date 2019年2月21日 下午2:17:00
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
	 * 端口号校验
	 * @Description (端口号范围在 1 和 65535之间)
	 * @param     (String)
	 * @return    (String) 1 格式异常   2 0--65535  0 成功 
	 * @Author 江岩 
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
