package com.zm.secretsign.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
public class MessageDialog extends Dialog {

    @BindView(R.id.tv_button)
    TextView tvButton;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.ll_center)
    LinearLayout llCenter;


    public MessageDialog(@NonNull Context context) {
        this(context, R.style.Dialog_Apply);
    }

    public MessageDialog(@NonNull Context context, String message) {
        this(context, R.style.Dialog_Apply);
        tvMessage.setText(message);
    }

    public MessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_button);
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

    @OnClick(R.id.tv_button)
    public void onButtonClick(View view) {
//        EventBus.getDefault().post(receiveEvent);
        dismiss();
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
