package com.zm.secretsign.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.GridSpacingItemDecoration;
import com.zhou.library.bean.Event;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.bean.DealSignItem;
import com.zm.secretsign.ui.base.BaseTitleActivity;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.JSCallBack;
import com.zm.secretsign.utils.WebJsUtil;
import com.zm.secretsign.view.EditDialog;
import com.zm.secretsign.view.MultipleItemQuickAdapter;
import com.zm.secretsign.view.SignCodeAdapter;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;


public class TransactionSignActivity extends BaseTitleActivity implements View.OnClickListener {
    @BindView(R.id.recycler)
    RecyclerView recycler;
//    @BindView(R.id.web_view)
//    WebView webView;

    private View rootView;
    private Unbinder unbinder;
    private int type;//1简单交易 2高级交易

    private TextView tvAddress, tvSign, tvFeeUnit;
    private EditText etFee, etMsg;

    private MultipleItemQuickAdapter mAdapter;
    private SignCodeAdapter codeAdapter;
    private List<DealSignItem> inputItems, outputItems;
    private EditDialog editDialog;
    private DealSignItem dealSignItemMsg;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.fragmnet_deal_sign);


        setDownTextRightIcon();
        if (savedInstanceState == null) {
            type = getIntent().getIntExtra("p0", 1);
        } else {
            type = savedInstanceState.getInt("p0", 1);
        }

        //        initWebView(webView);
        inputItems = new ArrayList<>();
        outputItems = new ArrayList<>();
        EventBus.getDefault().register(this);

        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MultipleItemQuickAdapter(mContext, type);
        recycler.setAdapter(mAdapter);


//        View headerView = getLayoutInflater().inflate(R.layout.header_sign, (ViewGroup) recycler.getParent(), false);
//        headerView.findViewById(R.id.tv_button).setOnClickListener(this);
//        mAdapter.addHeaderView(headerView);

        View footerView = getLayoutInflater().inflate(type == 1 ? R.layout.footer_simple_deal : R.layout.footer_high_deal, (ViewGroup) recycler.getParent(), false);
        footerView.findViewById(R.id.tv_button).setOnClickListener(this);
        if (type == 2) {
            setUpTextLeftBackIcon(R.string.high_deal);
            tvAddress = footerView.findViewById(R.id.tv_address);
            tvAddress.setOnClickListener(this);
            etFee = footerView.findViewById(R.id.et_fee);
            tvFeeUnit = footerView.findViewById(R.id.tv_fee_unit);
//            String coinType = SPUtil.getString(Constant.COIN_TYPE, "fch");
//            etFee.setText(coinType.equals("btc") ? "0.001" : "0.00001");
            setFee();
            etMsg = footerView.findViewById(R.id.et_msg);
//            ImageView ivMsgScan = footerView.findViewById(R.id.iv_msg_scan);
//            ivMsgScan.setOnClickListener(this);

            //TODO 测试
//            tvAddress.setText("165TC5vtWp2yH2P6PC6dRGTZdVoYSPRFsB");
//            etFee.setText("0.00001");
//            etMsg.setText("周周刻字");

            recycler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    AppUtil.hideKeyboard(mContext, etMsg);
                    return false;
                }
            });
        } else {
            setUpTextLeftBackIcon(R.string.simple_deal);
        }

        tvSign = footerView.findViewById(R.id.tv_sign);
        footerView.findViewById(R.id.tv_save).setOnClickListener(this);
        RecyclerView recyclerCode = footerView.findViewById(R.id.recycler);
        recyclerCode.setHasFixedSize(true);
        recyclerCode.setLayoutManager(new GridLayoutManager(mContext, 3));
        codeAdapter = new SignCodeAdapter();
        recyclerCode.setAdapter(codeAdapter);
        codeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                ((BaseActivity) mContext).advance(ImageBigListActivity.class, JSON.toJSONString(codeAdapter.getData()), position);
                goPicture(position);
            }
        });
        GridSpacingItemDecoration divider = new GridSpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_43),
                0, Color.WHITE, false);
        recyclerCode.addItemDecoration(divider);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                DealSignItem item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }

                switch (view.getId()) {
                    case R.id.iv_delete:
                        switch (item.getItemType()) {
                            case DealSignItem.INPUT:
                                for (DealSignItem inputItem : inputItems) {
                                    if (item.txId.equals(inputItem.txId)) {
                                        inputItems.remove(inputItem);
                                        break;
                                    }
                                }
                                break;
                            case DealSignItem.OUTPUT:
                                for (DealSignItem outputItem : outputItems) {
                                    if (item.address.equals(outputItem.address)) {
                                        outputItems.remove(outputItem);
                                        break;
                                    }
                                }
                                break;
                        }
                        mAdapter.remove(position);
                        mAdapter.notifyDataSetChanged();
//                        setFee();
                        break;
                    case R.id.ll_title:
                        item.expand = !item.expand;
                        mAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tv1:
                        String tag = String.valueOf(item.amount);
                        if (editDialog == null) {
                            editDialog = new EditDialog(mContext, getString(R.string.input_amount), tag, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    editDialog.dismiss();
                                    String text = editDialog.getEditText();
                                    if (TextUtils.isEmpty(text) || text.startsWith(".")) {
                                        return;
                                    }

//                                    item.amount = Double.valueOf(text);
                                    item.amount = text;
                                    mAdapter.notifyItemChanged(position);
                                }
                            });
                            editDialog.setInputType(8194);
                        } else {
                            editDialog.setEditText(tag);
                        }
                        editDialog.show();
                        break;
                }
            }
        });
        mAdapter.addFooterView(footerView);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("p0", type);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.SET_PWD:
                goLogin();
                break;
            case WebJsUtil.PAGE_FINISHED:
                hideProgress();
                break;
            case Constant.SELECT_ADDRESS:
                AddressKey addressKey = (AddressKey) event.object;
