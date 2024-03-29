package cn.com.infosec.netcert.kmcAdmin.ui.login;

import java.text.SimpleDateFormat;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cn.com.infosec.netcert.framework.crypto.impl.ukey.CertContainer;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CspCertInfo;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.framework.utils.DNItemReverseUtil;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;

/**
 * 选择证书
 * @Author 江岩    
 * @Time 2019-06-10 16:52
 */
public class Panel_ChooseCert extends ApplicationWindow {
	private Table tbl_login_cert;

	private CertContainer[] cc;
	private CspCertInfo[] cspList;
	public CertContainer chooseCert;
	public CspCertInfo chooseCsp;
	public String subject;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * 构造方法： 用于 SM2 算法的证书
	 * @wbp.parser.constructor
	 */
	public Panel_ChooseCert(CertContainer[] cc) {
		super(null);
		setShellStyle(SWT.MIN | SWT.RESIZE);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.cc = cc;
	}

	/**
	 * 构造方法： 用于 RSA 算法的证书
	 */
	public Panel_ChooseCert(CspCertInfo[] cspList) {
		super(null);
		setShellStyle(SWT.MIN | SWT.RESIZE);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.cspList = cspList;
	}

	/**
	 * 视图页面  
	 * @Author 江岩      
	 * @Time 2019-06-10 16:53
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginBottom = 10;
		container.setLayout(gridLayout);

		tbl_login_cert = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridData = new GridData();
		gridData.widthHint = 504;
		gridData.heightHint = 119;
		tbl_login_cert.setLayoutData(gridData);
		tbl_login_cert.setHeaderVisible(true);
		tbl_login_cert.setLinesVisible(true);

		TableColumn tblclmn_sn = new TableColumn(tbl_login_cert, SWT.NONE);
		tblclmn_sn.setWidth(99);
		tblclmn_sn.setText(l.getString("SN"));

		TableColumn tblclmn_dn = new TableColumn(tbl_login_cert, SWT.NONE);
		tblclmn_dn.setWidth(153);
		tblclmn_dn.setText(l.getString("subject"));

		TableColumn tblclmn_issuer = new TableColumn(tbl_login_cert, SWT.NONE);
		tblclmn_issuer.setWidth(140);
		tblclmn_issuer.setText(l.getString("issuer"));

		TableColumn tblclmn_date = new TableColumn(tbl_login_cert, SWT.NONE);
		tblclmn_date.setWidth(100);
		tblclmn_date.setText(l.getString("validity"));

		TableColumn tblclmn_container = new TableColumn(tbl_login_cert, SWT.NONE);
		tblclmn_container.setWidth(-2);
		tblclmn_container.setText(l.getString("container"));
		if (ALG.SM2 == Env.alg) {
			addData();
		} else {
			addRSAData();
		}
		Button btn_chooseCert = new Button(container, SWT.NONE);
		GridData gd_btn_chooseCert = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btn_chooseCert.widthHint = 84;
		btn_chooseCert.setText(l.getString("OK"));
		btn_chooseCert.setLayoutData(gd_btn_chooseCert);
		btn_chooseCert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = tbl_login_cert.getSelection();
				if (items.length == 0) {
					MessageBox mb = new MessageBox(getShell());
					mb.setMessage(l.getString("Notice_not_chooseCert"));
					mb.open();
					return;
				}
				// 选择证书 之后，根据 容器名 确定 容器，将容器对象返回 给 login
				if (ALG.SM2 == Env.alg) {
					for (int i = 0; i < cc.length; i++) {
						if (items[0].getText(4).equalsIgnoreCase(cc[i].getContainerName())) {
							chooseCert = cc[i];
							String subjectDN = items[0].getText(1);
							int beginIndex = subjectDN.indexOf("cn=");
							if (beginIndex == -1) { beginIndex = subjectDN.indexOf("CN="); }
							int endIndex = subjectDN.indexOf(",");
							if (endIndex > beginIndex) {
								subject = subjectDN.substring(beginIndex+3, endIndex);
							} else {
								subject = subjectDN.substring(beginIndex+3);
							}
						}
					}
				} else {
					for (int i = 0; i < cspList.length; i++) {
						if (items[0].getText(4).equalsIgnoreCase(cspList[i].container)) {
							chooseCsp = cspList[i];
							String subjectDN = items[0].getText(1);
							int beginIndex = subjectDN.indexOf("cn=");
							if (beginIndex == -1) { beginIndex = subjectDN.indexOf("CN="); }
							int endIndex = subjectDN.indexOf(",");
							if (endIndex > beginIndex) {
								subject = subjectDN.substring(beginIndex+3, endIndex);
							} else {
								subject = subjectDN.substring(beginIndex+3);
							}
						}
					}
				}
				close();
			}
		});
		getShell().setDefaultButton(btn_chooseCert);
		return container;
	}

	/**
	 *  添加证书条目
	 * @Description  login 页面 读取 证书，封装传递到chooseCert,该方法将证书按格式显示
	 * @Author 江岩 
	 * @Time   2019-07-08 18:36
	 * @version 1.0
	 * @Add   通过根证书过滤 无效证书
	 */
	private void addData() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < cc.length; i++) {
			String[] d = new String[5];
			d[0] = cc[i].getCert().getSerialNumber().toString(16).toUpperCase();
			try {
				d[1] = DNItemReverseUtil.bigEnding(cc[i].getCert().getSubjectDN().getName());
				d[2] = DNItemReverseUtil.bigEnding(cc[i].getCert().getIssuerDN().getName());
			} catch (Exception ee) {
				log.errlog("DN bigEnding fail", ee);
				MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
				mb.setMessage(l.getString("Notice_fail_DNItemReverse"));
				mb.open();
				return;
			}
			d[3] = df.format(cc[i].getCert().getNotAfter());
			d[4] = cc[i].getContainerName();
			TableItem item = new TableItem(tbl_login_cert, SWT.NONE);
			item.setText(d);
		}
	}
	/**
	 * RSA 证书的格式显示 
	 * @Author 江岩 
	 * @Time   2019-07-08 18:36
	 * @version 1.1
	 * @Add   07/23
	 */
	private void addRSAData() {
		for (int i = 0; i < cspList.length; i++) {
			String[] d = new String[5];
			d[0] = cspList[i].sn;
			try {
				d[1] = DNItemReverseUtil.bigEnding(cspList[i].subject);
				d[2] = DNItemReverseUtil.bigEnding(cspList[i].issuer);
			} catch (Exception ee) {
				log.errlog("DN bigEnding fail", ee);
				MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
				mb.setMessage(l.getString("Notice_fail_DNItemReverse"));
				mb.open();
			}
			d[3] = cspList[i].notAfter;
			d[4] = cspList[i].container;
			TableItem item = new TableItem(tbl_login_cert, SWT.NONE);
			item.setText(d);
		}
	}

	/**
	 * 视图标题栏设置
	 * @param (Shell)
	 * @Author 江岩      
	 * @Time 2019-06-10 16:54
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("choose_cert"));
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

}
