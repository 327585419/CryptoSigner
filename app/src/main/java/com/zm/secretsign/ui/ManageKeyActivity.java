package com.zm.secretsign.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhou.library.bean.Event;
import com.zhou.library.utils.DateTimeUtils;
import com.zhou.library.utils.EventBusUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseListActivity;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.WebJsUtil;
import com.zm.secretsign.view.CreateAddressDialog;
import com.zm.secretsign.view.DeleteDialog;
import com.zm.secretsign.view.EditDialog;
import com.zm.secretsign.view.PublicKeyDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;

public class ManageKeyActivity extends BaseListActivity<AddressKey> {

    @BindView(R.id.recycler)
    RecyclerView recycler;

    private int deletePosition;
    private EditDialog editDialog;
    private int type;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        bindContentView(R.layout.layout_recycler);

        if (savedInstanceState == null) {
            type = getIntent().getIntExtra("p0", 0);
        } else {
            type = savedInstanceState.getInt("p0");
        }

        setUpTextLeftBackIcon(R.string.manage_key);
        setDownTextRightIcon();

        initRecycler();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                AddressKey item = mAdapter.getItem(position);

                if (item == null) {
                    return;
                }

                switch (view.getId()) {
                    case R.id.tv_public_key:
                        new PublicKeyDialog(mContext, BaseApplication.decrypt(item.getKey()), item.getAddress()).show();
                        break;
                    case R.id.iv_address:
                        advance(ImageBigActivity.class, item.getAddress());
                        break;
                    case R.id.tv_tag:
                        String tag = item.getTag();
                        if (editDialog == null) {
                            editDialog = new EditDialog(mContext, getString(R.string.input_tag), tag, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    item.setTag(editDialog.getEditText());
                                    mAdapter.notifyItemChanged(position);
                                    editDialog.dismiss();
                                    DBUtil.updateKey(item);
//                                    showShortToast(R.string.save_success);
                                }
                            });
                        } else {
                            editDialog.setEditText(tag);
                        }
                        editDialog.show();
                        break;
                    case R.id.tv_code:
                        DialogUtil.showNormalDialog(mContext, getString(R.string.show_key), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new CreateAddressDialog(mContext, BaseApplication.decrypt(item.getKey()), item.getAddress()).show();
                            }
                        });
                        break;
                    case R.id.tv_delete:
                        deletePosition = position;
                        new DeleteDialog(mContext, item).show();
                        break;
                    case R.id.tv_top:
                        if (position == 0) {
                            showShortToast(R.string.key_first);
                            return;
                        }

                        Long clickOrder = item.getOrder();
                        AddressKey firstItem = mAdapter.getItem(0);
                        if (firstItem == null) {
                            return;
                        }
                        Long firstOrder = firstItem.getOrder();


                        //交换两个的order
                        firstItem.setOrder(clickOrder);
                        item.setOrder(firstOrder);
                        Collections.sort(mAdapter.getData(), new Comparator<AddressKey>() {
                            @Override
                            public int compare(AddressKey key, AddressKey t1) {
                                return key.getOrder().compareTo(t1.getOrder());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        DBUtil.updateKey(firstItem);
                        DBUtil.updateKey(item);
                        recycler.scrollToPosition(0);
                        break;
                    case R.id.tv_up:
                        if (position == 0) {
                            showShortToast(R.string.key_first);
                            return;
                        }

                        Long currentOrder = item.getOrder();
                        AddressKey lastItem = mAdapter.getItem(position - 1);
                        if (lastItem == null) {
                            return;
                        }
                        Long lastOrder = lastItem.getOrder();


                        //交换两个的order
                        lastItem.setOrder(currentOrder);
                        item.setOrder(lastOrder);
                        Collections.sort(mAdapter.getData(), new Comparator<AddressKey>() {
                            @Override
                            public int compare(AddressKey key, AddressKey t1) {
                                return key.getOrder().compareTo(t1.getOrder());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        DBUtil.updateKey(lastItem);
                        DBUtil.updateKey(item);
                        break;
                    case R.id.tv_down:
                        if (position == mAdapter.getData().size() - 1) {
                            showShortToast(R.string.key_last);
                            return;
                        }

                        Long currentO = item.getOrder();
                        AddressKey nextItem = mAdapter.getItem(position + 1);
                        if (nextItem == null) {
                            return;
                        }
                        Long nextOrder = nextItem.getOrder();
                        //交换两个的order
                        nextItem.setOrder(currentO);
                        item.setOrder(nextOrder);
                        Collections.sort(mAdapter.getData(), new Comparator<AddressKey>() {
                            @Override
                            public int compare(AddressKey key, AddressKey t1) {
                                return key.getOrder().compareTo(t1.getOrder());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        DBUtil.updateKey(nextItem);
                        DBUtil.updateKey(item);
                        break;
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.DELETE_ADDRESS:
                mAdapter.remove(deletePosition);
                break;
            case Constant.SET_PWD:
                finish();
                break;
            case WebJsUtil.PAGE_FINISHED:
                hideProgress();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("p0", type);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onItemViewClick(View view, int position, AddressKey item) {
        super.onItemViewClick(view, position, item);
        if (type == 1) {
            EventBusUtil.post(Constant.SELECT_ADDRESS, item);
            finish();
        }
    }


    @Override
    protected void onPopItemClick(String popPosition) {
        super.onPopItemClick(popPosition);
        refreshData();
    }

    @Override
    protected boolean loadMoreEnable() {
        return true;
    }

    @Override
    protected void loadData() {
        super.loadData();

        loadFinish(DBUtil.queryAllKeyItems(page, downTextView.getText().toString().toLowerCase()));
    }

    @Override
    public void convertItem(BaseViewHolder holder, AddressKey item) {
        holder.addOnClickListener(R.id.tv_code, R.id.tv_delete, R.id.tv_up, R.id.tv_down, R.id.tv_top, R.id.iv_address, R.id.tv_public_key)
                .setText(R.id.tv_tag, item.getTag())
                .setText(R.id.tv_address_text, holder.getAdapterPosition() == 0 ? R.string.address_default : R.string.address_)
                .setText(R.id.tv_address, item.getAddress())
                .setText(R.id.tv_key, String.format(getString(R.string.key_s), "****************************************************"))
                .setText(R.id.tv_time, String.format(getString(R.string.save_time_s), DateTimeUtils.getFormatForMinusAndColon(item.getSaveTime())));

        if (type == 1) {
            holder.setGone(R.id.ll_button, false)
                    .setVisible(R.id.tv_up, false)
                    .setVisible(R.id.tv_down, false)
                    .setVisible(R.id.tv_top, false);
        } else {
            holder.addOnClickListener(R.id.tv_tag);
        }
    }

    @Override
    public int bindItemView() {
        return R.layout.item_key;
    }

    @Override
    public RecyclerView bindRecyclerView() {
        return recycler;
    }

}
