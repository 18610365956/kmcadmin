package cn.com.infosec.netcert.kmcAdmin.ui.admin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
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
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import cn.com.infosec.netcert.kmcAdmin.utils.PageUtil;
import cn.com.infosec.netcert.kmcAdmin.utils.Utils;
import cn.com.infosec.netcert.resource.PropertiesKeysRes;
import cn.com.infosec.util.Base64;

/**
 * BO 业务操作员视图
 * @Author 江岩
 * @Time 2019-06-10 17:20
 */
public class BO extends ApplicationWindow {
	private Table table;
	private Combo combo_keystore, combo_ca, combo_currPage;
	private Text txt_qrySN;

	private PageUtil pageUtil;
	private String curr_Page, total_Page;
	private int pageSize = 18;
	private String subject;
	private FileLogger log = FileLogger.getLogger(this.getClass());
	private static ResourceBundle l = Env.getLanguage();

	/**
	 * 构造方法
	 */
	public BO(String subject) {
		super(null);
		this.subject = subject;
		addMenuBar();
		pageUtil = new PageUtil();
	}

	/**
	 * 视图页面
	 * @Author 江岩
	 * @Time 2019-06-10 17:21
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container = new GridLayout(6, false);

		gl_container.marginTop = 10;
		gl_container.marginBottom = 10;
		gl_container.marginLeft = 10;
		gl_container.marginRight = 20;
		gl_container.horizontalSpacing = 10;
		gl_container.verticalSpacing = 15;
		container.setLayout(gl_container);

		Label lbl_keyStore = new Label(container, SWT.NONE);
		GridData gd_lbl_keyStore = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_keyStore.widthHint = 82;
		lbl_keyStore.setLayoutData(gd_lbl_keyStore);
		lbl_keyStore.setAlignment(SWT.RIGHT);
		lbl_keyStore.setText(l.getString("keyStore") + "：");

		combo_keystore = new Combo(container, SWT.READ_ONLY);
		GridData gd_combo_keystore = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo_keystore.widthHint = 118;
		combo_keystore.setLayoutData(gd_combo_keystore);
		combo_keystore.add(l.getString("currKeyStore"));
		combo_keystore.add(l.getString("historyKeyStore"));
		combo_keystore.select(0);

		Label lbl_certSerial = new Label(container, SWT.NONE);
		GridData gd_lbl_certSerial = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_certSerial.widthHint = 118;
		lbl_certSerial.setLayoutData(gd_lbl_certSerial);
		lbl_certSerial.setAlignment(SWT.RIGHT);
		lbl_certSerial.setText(l.getString("SN") + "：");

		txt_qrySN = new Text(container, SWT.BORDER);
		GridData gd_txt_qrySN = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_qrySN.widthHint = 150;
		txt_qrySN.setTextLimit(20);
		txt_qrySN.setLayoutData(gd_txt_qrySN);

		Label lbl_Notice = new Label(container, SWT.NONE);
		GridData gd_lbl_Notice = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_Notice.widthHint = 106;
		lbl_Notice.setLayoutData(gd_lbl_Notice);
		lbl_Notice.setText("(" + l.getString("Hexadecimal") + ")");
		new Label(container, SWT.NONE);

		Label lbl_issuer = new Label(container, SWT.NONE);
		lbl_issuer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_issuer.setAlignment(SWT.RIGHT);
		lbl_issuer.setText(l.getString("CAinnerName") + "：");

		combo_ca = new Combo(container, SWT.READ_ONLY);
		GridData gd_combo_ca = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo_ca.widthHint = 120;
		combo_ca.setLayoutData(gd_combo_ca);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Button btn_qrySN = new Button(container, SWT.NONE);
		GridData gd_btn_qrySN = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_qrySN.widthHint = 86;
		btn_qrySN.setLayoutData(gd_btn_qrySN);
		btn_qrySN.setText(l.getString("queryKey"));

		btn_qrySN.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					search("1");
					pageUtil.btn_Change(curr_Page, total_Page);
				}
			}
		});
		new Label(container, SWT.NONE);

		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_table = new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1);
		gd_table.widthHint = 701;
		gd_table.heightHint = 388;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final Menu menu_root = new Menu(table);
		MenuItem item_judicial_Recover = new MenuItem(menu_root, SWT.PUSH);
		item_judicial_Recover.setText(l.getString("op_judicial_Recover"));
		table.setMenu(menu_root);

		item_judicial_Recover.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!Env.validSession()) {
					handleShellCloseEvent();
				} else {
					Env.setLastOperationTime();
					String targetCA = combo_ca.getText();
					String targetCertSN = table.getItem(table.getSelectionIndex()).getText(2);
					Panel_CatchPoll cp = new Panel_CatchPoll();
					cp.setBlockOnOpen(true);
					int w = cp.open();
					if (w == 0) {
						try {
							String protectCer = Base64.encode(cp.encCer.getEncoded());
							byte[] data = (targetCA + targetCertSN + protectCer).getBytes();
							byte[] cpSign = cp.sign(data);

							Properties p = new Properties();
							p.setProperty("CANAME", targetCA);
							p.setProperty("KMC_REQ_CERT_SN", targetCertSN);
							p.setProperty("RECOVER_CATCHPOLL_SIGN", Base64.encode(cpSign));
							p.setProperty("RECOVER_CATCHPOLL_CERT", Base64.encode(cp.signCer.getEncoded()));
							p.setProperty("RECOVER_PROTECT_CERT", Base64.encode(cp.encCer.getEncoded()));
							
							if (ALG.SM2 == Env.alg) {
								p.setProperty("RECOVER_CATCHPOLL_SIGN_ALG", "SM3withSM2");
								p.setProperty("RETSYMALG", "SM4");
							} 
							if (ALG.RSA == Env.alg) {
								p.setProperty("RECOVER_CATCHPOLL_SIGN_ALG", "SHA1withRSA");
								p.setProperty("RETSYMALG", "RC4");
							}

							Response resp = Env.client.sendRequest("RECOVERBYCATCHPOLL", p);
							// 写key 
							Properties pro = resp.getData();
							if (Env.alg == ALG.SM2) {
								String kmc_resp_prv_key = pro.getProperty(PropertiesKeysRes.KMC_RESP_PRV_KEY);
								Panel_WriteKey wk = new Panel_WriteKey(kmc_resp_prv_key);
								wk.setBlockOnOpen(true);
								wk.open();
							} else {
								String kmc_resp_prv_key = pro.getProperty(PropertiesKeysRes.KMC_RESP_PRV_KEY);
								String kmc_resp_sym_key = pro.getProperty(PropertiesKeysRes.KMC_RESP_SYM_KEY);
								String kmc_resp_pub_key = pro.getProperty(PropertiesKeysRes.KMC_RESP_PUB_KEY);

								StringBuffer sb = new StringBuffer();
								sb.append("KMC_RESP_PRV_KEY:" + Utils.separator);
								sb.append(kmc_resp_prv_key + Utils.separator + Utils.separator);
								sb.append("KMC_RESP_SYM_KEY:" + Utils.separator);
								sb.append(kmc_resp_sym_key + Utils.separator + Utils.separator);
								sb.append("KMC_RESP_PUB_KEY:" + Utils.separator);
								sb.append(kmc_resp_pub_key + Utils.separator + Utils.separator);

								FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
								fd.setFilterExtensions(new String[] { "*.txt" });
								fd.setFileName("CatchPollKey");
								String f = fd.open();
								if (f != null) {
									try {
										byte[] bs = sb.toString().getBytes();
										FileOutputStream fos = new FileOutputStream(f);
										fos.write(bs);
										fos.close();
									} catch (IOException e1) {
										log.errlog("Base64.decode fail/Save file file", e1);
										MessageBox mb = new MessageBox(getShell());
										mb.setMessage(l.getString("Notice_fail_saveFile"));
										mb.open();
										return;
									}
								}
							}
							MessageBox mb = new MessageBox(getShell(), SWT.NONE);
							mb.setMessage(l.getString("Notice_succ_recoveryKey"));
							mb.open();
						} catch (ServerException se) {
							log.errlog("RECOVERBYCATCHPOLL fail", se);
							MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
							mb.setMessage(l.getString("Notice_fail_recoveryKey") + "[" + se.getErrorNum() + "]："
									+ se.getErrorMsg());
							mb.open();
						} catch (Exception ee) {
							log.errlog("RECOVERBYCATCHPOLL fail", ee);
							MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
							mb.setMessage(l.getString("Notice_fail_recoveryKey"));
							mb.open();
						}
					}
				}
			}
		});

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(133);
		tblclmnNewColumn.setText(l.getString("key_SN"));

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(118);
		tblclmnNewColumn_1.setText(l.getString("keyLength"));

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(187);
		tblclmnNewColumn_3.setText(l.getString("userCert_SN"));

		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(204);
		tblclmnNewColumn_4.setText(l.getString("cert_expiryTime"));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		// 分页模块
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout gd_composite = new GridLayout(8, false);
		gd_composite.horizontalSpacing = 8;
		composite.setLayout(gd_composite);

		GridData grid_composite = new GridData(SWT.LEFT);
		grid_composite.horizontalAlignment = SWT.RIGHT;
		grid_composite.horizontalSpan = 3;
		grid_composite.heightHint = 48;
		grid_composite.widthHint = 237;
		composite.setLayoutData(grid_composite);
		Composite comp = (Composite) pageUtil.page(composite);

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
		loadCAinnerName();
		return container;
	}

	/**
	 *  获取 kmc 配置连接的CA
	 * @Author 江岩 
	 * @Time   2019-06-10 17:22
	 * @version 1.0
	 */
	private void loadCAinnerName() {
		try {
			//Thread.sleep(40 * 1000);
			//System.out.println("go");
			Response resp = Env.client.sendRequest("VIEWCASTATE", new Properties());
			Properties[] ps = resp.getPs();
			for (Properties p : ps) {
				combo_ca.add(p.getProperty("CAINNERNAME"));
			}
			combo_ca.select(0);
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
	 *   分页查询 密钥
	 * @Author 江岩 
	 * @Time   2019-06-10 17:29
	 * @version 1.0
	 */
	private void search(String pageNum) {
		String reqType = "VIEWCURKEYS";
		if (combo_keystore.getSelectionIndex() != 0) {
			reqType = "VIEWOLDKEYS";
		}
		Properties p = new Properties();
		if (combo_keystore.getSelectionIndex() == 0) {
			p.setProperty("RANGE", "Current");
		} else {
			p.setProperty("RANGE", "his");
		}
		if (combo_ca.getSelectionIndex() >= 0) {
			p.setProperty("CANAME", combo_ca.getText());
		}
		if (txt_qrySN.getText().trim().length() > 0) {
			p.setProperty("SERIALNUMBER", txt_qrySN.getText().trim());
		}
		p.setProperty("BLOCKNUMBER", pageNum);
		p.setProperty("BLOCKSIZE", String.valueOf(pageSize));

		try {
			Response resp = Env.client.sendRequest(reqType, p);
			total_Page = resp.getData().getProperty("BLOCKCOUNT","0");
			curr_Page = pageNum;

			Properties[] rows = resp.getPs();
			table.removeAll();
			if (rows.length <= 0) {
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				mb.setMessage(l.getString("Notice_null_queryResult"));
				mb.open();
				return;
			}
			for (Properties row : rows) {
				String keyid = row.getProperty("KEYID");
				String keySize = row.getProperty("keyLength");
				String sn = row.getProperty("SERIALNUMBER");
				String certEnd = row.getProperty("ENDTIME");

				TableItem ti = new TableItem(table, SWT.NONE);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ti.setText(new String[] { keyid, keySize, sn, df.format(new Date(Long.parseLong(certEnd))) });
			}
		} catch (ServerException se) {
			log.errlog(reqType + " fail", se);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_queryKey") + "[" + se.getErrorNum() + "]：" + se.getErrorMsg());
			mb.open();
			return;
		} catch (Exception ee) {
			log.errlog(reqType + " fail", ee);
			MessageBox mb = new MessageBox(getShell(), SWT.ERROR);
			mb.setMessage(l.getString("Notice_fail_queryKey"));
			mb.open();
			return;
		}
	}

	/**
	 * 菜单栏  
	 * @Author 江岩      
	 * @Time 2019-06-10 17:29
	 * @version 1.0
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager mm = new MenuManager();

		MenuManager menu = new MenuManager(l.getString("menu"));
		menu.add(new GenKeyAction());
		menu.add(new CountKeysAction());
		menu.add(new Separator());
		menu.add(new ExitAction());

		mm.add(menu);
		return mm;
	}

	/**
	 * 预产生密钥事件
	 * @Author 江岩    
	 * @Time 2019-06-10 17:30
	 */
	class GenKeyAction extends Action {
		public GenKeyAction() {
			setText(l.getString("pre_generate_key"));
		}

		public void run() {
			Panel_PreGenKey pg = new Panel_PreGenKey();
			pg.setBlockOnOpen(true);
			pg.open();
		}
	}
	/**
	 * 密钥统计事件
	 * @Author 江岩    
	 * @Time 2019-08-29 17:30
	 */
	class CountKeysAction extends Action {
		public CountKeysAction() {
			setText(l.getString("keysCount"));
		}

		public void run() {
			Panel_CountKeys ck = new Panel_CountKeys();
			ck.setBlockOnOpen(true);
			ck.open();
		}
	}

	/**
	 * 退出事件 
	 * @Author 江岩    
	 * @Time 2019-06-10 17:31
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
	 * 视图标题栏命名
	 * @param (Shell)   
	 * @Author 江岩      
	 * @Time 2019-06-10 17:29
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(l.getString("BO") + " [" + subject + "]");
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
}
