package com.zm.secretsign.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhou.library.utils.EventBusUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.ScreenUtil;
import com.zhou.library.utils.ToastUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.utils.DBUtil;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 *
 */
public class DeleteDialog extends Dialog {


    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ll_center)
    RelativeLayout llCenter;
    @BindView(R.id.tv_second)
    TextView tvSecond;

    private AddressKey item;

    /**
     * 长按时两次点差的最大偏移量，超过此偏移量则不是长按
     */
    private int _longTouchOffset = 100;
    private int _lastMotionX;
    private int _lastMotionY;

    private CountDownTimer timer = new CountDownTimer(2 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            tvSecond.setText("还剩" + millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
//            tvSecond.setText("还剩" + 0 + "秒");
            //长按两秒
            onButtonLongClick();
        }
    };


    public DeleteDialog(@NonNull Context context, AddressKey key) {
        this(context, R.style.Dialog_Apply);
        this.item = key;
    }


    @SuppressLint("ClickableViewAccessibility")
    private DeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_delete);
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

        tvConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int pointerCount = event.getPointerCount();
                if (pointerCount > 1) {
                    //多点触控，直接取消长按
                    cancelLongTouch();
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 弹起时,移除已有Runnable回调,弹起就算长按结束了(不需要考虑用户是否长按了超过预设的时间)
                        cancelLongTouch();
                        LogUtil.e("LongTouch UP");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(_lastMotionX - x) > _longTouchOffset
                                || Math.abs(_lastMotionY - y) > _longTouchOffset) {
                            // 移动误差阈值
                            // xy方向判断
                            // 移动超过阈值，则表示移动了,就不是长按(看需求),移除 已有的Runnable回调
                            cancelLongTouch();
                        }
                        LogUtil.e("LongTouch Move");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        // 每次按下重新计时
                        // 按下前,先移除 已有的Runnable回调,防止用户多次单击导致多次回调长按事件的bug
                        cancelLongTouch();
                        _lastMotionX = x;
                        _lastMotionY = y;
                        // 按下时,开始计时
//                        _longTouchHandle.postDelayed(_longTouchRunnable, _longTouchDownTime);
                        timer.start();
                        LogUtil.e("LongTouch Down");
                        break;
                }

                return true;
            }
        });
    }

    @OnClick(R.id.tv_cancel)
    public void onButtonClick(View view) {
        dismiss();
//        EventBus.getDefault().post(receiveEvent);

    }

    private void onButtonLongClick() {
        ToastUtil.showLong(R.string.delete_success);
        LogUtil.e("长按两秒了");
        DBUtil.deleteKey(item);
        EventBusUtil.post(Constant.DELETE_ADDRESS);
        dismiss();
    }

    private void cancelLongTouch() {
        LogUtil.e("取消了长按");
        timer.cancel();
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
