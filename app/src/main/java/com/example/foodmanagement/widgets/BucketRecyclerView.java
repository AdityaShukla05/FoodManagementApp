package com.example.foodmanagement.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodmanagement.extras.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BucketRecyclerView extends RecyclerView {

    private List<View> mNonEmptyViews = Collections.emptyList();
    private List<View> mEmptyViews = Collections.emptyList();
    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }

        @Override
        public void onStateRestorationPolicyChanged() {
            toggleViews();
        }
    };

    private void toggleViews() {
        if(getAdapter()!=null && !mEmptyViews.isEmpty() && !mNonEmptyViews.isEmpty()){
            if(getAdapter().getItemCount()==0){
                Util.showViews(mEmptyViews);
                setVisibility(View.GONE);
                Util.hideViews(mNonEmptyViews);
            }
            else{
                Util.showViews(mNonEmptyViews);
                setVisibility(View.VISIBLE);

                Util.hideViews(mEmptyViews);
            }
        }
    }

    public BucketRecyclerView(Context context) {
        super(context);
    }

    public BucketRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(Adapter adapter){
        super.setAdapter(adapter);
        if(adapter!=null){
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    public void hideIfEmpty(View ...views) {
        mNonEmptyViews = Arrays.asList(views);

    }

    public void showIfEmpty(View ... emptyViews) {
        mEmptyViews = Arrays.asList(emptyViews);
    }
}
