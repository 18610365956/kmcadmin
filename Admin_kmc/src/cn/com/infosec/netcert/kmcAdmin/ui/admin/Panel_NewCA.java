package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.infosec.netcert.base.Request;
import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.UsbKeySKFImpl;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import cn.com.infosec.util.Base64;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * new CA 
 * @Author 江岩    
 * @Time 2019-06-10 17:53
 */
public class Panel_NewCA extends ApplicationWindow {
	public Map<String, UsbKeySKFImpl> map;
	private Text txt_caName, txt_innerName, txt_cer, txt_idDN;
	private String cerB64;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();
	/**
	 * 构造方法
	 */
	public Panel_NewCA() {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
	}

	/**
	 * 视图页面
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginLeft = 20;
		gridLayout.marginBottom = 20;
		gridLayout.marginTop = 20;
		gridLayout.marginRight = 25;
		gridLayout.verticalSpacing = 15;
		container.setLayout(gridLayout);

		Label lbl_unIdentify = new Label(container, SWT.NONE);
		GridData gd_lbl_unIdentify = new GridData(SWT.RIGHT);
		gd_lbl_unIdentify.widthHint = 101;
		gd_lbl_unIdentify.horizontalAlignment = SWT.RIGHT;
		lbl_unIdentify.setLayoutData(gd_lbl_unIdentify);
		lbl_unIdentify.setAlignment(SWT.RIGHT);
		lbl_unIdentify.setText(l.getString("innerName") + "：");

		txt_innerName = new Text(container, SWT.BORDER);
		GridData gd_txt_innerName = new GridData();
		gd_txt_innerName.widthHint = 237;
		gd_txt_innerName.horizontalSpan = 2;
		txt_innerName.setTextLimit(20);
		txt_innerName.setLayoutData(gd_txt_innerName);

		Label lbl_caName = new Label(container, SWT.NONE);
		GridData gd_lbl_caName = new GridData(SWT.RIGHT);
		gd_lbl_caName.horizontalAlignment = SWT.RIGHT;
		lbl_caName.setLayoutData(gd_lbl_caName);
		lbl_caName.setAlignment(SWT.RIGHT);
		lbl_caName.setText(l.getString("CA_name") + "：");

		txt_caName = new Text(container, SWT.BORDER);
		GridData gd_txt_caName = new GridData();
		gd_txt_caName.widthHint = 237;
		txt_caName.setLayoutData(gd_txt_caName);
		txt_caName.setTextLimit(20);
		new Label(container, SWT.NONE);

		Label lbl_rootCert = new Label(container, SWT.NONE);
		GridData gd_lbl_rootCert = new GridData(SWT.RIGHT);
		gd_lbl_rootCert.horizontalAlignment = SWT.RIGHT;
		lbl_rootCert.setLayoutData(gd_lbl_rootCert);
		lbl_rootCert.setAlignment(SWT.RIGHT);
		lbl_rootCert.setText(l.getString("CA_cert") + "：");

		txt_cer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txt_cer = new GridData(SWT.LEFT);
		gd_txt_cer.widthHint = 237;
		txt_cer.setLayoutData(gd_txt_cer);

		Button btn_select = new Button(container, SWT.NONE);
		GridData gd_btn_select = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_select.widthHint = 69;
		btn_select.setLayoutData(gd_btn_select);
		btn_select.setText(l.getString("browse"));

		btn_select.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.cer" });
				String f = fd.open();
				if (f != null) {
					try {
						CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
						FileInputStream fin = new FileInputStream(f);
						Certificate cer = cf.generateCertificate(fin);
						fin.close();
						cerB64 = Base64.encode(cer.getEncoded());
						txt_cer.setText(f);
					} catch (Exception ex) {
						txt_cer.setText("");
						log.errlog("Choose cert fail", ex);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_error_CACertFormat"));
						mb.open();
					}
				}
			}
		});

		Label lbl_identifyCertDn = new Label(container, SWT.WRAP | SWT.RIGHT);
		GridData gd_lbl_identifyCertDn = new GridData(SWT.RIGHT);
		gd_lbl_identifyCertDn.horizontalAlignment = SWT.RIGHT;
		lbl_identifyCertDn.setLayoutData(gd_lbl_identifyCertDn);
		lbl_identifyCertDn.setAlignment(SWT.RIGHT);
		lbl_identifyCertDn.setText(l.getString("identify_certDN") + "：");

		txt_idDN = new Text(container, SWT.BORDER);
		GridData gd_txt_idDN = new GridData(SWT.LEFT);
		gd_txt_idDN.widthHint = 236;
		txt_idDN.setLayoutData(gd_txt_idDN);
		txt_idDN.setTextLimit(120);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Button btn_check = new Button(container, SWT.NONE);
		GridData gd_btn_check = new GridData(SWT.CENTER);
		gd_btn_check.horizontalAlignment = SWT.CENTER;
		gd_btn_check.widthHint = 77;
		btn_check.setLayoutData(gd_btn_check);
		btn_check.setText(l.getString("review"));
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		lblNewLabel.setText("* "+ l.getString("Notice_reviewAdmin"));
		
		btn_check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String innerName = txt_innerName.getText().trim();
				String caName = txt_caName.getText().trim();
				String idDN = txt_idDN.getText().trim();
				if (innerName == null || innerName.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_innerName"));
					mb.open();
					return;
				}
				if (caName == null || caName.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_CAname"));
					mb.open();
					return;
				}
				if (cerB64 == null || cerB64.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_CACert"));
					mb.open();
					return;
				}
				if (idDN == null || idDN.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_identifyCertDN"));
					mb.open();
					return;
				}
				// 选择 复核员
				try {
					Panel_Checking pc = new Panel_Checking();
					pc.setBlockOnOpen(true);
					if (0 == pc.open()) {
						Properties p = new Properties();
						p.setProperty("CAINNERNAME", innerName);
						p.setProperty("CANAME", caName);
						p.setProperty("CERT", cerB64);
						p.setProperty("IDENTITY_CERT_DN", txt_idDN.getText().trim());
						
						Request req = Env.client.makeRequest("ADDCA", p);
						if (Env.alg == ALG.SM2) {
							pc.genSM2Req(req);
						} else {
							pc.genRSAReq(req);
						}
						Env.client.send(req);
						MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						mb.setMessage(l.getString("Notice_succ_addCA") +","+ l.getString("Notice_isloadCA") + "?");
						int w = mb.open();
						if (w == 64) {
							Properties p_reload = new Properties();
							Env.client.sendRequest("RELOADCACHANNEL", p_reload);
							MessageBox mb_reload = new MessageBox(getShell(), SWT.OK);
							mb_reload.setMessage(l.getString("Notice_succ_loadCAInfo"));
							mb_reload.open();
						}
						close();
					}
				} catch (ServerException se) {
					log.errlog("Add CA fail", se);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_addCA") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
					mb.open();
				} catch (Exception ee) {
					log.errlog("Add CA fail", ee);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_addCA"));
					mb.open();
				}
			}
		});
		return container;
	}

	/**
	 * 视图标题命名
	 * @param (Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("addCA"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}
