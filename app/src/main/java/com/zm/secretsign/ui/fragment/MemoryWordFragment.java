package com.zm.secretsign.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.GridSpacingItemDecoration;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zhou.library.utils.StringUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseFragment;
import com.zm.secretsign.utils.PasswordUtil;
import com.zm.secretsign.view.MemoryWordAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MemoryWordFragment extends BaseFragment {

    @BindView(R.id.tv_input)
    TextView tvInput;
    @BindView(R.id.tv_button)
    TextView tvButton;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    @BindView(R.id.et_word)
    EditText etWord;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    //    @BindView(R.id.web_view)
//    WebView webView;
    @BindView(R.id.et_text)
    EditText etText;
    @BindView(R.id.ll_input)
    LinearLayout llInput;
    @BindView(R.id.first_line)
    View firstLine;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_key)
    TextView tvKey;
    @BindView(R.id.et_tag)
    EditText etTag;
    @BindView(R.id.tv_input_tip)
    TextView tvInputTip;
    @BindView(R.id.tv_path_left)
    TextView tvPathLeft;
    @BindView(R.id.tv_input_tip_gray)
    TextView tvInputTipGray;

    private View rootView;
    private Unbinder unbinder;
    private String privateKey;
    private String address;

    private MemoryWordAdapter wordAdapter;
    private List<String> data;

    /**
     * Fragment 实例
     */
    public static MemoryWordFragment newInstance(Object... pramars) {
        MemoryWordFragment fragment = new MemoryWordFragment();
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
            rootView = inflater.inflate(R.layout.fragment_memory_word, container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

//        initWebView(webView);
        tvInput.setText(R.string.path);
        tvButton.setText(R.string.import_);
        etText.setHint("");
        String codeInputNumber = "0123456789/'";
        etText.setKeyListener(DigitsKeyListener.getInstance(codeInputNumber));
        tvPathLeft.setVisibility(View.VISIBLE);
        initPathLeft();
        tvInputTip.setVisibility(View.VISIBLE);
        llContainer.setPadding(0, 0, 0, 0);
        tvInputTipGray.setVisibility(View.VISIBLE);

        //TODO test
//        etText.setText("m/0'/145'/1'/1/2");

        recycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        int dp = getResources().getDimensionPixelSize(R.dimen.dp_10);
        int dpV = getResources().getDimensionPixelSize(R.dimen.dp_30);
        GridSpacingItemDecoration divider = new GridSpacingItemDecoration(dpV, dp, Color.WHITE, false);
        recycler.addItemDecoration(divider);
        wordAdapter = new MemoryWordAdapter();
        wordAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                data.remove(position);
                data.add(position, "");
                wordAdapter.notifyDataSetChanged();
            }
        });
        recycler.setAdapter(wordAdapter);
        initWords();


//        etWord.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String s = editable.toString();
//                if (TextUtils.isEmpty(s)) {
//                    return;
//                }
//
//                if (StringUtil.isChineseChar(s.substring(0, 1))) {
//                    etWord.setMaxEms(1);
//                } else {
//                    etWord.setMaxEms(40);
//                }
//            }
//        });
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

    @OnClick({R.id.tv_add})
    public void onAddClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                int addPosition = -1;
                for (int i = 0; i < data.size(); i++) {

                    if (addPosition >= 0) {
                        break;
                    }

                    if (TextUtils.isEmpty(data.get(i))) {
                        addPosition = i;
                    }
                }

                if (addPosition < 0) {
                    showShortToast(R.string.input_memory_overflow);
                    return;
                }

                //添加助记词
                String word = etWord.getText().toString();
                if (TextUtils.isEmpty(word)) {
                    showShortToast(R.string.input_memory_word_please);
                    return;
                }

                if (word.length() > 1) {
                    String wordOne = word.substring(0, 1);
                    if (StringUtil.isChineseChar(wordOne)) {
                        word = wordOne;
                    } else if (word.contains(" ")) {
                        word = word.substring(0, word.indexOf(" "));
                    }
                }

                data.remove(addPosition);
                data.add(addPosition, word);
                wordAdapter.notifyItemChanged(addPosition);
                etWord.setText("");
                break;
        }
    }

    @OnClick({R.id.tv_button, R.id.tv_save})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_button:
                //导入
                StringBuilder memoryWords = new StringBuilder();
                for (String word : data) {
                    if (TextUtils.isEmpty(word)) {
                        break;
                    }

                    memoryWords.append(word).append(" ");
                }

                if (TextUtils.isEmpty(memoryWords)) {
                    showShortToast(R.string.add_word_please);
                    return;
                }

                String etPath = etText.getText().toString();
                if (TextUtils.isEmpty(etPath)) {
                    showShortToast(R.string.input_path_please);
                    return;
                }

                String coinType = "var cointype =\"" + SPUtil.getString(Constant.COIN_TYPE, "fch") + "\";";
                String words = "var words =\"" + memoryWords.substring(0, memoryWords.length() - 1) + "\";";
//                String words = "var words =\"" + "faculty matrix spoon adult enemy soon broccoli solution scissors speed negative cross" + "\";";
                String path = "var path =\"" + tvPathLeft.getText().toString() + etPath + "\";";
                String strJS = "javascript:" + coinType + words + path + " mnemonicWord(cointype,words,path)";
                callJavaScriptFunction(strJS);
                break;
            case R.id.tv_save:
                AppUtil.hideKeyboard(mContext, etTag);
                PasswordUtil.saveAddressKey(mContext, privateKey, address, etTag.getText().toString(), true);
                break;
        }
    }

    @Override
    protected void onJSCallBack(String value) {
        super.onJSCallBack(value);
        //此处为 js 返回的结果
        LogUtil.e("js 返回的结果:" + value);
//                        js 返回的结果:["Kxj3VrEmaFv923JCE8XYAG6cwiEmaQCY7UMcXqotD5dLZdu8VL4T","FT4FfeTp5kCvgRSQnutGAfGy1TVypKbJ13"]
        //第一个是私钥，第二个是地址
        if (TextUtils.isEmpty(value)) {
            showShortToast(R.string.import_incorrect);
            return;
        }

        //第一个是私钥，第二个是地址
        List<String> data = JSON.parseArray(value, String.class);
        if (data == null) {
            return;
        }

        if (data.size() > 0) {
            privateKey = data.get(0);
            tvKey.setText(privateKey);
        }

        if (data.size() > 1) {
            address = data.get(1);
            tvAddress.setText(address);
        }
    }

    private void initPathLeft() {
        String coinType = SPUtil.getString(Constant.COIN_TYPE, "fch");
        switch (coinType) {
            case "fch":
                tvPathLeft.setText("m/44'/485'/");
                break;
            case "btc":
                tvPathLeft.setText("m/44'/0'/");
                break;
            case "bch":
                tvPathLeft.setText("m/44'/145'/");
                break;
        }
        tvInputTipGray.setText("路径示例：" + tvPathLeft.getText().toString() + "0'/0/1");
    }

    private void initWords() {
        if (data == null) {
            data = new ArrayList<>();
        } else {
            data.clear();
        }
        //TODO　test
//        data.add("你");
//        data.add("好");
//        data.add("世");
//        data.add("界");
        for (int i = 0; i < 12; i++) {
            data.add("");
        }
        wordAdapter.setNewData(data);
    }

    @Override
    public void clearView() {
        super.clearView();

        privateKey = "";
        address = "";

        initWords();
        etWord.setText("");
        etText.setText("");
        tvAddress.setText("");
        tvKey.setText("");
        etTag.setText("");
        initPathLeft();
    }
}