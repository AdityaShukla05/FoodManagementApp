package com.example.foodmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.foodmanagement.adapters.CompleteListener;

public class DialogMarkFinish extends DialogFragment {

    private ImageButton mBtnClose;
    private Button mBtnCompleted;
    private Button mBtnWatchVideo;


    private View.OnClickListener mBtnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_completed:
                    markAsComplete();
                    break;
                case R.id.btn_watchrecp:
                    watchviedo();
                    break;
            }
            dismiss();
        }
    };

    private void watchviedo() {
        Bundle arguments = getArguments();
        if(mListener!=null && arguments != null){
            int positionnWV = arguments.getInt("POSITION");
            mListener.onComplete(positionnWV+10000);
        }
    }

    private CompleteListener mListener;

    private void markAsComplete() {
        Bundle arguments = getArguments();
        if(mListener!=null && arguments != null){
            int position = arguments.getInt("POSITION");
            mListener.onComplete(position);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.ThemeDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.dialog_mark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_closee);
        mBtnCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnWatchVideo = (Button) view.findViewById(R.id.btn_watchrecp);
        mBtnClose.setOnClickListener(mBtnClickListener);
        mBtnCompleted.setOnClickListener(mBtnClickListener);
        mBtnWatchVideo.setOnClickListener(mBtnClickListener);

    }

    public void setCompleteistener(CompleteListener mCompleteListener) {
        mListener = mCompleteListener;
    }

}
