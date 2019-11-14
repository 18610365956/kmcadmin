package cn.com.infosec.netcert.kmcAdmin.test;

import java.security.Security;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CertContainer;
import cn.com.infosec.netcert.framework.crypto.impl.ukey.CspCertInfo;
import cn.com.infosec.netcert.kmcAdmin.utils.Env;
import cn.com.infosec.netcert.kmcAdmin.utils.Env.ALG;
import org.eclipse.swt.widgets.Label;

/**
 * KMC �ĵ�¼��ͼ
 * @Author ����
 * @Time 2019-06-10 16:43
 */
public class Test_composion extends ApplicationWindow {

	/**
	 * ���췽��
	 */
	public Test_composion() {
		super(null);

		setShellStyle(SWT.MIN);

		addMenuBar();
		addToolBar(SWT.FLAT | SWT.WRAP);
	}

	/**
	 * ��ͼ�滭
	 * @Author ����
	 * @Time 2019-06-10 16:43
	 * @version 1.0
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginTop = 30;
		gridLayout.marginLeft = 30;
		gridLayout.marginRight = 60;
		gridLayout.verticalSpacing = 15;
		container.setLayout(gridLayout);

		MessageBox messagebox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messagebox.setMessage("11111111111");
		int closeCode = messagebox.open();
		System.out.println(closeCode);
		// kmc_admin
		/*
		table.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent me) {
				if (me.button == 3) {
					Menu menu = new Menu(table);
					table.setMenu(menu);
					MenuItem item = new MenuItem(menu, SWT.PUSH);
					item.setText("op_judicial_Recover");
					item.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							System.out.println("1111");
						}
					});
				}
				System.out.println("2222");
			}
		});
		*/

		return container;
	}

	/**
	 * ��¼��ʱ���Ȳ�ѯkey���Ƿ��и�CAǩ����֤��
	 * ͨ�� ���õ�CA��֤��䷢��ƥ���ѯ
	 * @param cc�� cspList�� devName
	 * @return boolean
	 */
	private boolean hasMatchIssuerCert(CertContainer[] cc, CspCertInfo[] cspList, String devName) {
		String issuerDN = Env.issuerDN;
		if (ALG.SM2 == Env.alg) {
			for (int i = 0; i < cc.length; i++) {
				if (issuerDN.equalsIgnoreCase(cc[i].getCert().getIssuerDN().getName())) {
					return true;
				}
			}
		} else {
			for (int i = 0; i < cspList.length; i++) {
				if (devName.equals(cspList[i].provider)) {
					if (issuerDN.equalsIgnoreCase(cspList[i].issuer)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * ��ͼ�˵���
	 * @Author ����
	 * @Time 2019-06-10 16:44
	 * @version 1.0
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager mm = new MenuManager();
		MenuManager menu = new MenuManager("menu");

		menu.add(new ConfigAction());
		if (ALG.SM2 == Env.alg) {
			menu.add(new KeyLibConfig());
		}

		Separator separator = new Separator();
		separator.setVisible(true);
		menu.add(separator);

		menu.add(new ExitAction());

		mm.add(menu);

		MenuManager about = new MenuManager("about");

		about.add(new Action("version" + "��KMCAdmin 6.2") {
		});
		mm.add(about);
		return mm;
	}

	/**
	 * main ���� 
	 * @Author ����
	 * @Time 2019-06-10 16:45
	 * @version 1.0
	 */
	public static void main(String args[]) {
		Security.addProvider(new InfosecProvider());

		Test_composion window = new Test_composion();
		window.setBlockOnOpen(true);
		window.open();
		Display.getCurrent().dispose();
	}

	/**
	 * ��ͼ����������
	 * @param (Shell)
	 * @Author ����
	 * @Time 2019-06-10 16:45
	 * @version 1.0
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Infosec KMCAdmin");
		shell.setImage(new Image(shell.getDisplay(), "res/logo.png"));
	}

	/**
	 * �˵��� config
	 * @Author ����
	 * @Time 2019-06-10 16:45
	 */
	private class ConfigAction extends Action {
		public ConfigAction() {
			setText("config");
		}

		public void run() {

		}
	}

	/**
	 * keyDriverName����
	 * @Author ����
	 * @Time 2019-06-04 19:17
	 * @version 1.0
	 */
	private class KeyLibConfig extends Action {
		public KeyLibConfig() {
			setText("KeyDriverNameconfig");
		}

		public void run() {

		}
	}

	/**
	 * �����˳���ť
	 * @Author ����
	 * @Time 2019-06-10 16:46
	 */
	private class ExitAction extends Action {
		public ExitAction() {
			setText("exit");
		}

		public void run() {

		}
	}
}
