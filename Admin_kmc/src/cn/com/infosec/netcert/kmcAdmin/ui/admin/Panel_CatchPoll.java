package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cn.com.infosec.netcert.framework.crypto.CryptoException;
import cn.com.infosec.netcert.framework.crypto.IHSM;
import cn.com.infosec.netcert.framework.crypto.SM2Id;
import cn.com.infosec.netcert.framework.crypto.impl.SoftCryptoAndStore;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CertContainer;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CspCertInfo;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.NeedPinException;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeyCSPImpl;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeySKFImpl;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.framework.utils.DNItemReverseUtil;
import cn.com.infosec.netcert.kmcAdmin.ui.login.Panel_ChooseCert;
import cn.com.infosec.netcert.kmcAdmin.ui.login.Panel_MessageDialog;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import cn.com.infosec.util.Base64;

/**
 * ˾����Կ�ָ�
 * @Author ����
 * @Time 2019-06-10 17:34
 */
public class Panel_CatchPoll extends ApplicationWindow {
	public Map<String, UsbKeySKFImpl> map;
	X509Certificate signCer, encCer;

	private Combo combo_dev, combo_devSN;
	private UsbKeySKFImpl userSKF;
	private UsbKeyCSPImpl userCSP;
	private CertContainer chooseCert;
	private CspCertInfo chooseCsp;
	private CertContainer[] cc, cc_enc;
	private CspCertInfo[] cspList;

	private String lib, containerName, keyDriverLibPath;
	private Properties pro;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * ���췽��
	 */
	public Panel_CatchPoll() {
		super(null);
		setShellStyle(SWT.MIN);
		try {
			pro = Env.getProperties(new File(Env.keyConfigFile));
		} catch (IOException e) {
			log.errlog("Load keyConfigFile fail.", e);
			Panel_MessageDialog dialog = new Panel_MessageDialog("error", "Load keyConfigFile fail.");
			dialog.setBlockOnOpen(true);
			dialog.open();
			return;
		}
	}