//                privateKey = BaseApplication.decrypt(addressKey.getKey());
                tvAddress.setText(addressKey.getAddress());
                break;
            case "BarcodeResult":
                String currentScanResult = event.param;
                afterScanBack(currentScanResult);
                break;
        }
    }

    @Override
    protected void onPopItemClick(String popPosition) {
        super.onPopItemClick(popPosition);
        mAdapter.setNewData(null);
        inputItems.clear();
        outputItems.clear();
        tvSign.setText("");
        tvSign.setVisibility(View.GONE);
        codeAdapter.setNewData(null);

        if (type == 2) {
            dealSignItemMsg = null;
            tvAddress.setText("");
            setFee();
//            etFee.setText("");
            etMsg.setText("");
        }
    }


    private String getOutputArrayTail(int i) {
        return i != outputItems.size() - 1 ? "\'," : "\'];";
    }

    private String getInputArrayTail(int i) {
        return i != inputItems.size() - 1 ? "\',\'" : "\'];";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.iv_msg_scan:
//                scanType = 1;
//                BarcodeUtil.goScan(this);
//                break;
            case R.id.tv_address:
                Intent intent = new Intent(mContext, ManageKeyActivity.class);
                intent.putExtra("p0", 1);
                mContext.startActivity(intent);
                break;
            case R.id.tv_button:
                //扫描
                BarcodeUtil.goScanContinue(this);
                break;
            case R.id.tv_save:
                if (inputItems.size() == 0) {
                    showShortToast(R.string.scan_input_please);
                    return;
                }

//                //交易输出的数量为空的话需要提醒报错
//                if (outputItems.size() == 0) {
//                    showShortToast(R.string.scan_output_please);
//                    return;
//                }

                //(inputprivatekeys, txids, inputamounts, indexs, outputaddresses, outputamounts, returnaddr, txfee, msg, msgtype)

                //inputprivatekeys 输入私钥列表
                StringBuilder inputprivatekeys = new StringBuilder("var inputprivatekeys =[\'");

                //txids  输入utxo的id列表 交易id
                StringBuilder txids = new StringBuilder("var txids =[\'");

                //inputamounts  输入数量 输入金额
                StringBuilder inputamounts = new StringBuilder("var inputamounts =[");

                //indexs utxo的索引列表
                StringBuilder indexs = new StringBuilder("var indexs=[");

                BigDecimal inputAmount = new BigDecimal("0");
                for (int i = 0; i < inputItems.size(); i++) {
                    DealSignItem item = inputItems.get(i);
                    if (!TextUtils.isEmpty(item.amount)) {
                        inputAmount = inputAmount.add(new BigDecimal(item.amount));
                    }

                    String tail = getInputArrayTail(i);

                    inputprivatekeys.append(item.key).append(tail);

                    txids.append(item.txId).append(tail);

                    inputamounts.append(item.amount).append(i != inputItems.size() - 1 ? "," : "];");

                    indexs.append(item.index).append(i != inputItems.size() - 1 ? "," : "];");
                }

                //outputaddresses 输出地址列表
                StringBuilder outputaddresses = new StringBuilder("var outputaddresses =[");

                //outputamounts 输出数量
                StringBuilder outputamounts = new StringBuilder("var outputamounts =[");

                BigDecimal outputAmount = new BigDecimal("0");
                if (outputItems.size() > 0) {
                    for (int i = 0; i < outputItems.size(); i++) {
                        DealSignItem item = outputItems.get(i);
                        if (!TextUtils.isEmpty(item.amount)) {
                            outputAmount = outputAmount.add(new BigDecimal(item.amount));
                        }

                        String tail = getOutputArrayTail(i);

                        outputaddresses.append("\'").append(item.address).append(tail);

                        outputamounts.append(item.amount).append(i != outputItems.size() - 1 ? "," : "];");
                    }
                } else {
                    outputaddresses.append("];");
                    outputamounts.append("];");
                }

//                //交易的输出数量为空或者超出了输入总额提醒报错
//                if (outputAmount == 0) {
//                    showShortToast(R.string.output_amount_zero);
//                    return;
//                }

                if (outputAmount.subtract(inputAmount).doubleValue() > 0) {
                    showShortToast(R.string.output_amount_exceed);
                    return;
                }


                String coinType = SPUtil.getString(Constant.COIN_TYPE, "fch");
                String paramTail = "\";";

                //returnaddr  找零地址
                //xxxxx简单交易 高级交易找零地址都设置为默认地址xxxxxx
//                3. 简单交易和高级交易的默认的找零地址统一为第一个输入地址。
                StringBuilder returnaddr = new StringBuilder();
                String returnAddress = null;
                if (tvAddress != null) {
                    returnAddress = tvAddress.getText().toString();
                }

                //未选择 默认第一个输入地址
                if (TextUtils.isEmpty(returnAddress)) {
//                    AddressKey address = DBUtil.getKeyFirst(coinType);
//                    if (address == null) {
//                        showShortToast(R.string.create_address_please);
//                        return;
//                    }
//                    returnAddress = address.getAddress();
                    returnAddress = inputItems.get(0).address;
                }
                returnaddr.append("var returnaddr =\"").append(returnAddress).append(paramTail);

                // txfee, msg, msgtype
                String msgStr = "";
                StringBuilder msg = new StringBuilder();
                StringBuilder msgtype = new StringBuilder();
                if (type == 2) {
                    //高级交易
                    //刻字
                    msgStr = etMsg.getText().toString();
                    msg.append("var msg =\"").append(msgStr).append(paramTail);

                    String msgtypeStr;
                    if (dealSignItemMsg != null && dealSignItemMsg.msg.equals(msgStr)) {
                        msgtypeStr = dealSignItemMsg.msgtype;
                    } else {
                        msgtypeStr = "1";
                    }

                    //msgtype  1是文本 2是base64文本 扫描到的是
                    msgtype.append("var msgtype =").append(msgtypeStr).append(";");
                } else {
                    //简单交易
                    //msg 刻字
                    msg.append("var msg =\"").append(paramTail);

                    //msgtype  1是文本 2是base64文本 扫描到的是
                    msgtype.append("var msgtype =1").append(";");
                }


                //   手续费改成1s/byte，并生成签名时给出提示。
                //   输入总量，输出总量，手续费，找零
                //获取简单交易的手续费
                String strJS = "javascript:var coinType=\"" + coinType + "\";" + inputprivatekeys + txids + inputamounts + indexs +
                        outputaddresses + outputamounts + returnaddr + msg + msgtype +
                        " calcMinTranscationFee(coinType,inputprivatekeys, txids, inputamounts, indexs, " +
                        "outputaddresses, outputamounts, returnaddr,msg,msgtype)";
                BigDecimal finalInputAmount = inputAmount;
                BigDecimal finalOutputAmount = outputAmount;
                String finalReturnaddr = returnAddress;
                String finalMsgStr = msgStr;
                callJavaScriptFunction(strJS, new JSCallBack() {
                    @Override
                    public void onSuccess(String value) {
                        super.onSuccess(value);
                        //此处为 js 返回的结果
                        LogUtil.e("js 返回的结果:" + value);
                        if (TextUtils.isEmpty(value)) {
                            showShortToast(R.string.create_sign_error);
                            return;
                        }

                        //去掉引号
                        value = value.replace("\"", "");
                        BigDecimal minFee = new BigDecimal(value).divide(new BigDecimal(100000000));
                        BigDecimal finalFee = minFee;
                        String fchFeeStr = new BigDecimal(value).divide(new BigDecimal(100)).stripTrailingZeros().toEngineeringString();
                        //要么用户自己改 如果用户自己不改就用我那函数计算
                        if (type == 2) {
                            String inputFeeStr = etFee.getText().toString();
                            if (!TextUtils.isEmpty(inputFeeStr)) {
                                BigDecimal inputFee;
                                //输入单位为c
                                if (coinType.equals(Constant.COIN_TYPE_FCH)) {
                                    inputFee = new BigDecimal(inputFeeStr).divide(new BigDecimal(1000000));
                                } else {
                                    inputFee = new BigDecimal(inputFeeStr);
                                }

                                //手续费必须>=最小值
                                if (inputFee.subtract(minFee).doubleValue() < 0) {
                                    showShortToast(R.string.fee_too_little);
                                    return;
                                }
                                finalFee = inputFee;
                                fchFeeStr = inputFeeStr;
                            }
                        }

                        //反正你记着这样的就行调我那个签名方法时。
                        //1 如果输入金额的总量<(输出金额的总量+手续费)，则提示:转出金额太多，余额不足
                        //2  如果手续费<之前用公式计算出来的值则提示 手续费太少
                        boolean insufficient = finalInputAmount.subtract(finalOutputAmount.add(finalFee)).doubleValue() < 0;
                        if (insufficient) {
                            showShortToast(R.string.insufficient_balance);
                        }
                        //找零=输入-输出-手续费
                        String txfeeStr = finalFee.stripTrailingZeros().toPlainString();///让bigdecimal不用科学计数法显示;
                        BigDecimal returnChange = finalInputAmount.subtract(finalOutputAmount).subtract(finalFee);
                        DialogUtil.showNormalDialog(mContext, String.format(getString(R.string.create_sign_tip),
                                finalInputAmount.stripTrailingZeros().toEngineeringString(),
                                finalOutputAmount.stripTrailingZeros().toEngineeringString(),
                                coinType.equals(Constant.COIN_TYPE_FCH) ? fchFeeStr + "c" : txfeeStr + "f",
                                returnChange.doubleValue() == 0 ? "0" : returnChange.stripTrailingZeros().toEngineeringString(),
                                finalReturnaddr + (type == 2 ? "\n" + getString(R.string.lettering_) + finalMsgStr : "")),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //反正你记着这样的就行调我那个签名方法时。
                                        //1 如果输入金额的总量<(输出金额的总量+手续费)，则提示:转出金额太多，余额不足
                                        //2  如果手续费<之前用公式计算出来的值则提示 手续费太少
                                        if (insufficient) {
                                            showShortToast(R.string.insufficient_balance);
                                            return;
                                        }

                                        dialogInterface.dismiss();
                                        //txfee  交易费
                                        StringBuilder txfee = new StringBuilder();
                                        txfee.append("var txfee =").append(txfeeStr).append(";");
                                        //去生成签名
                                        createSign(coinType, inputprivatekeys, txids, inputamounts, indexs,
                                                outputaddresses, outputamounts, returnaddr, txfee, msg, msgtype);
                                    }
                                });
                    }

                    @Override
                    public void onFailed(String msg) {
                        super.onFailed(msg);
                    }
                });
                break;
        }
    }

    private void createSign(String coinType, StringBuilder inputprivatekeys, StringBuilder txids, StringBuilder inputamounts, StringBuilder indexs,
                            StringBuilder outputaddresses, StringBuilder outputamounts, StringBuilder returnaddr, StringBuilder txfee,
                            StringBuilder msg, StringBuilder msgtype) {
        StringBuilder strJS = new StringBuilder();
        strJS.append("javascript:").append(inputprivatekeys).append(txids).append(inputamounts).append(indexs)
                .append(outputaddresses).append(outputamounts).append(returnaddr).append(txfee).append(msg).append(msgtype);

        switch (coinType) {
            case "fch":
                strJS.append(" createFchTranscationSig");
                break;
            case "btc":
                strJS.append(" createBtcTranscationSig");
                break;
            case "bch":
                strJS.append(" createBchTranscationSig");
                break;
        }

        strJS.append("(inputprivatekeys, txids, inputamounts, indexs, outputaddresses, outputamounts, returnaddr, txfee, msg, msgtype)");
        callJavaScriptFunction(strJS.toString());
    }

    @Override
    protected void onJSCallBack(String value) {
        super.onJSCallBack(value);
        //此处为 js 返回的结果
        LogUtil.e("js 返回的结果:" + value);
        //第一个是私钥，第二个是地址
        if (TextUtils.isEmpty(value)) {
            showShortToast(R.string.create_sign_error);
            return;
        }

        if (type == 2) {
            AppUtil.hideKeyboard(mContext, etFee);
        }
        //去掉引号
        value = value.replace("\"", "");
        tvSign.setText(value);
        tvSign.setVisibility(View.VISIBLE);
        //拆分生成二维码
        int codeMaxLength = 140;
        int valueLength = value.length();
        if (valueLength > codeMaxLength) {
            List<String> codes = new ArrayList<>();
            for (int i = 0; i < valueLength; i += codeMaxLength) {
                int end = i + codeMaxLength;
                codes.add(value.substring(i, end > valueLength ? valueLength : end));
            }
            codeAdapter.setNewData(codes);
        } else {
            codeAdapter.addData(value);
        }
        recycler.scrollToPosition(mAdapter.getItemCount() - 1);
        goPicture(0);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result == null || TextUtils.isEmpty(result.getContents())) {
//            super.onActivityResult(requestCode, resultCode, data);
//            return;
//        }
//
//        afterScanBack(result.getContents());
//    }

    private void afterScanBack(String result) {
        LogUtil.e("扫描到二维码：" + result);
        try {
            DealSignItem item = JSON.parseObject(result, DealSignItem.class);
            if (item == null) {
                showLongToast(R.string.scan_error);
                return;
            }

            //如果是输入 校验是否保存私钥
            String coinType = SPUtil.getString(Constant.COIN_TYPE, "fch");
            switch (item.getItemType()) {
                case DealSignItem.MSG:
//                {
//                    "dealType":3,
//                        "msg":"xxxxx".
//                    "msgtype":1,
//                        "fee":10000 这里的fee单位是c
//                }
                    //扫描刻字只有fch才有这个功能 且高级交易
                    if (!coinType.equals("fch") || type == 1) {
                        showLongToast(R.string.scan_error);
                        return;
                    }

                    etMsg.setText(item.msg);
                    if (!TextUtils.isEmpty(item.fee)) {
                        etFee.setText(item.fee);
                    }
                    dealSignItemMsg = item;
                    break;
                case DealSignItem.INPUT: {
                    if (TextUtils.isEmpty(item.txId) || TextUtils.isEmpty(item.index)) {
                        showLongToast(R.string.scan_error);
                        return;
                    }

                    //这个是每一个输入和输出 的amount必须>=0吧？
                    double amount = Double.valueOf(item.amount);
                    if (amount < 0) {
                        showShortToast(R.string.amount_zero);
                        return;
                    }


                    if (type == 1 && inputItems.size() > 0) {
//                    简单交易签名  如果有多个输入，输入的地址必须一样。否则提示错误。
                        String address = inputItems.get(0).address;
                        if (!address.equals(item.address)) {
                            showLongToast(R.string.input_address_different);
                            return;
                        }
                    }

                    //txid加索引去判断唯一，然后再覆盖
                    //已存在列表 下标
                    int position = -1;
                    for (int i = 0; i < inputItems.size(); i++) {
                        DealSignItem inputItem = inputItems.get(i);
                        if (item.txId.equals(inputItem.txId) && item.index.equals(inputItem.index)) {
                            position = i;
                            break;
                        }
                    }

                    final int pos = position;

                    //扫描输入时做匹配之前，调用一下地址转换函数addressConvert，再去匹配地址
                    String toCoinType = "var toCoinType =\"" + coinType + "\";";
                    String address = "var address =\"" + item.address + "\";";
                    String strJS = "javascript:" + toCoinType + address + " addressConvert(toCoinType,address)";
                    callJavaScriptFunction(strJS, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            LogUtil.e("js 返回的结果:" + s);

                            if (TextUtils.isEmpty(s)) {
                                showLongToast(R.string.output_address_invalid);
                                return;
                            }

                            //去掉引号
                            s = s.replace("\"", "");
                            AddressKey key = DBUtil.getKeyByAddressAndCoinType(s, coinType);
                            if (key == null) {
                                showLongToast(R.string.input_not_found);
                                return;
                            }

                            item.key = BaseApplication.decrypt(key.getKey());
                            item.addressTag = key.getTag();

                            //扫描到相同索引，提示，更新覆盖
                            if (pos >= 0) {
                                showLongToast(R.string.input_exist);
                                inputItems.remove(pos);
                                inputItems.add(pos, item);

                                for (DealSignItem inputItem : mAdapter.getData()) {
                                    if (item.txId.equals(inputItem.txId) && item.index.equals(inputItem.index)) {
//                            inputItem.index = item.index;
                                        inputItem.amount = item.amount;
                                        inputItem.address = item.address;
                                        break;
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                                return;
                            }

//                            mAdapter.addData(inputItems.size(), item);
                            inputItems.add(item);
                            Collections.sort(inputItems, new Comparator<DealSignItem>() {
                                @Override
                                public int compare(DealSignItem dealSignItem, DealSignItem t1) {
                                    return dealSignItem.seq - t1.seq;
                                }
                            });
                            mAdapter.setNewData(null);
                            mAdapter.addData(inputItems);
                            mAdapter.addData(outputItems);
//                            recycler.scrollToPosition(inputItems.size() - 1);
//                            setFee();

                        }
                    });
                    break;
                }
                case DealSignItem.OUTPUT: {
                    if (TextUtils.isEmpty(item.address)) {
                        showLongToast(R.string.scan_error);
                        return;
                    }

                    //这个是每一个输入和输出 的amount必须>=0吧？
                    double amount = Double.valueOf(item.amount);
                    if (amount < 0) {
                        showShortToast(R.string.amount_zero);
                        return;
                    }

                    //TODO 输出 校验地址合法
                    String secretKey = "var addr =\"" + item.address + "\";";
                    String strJS = "javascript:" + secretKey + " addressType(addr)";
                    callJavaScriptFunction(strJS, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            //此处为 js 返回的结果
                            LogUtil.e("js 返回的结果:" + s);

                            if (TextUtils.isEmpty(s)) {
                                showLongToast(R.string.output_address_invalid);
                                return;
                            }

                            //去掉引号
                            s = s.replace("\"", "");


                            switch (s) {
                                case "legacy":
                                    //btc
                                    if (!coinType.equals("btc")) {
                                        showLongToast(R.string.output_address_invalid);
                                        return;
                                    }
                                    break;
                                case "cashaddr":
                                    //bch
                                    if (!coinType.equals("bch")) {
                                        showLongToast(R.string.output_address_invalid);
                                        return;
                                    }
                                    break;
                                case "freecash":
                                    //fch
                                    if (!coinType.equals("fch")) {
                                        showLongToast(R.string.output_address_invalid);
                                        return;
                                    }
                                    break;
                                default:
                                    showLongToast(R.string.output_address_invalid);
                                    return;
                            }

                            LogUtil.e("output.before:" + JSON.toJSONString(outputItems));
//                            mAdapter.addData(item);
                            outputItems.add(item);
                            Collections.sort(outputItems, new Comparator<DealSignItem>() {
                                @Override
                                public int compare(DealSignItem dealSignItem, DealSignItem t1) {
                                    return dealSignItem.seq - t1.seq;
                                }
                            });
                            mAdapter.setNewData(null);
                            mAdapter.addData(inputItems);
                            mAdapter.addData(outputItems);
                            LogUtil.e("input:" + JSON.toJSONString(inputItems));
                            LogUtil.e("output:" + JSON.toJSONString(outputItems));

                            //滚动到输出最后一个
//                            recycler.scrollToPosition(mAdapter.getData().size() - 1);
//                            setFee();
                        }
                    });

                    break;
                }
            }


//            //设置扫描 序号
//            item.sort = mAdapter.getItemTypeCount(item.getItemType()) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            showLongToast(R.string.scan_error);
        }
    }

    private void setFee() {
        if (type == 1) {
            return;
        }
        String coinType = SPUtil.getString(Constant.COIN_TYPE, Constant.COIN_TYPE_FCH);
        boolean isFch = coinType.equals(Constant.COIN_TYPE_FCH);
        etFee.setText("");
        tvFeeUnit.setText(isFch ? "c" : coinType);
    }

    private BigDecimal getFeeDividend() {
        //默认手续费计算公式  (input*148+34*out+10)/100000000,input是输入的个数，out是输出的个数
        int input = inputItems.size();
        int out = outputItems.size();
        BigDecimal bigDecimal = new BigDecimal(input * 148 + 34 * out + 10);
        LogUtil.e("被除数：" + bigDecimal);
        return bigDecimal;
    }

    private void goPicture(int position) {
        advance(ImageBigListActivity.class, JSON.toJSONString(codeAdapter.getData()), position);
    }

}
