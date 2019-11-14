package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
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

import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.util.Base64;

/**
 * new BO 业务操作员
 * @Author 江岩    
 * @Time 2019-06-10 17:51
 */
public class Panel_NewBO extends ApplicationWindow {
	private Text txt_userName, txt_cer;
	private String cerB64;

	private Button rBO, rCatchpoll;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * 构造方法
	 */
	public Panel_NewBO() {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
	}

	/**
	 * 视图页面
	 * @Author 江岩      
	 * @Time 2019-06-10 17:52
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginBottom = 40;
		gridLayout.marginTop = 30;
		gridLayout.marginLeft = 30;
		gridLayout.marginRight = 40;
		gridLayout.verticalSpacing = 20;
		container.setLayout(gridLayout);

		Label lbl_username = new Label(container, SWT.NONE);
		lbl_username.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_username.setAlignment(SWT.RIGHT);
		lbl_username.setText(l.getString("username") + "：");

		txt_userName = new Text(container, SWT.BORDER);
		GridData gd_txt_userName = new GridData();
		gd_txt_userName.widthHint = 197;
		gd_txt_userName.horizontalSpan = 2;
		txt_userName.setLayoutData(gd_txt_userName);
		txt_userName.setTextLimit(20);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		rBO = new Button(container, SWT.RADIO);
		GridData gd_rBO = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_rBO.widthHint = 94;
		rBO.setLayoutData(gd_rBO);
		rBO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		rBO.setBounds(82, 61, 83, 19);
		rBO.setText(l.getString("BO"));
		rBO.setSelection(true);

		rCatchpoll = new Button(container, SWT.RADIO);
		rCatchpoll.setBounds(204, 61, 83, 19);
		rCatchpoll.setText(l.getString("restorer"));
		new Label(container, SWT.NONE);

		Label lbl_cert = new Label(container, SWT.NONE);
		lbl_cert.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_cert.setAlignment(SWT.RIGHT);
		lbl_cert.setBounds(24, 98, 49, 21);
		lbl_cert.setText(l.getString("cert") + "：");

		txt_cer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txt_cer = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_txt_cer.widthHint = 195;
		txt_cer.setLayoutData(gd_txt_cer);
		txt_cer.setBounds(79, 95, 242, 19);

		Button btn_browse = new Button(container, SWT.NONE);
		btn_browse.setBounds(79, 120, 68, 29);
		btn_browse.setText(l.getString("browse"));
		GridData gd_btn_borwse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_borwse.widthHint = 67;
		btn_browse.setLayoutData(gd_btn_borwse);
		btn_browse.addSelectionListener(new SelectionAdapter() {
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
		new Label(container, SWT.NONE);
		Button btn_ok = new Button(container, SWT.NONE);
		GridData gd_btn_ok = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_btn_ok.widthHint = 82;
		btn_ok.setLayoutData(gd_btn_ok);
		btn_ok.setBounds(253, 176, 68, 29);
		btn_ok.setText(l.getString("OK"));
		new Label(container, SWT.NONE);
		btn_ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String uname = txt_userName.getText().trim();
				if (uname == null || uname.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_username"));
					mb.open();
					return;
				}
				if (cerB64 == null || cerB64.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_not_chooseCert"));
					mb.open();
					return;
				}
				String policy = null;
				Properties p = new Properties();
				p.setProperty("ID", uname);
				p.setProperty("ROLE", String.valueOf(Env.Role_BO));
				if (rCatchpoll.getSelection()) {
					policy = String.valueOf(Env.Role_BO) + Env.CatchPoll_Policy;
				} else {
					policy = String.valueOf(Env.Role_BO) + Env.BO_Policy;
				}
				p.setProperty("POLICY", policy);
				p.setProperty("CERT", cerB64);
				try {
					Env.client.sendRequest("APPLYOPERATORID", p);
					MessageBox mb = new MessageBox(getShell(), SWT.OK);
					mb.setMessage(l.getString("Notice_succ_newAdmin"));
					mb.open();
					close();
				} catch (ServerException se) {
					log.errlog("New admin fail", se);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(
							l.getString("Notice_fail_newAdmin") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
					mb.open();
				} catch (Exception ee) {
					log.errlog("New admin fail", ee);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_newAdmin"));
					mb.open();
				}
			}
		});
		return container;
	}

	/**
	 * 视图标题栏命名
	 * @param   (Shell)  
	 * @Author 江岩      
	 * @Time 2019-06-10 17:52
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("newBO"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

}
