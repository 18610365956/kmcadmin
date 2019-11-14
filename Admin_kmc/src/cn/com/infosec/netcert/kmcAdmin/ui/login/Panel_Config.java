package cn.com.infosec.netcert.kmcAdmin.ui.login;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Utils;

/**
 * kmcAdmin 配置视图
 * @Author 江岩
 * @Time 2019-06-10 16:57
 */
public class Panel_Config extends ApplicationWindow {

	private Text host, port, timeout, caCerFile;
	private Button chkSSL;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * 构造方法
	 */
	public Panel_Config() {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
	}

	/**
	 * 视图页面
	 * @Author 江岩
	 * @Time 2019-06-10 16:57
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.verticalSpacing = 15;
		gl_container.horizontalSpacing = 8;
		gl_container.marginTop = 15;
		gl_container.marginLeft = 20;
		gl_container.marginBottom = 5;
		container.setLayout(gl_container);

		Label lbl_kmcAddress = new Label(container, SWT.NONE);
		GridData gd_lbl_kmcAddress = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_kmcAddress.widthHint = 72;
		lbl_kmcAddress.setLayoutData(gd_lbl_kmcAddress);
		lbl_kmcAddress.setText(l.getString("host") + "：");
		lbl_kmcAddress.setAlignment(SWT.RIGHT);

		host = new Text(container, SWT.BORDER);
		GridData gd_host = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_host.widthHint = 135;
		host.setLayoutData(gd_host);
		host.setTextLimit(15);
		new Label(container, SWT.NONE);

		Label lbl_port = new Label(container, SWT.NONE);
		GridData gd_lbl_port = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_port.widthHint = 60;
		lbl_port.setLayoutData(gd_lbl_port);
		lbl_port.setText(l.getString("port") + "：");
		lbl_port.setAlignment(SWT.RIGHT);

		port = new Text(container, SWT.BORDER);
		GridData gd_port = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_port.widthHint = 59;
		port.setLayoutData(gd_port);
		port.setTextLimit(5);
		new Label(container, SWT.NONE);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText(l.getString("timeout") + "：");

		timeout = new Text(container, SWT.BORDER);
		GridData gd_timeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_timeout.widthHint = 57;
		timeout.setTextLimit(3);
		timeout.setLayoutData(gd_timeout);

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_1.widthHint = 50;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel_1.setText(l.getString("seconds"));

		chkSSL = new Button(container, SWT.CHECK);
		chkSSL.setText("SSL");

		Label lbl_trustCert = new Label(container, SWT.NONE);
		lbl_trustCert.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_trustCert.setText(l.getString("trust_Certificate") + "：");
		lbl_trustCert.setAlignment(SWT.RIGHT);

		caCerFile = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_caCerFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_caCerFile.widthHint = 210;
		caCerFile.setLayoutData(gd_caCerFile);
		caCerFile.setText("");

		Button btnBrowse = new Button(container, SWT.NONE);
		GridData gd_btnBrowse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse.widthHint = 70;
		btnBrowse.setLayoutData(gd_btnBrowse);
		btnBrowse.setText(l.getString("browse") + "...");

		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.cer", "*.*" });
				String f = fd.open();
				if (f != null) {
					caCerFile.setText(f);
				} else {
					caCerFile.setText("");
				}
			}
		});
		new Label(container, SWT.NONE);

		Button btnOk = new Button(container, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_btnOk.widthHint = 61;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText(l.getString("OK"));
		new Label(container, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String hostS = host.getText().trim();
				String portS = port.getText().trim();
				String timeoutS = timeout.getText().trim();
				String caCerS = caCerFile.getText().trim();

				if (!Utils.checkIp(hostS)) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_error_IP"));
					mb.open();
					return;
				}
				int result = Utils.checkPort(portS);
				if (result == 1) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_error_port"));
					mb.open();
					return;
				} else if (result == 2) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_portRange"));
					mb.open();
					return;
				}
				if (timeoutS == null || timeoutS.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_timeout"));
					mb.open();
					return;
				}
				try {
					int timeoutInt = Integer.valueOf(timeoutS);
					if (timeoutInt < 30 || timeoutInt > 300) {
						MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING);
						mb.setMessage(l.getString("Notice_error_timeoutRange"));
						mb.open();
						return;
					}
				} catch (NumberFormatException e1) {
					log.errlog("String_to_int fail", e1);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_error_timeoutFormat"));
					mb.open();
					return;
				}
				if (caCerS == null || caCerS.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_rootCert"));
					mb.open();
					return;
				}

				Properties p = new Properties();
				p.setProperty("host", hostS);
				p.setProperty("port", portS);
				p.setProperty("timeout", timeoutS);
				p.setProperty("useSSL", String.valueOf(chkSSL.getSelection()));
				p.setProperty("caCer", caCerS);

				try {
					FileOutputStream os = new FileOutputStream(Env.ConfigFile);
					p.store(os, null);
					os.close();
					Env.initConfig();
					close();
				} catch (FileNotFoundException e1) {
					log.errlog("File not found", e1);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_not_foundFile"));
					mb.open();
				} catch (IOException e1) {
					log.errlog("Save file error", e1);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_saveFile"));
					mb.open();
				} catch (Exception e1) {
					log.errlog("Init Admin fail", e1);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_init"));
					mb.open();
				}
			}
		});
		if (Env.isInited()) {
			host.setText(Env.host);
			port.setText(String.valueOf(Env.port));
			timeout.setText(String.valueOf(Env.timeout));
			chkSSL.setSelection(Env.isSSL);
			caCerFile.setText((Env.trustCerFile == null) ? "" : Env.trustCerFile);
		}
		getShell().setDefaultButton(btnOk);
		return container;
	}

	/**
	 * 视图标题栏设置
	 * @param (Shell)
	 * @Author 江岩      
	 * @Time 2019-06-10 16:58
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("AdminConfig"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}
