package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;

/**   
 * 日志详情
 * @Author 江岩    
 * @Time 2019-07-11 17:24
 */
public class Panel_LogDetail extends ApplicationWindow {
	private Text t_requestData, t_respData;
	private String logID;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();
	/**
	 * Create the application window.
	 */
	public Panel_LogDetail(String logID) {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.MIN);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.logID = logID;
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 10, 607, 188);
		group.setText(l.getString("ReqData"));

		t_requestData = new Text(group, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		t_requestData.setBounds(10, 22, 587, 144);

		Group group_1 = new Group(container, SWT.NONE);
		group_1.setBounds(10, 222, 607, 193);
		group_1.setText(l.getString("RespData"));

		t_respData = new Text(group_1, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		t_respData.setBounds(10, 30, 587, 142);

		// 查询日志 详细信息
		try {
			Properties properties = new Properties();
			properties.setProperty("LOGID", logID);
			Response resp = Env.client.sendRequest("VIEWLOGDETAIL", properties);

			Properties p = resp.getData();
			String reqData = p.getProperty("LOGREQUEST");
			String respData = p.getProperty("LOGRESPONSE");

			t_requestData.setText(reqData);
			t_respData.setText(respData);
		} catch (ServerException se) {
			log.errlog("Query log detail fail", se);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_queryLogDetail") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
			mb.open();
		} catch (Exception ee) {
			log.errlog("Query log detail fail", ee);
			MessageBox mb = new MessageBox(getShell());
			mb.setMessage(l.getString("Notice_fail_queryLogDetail"));
			mb.open();
		}
		return container;
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("logDetail"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(639, 510);
	}
}
