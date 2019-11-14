package cn.com.infosec.netcert.kmcAdmin.ui.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.sun.jna.Platform;

import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;

/**
 * 自定义 key 的名称
 * @Author 江岩
 * @Time 2019-08-02 11:57
 */
public class Panel_KeyLibConfig extends ApplicationWindow {
	private Text text_keyFile;
	private Combo combo_Name;
	
	private Properties pro;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();
	/**
	 * 构造方法
	 */
	public Panel_KeyLibConfig() {
		super(null);
		setShellStyle(SWT.MIN | SWT.RESIZE); // 支持最小化 和 拖动
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
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
	 * 视图页面绘画
	 * @Author 江岩
	 * @Time 2019-06-04 20:48
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container = new GridLayout(6, false);
		gl_container.marginTop = 15;
		gl_container.marginLeft = 15;

		container.setLayout(gl_container);

		Label lbl_diyName = new Label(container, SWT.NONE);
		GridData gd_lbl_diyName = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_diyName.widthHint = 92;
		lbl_diyName.setLayoutData(gd_lbl_diyName);
		lbl_diyName.setAlignment(SWT.RIGHT);
		lbl_diyName.setText(l.getString("customName") + "：");

		combo_Name = new Combo(container, SWT.NONE);
		GridData gd_combo_Name = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gd_combo_Name.widthHint = 224;
		combo_Name.setTextLimit(20);
		combo_Name.setLayoutData(gd_combo_Name);

		new Label(container, SWT.NONE);

		Label lbl_USBKey = new Label(container, SWT.NONE);
		GridData gd_lbl_USBKey = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_USBKey.widthHint = 73;
		lbl_USBKey.setLayoutData(gd_lbl_USBKey);
		lbl_USBKey.setAlignment(SWT.RIGHT);
		lbl_USBKey.setText(l.getString("driverName") + "：");

		text_keyFile = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_text_keyFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_text_keyFile.widthHint = 240;
		text_keyFile.setLayoutData(gd_text_keyFile);
		refreshName();
		if (combo_Name.getItemCount() != 0) {
			combo_Name.select(0);
			text_keyFile.setText(pro.getProperty(combo_Name.getText().trim()));
		}
		// 选中某一项
		combo_Name.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String libPath = pro.getProperty(combo_Name.getText().trim());
				text_keyFile.setText(libPath);
			}
		});
		// 修改了 值，做添加操作
		combo_Name.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String libPath = pro.getProperty(combo_Name.getText().trim());
				if (libPath == null || libPath.length() == 0) {
					text_keyFile.setText("");
				} else {
					text_keyFile.setText(libPath);
				}
			}
		});

		Button btn_browse = new Button(container, SWT.NONE);
		GridData gd_btn_browse = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btn_browse.widthHint = 67;
		btn_browse.setLayoutData(gd_btn_browse);
		btn_browse.setText(l.getString("browse") + "...");
		btn_browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				if (Env.isWindows()) {
					fd.setFilterExtensions(new String[] { "*.dll", "*.*" });
				} else {
					fd.setFilterExtensions(new String[] { "*.so", "*.*" }); // linux的库文件
				}
				String f = fd.open();
				if (f != null) {
					text_keyFile.setText(f);
				} else {
					text_keyFile.setText("");
				}
			}
		});
		new Label(container, SWT.NONE);

		Label lbl_redStar = new Label(container, SWT.NONE);
		GridData gd_lbl_redStar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_redStar.widthHint = 11;
		lbl_redStar.setLayoutData(gd_lbl_redStar);
		lbl_redStar.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lbl_redStar.setText("*");

		Label lbl_notice_64Bit = new Label(container, SWT.NONE);
		lbl_notice_64Bit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		if (Platform.is64Bit()) {
			lbl_notice_64Bit.setText(l.getString("Notice_choose64BitKeyDrive"));
		} else {
			lbl_notice_64Bit.setText(l.getString("Notice_choose32BitKeyDrive"));
		}

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblNewLabel = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 67;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("");

		Button btn_bind = new Button(container, SWT.NONE);
		GridData gd_btn_bind = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btn_bind.widthHint = 77;
		btn_bind.setLayoutData(gd_btn_bind);
		btn_bind.setText(l.getString("bind"));

		btn_bind.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String libName = combo_Name.getText().trim();
				String keyFile = text_keyFile.getText().trim();
				if (libName == null || libName.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_customName"));
					mb.open();
					return;
				}
				if (keyFile == null || keyFile.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_keyDriverFile"));
					mb.open();
					return;
				}
				String value = pro.getProperty(libName);
				String type = null;
				if (value == null || value.length() == 0) {
					pro.setProperty(libName, keyFile);
					type = "Notice_succ_bind";
				} else {
					pro.remove(libName);
					pro.setProperty(libName, keyFile);
					type = "Notice_succ_modify";
				}
				try {
					FileOutputStream fos = new FileOutputStream(Env.keyConfigFile);
					pro.store(fos, "");
					fos.close();
					refreshName();
					MessageBox mb = new MessageBox(getShell(), SWT.OK);
					mb.setMessage(l.getString(type));
					mb.open();
					close();
				} catch (Exception e2) {
					log.errlog("File not found/Load configFile fail.", e2);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_IOLoad"));
					mb.open();
				}
			}
		});

		Button btn_unBind = new Button(container, SWT.NONE);
		GridData gd_btn_unBind = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btn_unBind.widthHint = 74;
		btn_unBind.setLayoutData(gd_btn_unBind);
		btn_unBind.setText(l.getString("unbind"));
		new Label(container, SWT.NONE);
		btn_unBind.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				String libName = combo_Name.getText().trim();
				String keyFile = text_keyFile.getText().trim();
				if (libName == null || libName.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_customName"));
					mb.open();
					return;
				}
				if (keyFile == null || keyFile.length() == 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_null_keyDriverFile"));
					mb.open();
					return;
				}
				Object value = pro.get(libName);
				if (value != null) {
					try {
						pro.remove(libName);
						FileOutputStream fos = new FileOutputStream(Env.keyConfigFile);
						pro.store(fos, "");
						fos.close();
						refreshName();
						MessageBox mb = new MessageBox(getShell(), SWT.OK);
						mb.setMessage(l.getString("Notice_succ_unbind"));
						mb.open();
					} catch (Exception e2) {
						log.errlog("File not found/Load configFile fail.", e2);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_IOLoad"));
						mb.open();
					}
				} else {
					MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION);
					mb.setMessage(l.getString("Notice_notExist_libName"));
					mb.open();
				}
			}
		});
		return container;
	}

	/**
	 * 刷新 libName 的内容
	 */
	private void refreshName() {
		combo_Name.removeAll();
		@SuppressWarnings("unchecked")
		Enumeration<String> keyList = (Enumeration<String>) pro.propertyNames();
		while (keyList.hasMoreElements()) {
			combo_Name.add(keyList.nextElement());
		}
		if (combo_Name.getItemCount() != 0) {
			combo_Name.select(0);
			text_keyFile.setText(pro.getProperty(combo_Name.getText().trim()));
		}
	}

	/**
	 * 视图标题栏命名
	 * @param (shell)
	 * @Author 江岩
	 * @Time 2019-06-04 20:49
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("keyDriverNameConfig"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}
