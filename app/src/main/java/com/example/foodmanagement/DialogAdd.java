package com.example.foodmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.foodmanagement.beans.Drop;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static io.realm.Realm.getApplicationContext;

public class DialogAdd extends DialogFragment {

    private ImageButton mBtnClose;
    private EditText mInputWhat;
    private DatePicker mInputWhen;
    private Button mBtnAdd;

    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            String name = mInputWhat.getText().toString();
            switch (id){
                case R.id.btn_add_itt:
                    if (name.isEmpty()){
//                        Toast.makeText(getApplicationContext(), "Enter Valid Item Name", Toast.LENGTH_SHORT).show();
                            mInputWhat.setError("Cannot be empty");
                        break;
                    }
                    else {
                        addAction();
                        dismiss();
                        break;
                    }
                case R.id.btn_closee:
                    dismiss();
                    break;
            }
        }
    };

    private void addAction() {
        String what = mInputWhat.getText().toString();
        String date = mInputWhen.getDayOfMonth()+"/"+mInputWhen.getMonth()+"/"+mInputWhen.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,mInputWhen.getDayOfMonth());
        calendar.set(Calendar.MONTH,mInputWhen.getMonth());
        calendar.set(Calendar.YEAR, mInputWhen.getYear());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long now = System.currentTimeMillis();
        Realm realm = Realm.getDefaultInstance();
        Drop drop = new Drop(what, now, calendar.getTimeInMillis(), false);
        realm.beginTransaction();
        realm.copyToRealm(drop);
        realm.commitTransaction();
        realm.close();
    }

    public DialogAdd() {
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL,R.style.ThemeDialog);
//    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL,R.style.ThemeDialog);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputWhat = (EditText)view.findViewById(R.id.et_item);
        mBtnClose = (ImageButton)view.findViewById(R.id.btn_closee);
        mInputWhen = (DatePicker)view.findViewById(R.id.fmv_date);
        mBtnAdd = (Button) view.findViewById(R.id.btn_add_itt);
        mBtnClose.setOnClickListener(mBtnClickListener);
        mBtnAdd.setOnClickListener(mBtnClickListener);
    }
}
