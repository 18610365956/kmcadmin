package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import cn.com.infosec.netcert.base.Response;
import cn.com.infosec.netcert.framework.ServerException;
import cn.com.infosec.netcert.framework.log.FileLogger;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.PageUtil;

/**
 * AO 审计管理员
 * @Author 江岩    
 * @Time 2019-06-11 09:58
 */
public class AO extends ApplicationWindow {
	private Table table;
	private Menu menu;
	private MenuItem auditLog, auditLogDetail;
	private DateTime qry_stDate, qry_stTime, qry_endDate, qry_endTime, date_archive, time_archive;
	private Combo combo_opType, combo_currPage;
	private Text qry_user;

	private PageUtil pageUtil;
	private List<String> opTypes = new ArrayList<String>(), opDesc = new ArrayList<String>();
	private int pageSize = 15;
	private String curr_Page, total_Page, subject;

	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();
	/**
	 * 构造方法
	 */
	public AO(String subject) {
		super(null);
		addMenuBar();
		this.subject = subject;
		pageUtil = new PageUtil();

		opTypes.add("");
		opDesc.add(l.getString("All"));
		opTypes.add("LOGIN");
		opDesc.add(l.getString("op_Login"));
		opTypes.add("ADDCA");
		opDesc.add(l.getString("op_addCA"));
		opTypes.add("UPDATECA");
		opDesc.add(l.getString("op_updateCAcert"));
		opTypes.add("SETCASTATE");
		opDesc.add(l.getString("op_modifyCAstate"));
		opTypes.add("VIEWCASTATE");
		opDesc.add(l.getString("op_viewCAstate"));
		opTypes.add("RELOADCACHANNEL");
		opDesc.add(l.getString("op_reloadCAchannel"));
		
		opTypes.add("APPLYKEY");
		opDesc.add(l.getString("op_applyKey"));
		opTypes.add("GENPREKEY");
		opDesc.add(l.getString("op_preGenKeys"));
		opTypes.add("REVOKEKEY");
		opDesc.add(l.getString("op_revokeKey"));
		opTypes.add("RECOVERBYCA");
		opDesc.add(l.getString("op_recoverKey"));
		opTypes.add("RECOVERBYCATCHPOLL");
		opDesc.add(l.getString("op_recoverCAjudicial"));
		opTypes.add("COUNTKEYS");
		opDesc.add(l.getString("op_countKeys"));
		opTypes.add("VIEWCURKEYS");
		opDesc.add(l.getString("op_viewCurrentKeys"));
		opTypes.add("VIEWOLDKEYS");
		opDesc.add(l.getString("op_viewOLDKeys"));
		
		opTypes.add("APPLYID");
		opDesc.add(l.getString("op_newBA"));
		opTypes.add("MODIFYID");
		opDesc.add(l.getString("op_updateBAcert"));
		opTypes.add("DELETEID");
		opDesc.add(l.getString("op_disEnableBA"));
		opTypes.add("ENABLEID");
		opDesc.add(l.getString("op_enableBA"));
		opTypes.add("VIEWIDS");
		opDesc.add(l.getString("op_viewBA"));
		
		opTypes.add("APPLYOPERATORID");
		opDesc.add(l.getString("op_newBO"));
		opTypes.add("MODIFYOPERATORID");
		opDesc.add(l.getString("op_updateBO"));
		opTypes.add("DELETEOPERATORID");
		opDesc.add(l.getString("op_disEnableBO"));
		opTypes.add("ENABLEOPERATORID");
		opDesc.add(l.getString("op_enableBO"));
		opTypes.add("VIEWOPERATORIDS");
		opDesc.add(l.getString("op_viewBO"));
		
		opTypes.add("APPLYAUDITID");
		opDesc.add(l.getString("op_newAO"));
		opTypes.add("MODIFYAUDITID");
		opDesc.add(l.getString("op_updateAOcert"));
		opTypes.add("DELETEAUDITID");
		opDesc.add(l.getString("op_disEnableAO"));
		opTypes.add("ENABLEAUDITID");
		opDesc.add(l.getString("op_enableAO"));
		opTypes.add("VIEWAUDITID");
		opDesc.add(l.getString("op_viewAO"));
		
		opTypes.add("VIEWLOGS");
		opDesc.add(l.getString("op_viewLog"));
		opTypes.add("AUDITLOG");
		opDesc.add(l.getString("op_auditLog"));
		opTypes.add("VIEWLOGDETAIL");
		opDesc.add(l.getString("op_viewLogDetail"));
		opTypes.add("VIEWAUDITIDS");
		opDesc.add(l.getString("op_viewAuditedLog"));
		opTypes.add("BACKUPLOGS");
		opDesc.add(l.getString("op_backupLogs"));
	
		
	}

