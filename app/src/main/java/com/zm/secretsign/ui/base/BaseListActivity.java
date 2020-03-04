package com.zm.secretsign.ui.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.divider.RecycleViewDivider;
import com.zm.secretsign.R;
import com.zm.secretsign.utils.DBUtil;

import java.util.List;


/**
 * 下拉列表
 */
public abstract class BaseListActivity<T> extends BaseTitleActivity {

    protected int page = 1;

    /**
     * 基类适配器
     */
    protected BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    protected RecyclerView.ItemDecoration divider;
    protected RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
    }

    /**
     * 分隔线高度
     */
    protected void initRecycler() {
        RecyclerView recyclerView = bindRecyclerView();
        if (recyclerView == null) {
            return;
        }

        mAdapter = getmAdapter();
        mAdapter.openLoadAnimation();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onItemViewClick(view, position, mAdapter.getItem(position));
            }
        });
        if (loadMoreEnable()) {
            mAdapter.setEnableLoadMore(true);
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loadMore();
                }
            }, recyclerView);
        }

        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(mAdapter);

        if (divider != null) {
            //添加Android自带的分割线
            recyclerView.addItemDecoration(divider);
        }
        loadData();
    }

    protected void onItemViewClick(View view, int position, T item) {

    }

    protected boolean loadMoreEnable() {
        return false;
    }

    protected void loadData() {

    }

    /**
     * 刷新列表
     */
    protected void refreshData() {
        page = 1;
        loadData();
    }

    /**
     * 加载更多，下一页
     */
    private void loadMore() {
        page++;
        loadData();
    }

    protected void loadFinish(String msg, List<T> data) {
        if (TextUtils.isEmpty(msg)) {
//            if (page >= data.getTotalPageSize()) {
//                mAdapter.setEnableLoadMore(false);
//            }

            if (page == 1) {
                mAdapter.setNewData(data);
            } else {
                mAdapter.addData(data);
            }

            int size = data.size();
            if (size < DBUtil.LIMIT) {
                //第一页如果不够一页就不显示没有更多数据布局
                mAdapter.loadMoreEnd(page == 1);
            } else {
                mAdapter.loadMoreComplete();
            }

        } else {
            showLongToast(msg);
            mAdapter.loadMoreFail();
        }
    }

    protected void loadFinish(List<T> data) {
        loadFinish("", data);
    }


    protected void loadFinish(String errorMsg) {
        loadFinish(errorMsg, null);
    }

//    protected void loadPageFinish(boolean success, PageList<T> data) {
//        if (success) {
//            if (page >= data.getTotalPageSize()) {
//                mAdapter.setEnableLoadMore(false);
//            }
//
//            if (page == 1) {
//                mAdapter.setNewData(data.list);
//            } else {
//                mAdapter.addData(data.list);
//            }
//
//            int size = data.list == null ? 0 : data.list.size();
//            if (size < HttpUtil.LIMIT) {
//                //第一页如果不够一页就不显示没有更多数据布局
//                mAdapter.loadMoreEnd(page == 1);
//            } else {
//                mAdapter.loadMoreComplete();
//            }
//        } else {
//            mAdapter.loadMoreFail();
//        }
//    }

    protected BaseQuickAdapter<T, BaseViewHolder> getmAdapter() {
        return mAdapter == null ? new RecyclerAdapter() : mAdapter;
    }

    protected void setDefaultDivider() {
        divider = new RecycleViewDivider(mContext, DividerItemDecoration.HORIZONTAL, getResources().getDimensionPixelSize(R.dimen.dp_05),
                ContextCompat.getColor(mContext, R.color.colorLineDivider));
    }


    protected RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager == null ? new LinearLayoutManager(mContext) : layoutManager;
    }
//    @Override
//    public void onLoadMoreRequested() {
//        delayPost(new Runnable() {
//            @Override
//            public void run() {
//                loadList();
//            }
//        });
//    }

//    @Override
//    public void onItemChildClick(BaseQuickAdapter mAdapter, View view, int position) {
//        onChildViewClick(mAdapter, view, position);
//    }
//
//    @Override
//    public void onItemClick(View view, int position) {
//        onViewClick(view, position);
//    }

//    public abstract int pageSize();
//
//    public abstract void loadList();

    public abstract void convertItem(BaseViewHolder holder, T item);

    public abstract int bindItemView();

    public abstract RecyclerView bindRecyclerView();


    public class RecyclerAdapter extends BaseQuickAdapter<T, BaseViewHolder> {
        public RecyclerAdapter() {
            super(bindItemView());
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            convertItem(helper, item);
        }
    }

}
