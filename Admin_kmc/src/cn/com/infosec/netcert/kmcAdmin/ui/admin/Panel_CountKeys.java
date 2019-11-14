package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;

/**   
 * @Description 密钥统计
 * @Author 江岩    
 * @Time 2019-08-29 15:15
 */
public class Panel_CountKeys extends ApplicationWindow {
	private Text text_result;
	private Button btn_curKeys, btn_exKeys, btn_revokeKeys, btn_allKeys, btn_standbyKeys;
	private Combo combo_caInnerName;
	private ResourceBundle l = Env.getLanguage();
	private FileLogger log = FileLogger.getLogger(this.getClass());
	
	public Panel_CountKeys() {
		super(null);
		addToolBar(SWT.FLAT | SWT.WRAP);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 10, 220, 53);
		group.setText(l.getString("currentKeys") + ":");
		
		btn_curKeys = new Button(group, SWT.RADIO);
		btn_curKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_exKeys.setSelection(false);
				btn_revokeKeys.setSelection(false);
				btn_allKeys.setSelection(false);
				btn_standbyKeys.setSelection(false);
				combo_caInnerName.setText("");
			}
		});
		btn_curKeys.setBounds(33, 26, 155, 17);
		btn_curKeys.setText(l.getString("countCurrentKeys"));
	
		
		Group group_1 = new Group(container, SWT.NONE);
		group_1.setBounds(10, 69, 220, 109);
		group_1.setText(l.getString("hisKeys"));
		
		btn_exKeys = new Button(group_1, SWT.RADIO);
		btn_exKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_curKeys.setSelection(false);
				btn_standbyKeys.setSelection(false);
			}
		});
		btn_exKeys.setBounds(32, 23, 144, 17);
		btn_exKeys.setText(l.getString("countExpireKeys"));
		
		btn_revokeKeys = new Button(group_1, SWT.RADIO);
		btn_revokeKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_curKeys.setSelection(false);
				btn_standbyKeys.setSelection(false);
			}
		});
		btn_revokeKeys.setBounds(32, 52, 154, 17);
		btn_revokeKeys.setText(l.getString("countRevokeKeys"));
		
		btn_allKeys = new Button(group_1, SWT.RADIO);
		btn_allKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_curKeys.setSelection(false);
				btn_standbyKeys.setSelection(false);
			}
		});
		btn_allKeys.setBounds(32, 82, 144, 17);
		btn_allKeys.setText(l.getString("countAllKeys"));
		
		Group group_2 = new Group(container, SWT.NONE);
		group_2.setBounds(10, 184, 220, 80);
		group_2.setText(l.getString("standBy"));

		btn_standbyKeys = new Button(group_2, SWT.RADIO);
		btn_standbyKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_curKeys.setSelection(false);
				btn_exKeys.setSelection(false);
				btn_revokeKeys.setSelection(false);
				btn_allKeys.setSelection(false);
				combo_caInnerName.setText("");
			}
		});
		btn_standbyKeys.setBounds(32, 21, 149, 17);
		btn_standbyKeys.setText(l.getString("countStandbyKeys"));
		
		Label lblNewLabel = new Label(group_2, SWT.NONE);
		lblNewLabel.setBounds(47, 50, 69, 17);
		lblNewLabel.setText(l.getString("keyLength") + ":");
		
		final Combo combo_keyLength = new Combo(group_2, SWT.NONE | SWT.READ_ONLY);
		combo_keyLength.setBounds(122, 47, 78, 25);
		if (Env.alg == ALG.SM2) {
			combo_keyLength.add("256");
		} else if (Env.alg == ALG.RSA){
			combo_keyLength.add("1024");
			combo_keyLength.add("2048");
		}
		combo_keyLength.select(0);
		
		Label lbl_innerName = new Label(container, SWT.RIGHT);
		lbl_innerName.setBounds(253, 30, 84, 17);
		lbl_innerName.setText(l.getString("CAinnerName") + ":");
		
		combo_caInnerName = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		combo_caInnerName.setBounds(348, 27, 149, 25);
		//combo_caInnerName.add("");
		combo_caInnerName.select(0);
		
		Group group_3 = new Group(container, SWT.NONE);
		group_3.setBounds(253, 85, 244, 93);
		group_3.setText(l.getString("countResult"));
		
		text_result = new Text(group_3, SWT.BORDER | SWT.READ_ONLY);
		text_result.setBounds(41, 41, 193, 23);
		
		Button btn_count = new Button(container, SWT.NONE);
		btn_count.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_result.setText("");
				String range = null;
				String caName = combo_caInnerName.getText();
				String condition = "0";
				if (btn_curKeys.getSelection()) {
					range = "Current";
				} 
				if (btn_exKeys.getSelection()) {
					range = "his";
					condition = "1";
					if (caName == null || caName.length() == 0) {
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_null_CAname"));
						mb.open();
						return;
					}
				}
				if (btn_revokeKeys.getSelection()) {
					range = "his";
					condition = "2";
					if (caName == null || caName.length() == 0) {
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_null_CAname"));
						mb.open();
						return;
					}
				}
				if (btn_allKeys.getSelection()) {
					range = "his";
					condition = "3";
					if (caName == null || caName.length() == 0) {
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_null_CAname"));
						mb.open();
						return;
					}
				}
				if (btn_standbyKeys.getSelection()) {
					range = "pre";
					condition = combo_keyLength.getText();
				}
				Properties p = new Properties();
				p.setProperty("RANGE", range);
				p.setProperty("CANAME", caName);
				p.setProperty("STATTYPE", condition);
				try {
					Properties r = Env.client.sendRequest("COUNTKEYS", p).getData();
					int n = Integer.parseInt(r.getProperty("KEYNUMBERS", "0"));
					text_result.setText(String.valueOf(n));
				} catch (ServerException se) {
					log.errlog("Count key fail", se);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(
							l.getString("Notice_fail_countKeyNum") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
					mb.open();
				} catch (Exception ee) {
					log.errlog("Count key fail", ee);
					MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
					mb.setMessage(l.getString("Notice_fail_countKeyNum"));
					mb.open();
				}
			}
		});
		btn_count.setBounds(415, 231, 82, 33);
		btn_count.setText(l.getString("count"));
		loadCAinnerName();
		return container;
	}

	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Panel_CountKeys window = new Panel_CountKeys();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 获取 kmc 配置连接的CA
	 * @Author 江岩 
	 * @Time   2019-06-10 17:22
	 * @version 1.0
	 */
	private void loadCAinnerName() {
		try {
			Response resp = Env.client.sendRequest("VIEWCASTATE", new Properties());
			Properties[] ps = resp.getPs();
			for (Properties p : ps) {
				combo_caInnerName.add(p.getProperty("CAINNERNAME"));
			}
			combo_caInnerName.select(0);
		} catch (ServerException se) {
			log.errlog("Query CA status", se);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_getCAstate") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
			mb.open();
		} catch (Exception ee) {
			log.errlog("Query CA status", ee);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_getCAstate"));
			mb.open();
		}
	}
	
	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("keysCount"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(524, 316);
	}
}
