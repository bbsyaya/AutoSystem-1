package com.vampire.shareforwechat.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.vampire.shareforwechat.R;


/**
 * 底部弹出对话框，需要setFB_AddCustomView
 */
public class FormBotomDialogBuilder extends Dialog implements
		DialogInterface {

	private Context tmpContext;
	/**
	 * dialog布局
	 */
	private View mDialogView;

	private LinearLayout mCustumView;
	/**
	 * 是否在弹出键盘的时候，向上移动，以免被键盘覆盖
	 */
	private  boolean isShowKeyboardTotop = false;

	public FormBotomDialogBuilder(Context context) {

		this(context, R.style.dialog_untran);

	}
	public FormBotomDialogBuilder(Context context, boolean isShowKeyboardToTop) {
		super(context, R.style.dialog_untran);
		this.tmpContext = context;
		this.isShowKeyboardTotop=isShowKeyboardToTop;
		init(context);
	}

	public FormBotomDialogBuilder(Context context, int theme) {
		super(context, R.style.dialog_untran);
		this.tmpContext = context;
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isShowKeyboardTotop) {
			getWindow()
					.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
									| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}

		Window window = this.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		// 设置显示位置
		this.onWindowAttributesChanged(wl);

	}
	
	
	private void init(Context context) {

		mDialogView = View.inflate(context, R.layout.form_bottom_by_all_dialog,
				null);
		mCustumView = (LinearLayout) mDialogView
				.findViewById(R.id.ll_customview);

		setContentView(mDialogView);

	}

	/**
	 * 在内容区域添加一个view
	 * 
	 * @param
	 * @return
	 */
	public FormBotomDialogBuilder setFB_AddCustomView(View view) {
		mCustumView.addView(view);

		return this;
	}

}
