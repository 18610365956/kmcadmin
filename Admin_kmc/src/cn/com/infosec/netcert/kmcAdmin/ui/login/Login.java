package cn.com.infosec.netcert.kmcAdmin.ui.login;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netcert.framework.crypto.CryptoException;
import cn.com.infosec.netcert.framework.crypto.IHSM;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CertContainer;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CspCertInfo;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.NeedPinException;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeyCSPImpl;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeySKFImpl;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.framework.transport.chanel.ChannelSign;
import cn.com.infosec.netcert.framework.utils.DNItemReverseUtil;
import cn.com.infosec.netcert.kmcAdmin.ui.admin.AO;
import cn.com.infosec.netcert.kmcAdmin.ui.admin.BA;
import cn.com.infosec.netcert.kmcAdmin.ui.admin.BO;
import cn.com.infosec.netcert.kmcAdmin.ui.admin.Panel_VerifyPin;
import cn.com.infosec.netcert.kmcAdmin.ui.admin.SA_AA;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import cn.com.infosec.netcert.kmcAdmin.utils.KMClient;
import cn.com.infosec.netcert.kmcAdmin.utils.Utils;

/**
 * KMC �ĵ�¼��ͼ
 * @Author ����
 * @Time 2019-06-10 16:43
 */
public class Login extends ApplicationWindow {
	private static Login login;
	private KMClient client;
	private Combo combo_dev, combo_devSN;
	private UsbKeySKFImpl skf;
	private Properties pro;
	private CertContainer[] cc;
	private CertContainer chooseCert;
	private CspCertInfo[] cspList;
	private CspCertInfo chooseCsp;
	private String lib, keyDriverLibPath, subject;
	private ChannelSign chSign;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * ���췽��
	 */
	public Login() {
		super(null);
		setShellStyle(SWT.MIN);
		Env.alg = ALG.SM2;
		try {
			if (Env.alg == ALG.SM2) {
				File f = new File(Env.keyConfigFile);
				if (!f.exists()) {
					f.createNewFile();
				}
				pro = Env.getProperties(f);
			}
			Env.initConfig();
		} catch (Exception e) {
			log.errlog("Admin init error", e);
			Panel_MessageDialog dialog = new Panel_MessageDialog("error", e.getMessage());
			dialog.setBlockOnOpen(true);
			dialog.open();
		}
		addMenuBar();
		addToolBar(SWT.FLAT | SWT.WRAP);
	}

