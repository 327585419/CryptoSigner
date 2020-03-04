package com.zm.secretsign.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhou.library.utils.ScreenUtil;
import com.zm.secretsign.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 *
 */
public class StatementDialog extends Dialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    private View.OnClickListener listener;
    int type;//0协议 安全须知

    public StatementDialog(@NonNull Context context, int type, View.OnClickListener onClickListener) {
        this(context, R.style.Dialog_Apply);
        listener = onClickListener;
        this.type = type;
        switch (type) {
            case 0:
                tvTitle.setText(R.string.statement_name);
                tvContent.setText(R.string.statement);
                break;
            case 1:
                tvTitle.setText(R.string.safety_instructions);
                tvContent.setText(R.string.safety_instructions_statement);
                tvCancel.setVisibility(View.INVISIBLE);
                tvConfirm.setText(R.string.confirm);
                break;
        }
    }

    public StatementDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_statement);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ButterKnife.bind(this);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            int screenWidth = ScreenUtil.getScreenWidth();
//            lp.width = (int) (screenWidth * 0.77);
            lp.width = screenWidth;
            lp.gravity = Gravity.CENTER;
            lp.dimAmount = 0.6f;
            window.setAttributes(lp);
//            window.setWindowAnimations(R.style.Dialog_Apply_Animation);
            WindowManager.LayoutParams wl = window.getAttributes();
            window.setAttributes(wl);
        }
    }

    @OnClick({
            R.id.tv_cancel,
            R.id.tv_confirm
    })
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
            case R.id.tv_confirm:
                //同意
                //不同意，退出app
                dismiss();
                if (listener != null) {
                    listener.onClick(view);
                }
                break;
        }
    }


//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onFetchEvent(PrinterFetchEvent event) {
//
//    }
}
