package com.zm.secretsign.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.GridSpacingItemDecoration;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zhou.library.bean.Event;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.ImageBigListActivity;
import com.zm.secretsign.ui.ManageKeyActivity;
import com.zm.secretsign.ui.base.BaseActivity;
import com.zm.secretsign.ui.base.BaseFragment;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.bean.DealSignItem;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.view.EditDialog;
import com.zm.secretsign.view.MultipleItemQuickAdapter;
import com.zm.secretsign.view.SignCodeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DealSignFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.recycler)
    RecyclerView recycler;
//    @BindView(R.id.web_view)
//    WebView webView;

    private View rootView;
    private Unbinder unbinder;
    private int type;//1简单交易 2高级交易

    private TextView tvAddress, tvSign;
    private EditText etFee, etMsg;

    private MultipleItemQuickAdapter mAdapter;
    private SignCodeAdapter codeAdapter;
    private List<DealSignItem> inputItems, outputItems;
    private EditDialog editDialog;
    private int scanType;//0 扫描输入输出  1扫描刻字（拓展信息）
    private DealSignItem dealSignItemMsg;

    /**
     * Fragment 实例
     */
    public static DealSignFragment newInstance(Object... pramars) {
        DealSignFragment fragment = new DealSignFragment();
        if (pramars != null && pramars.length > 0) {
            Bundle bundle = new Bundle();
            for (int i = 0; i < pramars.length; i++) {
                bundle.putSerializable("p" + i, (Serializable) pramars[i]);
            }
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragmnet_deal_sign, container, false);
        }
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            type = getArguments().getInt("p0", 1);
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
            tvAddress = footerView.findViewById(R.id.tv_address);
            tvAddress.setOnClickListener(this);
            etFee = footerView.findViewById(R.id.et_fee);
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
                ((BaseActivity) mContext).advance(ImageBigListActivity.class, JSON.toJSONString(codeAdapter.getData()), position);
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
                        setFee();
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


        //TODO 测试
//        DealSignItem dealSignItem = new DealSignItem();
//        dealSignItem.setDealType(DealSignItem.INPUT);
//        dealSignItem.key = "KzpJU6T6dhBsgifeNE4VMZBbhVTmi2QiK3UtN8CtmFT7rnBQ4XAo";
//        dealSignItem.txId = "0159618c8bf87d2dfb9c21a05430925fcaf95d9684d32a955b9e42102d807bba";
//        dealSignItem.index = "0";
//        dealSignItem.amount = 0.1;
//        dealSignItem.address = "bitcoincash:qzvfwyaes7gjcfcpd8pgm7r9gpl4zeyf5sx2rw3r4d";
//        mAdapter.addData(dealSignItem);
//        inputItems.add(dealSignItem);
////        LogUtil.e("输入：" + JSON.toJSONString(dealSignItem));
//
//        dealSignItem = new DealSignItem();
//        dealSignItem.setDealType(DealSignItem.INPUT);
//        dealSignItem.key = "L5LZd6XB9ZrqrXHDzFBXpHd1mYsV7spXpVshVsPswtJKhTFGQdne";
//        dealSignItem.txId = "0197325791a93e55f2cab0c51d41e88ed9ce20988e3b7c38e1283e2e1ee85e4c";
//        dealSignItem.index = "2";
//        dealSignItem.amount = 0.03;
//        dealSignItem.address = "bitcoincash:qzvfwyaes7gjcfcpd8pgm7r9gpl4zeyf5sx2rw3r4d";
//        mAdapter.addData(dealSignItem);
//        inputItems.add(dealSignItem);
//
//        dealSignItem = new DealSignItem();
//        dealSignItem.setDealType(DealSignItem.OUTPUT);
//        dealSignItem.address = "bitcoincash:qrzvgzy7u220mxm9gq2874eams52aa8k7qsmtflx0y";
//        dealSignItem.amount = "0.0000005";
//        mAdapter.addData(dealSignItem);
//        outputItems.add(dealSignItem);
//
//        dealSignItem = new DealSignItem();
//        dealSignItem.setDealType(DealSignItem.OUTPUT);
//        dealSignItem.address = "1K9eXL4Vp6aMQzbSVUfNQYZDLKngTK85wQ";
//        dealSignItem.amount = 0.09;
//        mAdapter.addData(dealSignItem);
//        outputItems.add(dealSignItem);