	/**
	 * ��ͼ�滭
	 * @Author ����
	 * @Time 2019-06-10 16:43
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginTop = 30;
		gridLayout.marginLeft = 20;
		gridLayout.marginRight = 60;
		gridLayout.verticalSpacing = 15;
		container.setLayout(gridLayout);

		Label lblUsbkey = new Label(container, SWT.NONE);
		GridData gd_lblUsbkey = new GridData(SWT.RIGHT);
		gd_lblUsbkey.widthHint = 55;
		gd_lblUsbkey.horizontalAlignment = SWT.RIGHT;
		lblUsbkey.setLayoutData(gd_lblUsbkey);
		lblUsbkey.setAlignment(SWT.RIGHT);
		lblUsbkey.setText(l.getString("driverName") + ":");

		combo_dev = new Combo(container, SWT.READ_ONLY);
		GridData gd_combo_dev = new GridData(SWT.LEFT);
		gd_combo_dev.widthHint = 180;
		combo_dev.setLayoutData(gd_combo_dev);
		combo_dev.add(l.getString("Please_choose_keyFile"));
		combo_dev.select(0);
		
		if (ALG.SM2 == Env.alg) {
			@SuppressWarnings("unchecked")
			Enumeration<String> keyList = (Enumeration<String>) pro.propertyNames();
			while (keyList.hasMoreElements()) {
				combo_dev.add(keyList.nextElement());
			}

			Label lblSN = new Label(container, SWT.NONE);
			GridData gd_lblSN = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			gd_lblSN.widthHint = 86;
			lblSN.setLayoutData(gd_lblSN);
			lblSN.setAlignment(SWT.RIGHT);
			lblSN.setText(l.getString("SN") + ":");

			combo_devSN = new Combo(container, SWT.READ_ONLY);
			GridData gd_combo_devSN = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_combo_devSN.widthHint = 180;
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
		}
		new Label(container, SWT.NONE);
		final Button btn_login = new Button(container, SWT.NONE);
		GridData gridDatabtn = new GridData();
		gridDatabtn.widthHint = 75;
		gridDatabtn.horizontalAlignment = SWT.CENTER;
		btn_login.setLayoutData(gridDatabtn);
		btn_login.setText(l.getString("login"));
		btn_login.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.isInited()) {
					MessageBox mb = new MessageBox(getShell());
					mb.setMessage(l.getString("Notice_not_KMCconfig"));
					mb.open();
					return;
				}
				if (ALG.SM2 == Env.alg && (combo_devSN.getText() == null || combo_devSN.getText().length() == 0)) {
					MessageBox mb = new MessageBox(getShell());
					mb.setMessage(l.getString("Notice_null_SN"));
					mb.open();
					return;
				}
				if (ALG.SM2 == Env.alg) {
					try {
						keyDriverLibPath = pro.getProperty(combo_dev.getText().trim());
						skf = new UsbKeySKFImpl(keyDriverLibPath, combo_devSN.getText().trim());
						Env.getMap().put(pro.getProperty(combo_dev.getText()) + combo_devSN.getText().trim(), skf);
					} catch (CryptoException e2) {
						log.errlog("New SKF fail", e2);
						MessageBox mb = new MessageBox(getShell());
						mb.setMessage(l.getString("Notice_fail_newSKF"));
						mb.open();
						return;
					}
					try {
						try {
							cc = skf.enumCert(IHSM.SIGN);
						} catch (NeedPinException ee) {
							Panel_VerifyPin verifyPin = new Panel_VerifyPin();
							verifyPin.setBlockOnOpen(true);
							int w = verifyPin.open();
							if (w != 0) {
								return;
							} else {
								int result = skf.verifyPIN(verifyPin.pin);
								if (result == 0) {
									cc = skf.enumCert(IHSM.SIGN);
								} else {
									MessageBox mb = new MessageBox(getShell());
									mb.setMessage(l.getString("Notice_error_PIN"));
									mb.open();
									return;
								}
							}
						}
						CertContainer[] skfs = getMatchIssuerSM2Cert(cc);
						if (skfs.length == 0) {
							MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
							mb.setMessage(l.getString("Notice_not_foundIssuerCert"));
							mb.open();
							return;
						} else {
							Panel_ChooseCert choose = new Panel_ChooseCert(cc);
							choose.setBlockOnOpen(true);
							int w = choose.open();
							if (w == 0) { // ���Ͻǹر�(�����ر�)����ֵ Ϊ1 close() �رշ���ֵ
								chooseCert = choose.chooseCert;
								subject = choose.subject;
							} else {
								return;
							}
						}
					} catch (CryptoException e2) {
						log.errlog("Enum Cert fail", e2);
						MessageBox mb = new MessageBox(getShell());
						mb.setMessage(l.getString("Notice_fail_enumCert"));
						mb.open();
						return;
					}
					String keyIdx = combo_devSN.getText().trim() + Utils.NameConnector + chooseCert.getContainerName();
					chSign = new ChannelSign(keyDriverLibPath, keyIdx, null, IHSM.SM3withSM2, chooseCert.getCert());
				} else { // RSA
					lib = combo_dev.getText().trim();
					cspList = new UsbKeyCSPImpl(lib).listCert(IHSM.SIGN);
					CspCertInfo[] csps = getMatchIssuerRSACert(cspList);
					if (csps.length == 0) {
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_not_foundIssuerCert"));
						mb.open();
						return;
					} else {
						Panel_ChooseCert choose = new Panel_ChooseCert(csps);
						choose.setBlockOnOpen(true);
						int w = choose.open();
						if (w == 0) { // ���Ͻǹر�(�����ر�)����ֵ Ϊ1 close() �رշ���ֵ
							chooseCsp = choose.chooseCsp;
							subject = choose.subject;
						} else {
							return;
						}
					}
					String keyIdx = chooseCsp.sn + Utils.NameConnector + chooseCsp.container;
					chSign = new ChannelSign(lib, keyIdx, null, IHSM.SHA1withRSA, null);
				}
				try {
					client = Env.getClient(chSign);
					Properties p = new Properties();
					btn_login.setEnabled(false);
					Properties resp = client.sendRequest("LOGIN", p).getData();
					// ���õ�¼����Ա�Ĵ�ǩ����ͨѶ��ʽ
					Env.client = client;
					Env.loginID = resp.getProperty("ID");
					close();
					char role = resp.getProperty("POLICY").charAt(0);
					Env.setLastOperationTime(); // �ڵ�¼ʱ����ʼ����ǰʱ��

					switch (role) {
					case Env.Role_SA:
					case Env.Role_AA:
						SA_AA sa = new SA_AA(role, subject);
						sa.setBlockOnOpen(true);
						sa.open();
						break;
					case Env.Role_BA:
						BA ba = new BA(subject);
						ba.setBlockOnOpen(true);
						ba.open();
						break;
					case Env.Role_BO:
						BO bo = new BO(subject);
						bo.setBlockOnOpen(true);
						bo.open();
						break;
					case Env.Role_AO:
						AO ao = new AO(subject);
						ao.setBlockOnOpen(true);
						ao.open();
						break;
					}
					if (Env.alg == ALG.RSA) {
						release();
					} 
				} catch (Exception ee) {
					log.errlog("Login fail", ee);
					Panel_MessageDialog dialog = new Panel_MessageDialog("error", l.getString("Notice_fail_login"));
					dialog.setBlockOnOpen(true);
					dialog.open();
					btn_login.setEnabled(true);
				} finally {
					if (Env.alg == ALG.SM2) {
						release();
					}
				}
				
			}
		});
		getShell().setDefaultButton(btn_login);
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
	 * ��ͼ�˵���
	 * @Author ����
	 * @Time 2019-06-10 16:44
	 * @version 1.0
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager mm = new MenuManager();
		MenuManager menu = new MenuManager(l.getString("menu"));
		menu.add(new ConfigAction());
		if (ALG.SM2 == Env.alg) {
			menu.add(new KeyLibConfig());
		}
		Separator separator = new Separator();
		separator.setVisible(true);
		menu.add(separator);
		menu.add(new ExitAction());
		mm.add(menu);

		MenuManager about = new MenuManager(l.getString("about"));
		String version = Login.class.getPackage().getImplementationVersion();
		about.add(new Action(l.getString("version") + ":" + version) {
		});
		mm.add(about);
		return mm;
	}

	/**
	 * main ���� 
	 * @Author ����
	 * @Time 2019-06-10 16:45
	 * @version 1.0
	 */
	public static void main(String args[]) {
		Security.addProvider(new InfosecProvider());
		Login window = new Login();
		window.setBlockOnOpen(true);
		window.open();
		Display.getCurrent().dispose();
	}

