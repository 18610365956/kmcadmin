package cn.com.infosec.netcert.kmcAdmin.test;

import cn.com.infosec.netcert.framework.crypto.IHSM;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CspCertInfo;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeyCSPImpl;
import cn.com.infosec.util.Base64;

public class Test_RSA_Demo {
	
	//private static boolean showMicro = false;
	public static void main(String[] args) throws Exception{
		//list CSP provider
		String[] pro = UsbKeyCSPImpl.listProvider();
		for(String s : pro){
			System.out.println(s);
		}
		
		//list cer
		//CspCertInfo[] cerList = new UsbKeyCSPImpl("HaiTai Cryptographic Service Provider 00001").listCert(IHSM.SIGN);
		CspCertInfo[] cerList = new UsbKeyCSPImpl("XASJ Cryptographic Service Provider 31001").listCert(IHSM.SIGN);
		System.out.println("----------------------------");
		for(CspCertInfo cc : cerList){

			System.out.println(cc.provider);
			System.out.println("[容器: "+cc.container+"], [SN: "+cc.sn+"], [主题: "+cc.subject+"], [NB: "+cc.notBefore+"]");
//			if("XASJ Cryptographic Service Provider 31001".equals(cc.provider)){
//				System.out.println("[容器: "+cc.container+"], [SN: "+cc.sn+"], [主题: "+cc.subject+"], [NB: "+cc.notBefore+"]");
//			}
		}

		//gen P10
		UsbKeyCSPImpl csp = new UsbKeyCSPImpl("XASJ Cryptographic Service Provider 31001");
		//System.out.println("----------------------------");
		//String[] r = csp.genP10(1024, "cn=test");
		//System.out.println("容器："+r[0]);
		//System.out.println("p10: "+r[1]);
		
		//写签名证书
	//	csp.importCert(r[0], "MIIDNDCCAhygAwIBAgIDCAlbMA0GCSqGSIb3DQEBCwUAMCUxFTATBgNVBAMTDEFCQyBURVNUIENBMjEMMAoGA1UEChMDQUJDMB4XDTE1MTAzMTE2MDAwMFoXDTIwMTAzMTE2MDAwMFowUDEbMBkGA1UEAwwSd3d3MS50ZXN0Ljk1NTk5LmNuMSMwIQYDVQQLDBpBZ3JpY3VsdHVyYWwgQmFuayBvZiBDaGluYTEMMAoGA1UECgwDQUJDMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6lUgyMKm9FJ1yRsfCkZkjDo8bGHEYY0r+nTiFTbyGodzqUA007n+2RgI1uQ2V8VuNX8XYVsKXP+wSAuN/nFvY0wpAHyB4cKYEHfj75n9N4y+s31mXOTPhiChpnU/CbvYS1kUtc9DM+1gXnln+11XeakGrKNUYhWftogFqhSXh6Iq/I2xBctBm8Kr9yS0KqwUwMD/T+VJsopE98eb2pXVWfzjKRV2OLHv86Zyfh5JhzsXDuLfJfv7btKPpx3H66xRP5erLxPikoBfBXvTEcDVYMPzGrDaPmEGoz3cHYP3qgRN5KzYEdbdMsVGibuSw8u8L9mWxpXVvSiS73s8UjiVbwIDAQABo0IwQDAdBgNVHQ4EFgQUv+9STjJ6dN7zejd3H0SajBx42QYwHwYDVR0jBBgwFoAUErtyLm5Iqy6z01KtzsJ33EmoKtIwDQYJKoZIhvcNAQELBQADggEBABAUPsUSb2aC2Pn0YqE+xh2Kw0QfssRW54AgpoK1uKl7KYV1NWg62ZR0T0akZS7PJg3qIkbTAtwnjKGA5ZT3BpPy/SjWWGtFrpFR7iAnrsl3K8ccVr/JwhYF0HLod5ocp3MKvw0nHyCnjjJnrTZutJjbxCKecDaJZ4b04FSt9wQz3KNUdG3sJcuhvqwEb84VhmHvsVTBwrhDGTmiXIEpk8czkdBl0T3EvInREa8Pu0uuAfmtnjsijM0IKsMOXNmCVmQ7w0mtf1pz9I7fBF0O+TWVVPJZrDrYmlBmHgXw5SSn/qO9bH5mbWw/Wo19rlZDXRZnm91JWtCPnDoYE+YDjbk=");

		//写加密证书
	//	String tmpPk = csp.genTmpKeyPair();
	//	csp.importEncKeyPair(r[0], "x509CertBase64", "encPrikeyBase64", "ukekBase64");
		
		//sign
		// [60, 115, 111, 117, 114, 99, 101, 62, 60, 104, 101, 97, 100, 101, 114, 62, 60, 73, 68, 62, 48, 48, 68, 51, 55, 50, 65, 57, 60, 47, 73, 68, 62, 60, 84, 65, 83, 75, 84, 65, 71, 62, 57, 57, 49, 57, 50, 60, 47, 84, 65, 83, 75, 84, 65, 71, 62, 60, 76, 79, 67, 65, 76, 84, 73, 77, 69, 62, 49, 53, 54, 53, 53, 55, 57, 53, 56, 50, 48, 56, 48, 60, 47, 76, 79, 67, 65, 76, 84, 73, 77, 69, 62, 60, 86, 69, 82, 83, 73, 79, 78, 62, 48, 60, 47, 86, 69, 82, 83, 73, 79, 78, 62, 60, 84, 89, 80, 69, 62, 76, 79, 71, 73, 78, 60, 47, 84, 89, 80, 69, 62, 60, 47, 104, 101, 97, 100, 101, 114, 62, 60, 47, 115, 111, 117, 114, 99, 101, 62]


		//byte[] ss = new byte[]{60, 104, 101, 97, 100, 101, 114, 62, 60, 118, 101, 114, 115, 105, 111, 110, 62, 49, 46, 48, 60, 47, 118, 101, 114, 115, 105, 111, 110, 62, 60, 116, 121, 112, 101, 62, 76, 79, 71, 73, 78, 60, 47, 116, 121, 112, 101, 62, 60, 108, 111, 99, 97, 108, 116, 105, 109, 101, 62, 49, 53, 54, 53, 53, 55, 57, 57, 52, 52, 53, 55, 51, 60, 47, 108, 111, 99, 97, 108, 116, 105, 109, 101, 62, 60, 116, 97, 115, 107, 116, 97, 103, 62, 50, 52, 56, 55, 56, 48, 55, 56, 57, 60, 47, 116, 97, 115, 107, 116, 97, 103, 62, 60, 105, 100, 62, 60, 47, 105, 100, 62, 60, 47, 104, 101, 97, 100, 101, 114, 62, 60, 98, 111, 100, 121, 62, 60, 47, 98, 111, 100, 121, 62};
/*
		try {
			byte[] s = csp.sign("6EAB26CD-91B9-49B7-A16B-6B4E6FBAC36D", 0, null, ss, "SHA1withRSA", null);
			System.out.println(Base64.encode(s));
			
		} catch (Exception e){
			e.printStackTrace();
		}
		*/
		try {
			UsbKeyCSPImpl userCSP = new UsbKeyCSPImpl("XASJ Cryptographic Service Provider 31001");
			//UsbKeyCSPImpl userCSP = new UsbKeyCSPImpl("HaiTai Cryptographic Service Provider 00001");
			userCSP.exportCertBase64(IHSM.SIGN, "14BD6AE4-359B-42F0-BB4A-7429A815377A"); // AA 证书的容器名
		} catch (Exception e){
			e.printStackTrace();
		} 
		
		System.out.println("111");
		UsbKeyCSPImpl.free();
	}
}



