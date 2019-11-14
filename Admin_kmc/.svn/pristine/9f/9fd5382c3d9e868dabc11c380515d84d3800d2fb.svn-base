package cn.com.infosec.netcert.kmcAdmin.utils;

import java.util.ResourceBundle;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 *  分页组件 可以内嵌到 其他页面
 * @Author 江岩    
 * @Time 2019-06-05 10:08
 */
public class PageUtil extends ApplicationWindow {

	private Label lblBegin, lblBack, lblForward, lblEnd;
	private Combo combo_currPage;
	private static ResourceBundle l = Env.getLanguage();
	
	public PageUtil() {
		super(null);
	}
	/**
	 *  分页组件的翻页按钮 和 跳转下拉框
	 * @Description (添加补充描述)
	 * @return   (Control)      
	 * @Author 江岩 
	 * @Time   2019-06-05 10:08
	 * @version 1.0
	 */
	public Control page(Composite composite) {
		Shell shell = composite.getShell();
			
		// 第一页
		lblBegin = new Label(composite, SWT.NONE);
		lblBegin.setBounds(10, 10, 18, 21);
		lblBegin.setImage(new Image(shell.getDisplay(), "res/begin.png"));
		lblBegin.setEnabled(false);
		
		// 上一页
		lblBack = new Label(composite, SWT.NONE);
		lblBack.setBounds(34, 9, 18, 21);
		lblBack.setImage(new Image(shell.getDisplay(), "res/back.png"));
		lblBack.setEnabled(false);
		
		// 下一页
		lblForward = new Label(composite, SWT.NONE);
		lblForward.setBounds(58, 10, 18, 21);
		lblForward.setImage(new Image(shell.getDisplay(), "res/forward.png"));
		lblForward.setEnabled(false);
			
		// 最后一页
		lblEnd = new Label(composite, SWT.NONE);
		lblEnd.setBounds(82, 9, 18, 21);
		lblEnd.setImage(new Image(shell.getDisplay(), "res/end.png"));
		lblEnd.setEnabled(false);
		
		if ("en".equalsIgnoreCase(l.getString("language"))) {
			Label lblPage_1 = new Label(composite, SWT.NONE);
			lblPage_1.setBounds(106, 10, 12, 21);
			lblPage_1.setText("No.");
				
			combo_currPage = new Combo(composite, SWT.READ_ONLY);
			combo_currPage.setBounds(124, 7, 35, 25);
			combo_currPage.setText("");
		} else {
			Label lblPage_1 = new Label(composite, SWT.NONE);
			lblPage_1.setBounds(106, 10, 12, 21);
			lblPage_1.setText(l.getString("No"));

			combo_currPage = new Combo(composite, SWT.READ_ONLY);
			combo_currPage.setBounds(124, 7, 35, 25);
			combo_currPage.setText("");

			Label lblPage_2 = new Label(composite, SWT.NONE);
			lblPage_2.setBounds(165, 10, 12, 17);
			lblPage_2.setText(l.getString("page"));
		}
		return composite;
	}

	/**
	 * @Desc TODO  设置 页码，并修改按钮状态
	 * @Authod 江岩
	 * @Date 2019年2月25日 下午4:04:40
	 */
	public void btn_Change(String curr_Page,String total_Page){

		combo_currPage.removeAll();
		if("0".equalsIgnoreCase(curr_Page) && "0".equalsIgnoreCase(total_Page)){
			lblBack.setEnabled(false);
			lblBegin.setEnabled(false);
			lblForward.setEnabled(false);
			lblEnd.setEnabled(false);
			return ;
		}	
		for(int i = 1; i <= Integer.valueOf(total_Page); i++){
			combo_currPage.add(String.valueOf(i));
		}
		if("0".equalsIgnoreCase(curr_Page)){ // 是否可省略
			combo_currPage.setText("");
		}else{
			combo_currPage.setText(curr_Page);
		}
		
		if(Integer.valueOf(curr_Page) == 1){	
			lblBack.setEnabled(false);
			lblBegin.setEnabled(false);
		}		
		if(Integer.valueOf(curr_Page) > 1){
			lblBegin.setEnabled(true);
			lblBack.setEnabled(true);
		}
		if(Integer.valueOf(curr_Page) < Integer.valueOf((total_Page))){
			lblForward.setEnabled(true);
			lblEnd.setEnabled(true);
		}
		if(Integer.valueOf(curr_Page) == Integer.valueOf((total_Page))){
			lblForward.setEnabled(false);
			lblEnd.setEnabled(false);
		}
	}
}
