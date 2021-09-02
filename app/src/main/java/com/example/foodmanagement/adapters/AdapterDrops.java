package com.example.foodmanagement.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodmanagement.AppMyKitchen;
import com.example.foodmanagement.MainActivity;
import com.example.foodmanagement.R;
import com.example.foodmanagement.beans.Drop;
import com.example.foodmanagement.extras.Util;

import org.w3c.dom.Text;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterDrops extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {

    public static final int COUNT_FOOTER =1;
    public static final int COUNT_NO_ITEMS =1;
    public static final int ITEM = 0;
    public static final int FOOTER = 2;
    public static final int NO_ITEM = 1;
    private MarkListener mMarkListener;
    private final ResetListener mResetListener;

    private LayoutInflater mInflater;
    private Realm mRealm;
    private RealmResults<Drop> mResults;
    private AddListener mAddListener;
    private Context mContext;
    private String g = null;
    private int mFilterOption;


    public AdapterDrops(Context context, Realm realm, RealmResults<Drop> results, AddListener listener, MarkListener markListener, ResetListener resetListener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        update(results);
        mRealm = realm;
        mAddListener = listener;
        mMarkListener = markListener;
        mResetListener = resetListener;
//        mFilterOption = AppMyKitchen.load(context);
    }

    public void update(RealmResults<Drop> results) {
        mResults = results;
        mFilterOption = AppMyKitchen.load(mContext);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (!mResults.isEmpty()){
            if (position<mResults.size()){
                return ITEM;
            }
            else {
                return FOOTER;
            }
        }
        else {
            if(mFilterOption == Filter.FINISHED|| mFilterOption == Filter.REMAINING){
                if (position==0){
                    return NO_ITEM;
                }
                else {
                    return 0;
                }
//                else {
//                    return FOOTER;
//                }
            }
            else {
                return ITEM;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == FOOTER) {
//            View view = mInflater.inflate(R.layout.footer, parent, false);
//            return new FooterHolder(view, mAddListener);
//        }
        if(viewType == NO_ITEM){
            View view = mInflater.inflate(R.layout.no_item, parent, false);
            return new NoItemsHolder(view);
        }
        else {
            View view = mInflater.inflate(R.layout.row_drop, parent, false);
            return new DropHolder(view, mMarkListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DropHolder) {
            Realm realm = null;
            realm = Realm.getDefaultInstance();
            RealmResults<Drop> results = realm.where(Drop.class).equalTo("completed", false).findAll();
            DropHolder dropHolder = (DropHolder) holder;
            Drop drop = mResults.get(position);
            dropHolder.setWhat(drop.getWhat());
            dropHolder.setWhen(drop.getWhen());
            dropHolder.setBackground(drop.isCompleted(), drop.getWhen());
        }

    }

    public long getItemId(int position){
        if (position<mResults.size()){
            return mResults.get(position).getAdded();
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemCount() {
        if(!mResults.isEmpty()){
            return mResults.size();
//            return mResults.size() + COUNT_FOOTER;
        }
        else {
            if (mFilterOption == Filter.LEAST_DAYS_LEFT || mFilterOption == Filter.MOST_DAYS_LEFT || mFilterOption == Filter.NONE){
                return 0;
            }
            else {
                return COUNT_NO_ITEMS;
//                return COUNT_NO_ITEMS + COUNT_FOOTER;
            }
        }
    }

    @Override
    public void onSwipe(int position) {
        if (position < mResults.size()) {
            mRealm.beginTransaction();
            mResults.get(position).deleteFromRealm();
            mRealm.commitTransaction();
            notifyItemChanged(position);
        }
        resetFilterIfEmpty();
    }

    private void resetFilterIfEmpty() {
        if(mResults.isEmpty() && (mFilterOption == Filter.FINISHED || mFilterOption == Filter.REMAINING)){
            mResetListener.onReset();
        }
    }

    public void markComplete(int position) {
        //todo youtube link created here video 81
        mRealm.beginTransaction();
        mResults.get(position).setCompleted(true);
        mResults.get(position).setWhen(System.currentTimeMillis()+System.currentTimeMillis());
        Toast.makeText(mContext.getApplicationContext(), "Hurray!", Toast.LENGTH_LONG).show();
        // String m = mResults.get(position).getWhat();
        mRealm.commitTransaction();
        notifyItemChanged(position);
    }

    public void watchVideo(int i) {
        mRealm.beginTransaction();
        String m = mResults.get(i).getWhat();
        mRealm.commitTransaction();
        Toast.makeText(mContext, "Taking you to youtube!", Toast.LENGTH_LONG).show();
        g = "https://www.youtube.com/results?search_query=" + m + " recipe";
        Intent video = new Intent(Intent.ACTION_VIEW, Uri.parse(g));
        mContext.startActivity(video);
    }

    public static class DropHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextWhat;
        TextView mTextWhen;
        MarkListener mMarkListener;
        Context mContext;
        View mItemView;

        public DropHolder(View itemView, MarkListener listener) {
            super(itemView);
            mItemView = itemView;
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            mTextWhat = (TextView) itemView.findViewById(R.id.tv_what);
            mTextWhen = (TextView) itemView.findViewById(R.id.tv_when);
            mMarkListener = listener;
        }

        public void setWhat(String what) {
            mTextWhat.setText(what);
        }

        @Override
        public void onClick(View v) {
            //TODO check this if error then use getBindingAdapterPosition()
            mMarkListener.onMark(getAdapterPosition());
        }

        public void setBackground(boolean completed, long when) {
            Drawable drawable;
            long now = System.currentTimeMillis()-90000000;
            if(now > when ){
                drawable = ContextCompat.getDrawable(mContext, R.color.red);
            }
            else {
                if (completed) {
                    drawable = ContextCompat.getDrawable(mContext, R.color.bg_item_finished);
                } else {
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_row_drop);
                }
            }
            Util.setBackground(mItemView,drawable);
        }

        public void setWhen(long when) {
            mTextWhen.setText(DateUtils.getRelativeTimeSpanString(when,System.currentTimeMillis(),DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL));
        }


    }
    public static class NoItemsHolder extends RecyclerView.ViewHolder{
        public NoItemsHolder(View itemView){
            super(itemView);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button mBtnAdd;
        AddListener mListener;

        public FooterHolder(View itemView) {
            super(itemView);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_footer);
            mBtnAdd.setOnClickListener(this);
        }

        public FooterHolder(View itemView, AddListener listener) {
            super(itemView);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_footer);
            mBtnAdd.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.add();
        }
    }
}