	/**
	 * 视图页面
	 */
	@Override
	protected Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());

		table = new Table(container, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.left = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -7);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// 右键菜单
		menu = new Menu(table);
		auditLog = new MenuItem(menu, SWT.PUSH);
		auditLog.setText(l.getString("auditLog"));
		table.setMenu(menu);

		auditLog.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					TableItem[] items = table.getSelection();
					StringBuffer sb = new StringBuffer();
					for (TableItem item : items) {
						if (item.getText(7) != null && item.getText(7).length() != 0) {
							MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
							mb.setMessage(l.getString("Notice_audited"));
							mb.open();
							return;
						} else {
							sb.append(item.getText(0) + ":" + "" + item.getText(8) + ",");
						}
					}
					sb.substring(0, sb.length() - 1);

					Properties p = new Properties();
					p.setProperty("LOGID", sb.substring(0, sb.length() - 1));

					try {
						Env.client.sendRequest("AUDITLOG", p);
						MessageBox mb = new MessageBox(getShell(), SWT.OK);
						mb.setMessage(l.getString("Notice_succ_audited"));
						mb.open();
					} catch (ServerException se) {
						log.errlog("Audit log fail", se);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(
								l.getString("Notice_fail_audit") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
						mb.open();
					} catch (Exception ee) {
						log.errlog("Audit log fail", ee);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_audit"));
						mb.open();
					}
				}
			}
		});

		auditLogDetail = new MenuItem(menu, SWT.PUSH);
		auditLogDetail.setText(l.getString("logDetail"));
		auditLogDetail.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					Panel_LogDetail panel_logDetail = new Panel_LogDetail(table.getSelection()[0].getText(0));
					panel_logDetail.setBlockOnOpen(true);
					panel_logDetail.open();
				}
			}
		});

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(76);
		tblclmnNewColumn.setText(l.getString("log_id"));

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(103);
		tblclmnNewColumn_1.setText(l.getString("operator"));

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(111);
		tblclmnNewColumn_2.setText(l.getString("operation_type"));

		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(101);
		tblclmnNewColumn_4.setText(l.getString("operation_result"));

		TableColumn tblclmnNewColumn_5 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_5.setWidth(155);
		tblclmnNewColumn_5.setText(l.getString("operation_time"));

		TableColumn tblclmnNewColumn_6 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_6.setWidth(115);
		tblclmnNewColumn_6.setText(l.getString("request_ip"));

		TableColumn tblclmnNewColumn_7 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_7.setWidth(112);
		tblclmnNewColumn_7.setText(l.getString("audit_time"));

		TableColumn tblclmnNewColumn_8 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_8.setWidth(85);
		tblclmnNewColumn_8.setText(l.getString("auditor_ID"));

		TableColumn tblclmnNewColumn_9 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_9.setWidth(0);
		tblclmnNewColumn_9.setText(l.getString("log_hash"));

		// 分页模块
		Composite composite = new Composite(container, SWT.NONE);
		fd_table.bottom = new FormAttachment(composite, -13);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 522);
		fd_composite.bottom = new FormAttachment(100, -10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 700);
		composite.setLayoutData(fd_composite);
		GridLayout gd_composite = new GridLayout(7, false);
		gd_composite.horizontalSpacing = 8;
		composite.setLayout(gd_composite);
		Composite comp = (Composite) pageUtil.page(composite);

		Composite composite_1 = new Composite(container, SWT.NONE);
		fd_table.top = new FormAttachment(composite_1, 6);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.right = new FormAttachment(100, -17);
		fd_composite_1.left = new FormAttachment(0);
		fd_composite_1.top = new FormAttachment(0);
		fd_composite_1.bottom = new FormAttachment(0, 154);
		composite_1.setLayoutData(fd_composite_1);

		Group group_2 = new Group(composite_1, SWT.NONE);
		group_2.setText(l.getString("auditLog"));
		group_2.setBounds(10, 0, 556, 147);

		Label lbl_operator = new Label(group_2, SWT.NONE);
		lbl_operator.setBounds(10, 30, 63, 17);
		lbl_operator.setAlignment(SWT.RIGHT);
		lbl_operator.setText(l.getString("operator") + ":");

		Label lbl_operationType = new Label(group_2, SWT.NONE);
		lbl_operationType.setBounds(10, 73, 63, 17);
		lbl_operationType.setAlignment(SWT.RIGHT);
		lbl_operationType.setText(l.getString("operation_type") + ":");

		qry_user = new Text(group_2, SWT.BORDER);
		qry_user.setBounds(92, 27, 112, 23);
		qry_user.setTextLimit(20);

		combo_opType = new Combo(group_2, SWT.READ_ONLY);
		combo_opType.setBounds(92, 70, 112, 25);
		for (String s : opDesc) {
			combo_opType.add(s);
		}
		combo_opType.select(0);

		Label lbl_operationTime = new Label(group_2, SWT.NONE);
		lbl_operationTime.setBounds(226, 30, 76, 17);
		lbl_operationTime.setAlignment(SWT.RIGHT);
		lbl_operationTime.setText(l.getString("operation_time") + ":");
	
		Calendar cl_stDate = Calendar.getInstance();
		cl_stDate.add(Calendar.DATE, -7);
		
		qry_stDate = new DateTime(group_2, SWT.DROP_DOWN);
		qry_stDate.setBounds(319, 29, 112, 24);
		qry_stDate.setDate(cl_stDate.get(Calendar.YEAR), cl_stDate.get(Calendar.MONTH), cl_stDate.get(Calendar.DATE));
	
		qry_stTime = new DateTime(group_2, SWT.BORDER | SWT.TIME);
		qry_stTime.setBounds(452, 30, 86, 24);

		Label label = new Label(group_2, SWT.NONE);
		label.setBounds(323, 56, 9, 14);
		label.setText("~");

		qry_endDate = new DateTime(group_2, SWT.BORDER | SWT.DROP_DOWN);
		qry_endDate.setBounds(319, 72, 112, 24);

		qry_endTime = new DateTime(group_2, SWT.BORDER | SWT.TIME);
		qry_endTime.setBounds(452, 73, 83, 24);

		Button btn_qry = new Button(group_2, SWT.NONE);
		btn_qry.setBounds(462, 110, 76, 27);
		btn_qry.setText(l.getString("log_query"));

		Group group = new Group(composite_1, SWT.NONE);
		group.setText(l.getString("archiveLog"));
		group.setBounds(584, 0, 338, 147);

		Label lbl_archiveTime = new Label(group, SWT.RIGHT);
		lbl_archiveTime.setBounds(10, 43, 86, 17);
		lbl_archiveTime.setText(l.getString("archiveTime") + ":");

		date_archive = new DateTime(group, SWT.BORDER | SWT.DROP_DOWN);
		date_archive.setBounds(102, 40, 98, 24);

		time_archive = new DateTime(group, SWT.BORDER | SWT.TIME);
		time_archive.setBounds(223, 40, 88, 24);

		btn_qry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					if (qry_user.getText() == null || combo_opType.getSelectionIndex() == -1
							|| opTypes.get(combo_opType.getSelectionIndex()) == null) {
						MessageBox mb = new MessageBox(getShell());
						mb.setMessage(l.getString("Notice_null_queryConditions"));
						mb.open();
						return;
					}
					search("1");
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});

		Button btn_archive = new Button(group, SWT.NONE);
		btn_archive.setBounds(231, 110, 80, 27);
		btn_archive.setText(l.getString("archiveLog"));
		btn_archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					Properties p = new Properties();
	
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, date_archive.getYear());
					c.set(Calendar.MONTH, date_archive.getMonth());
					c.set(Calendar.DATE, date_archive.getDay());
					c.set(Calendar.HOUR, time_archive.getHours());
					c.set(Calendar.MINUTE, time_archive.getMinutes());
					c.set(Calendar.SECOND, 0);
	
					p.setProperty("DEADLINE", String.valueOf(c.getTimeInMillis()));
	
					try {
						Env.client.sendRequest("BACKUPLOGS", p);
						MessageBox mb = new MessageBox(getShell(), SWT.OK);
						mb.setMessage(l.getString("Notice_succ_setArchiveLogTask"));
						mb.open();
					} catch (Exception e1) {
						log.errlog("Archive log fail", e1);
						MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
						mb.setMessage(l.getString("Notice_fail_archiveLog"));
						mb.open();
						return;
					}
				}
			}
		});
		Control[] controls = comp.getChildren();

		// 修改当前页
		combo_currPage = (Combo) controls[5];
		GridData gd_combo_currPage = new GridData();
		gd_combo_currPage.widthHint = 30;
		gd_combo_currPage.heightHint = 25;
		combo_currPage.setLayoutData(gd_combo_currPage);
		combo_currPage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					search(combo_currPage.getText());
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});
		// lblBegin 第一页
		controls[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					search("1");
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});
		// lblBack 上一页
		controls[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					search(String.valueOf(Integer.parseInt(curr_Page) - 1));
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});
		// lblForward 下一页
		controls[2].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					search(String.valueOf(Integer.parseInt(curr_Page) + 1));
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});
		// lblEnd 最后一页
		controls[3].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					search(total_Page);
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});
		return container;
	}

	/**
	 * 分页查询日志
	 * @param    (String)
	 * @Author 江岩 
	 * @Time   2019-06-11 09:59
	 * @version 1.0
	 */
	private void search(String pageNum) {

		Properties p = new Properties();
		p.setProperty("OPERATOR", qry_user.getText().trim());
		p.setProperty("TYPE", opTypes.get(combo_opType.getSelectionIndex()));

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, qry_stDate.getYear());
		c.set(Calendar.MONTH, qry_stDate.getMonth());
		c.set(Calendar.DATE, qry_stDate.getDay());
		c.set(Calendar.HOUR, qry_stTime.getHours());
		c.set(Calendar.MINUTE, qry_stTime.getMinutes());
		c.set(Calendar.SECOND, qry_stTime.getSeconds());
		p.setProperty("SEARCH_STARTTIME", String.valueOf(c.getTimeInMillis()));

		c.set(Calendar.YEAR, qry_endDate.getYear());
		c.set(Calendar.MONTH, qry_endDate.getMonth());
		c.set(Calendar.DATE, qry_endDate.getDay());
		c.set(Calendar.HOUR, qry_endTime.getHours());
		c.set(Calendar.MINUTE, qry_endTime.getMinutes());
		c.set(Calendar.SECOND, qry_endTime.getSeconds());
		p.setProperty("SEARCH_ENDTIME", String.valueOf(c.getTimeInMillis()));

		p.setProperty("BLOCKNUMBER", pageNum);
		p.setProperty("BLOCKSIZE", String.valueOf(pageSize));

		try {
			Response resp = Env.client.sendRequest("VIEWLOGS", p);
			total_Page = resp.getData().getProperty("BLOCKCOUNT");
			curr_Page = pageNum;

			Properties[] rows = resp.getPs();
			table.removeAll();
			for (Properties row : rows) {
				int n = opTypes.indexOf(row.getProperty("TYPE"));
				if (n >= 0) {
					String logid = row.getProperty("LOGID");
					String optype = opDesc.get(n);
					String succ = row.getProperty("RESULT");
					String oper = row.getProperty("ID");
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String opTime = df.format(new Date(Long.parseLong(row.getProperty("LOGTIME"))));
					String opIp = row.getProperty("LOGIP");
					String log_hash = row.getProperty("RESPONSE_LOG_HASH");

					String logAuditTime = "";
					String logAuditor = "";

					if (row.getProperty("LOGAUDITTIME") != null && row.getProperty("LOGAUDITTIME").length() != 0) {
						logAuditTime = row.getProperty("LOGAUDITTIME");
						logAuditor = row.getProperty("LOGAUDITOR");
					}

					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(new String[] { logid, oper, optype,
							("0".equals(succ) ? l.getString("success") : l.getString("failure")), opTime, opIp,
							logAuditTime, logAuditor, log_hash });
				}
			}
		} catch (ServerException se) {
			log.errlog("Query log fail", se);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_queryLog") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
			mb.open();
		} catch (Exception ee) {
			log.errlog("Query log fail", ee);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_queryLog"));
			mb.open();
		}
	}

	/**
	 * 视图菜单栏
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager mm = new MenuManager();
		MenuManager menu = new MenuManager(l.getString("menu"));
		menu.add(new ExitAction());
		mm.add(menu);
		return mm;
	}

	/**
	 * 菜单栏退出事件
	 * @Author 江岩    
	 * @Time 2019-06-11 10:01
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
	 * 视图栏标题栏命名
	 * @param   (Shell)   
	 * @Author 江岩      
	 * @Time 2019-06-11 10:00
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("AO") + " [" + subject + "]");
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

	/**
	 * 重写窗口关闭事件
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
			super.handleShellCloseEvent();
		}
	}
}
