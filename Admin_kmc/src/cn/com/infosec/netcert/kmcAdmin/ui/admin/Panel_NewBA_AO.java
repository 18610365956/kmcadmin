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
 * new BA
 * @Author 江岩
 * @Time 2019-06-10 17:50
 */
public class Panel_NewBA_AO extends ApplicationWindow {
	private Text txt_userName, txt_cer;
	private String policy = "", subRole = "", cerB64;
	private char currRole;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * 构造方法
	 */
	public Panel_NewBA_AO(char role) {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.currRole = role;
		if (Env.Role_SA == currRole) {
			subRole = String.valueOf(Env.Role_BA);
			policy = subRole + SA_AA.BA_Policy;
		} else if (Env.Role_AA == currRole) {
			subRole = String.valueOf(Env.Role_AO);
			policy = subRole + SA_AA.AO_Policy;
		}
	}

	/**
	 * 视图页面
	 * @Author 江岩
	 * @Time 2019-06-10 17:50
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginLeft = 30;
		gridLayout.marginBottom = 30;
		gridLayout.marginTop = 30;
		gridLayout.marginRight = 40;
		gridLayout.verticalSpacing = 20;
		container.setLayout(gridLayout);

		Label lbl_username = new Label(container, SWT.NONE);
		lbl_username.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_username.setAlignment(SWT.RIGHT);
		lbl_username.setText(l.getString("username") + "：");

		txt_userName = new Text(container, SWT.BORDER);
		GridData gd_txt_userName = new GridData(SWT.LEFT);
		gd_txt_userName.widthHint = 187;
		txt_userName.setLayoutData(gd_txt_userName);
		txt_userName.setTextLimit(20);
		new Label(container, SWT.NONE);

		Label lbl_cert = new Label(container, SWT.NONE);
		lbl_cert.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_cert.setAlignment(SWT.RIGHT);
		lbl_cert.setText(l.getString("cert") + "：");

		txt_cer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txt_cer = new GridData(SWT.LEFT);
		gd_txt_cer.widthHint = 188;
		txt_cer.setLayoutData(gd_txt_cer);

		Button btn_browse = new Button(container, SWT.NONE);
		btn_browse.setText(l.getString("browse"));
		GridData fd_btn_browse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		fd_btn_browse.widthHint = 69;
		btn_browse.setLayoutData(fd_btn_browse);
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
		btn_ok.setText(l.getString("OK"));
		GridData gd_btn_ok = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btn_ok.widthHint = 58;
		btn_ok.setLayoutData(gd_btn_ok);
		new Label(container, SWT.NONE);
		btn_ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String uname = txt_userName.getText().trim();
				if (uname == null || uname.length() == 0) {
					MessageBox mb = new MessageBox(getShell());
					mb.setMessage(l.getString("Notice_null_username"));
					mb.open();
					return;
				}
				if (cerB64 == null || cerB64.length() == 0) {
					MessageBox mb = new MessageBox(getShell());
					mb.setMessage(l.getString("Notice_not_chooseCert"));
					mb.open();
					return;
				}
				Properties p = new Properties();
				p.setProperty("ID", txt_userName.getText().trim());
				p.setProperty("ROLE", subRole);
				p.setProperty("POLICY", policy);
				p.setProperty("CERT", cerB64);

				String reqType = "APPLYID";
				if (Env.Role_AA == currRole)
					reqType = "APPLYAUDITID";
				try {
					Env.client.sendRequest(reqType, p);
					MessageBox mb = new MessageBox(getShell(), SWT.OK);
					mb.setMessage(l.getString("Notice_succ_newAdmin"));
					mb.open();
					close();
				} catch (ServerException se) {
					log.errlog("Env.client.sendRequest(reqType, p)", se);
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
	 * @return      
	 * @throws   
	 * @Author 江岩      
	 * @Time 2019-06-10 17:51
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (Env.Role_SA == currRole) {
			shell.setText(l.getString("newBA"));
		} else {
			shell.setText(l.getString("newAO"));
		}
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}