//        String value = "我是周，我来拆分字符串，看对不对";
//        int codeMaxLength = 5;
//        int valueLength = value.length();
//        List<String> codes = new ArrayList<>();
//        for (int i = 0; i < valueLength; i += codeMaxLength) {
//            int end = i + codeMaxLength;
//            codes.add(value.substring(i, end > valueLength ? valueLength : end));
//        }
//        LogUtil.e("拆分：" + JSON.toJSONString(codes));

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("p0", type);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
//        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        super.onDestroyView();
    }

    private String getOutputArrayTail(int i) {
        return i != outputItems.size() - 1 ? "\',\'" : "\'];";
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
                scanType = 0;
                BarcodeUtil.goScan(this);
                break;
            case R.id.tv_save:
                if (inputItems.size() == 0) {
                    showShortToast(R.string.scan_input_please);
                    return;
                }

                String coinType = SPUtil.getString(Constant.COIN_TYPE, "fch");
                StringBuilder highParams = new StringBuilder();
                String paramTail = "\";";
                if (type == 2) {
                    //高级交易可以没有输出
                    //returnaddr  找零地址
                    String returnaddr = tvAddress.getText().toString();
                    //未选择 默认第一个地址
                    if (TextUtils.isEmpty(returnaddr)) {
                        AddressKey address = DBUtil.getKeyFirst(coinType);
                        if (address == null) {
                            showShortToast(R.string.create_address_please);
                            return;
                        }
                        returnaddr = address.getAddress();
                    }
                    highParams.append("var returnaddr =\"").append(returnaddr).append(paramTail);

                    //txfee  交易费
                    String txfee;
                    String msgtype;
                    if (dealSignItemMsg != null) {
                        //有扫描信息
                        // 这里的fee单位是c
//                        {
//                            "dealType":3,
//                                "msg":"xxxxx".
//                            "msgtype":1,
//                                "fee":10000
//                        }
                        //手续费
                        BigDecimal dividend = new BigDecimal(dealSignItemMsg.fee);//被除数
                        BigDecimal divisor = new BigDecimal(1000000);//除数
                        LogUtil.e("除数：" + divisor);
                        BigDecimal fee = dividend.divide(divisor);
                        LogUtil.e("商：" + fee);
                        txfee = fee.stripTrailingZeros().toPlainString();///让bigdecimal不用科学计数法显示;

                        msgtype = dealSignItemMsg.msgtype;
                    } else {
                        //手续费
                        BigDecimal dividend = getFeeDividend();//被除数
                        BigDecimal divisor = new BigDecimal(100000000);//除数
                        LogUtil.e("除数：" + divisor);
                        BigDecimal fee = dividend.divide(divisor);
                        LogUtil.e("商：" + fee);
                        txfee = fee.stripTrailingZeros().toPlainString();///让bigdecimal不用科学计数法显示;

                        msgtype = "1";
                    }

                    highParams.append("var txfee =").append(txfee).append(";");

                    //刻字
                    String msg = etMsg.getText().toString();
                    highParams.append("var msg =\"").append(msg).append(paramTail);

                    //msgtype  1是文本 2是base64文本 扫描到的是
                    highParams.append("var msgtype =").append(msgtype).append(";");
                } else {
                    //简单交易
                    if (outputItems.size() == 0) {
                        showShortToast(R.string.scan_output_please);
                        return;
                    }

                    //returnaddr  找零地址
                    highParams.append("var returnaddr =\"").append(inputItems.get(0).address).append(paramTail);

                    //txfee  交易费
                    String txfee = coinType.equals("btc") ? "0.001" : "0.00001";
//                    String txfee = etFee.getText().toString();
                    highParams.append("var txfee =").append(txfee).append(";");
//                    highParams.append("var txfee =").append(TextUtils.isEmpty(txfee) ? "0" : txfee).append(";");

                    //msg 刻字
                    highParams.append("var msg =\"").append(paramTail);

                    //msgtype  1是文本 2是base64文本 扫描到的是
                    highParams.append("var msgtype =1").append(";");
                }

                //inputprivatekeys 输入私钥列表
                StringBuilder inputprivatekeys = new StringBuilder("var inputprivatekeys =[\'");

                //txids  输入utxo的id列表 交易id
                StringBuilder txids = new StringBuilder("var txids =[\'");

                //inputamounts  输入数量 输入金额
                StringBuilder inputamounts = new StringBuilder("var inputamounts =[");

                //indexs utxo的索引列表
                StringBuilder indexs = new StringBuilder("var indexs=[");

                for (int i = 0; i < inputItems.size(); i++) {
                    DealSignItem item = inputItems.get(i);
                    String tail = getInputArrayTail(i);

                    inputprivatekeys.append(item.key).append(tail);

                    txids.append(item.txId).append(tail);

                    inputamounts.append(item.amount).append(i != inputItems.size() - 1 ? "," : "];");

                    indexs.append(item.index).append(i != inputItems.size() - 1 ? "," : "];");
                }

                //outputaddresses 输出地址列表
                StringBuilder outputaddresses = new StringBuilder("var outputaddresses =[\'");

                //outputamounts 输出数量
                StringBuilder outputamounts = new StringBuilder("var outputamounts =[");

                if (outputItems.size() > 0) {
                    for (int i = 0; i < outputItems.size(); i++) {
                        DealSignItem item = outputItems.get(i);
                        String tail = getOutputArrayTail(i);

                        outputaddresses.append(item.address).append(tail);

                        outputamounts.append(item.amount).append(i != outputItems.size() - 1 ? "," : "];");
                    }
                } else {
                    outputaddresses.append("];");
                    outputamounts.append("];");
                }


                StringBuilder strJS = new StringBuilder();
                strJS.append("javascript:").append(inputprivatekeys).append(txids).append(inputamounts).append(indexs)
                        .append(outputaddresses).append(outputamounts).append(highParams);
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
                break;
        }
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null || TextUtils.isEmpty(result.getContents())) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        LogUtil.e("扫描到二维码：" + result.getContents());
        if (scanType == 1) {
            //扫描的是刻字
            etMsg.setText(result.getContents());
            etMsg.setSelection(etMsg.length());
            return;
        }

        try {
            DealSignItem item = JSON.parseObject(result.getContents(), DealSignItem.class);
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
                    etFee.setText(String.format("%sc", item.fee));
                    dealSignItemMsg = item;

                    break;
                case DealSignItem.INPUT: {
                    if (TextUtils.isEmpty(item.txId) || TextUtils.isEmpty(item.index)) {
                        showLongToast(R.string.scan_error);
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

                            mAdapter.addData(inputItems.size(), item);
                            inputItems.add(item);
                            recycler.scrollToPosition(inputItems.size() - 1);
                            setFee();

                        }
                    });
                    break;
                }
                case DealSignItem.OUTPUT: {
                    if (TextUtils.isEmpty(item.address)) {
                        showLongToast(R.string.scan_error);
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

                            mAdapter.addData(item);
                            outputItems.add(item);

                            //滚动到输出最后一个
                            recycler.scrollToPosition(mAdapter.getData().size() - 1);
                            setFee();
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
        String coinType = SPUtil.getString(Constant.COIN_TYPE, "fch");
        boolean isFch = coinType.equals("fch");
        String feeStr;
        if (outputItems.size() == 0 && inputItems.size() == 0) {
            feeStr = coinType.equals("btc") ? "0.001" : "0.00001";
        } else {
            BigDecimal dividend = getFeeDividend();//被除数
            BigDecimal divisor = new BigDecimal(isFch ? 100 : 100000000);//除数
            LogUtil.e("除数：" + divisor);
            BigDecimal fee = dividend.divide(divisor);
            LogUtil.e("商：" + fee);
            feeStr = fee.stripTrailingZeros().toPlainString();///让bigdecimal不用科学计数法显示;
        }
        etFee.setText(String.format("%s%s", feeStr, isFch ? "c" : coinType));
    }

    private BigDecimal getFeeDividend() {
        //默认手续费计算公式  (input*148+34*out+10)/100000000,input是输入的个数，out是输出的个数
        int input = inputItems.size();
        int out = outputItems.size();
        BigDecimal bigDecimal = new BigDecimal(input * 148 + 34 * out + 10);
        LogUtil.e("被除数：" + bigDecimal);
        return bigDecimal;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.SELECT_ADDRESS:
                AddressKey addressKey = (AddressKey) event.object;
//                privateKey = BaseApplication.decrypt(addressKey.getKey());
                tvAddress.setText(addressKey.getAddress());
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void clearView() {
        super.clearView();

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
}