package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

//import cn.com.infosec.netcert.framework.Request;
//import cn.com.infosec.netcert.framework.Response;
import cn.com.infosec.netcert.base.Request;
import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import cn.com.infosec.util.Base64;

/**
 * BA 业务管理员视图
 * @Author 江岩    
 * @Time 2019-06-10 17:16
 */
public class BA extends ApplicationWindow {
	
	private Table table, table_ca;
	private Text txt_cer, txt_updCer;
	private Label lb_uname, lb_innerName;
	private Button btnChkBO, chkCAState;

	private List<Properties> rowsBO = new ArrayList<Properties>(), rowsCA = new ArrayList<Properties>();
	private String cerB64, subject;

	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * 构造方法
	 */
	public BA(String subject) {
		super(null);
		this.subject = subject;
		addMenuBar();
	}

	/**
	 * 视图页面  
	 * @Author 江岩      
	 * @Time 2019-06-10 17:17
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.marginBottom = 5;
		gl_container.marginLeft = 5;
		gl_container.marginRight = 5;
		gl_container.verticalSpacing = 5;
		container.setLayout(gl_container);

		final TabFolder tabFolder = new TabFolder(container, SWT.NONE);

		TabItem tabBO = new TabItem(tabFolder, SWT.NONE);
		tabBO.setText(l.getString("BO"));

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tabBO.setControl(composite);
		composite.setLayout(new FormLayout());

		Group group = new Group(composite, SWT.NONE);
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(0, 515);
		fd_group.left = new FormAttachment(0, 10);
		group.setLayoutData(fd_group);
		group.setText(l.getString("adminList"));
		group.setLayout(new GridLayout(1, false));

		table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_table = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_table.widthHint = 463;
		gd_table.heightHint = 222;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = table.getSelectionIndex();
				lb_uname.setText(rowsBO.get(i).getProperty("ID"));
				String st = rowsBO.get(i).getProperty("STATUS");
				btnChkBO.setSelection("0".equals(st));
			}
		});

		TableColumn tblcolume_username = new TableColumn(table, SWT.NONE);
		tblcolume_username.setWidth(146);
		tblcolume_username.setText(l.getString("username"));

		TableColumn tblcolume_role = new TableColumn(table, SWT.NONE);
		tblcolume_role.setWidth(132);
		tblcolume_role.setText(l.getString("role"));

		TableColumn tblcolume_state = new TableColumn(table, SWT.NONE);
		tblcolume_state.setWidth(154);
		tblcolume_state.setText(l.getString("state"));

		Group group_1 = new Group(composite, SWT.NONE);
		fd_group.bottom = new FormAttachment(group_1, -6);
		group_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		FormData fd_group_1 = new FormData();
		fd_group_1.right = new FormAttachment(100, -10);
		fd_group_1.left = new FormAttachment(0, 10);
		fd_group_1.bottom = new FormAttachment(0, 491);
		fd_group_1.top = new FormAttachment(0, 294);
		group_1.setLayoutData(fd_group_1);
		group_1.setText(l.getString("adminInfo"));

		GridLayout gl_group_1 = new GridLayout(3, false);
		gl_group_1.verticalSpacing = 15;
		gl_group_1.horizontalSpacing = 10;
		gl_group_1.marginTop = 10;
		group_1.setLayout(gl_group_1);

		Label lbl_username = new Label(group_1, SWT.NONE);
		GridData gd_lbl_username = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_username.widthHint = 99;
		lbl_username.setLayoutData(gd_lbl_username);
		lbl_username.setAlignment(SWT.RIGHT);
		lbl_username.setText(l.getString("username") + ":");

		lb_uname = new Label(group_1, SWT.NONE | SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		gridData.widthHint = 187;
		lb_uname.setLayoutData(gridData);
		lb_uname.setText("");
		new Label(group_1, SWT.NONE);

		Label lbl_cert = new Label(group_1, SWT.NONE);
		lbl_cert.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_cert.setAlignment(SWT.RIGHT);
		lbl_cert.setText(l.getString("cert") + ":");

		txt_cer = new Text(group_1, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txt_cer = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_cer.widthHint = 191;
		txt_cer.setLayoutData(gd_txt_cer);

		Button btn_browse = new Button(group_1, SWT.NONE);
		GridData gd_btn_browse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_browse.widthHint = 56;
		btn_browse.setLayoutData(gd_btn_browse);
		btn_browse.setText(l.getString("browse"));
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
					} catch (Exception e1) {
						log.errlog("Choose certificate Exception", e1);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_chooseCert"));
						mb.open();
					}
					txt_cer.setText(f);
				}
			}
		});

		Label lbl_state = new Label(group_1, SWT.NONE);
		lbl_state.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_state.setAlignment(SWT.RIGHT);
		lbl_state.setText(l.getString("state") + ":");

		btnChkBO = new Button(group_1, SWT.CHECK);
		btnChkBO.setText(l.getString("valid"));
		new Label(group_1, SWT.NONE);
		new Label(group_1, SWT.NONE);

		Button btn_Change = new Button(group_1, SWT.NONE);
		btn_Change.setText(l.getString("modify"));
		GridData gd_btnChange = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnChange.widthHint = 79;
		btn_Change.setLayoutData(gd_btnChange);
		btn_Change.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					if (lb_uname.getText() == null || lb_uname.getText().length() == 0) {
						MessageBox mb = new MessageBox(getShell());
						mb.setMessage(l.getString("Notice_null_username"));
						mb.open();
						return;
					}
					Properties p = new Properties();
					p.setProperty("ID", lb_uname.getText().trim());
					String reqType = "";
					try {
						if (txt_cer.getText() != null && txt_cer.getText().length() > 0 && cerB64 != null) {
							p.setProperty("POLICY", rowsBO.get(table.getSelectionIndex()).getProperty("POLICY"));
							p.setProperty("CERT", cerB64);
							Env.client.sendRequest("MODIFYOPERATORID", p);
						}
						if (btnChkBO.getSelection()) {
							reqType = "ENABLEOPERATORID";
						} else {
							reqType = "DELETEOPERATORID";
						}
						Env.client.sendRequest(reqType, p);
						MessageBox mb = new MessageBox(getShell());
						mb.setMessage(l.getString("Notice_succ_modifyAdminInfo"));
						mb.open();
						refresh_bo();
					} catch (ServerException se) {
						log.errlog(reqType + " fail", se);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_modifyAdminInfo") + "：" + "[" + se.getErrorNum() + "]："
								+ se.getErrorMsg());
						mb.open();
					} catch (Exception ee) {
						log.errlog(reqType + " fail", ee);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_modifyAdminInfo"));
						mb.open();
					}
				}
			}
		});

		new Label(group_1, SWT.NONE);

		TabItem tabCA = new TabItem(tabFolder, SWT.NONE);
		tabCA.setText(l.getString("CA"));

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tabCA.setControl(composite_1);
		composite_1.setLayout(new FormLayout());

		Group group_2 = new Group(composite_1, SWT.NONE);
		FormData fd_group_2 = new FormData();
		fd_group_2.bottom = new FormAttachment(0, 275);
		fd_group_2.right = new FormAttachment(0, 514);
		fd_group_2.top = new FormAttachment(0, 10);
		fd_group_2.left = new FormAttachment(0, 10);
		group_2.setLayoutData(fd_group_2);
		group_2.setText(l.getString("trust_CA_list"));
		group_2.setLayout(new GridLayout(1, false));

		table_ca = new Table(group_2, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_table_ca = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_table_ca.widthHint = 467;
		gd_table_ca.heightHint = 215;
		table_ca.setLayoutData(gd_table_ca);
		table_ca.setHeaderVisible(true);
		table_ca.setLinesVisible(true);
		table_ca.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = table_ca.getSelectionIndex();
				lb_innerName.setText(rowsCA.get(i).getProperty("CAINNERNAME"));
				String st = rowsCA.get(i).getProperty("RUNSTATUS");
				chkCAState.setSelection("1".equals(st));
			}
		});

		TableColumn tblclmnNewColumn_3 = new TableColumn(table_ca, SWT.NONE);
		tblclmnNewColumn_3.setWidth(152);
		tblclmnNewColumn_3.setText(l.getString("innerName"));

		TableColumn tblclmnNewColumn_4 = new TableColumn(table_ca, SWT.NONE);
		tblclmnNewColumn_4.setWidth(160);
		tblclmnNewColumn_4.setText(l.getString("CA_name"));

		TableColumn tblclmnNewColumn_5 = new TableColumn(table_ca, SWT.NONE);
		tblclmnNewColumn_5.setWidth(122);
		tblclmnNewColumn_5.setText(l.getString("state"));

		Group group_3 = new Group(composite_1, SWT.NONE);
		FormData fd_group_3 = new FormData();
		fd_group_3.top = new FormAttachment(group_2, 6);
		fd_group_3.left = new FormAttachment(group_2, 0, SWT.LEFT);
		fd_group_3.bottom = new FormAttachment(0, 483);
		fd_group_3.right = new FormAttachment(0, 505);
		group_3.setLayoutData(fd_group_3);
		group_3.setText(l.getString("CAInfoModify"));

		GridLayout gl_group_3 = new GridLayout(3, false);
		gl_group_3.marginTop = 10;
		gl_group_3.marginBottom = 10;
		gl_group_3.marginLeft = 10;
		gl_group_3.marginRight = 10;
		gl_group_3.verticalSpacing = 15;
		gl_group_3.horizontalSpacing = 10;
		group_3.setLayout(gl_group_3);

		Label lbl_innerName = new Label(group_3, SWT.NONE);
		GridData gd_lbl_innerName = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_innerName.widthHint = 82;
		lbl_innerName.setLayoutData(gd_lbl_innerName);
		lbl_innerName.setAlignment(SWT.RIGHT);
		lbl_innerName.setText(l.getString("uniqueIdentify") + "：");

		lb_innerName = new Label(group_3, SWT.NONE);
		GridData gd_lb_innerName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		//gd_lb_innerName.heightHint = 21;
		gd_lb_innerName.widthHint = 149;
		lb_innerName.setLayoutData(gd_lb_innerName);
		lb_innerName.setText("111");
		new Label(group_3, SWT.NONE);

		Label lbl_updCer = new Label(group_3, SWT.NONE);
		lbl_updCer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_updCer.setAlignment(SWT.RIGHT);
		lbl_updCer.setText(l.getString("CA_cert") + "：");

		txt_updCer = new Text(group_3, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txt_updCer = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_updCer.widthHint = 199;
		txt_updCer.setLayoutData(gd_txt_updCer);

		Button btn_select = new Button(group_3, SWT.NONE);
		GridData gd_btn_select = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_select.widthHint = 76;
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
						txt_updCer.setText(f);
					} catch (Exception ex) {
						txt_updCer.setText("");
						log.errlog("Choose Cert fail", ex);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_error_CACertFormat"));
						mb.open();
					}
				}
			}
		});

		Label lbl_status = new Label(group_3, SWT.NONE);
		lbl_status.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_status.setAlignment(SWT.RIGHT);
		lbl_status.setText(l.getString("state") + "：");

		chkCAState = new Button(group_3, SWT.CHECK);
		chkCAState.setText(l.getString("valid"));
		new Label(group_3, SWT.NONE);
		new Label(group_3, SWT.NONE);

		Button btn_upd = new Button(group_3, SWT.NONE);
		btn_upd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btn_upd.setText(l.getString("modifyReview"));
		new Label(group_3, SWT.NONE);

		btn_upd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					if (lb_innerName.getText() == null || lb_innerName.getText().length() == 0) {
						MessageBox mb = new MessageBox(getShell());
						mb.setMessage(l.getString("Notice_null_innerName"));
						mb.open();
						return;
					}
					try {
						Panel_Checking pc = new Panel_Checking();
						pc.setBlockOnOpen(true);
						if (0 == pc.open()) {
							Properties p = new Properties();
							p.setProperty("CAINNERNAME", lb_innerName.getText().trim());
							p.setProperty("RUNSTATUS", (chkCAState.getSelection() ? "1" : "0"));
							
							Request req = Env.client.makeRequest("SETCASTATE", p);
							if (Env.alg == ALG.SM2) {
								pc.genSM2Req(req);
							} else {
								pc.genRSAReq(req);
							}
							Env.client.send(req);
							if (txt_updCer.getText() != null && txt_updCer.getText().length() > 0 && cerB64 != null) {
								p.setProperty("CERT", cerB64);
								
								Request req_1 = Env.client.makeRequest("UPDATECA", p);
								if (Env.alg == ALG.SM2) {
									pc.genSM2Req(req_1);
								} else {
									pc.genRSAReq(req_1);
								}
								Env.client.send(req_1);
							}
							MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							mb.setMessage(l.getString("Notice_succ_modifyCAInfo") +","+ l.getString("Notice_isloadCA") + "?");
							int w = mb.open();
							if (w == 64) {
								Properties p_reload = new Properties();
								Env.client.sendRequest("RELOADCACHANNEL", p_reload);
								MessageBox mb_reload = new MessageBox(getShell(), SWT.OK);
								mb_reload.setMessage(l.getString("Notice_succ_loadCAInfo"));
								mb_reload.open();
							}
							/*
							MessageBox mb = new MessageBox(getShell());
							mb.setMessage(l.getString("Notice_succ_modifyCAInfo"));
							mb.open();
							*/
							refresh_ca();
						}
					} catch (ServerException se) {
						log.errlog("Update CA fail", se);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_modifyCAInfo") + "[" + se.getErrorNum() + "]："
								+ se.getErrorMsg());
						mb.open();
					} catch (Exception ee) {
						log.errlog("Update CA fail", ee);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_modifyCAInfo"));
						mb.open();
					}
				}
			}
		});

		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (0 == tabFolder.getSelectionIndex()) {
					refresh_bo();
				} else {
					refresh_ca();
				}
			}
		});
		tabFolder.setSelection(0);
		refresh_bo();
		return container;
	}

	/**
	 * 刷新 BO内容  
	 * @Author 江岩 
	 * @Time   2019-06-10 17:17
	 * @version 1.0
	 */
	void refresh_bo() {
		this.rowsBO.clear();
		table.removeAll();
		lb_uname.setText("");
		txt_cer.setText("");
		btnChkBO.setSelection(false);
		try {
			String reqType = "VIEWOPERATORIDS";
			Response resp = Env.client.sendRequest(reqType, new Properties());
			Properties[] ps = resp.getPs();
			for (Properties p : ps) {
				this.rowsBO.add(p);
			}
		} catch (ServerException se) {
			log.errlog("Query operator_ID fail", se);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_viewOperator") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
			mb.open();
		} catch (Exception ee) {
			log.errlog("Query operator_ID fail", ee);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_viewOperator"));
			mb.open();
		}

		for (Properties p : this.rowsBO) {
			String name = p.getProperty("ID");
			String r = l.getString("BO");
			String policy = p.getProperty("POLICY");
			if (policy.indexOf("(CATCHPOLL)") > 0) {
				r = l.getString("restorer");
			}
			String _st = p.getProperty("STATUS");
			String st = ("0".equals(_st) ? l.getString("valid") : l.getString("disable"));

			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText(new String[] { name, r, st });
		}
	}

	/**
	 * 刷新CA信息  
	 * @Author 江岩 
	 * @Time   2019-06-10 17:18
	 * @version 1.0
	 */
	void refresh_ca() {
		this.rowsCA.clear();
		table_ca.removeAll();
		lb_innerName.setText("");
		txt_updCer.setText("");
		btnChkBO.setSelection(false);
		try {
			String reqType = "VIEWCASTATE";
			Response resp = Env.client.sendRequest(reqType, new Properties());
			Properties[] ps = resp.getPs();
			for (Properties p : ps) {
				this.rowsCA.add(p);
			}
		} catch (ServerException se) {
			log.errlog("Query CA status fail", se);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_viewCAstate") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
			mb.open();
		} catch (Exception ee) {
			log.errlog("Query CA status fail", ee);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_viewCAstate"));
			mb.open();
		}
		for (Properties p : this.rowsCA) {
			String innerName = p.getProperty("CAINNERNAME");
			String name = p.getProperty("CANAME");
			String _st = p.getProperty("RUNSTATUS");
			String st = ("1".equals(_st) ? l.getString("valid") : l.getString("disable"));

			TableItem ti = new TableItem(table_ca, SWT.NONE);
			ti.setText(new String[] { innerName, name, st });
		}
	}

	/**
	 * 视图菜单栏  
	 * @Author 江岩      
	 * @Time 2019-06-10 17:19
	 * @version 1.0
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager mm = new MenuManager();
		MenuManager menu = new MenuManager(l.getString("menu"));
		menu.add(new AddBOAction());
		menu.add(new AddCAAction());
		menu.add(new Separator());
		menu.add(new ExitAction());
		mm.add(menu);
		return mm;
	}

	/**
	 * new BO 事件
	 * @Author 江岩    
	 * @Time 2019-06-10 17:19
	 */
	class AddBOAction extends Action {
		public AddBOAction() {
			setText(l.getString("newAdmin"));
		}

		public void run() {
			Panel_NewBO pb = new Panel_NewBO();
			pb.setBlockOnOpen(true);
			int w = pb.open();
			if (w == 0) {
				refresh_bo();
			}
		}
	}

	/**
	 * 添加 CA 事件
	 * @Author 江岩    
	 * @Time 2019-06-10 17:20
	 */
	class AddCAAction extends Action {
		public AddCAAction() {
			setText(l.getString("addCA"));
		}

		public void run() {
			Panel_NewCA addCA = new Panel_NewCA();
			addCA.setBlockOnOpen(true);
			int w = addCA.open();
			if (w == 0) {
				refresh_ca();
			}
		}
	}

	/**
	 * 程序退出事件
	 * @Author 江岩    
	 * @Time 2019-06-10 17:20
	 */
	class ExitAction extends Action {
		public ExitAction() {
			setText(l.getString("exit"));
		}

		public void run() {
			handleShellCloseEvent();
		}
	}

	/**
	 * 视图标题栏设置
	 * @param (Shell)
	 * @Author 江岩      
	 * @Time 2019-06-10 17:19
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("BA") + " [" + subject + "]");
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

		
	/**
	 * 重写窗口关闭事件,主动关闭按钮触发
	 * @Description 在关闭窗口之前，释放占用的key资源
	 * @Author 江岩
	 * @Time 2019-06-04 19:20
	 * @version 1.0
	 */
	@Override
	public void handleShellCloseEvent() {
		int closeCode = -1;
		if (!Env.validSession()) { // session超时
			MessageBox messagebox = new MessageBox(getShell());
			messagebox.setMessage(l.getString("Notice_invalidSession"));
			messagebox.open();
			closeCode = SWT.YES;
		} else {
			MessageBox messagebox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messagebox.setMessage(l.getString("Notice_exit") + "?");
			closeCode = messagebox.open();
		}
		if (closeCode == SWT.YES) {	
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION);
			mb.setMessage(l.getString("Notice_removeCert"));
			mb.open();
			super.handleShellCloseEvent();
		}
	}
	/*public static void main(String[] args) {
		BA ba = new BA("CN = BA_KMC");
		ba.setBlockOnOpen(true);
		ba.open();
	}*/
}
