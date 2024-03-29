package cn.com.infosec.netcert.kmcAdmin.ui.admin;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.infosec.netcert.kmcAdmin.utils.Env;
/**
 * Pin 码输入框
 * @Description  国密 的  SKF 格式下，需要输入 pin码
 * @Author 江岩    
 * @Time 2019-08-21 15:48
 * @Add PIN码为密文
 */
public class Panel_VerifyPin extends ApplicationWindow {
	public String pin;
	private Text t_pin;
	private static ResourceBundle l = Env.getLanguage();
	
	public Panel_VerifyPin() {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
		addMenuBar();
	}

	/**
	 * 视图页面绘画
	 * @Author 江岩      
	 * @Time 2019-06-04 21:00
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Shell shell = parent.getShell();
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout gl_container = new GridLayout(6, false);
		gl_container.marginTop = 10;
		gl_container.marginLeft = 10;
		gl_container.verticalSpacing = 8;
		container.setLayout(gl_container);
		
		Label lbl_icon = new Label(container, SWT.NONE);
		lbl_icon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		lbl_icon.setImage(new Image(shell.getDisplay(), "res/key_yellow_48.png"));
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblNewLabel_2.setText(l.getString("Notice_input_PIN") + ":");
		new Label(container, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_1.widthHint = 21;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel_1.setText("");
		
		t_pin = new Text(container, SWT.BORDER | SWT.PASSWORD);
		GridData gd_t_pin = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_t_pin.widthHint = 235;
		t_pin.setTextLimit(20);
		t_pin.setLayoutData(gd_t_pin);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 83;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("");
		
		Button btn_submit = new Button(container, SWT.NONE);
		GridData gd_btn_submit = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btn_submit.widthHint = 74;
		btn_submit.setLayoutData(gd_btn_submit);
		btn_submit.setText(l.getString("OK"));
		btn_submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pin = t_pin.getText().trim();
				close();
			}
		});
		Button btn_cancle = new Button(container, SWT.NONE);
		btn_cancle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleShellCloseEvent();
			}
		});
		GridData gd_btn_cancle = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btn_cancle.widthHint = 69;
		btn_cancle.setLayoutData(gd_btn_cancle);
		btn_cancle.setText(l.getString("cancle"));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		getShell().setDefaultButton(btn_submit);
		return container;
	}

	/**
	 * 视图标题栏命名
	 * @param   (Shell)  
	 * @Author 江岩      
	 * @Time 2019-06-04 20:58
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("verifyPIN"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}
}