	/**
	 * ��ͼҳ��
	 * @Author ����
	 * @Time 2019-06-10 17:35
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginBottom = 40;
		gridLayout.marginLeft = 30;
		gridLayout.marginTop = 30;
		gridLayout.marginRight = 60;
		gridLayout.verticalSpacing = 15;
		container.setLayout(gridLayout);

		Label lbl_Usbkey = new Label(container, SWT.NONE);
		GridData gd_lblUsbkey = new GridData(SWT.RIGHT);
		gd_lblUsbkey.horizontalAlignment = SWT.RIGHT;
		lbl_Usbkey.setLayoutData(gd_lblUsbkey);
		lbl_Usbkey.setAlignment(SWT.RIGHT);
		lbl_Usbkey.setText(l.getString("driverName") + "��");

		combo_dev = new Combo(container, SWT.READ_ONLY);
		GridData gd_combo_dev = new GridData(SWT.LEFT);
		gd_combo_dev.widthHint = 173;
		combo_dev.setLayoutData(gd_combo_dev);
		combo_dev.add(l.getString("Please_choose_keyFile"));
		//combo_dev.select(0);
		
		if (ALG.SM2 == Env.alg) {
			@SuppressWarnings("unchecked")
			Enumeration<String> keyList = (Enumeration<String>) pro.propertyNames();
			while (keyList.hasMoreElements()) {
				combo_dev.add(keyList.nextElement());
			}
			if (combo_dev.getItemCount() != 0) {
				combo_dev.select(0);
				keyDriverLibPath = pro.getProperty(combo_dev.getText());
			}

			Label lbl_serialNum = new Label(container, SWT.NONE);
			lbl_serialNum.setLayoutData(new GridData(SWT.RIGHT));
			lbl_serialNum.setAlignment(SWT.RIGHT);
			lbl_serialNum.setText(l.getString("SN") + "��");

			combo_devSN = new Combo(container, SWT.READ_ONLY);
			GridData gd_combo_devSN = new GridData(SWT.LEFT);
			gd_combo_devSN.widthHint = 173;
			combo_devSN.setLayoutData(gd_combo_devSN);

			combo_dev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					combo_devSN.removeAll();
					if (combo_dev.getSelectionIndex() != 0) {
						keyDriverLibPath = pro.getProperty(combo_dev.getText());
						try {
							String[] devItems = UsbKeySKFImpl.enumDev(keyDriverLibPath);
							for (String s : devItems) {
								combo_devSN.add(s);
							}
							if (devItems.length != 0) {
								combo_devSN.select(0);
							}
						} catch (CryptoException e) {
							log.errlog("Enum device fail", e);
							MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
							mb.setMessage(l.getString("Notice_fail_enumDev"));
							mb.open();
						}
					}
				}
			});
		} else {
			String[] pro = UsbKeyCSPImpl.listProvider();
			for (String s : pro) {
				combo_dev.add(s);
			}
			if (pro.length != 0) {
				combo_dev.select(0);
			}			
		}
		new Label(container, SWT.NONE);
		Button btn_submit = new Button(container, SWT.NONE);
		GridData gd_btn_submit = new GridData(SWT.CENTER);
		gd_btn_submit.horizontalAlignment = SWT.CENTER;
		gd_btn_submit.widthHint = 78;
		btn_submit.setLayoutData(gd_btn_submit);
		btn_submit.setText(l.getString("OK"));
		btn_submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ALG.SM2 == Env.alg
						&& (combo_devSN.getText() == null || combo_devSN.getText().length() == 0)) {
					MessageBox mb = new MessageBox(getShell());
					mb.setMessage(l.getString("Notice_null_SN"));
					mb.open();
					return;
				}
				if (ALG.SM2 == Env.alg) {
					try {
						userSKF = new UsbKeySKFImpl(keyDriverLibPath, combo_devSN.getText().trim());
						Env.getMap().put(pro.getProperty(combo_dev.getText()) + combo_devSN.getText().trim(), userSKF);
					} catch (CryptoException ce) {
						log.errlog("New UsbKeySKFImpl fail", ce);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_newSKF"));
						mb.open();
						return;
					}
					try {
						try {
							cc = userSKF.enumCert(IHSM.SIGN);
						} catch (NeedPinException ee) {
							Panel_VerifyPin verifyPin = new Panel_VerifyPin();
							verifyPin.setBlockOnOpen(true);
							int w = verifyPin.open();
							if (w != 0) {
								return;
							}
							int result = userSKF.verifyPIN(verifyPin.pin);
							if (result == 0) {
								cc = userSKF.enumCert(IHSM.SIGN);
							}
						}
						CertContainer[] skfs = getMatchIssuerSM2Cert(cc);
						if (skfs.length == 0) {
							MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
							mb.setMessage(l.getString("Notice_not_foundIssuerCert"));
							mb.open();
							return;
						} else if (skfs.length == 1) {
							chooseCert = skfs[0];
						} else {
							Panel_ChooseCert choose = new Panel_ChooseCert(cc);
							choose.setBlockOnOpen(true);
							int w = choose.open();
							if (w == 0) { // ���Ͻǹر�(�����ر�)����ֵ Ϊ1 close() �رշ���ֵ
								chooseCert = choose.chooseCert;
							} else {
								return;
							}
						}
					} catch (CryptoException ee) {
						log.errlog("Enum Cert fail", ee);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_enumCert"));
						mb.open();
						return;
					}
					try {
						try {
							cc_enc = userSKF.enumCert(IHSM.ENC);
						} catch (NeedPinException ee) {
							Panel_VerifyPin verifyPin = new Panel_VerifyPin();
							verifyPin.setBlockOnOpen(true);
							int w = verifyPin.open();
							if (w != 0) {
								return;
							}
							int result = userSKF.verifyPIN(verifyPin.pin);
							if (result == 0) {
								cc_enc = userSKF.enumCert(IHSM.ENC);
							}
						}
					} catch (CryptoException ce) {
						log.errlog("Enum Cert fail", ce);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_enumCert"));
						mb.open();
						return;
					}
					containerName = chooseCert.getContainerName();
					signCer = chooseCert.getCert();
					for (CertContainer c : cc_enc) {
						if (containerName.equalsIgnoreCase(c.getContainerName())) {
							encCer = c.getCert();
						}
					}
					close();
				} else { // RSA
					lib = combo_dev.getText().trim();
					userCSP = new UsbKeyCSPImpl(lib);
					cspList = userCSP.listCert(IHSM.SIGN);
					CspCertInfo[] csps = getMatchIssuerRSACert(cspList);
					if (csps.length == 0) {
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_not_foundIssuerCert"));
						mb.open();
						return;
					} else if (csps.length == 1) {
						chooseCsp = csps[0];
					} else {
						Panel_ChooseCert choose = new Panel_ChooseCert(csps);
						choose.setBlockOnOpen(true);
						int w = choose.open();
						if (w == 0) { // ���Ͻǹر�(�����ر�)����ֵ Ϊ1 close() �رշ���ֵ
							chooseCsp = choose.chooseCsp;
						} else {
							return;
						}
					}
					containerName = chooseCsp.container;
					try {
						String signCerStr = userCSP.exportCertBase64(IHSM.SIGN, containerName);
						String encCerStr = userCSP.exportCertBase64(IHSM.ENC, containerName);			
						CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
						signCer = (X509Certificate) cf
								.generateCertificate(new ByteArrayInputStream(Base64.decode(signCerStr)));
						encCer = (X509Certificate) cf
								.generateCertificate(new ByteArrayInputStream(Base64.decode(encCerStr)));
						
					} catch (CryptoException e2) {
						log.errlog("Export Base64_Cert fail", e2);
						MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION);
						mb.setMessage(l.getString("Notice_error_exportBase64Cert"));
						mb.open();
					} catch (Exception e1) {
						log.errlog("Certificate_operation exception", e1);
						MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION);
						mb.setMessage(l.getString("Notice_error_CertificateFormat"));
						mb.open();
					}
					close();
				}
			}
		});
		return container;
	}

	// ƥ�� ��֤��䷢�� SM2 ��֤��
	private CertContainer[] getMatchIssuerSM2Cert(CertContainer[] cc) {
		List<CertContainer> matchSKFList = new ArrayList<CertContainer>();
		for (int i = 0; i < cc.length; i++) {
			if (isIssuerMatch(cc[i].getCert().getIssuerDN().getName())) {
				matchSKFList.add(cc[i]);
			}
		}
		CertContainer[] matchCSPs = new CertContainer[matchSKFList.size()];
		for (int i = 0; i < matchSKFList.size(); i++) {
			matchCSPs[i] = matchSKFList.get(i);
		}
		return matchCSPs;
	}

	// ƥ�� ��֤��䷢�� RSA ��֤��
	private CspCertInfo[] getMatchIssuerRSACert(CspCertInfo[] cspList) {
		List<CspCertInfo> matchCSPList = new ArrayList<CspCertInfo>();
		for (int i = 0; i < cspList.length; i++) {
			if (isIssuerMatch(cspList[i].issuer)) {
				matchCSPList.add(cspList[i]);
			}
		}
		CspCertInfo[] matchCSPs = new CspCertInfo[matchCSPList.size()];
		for (int i = 0; i < matchCSPList.size(); i++) {
			matchCSPs[i] = matchCSPList.get(i);
		}
		return matchCSPs;
	}

	// ֤�� DN ƥ�� issuerDN
	private boolean isIssuerMatch(String userDN) {
		try {
			String dn = DNItemReverseUtil.bigEnding(userDN);
			return DNItemReverseUtil.bigEnding(Env.issuerDN).equalsIgnoreCase(dn);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ǩ����ǩ
	 * @Time   2019-08-01 17:38
	 * @version 1.0
	 */
	byte[] sign(byte[] data) {
		byte[] bs = null;
		try {
			if (ALG.SM2 == Env.alg) {
				byte[] hash = SoftCryptoAndStore.SM3WithId(data, SM2Id.getSignId("ADMIN").getBytes(),
						signCer.getPublicKey());
				try {
					bs = userSKF.sign(containerName, 0, null, hash, IHSM.SM3withSM2, SM2Id.getSignId("ADMIN"));
				} catch (NeedPinException ee) {
					Panel_VerifyPin verifyPin = new Panel_VerifyPin();
					verifyPin.setBlockOnOpen(true);
					int w = verifyPin.open();
					if (w == 0) {
						int result = userSKF.verifyPIN(verifyPin.pin);
						if (result == 0) {
							bs = userSKF.sign(containerName, 0, null, hash, IHSM.SM3withSM2, SM2Id.getSignId("ADMIN"));
						} else {
							Panel_MessageDialog md = new Panel_MessageDialog("error", l.getString("Notice_error_PIN"));
							md.setBlockOnOpen(true);
							md.open();
							return null;
						}
					}
				}
			} else {
				byte[] hash = data;
				bs = userCSP.sign(containerName, 0, null, hash, IHSM.SHA1withRSA, null);
			}
			return bs;
		} catch (CryptoException e) {
			log.errlog("Sign fail.", e);
			String message = l.getString("Notice_fail_genSign");
			Panel_MessageDialog md = new Panel_MessageDialog("error", message);
			md.setBlockOnOpen(true);
			md.open();
			return null;
		}
	}

	/**
	 * ��ͼ����������
	 * @param     
	 * @Author ����      
	 * @Time 2019-06-10 17:36
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("key_judical_recover"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}