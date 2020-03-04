package com.zm.secretsign.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.ScreenUtil;
import com.zm.secretsign.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class EditDialog extends Dialog {

    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.et)
    EditText et;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    public EditDialog(@NonNull Context context) {
        this(context, R.style.Dialog_Apply);
    }

    public EditDialog(@NonNull Context context, String message, String oldValue, View.OnClickListener listener) {
        this(context, R.style.Dialog_Apply);
        tvMessage.setText(message);
        setEditText(oldValue);
        tvConfirm.setOnClickListener(listener);
    }

    private EditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_edit);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ButterKnife.bind(this);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            int screenWidth = ScreenUtil.getScreenWidth();
            lp.width = (int) (screenWidth * 0.77);
            lp.gravity = Gravity.CENTER;
            lp.dimAmount = 0.6f;
            window.setAttributes(lp);
//            window.setWindowAnimations(R.style.Dialog_Apply_Animation);
            WindowManager.LayoutParams wl = window.getAttributes();
            window.setAttributes(wl);
        }
    }

    public void setInputType(int type) {
        et.setInputType(type);
    }

    @OnClick({R.id.tv_cancel,})
    public void onButtonClick(View view) {
//        EventBus.getDefault().post(receiveEvent);
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    public void setEditText(String oldValue) {
        et.setText(oldValue);
        et.requestFocus();
        et.selectAll();
        et.setSelection(et.length());
        AppUtil.showKeyboard(getContext(), et);
    }

    public String getEditText() {
        return et.getText().toString();
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