	/**
	 * �˵��� config
	 * @Author ����
	 * @Time 2019-06-10 16:45
	 */
	private class ConfigAction extends Action {
		public ConfigAction() {
			setText(l.getString("config"));
		}

		public void run() {
			Panel_Config pc = new Panel_Config();
			pc.setBlockOnOpen(true);
			int w = pc.open();
			if (w == 0) {
				refreshLogin();
			}
		}
	}

	/**
	 * keyDriverName����
	 * @Author ����
	 * @Time 2019-06-04 19:17
	 * @version 1.0
	 */
	private class KeyLibConfig extends Action {
		public KeyLibConfig() {
			setText(l.getString("keyDriverNameConfig"));
		}

		public void run() {
			Panel_KeyLibConfig pc = new Panel_KeyLibConfig();
			pc.setBlockOnOpen(true);
			pc.open();
			refreshLogin();
		}
	}

	/**
	 * �����˳���ť
	 * @Author ����
	 * @Time 2019-06-10 16:46
	 */
	private class ExitAction extends Action {
		public ExitAction() {
			setText(l.getString("exit"));
		}

		public void run() {
			handleShellCloseEvent();
		}
	}

	/**
	 * ��д���ڹر��¼�,�����رհ�ť����
	 * @Description �ͷ�ռ�õ�key��Դ���ر���ͼ
	 * @Author ����
	 * @Time 2019-06-04 19:20
	 * @version 1.0
	 */
	@Override
	public void handleShellCloseEvent() {
		MessageBox messagebox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messagebox.setMessage(l.getString("Notice_exit") + "?");
		int closeCode = messagebox.open();
		if (closeCode == SWT.YES) {
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION);
			mb.setMessage(l.getString("Notice_removeCert"));
			mb.open();
			if(Env.alg == ALG.RSA) {
				release();
			}
			super.handleShellCloseEvent();
		}
	}

	/**
	 * �޸�ϵͳ���ã�ˢ�´��ڣ�����ʵ��
	 * @Author ���� 
	 * @Time   2019-08-14 13:41
	 * @version 1.0
	 */
	private void refreshLogin() {
		super.handleShellCloseEvent();
		login = new Login();
		login.setBlockOnOpen(true);
		login.open();
	}

	/**
	 * �ͷ� ռ����Դ
	 * @Author ����
	 * @Time 2019-06-10 10:26
	 * @version 1.0
	 */
	public void release() {
		if (ALG.SM2 == Env.alg) {
			Set<String> keySet = Env.getMap().keySet();
			for (String key : keySet) {
				UsbKeySKFImpl usbKeyImpl = Env.getMap().get(key);
				usbKeyImpl.release();
				break;
			}
		} else {
			UsbKeyCSPImpl.free();
		}
	}

	/**
	 * ��ͼ����������
	 * @param (Shell)
	 * @Author ����
	 * @Time 2019-06-10 16:45
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Infosec KMC-Admin");
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}