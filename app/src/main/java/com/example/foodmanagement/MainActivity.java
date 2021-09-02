package com.example.foodmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toolbar;
//import android.widget.Toolbar;

import com.example.foodmanagement.adapters.AdapterDrops;
import com.example.foodmanagement.adapters.AddListener;
import com.example.foodmanagement.adapters.CompleteListener;
import com.example.foodmanagement.adapters.CompleteListener2;
import com.example.foodmanagement.adapters.Divider;
import com.example.foodmanagement.adapters.Filter;
import com.example.foodmanagement.adapters.MarkListener;
import com.example.foodmanagement.adapters.ResetListener;
import com.example.foodmanagement.adapters.SimpleTouchCallback;
import com.example.foodmanagement.beans.Drop;
import com.example.foodmanagement.extras.Util;
import com.example.foodmanagement.services.NotificationServiceses;
import com.example.foodmanagement.widgets.BucketRecyclerView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 4000;
    //    Toolbar mToolbar;
    Button mBtnAdd;
    Button mDBtnAdd;
    androidx.appcompat.widget.Toolbar mToolbar;
    BucketRecyclerView mRecycler;
    Realm mRealm;
    RealmResults<Drop> mResults;
    View mEmptyView;
    AdapterDrops mAdapter;

    private View.OnClickListener mBtnAddListener = (v) -> {
        showDialogAdd();
    };

    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };

    private RealmChangeListener mChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            Log.d(TAG, "onChange: was called");
            mAdapter.update(mResults);
        }
    };

    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMarkFinish(position);
        }
    };


    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            if(position<10000) {
                mAdapter.markComplete(position);
            }
            else {
                mAdapter.watchVideo(position-10000);
            }
        }
    };
    private ResetListener mResetListener = new ResetListener() {
        @Override
        public void onReset() {
            AppMyKitchen.save(MainActivity.this, Filter.LEAST_DAYS_LEFT);
            loadResults(Filter.LEAST_DAYS_LEFT);
        }
    };

    private void showDialogAdd() {
        DialogAdd dialog = new DialogAdd();
        dialog.show(getSupportFragmentManager(), "Add");
    }

    private void showDialogMarkFinish(int position) {
        DialogMarkFinish dialog = new DialogMarkFinish();
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialog.setArguments(bundle);
        dialog.setCompleteistener(mCompleteListener);
        dialog.show(getSupportFragmentManager(), "Mark");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activitymain);
        mRealm = Realm.getDefaultInstance();
        int filterOption = AppMyKitchen.load(this);
        loadResults(filterOption);
        mResults = mRealm.where(Drop.class).sort("when").findAllAsync();
        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mEmptyView = findViewById(R.id.empty_drops);
        mRecycler = (BucketRecyclerView) findViewById(R.id.rv_drops);
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.hideIfEmpty(mToolbar);
        mRecycler.showIfEmpty(mEmptyView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(manager);
        mAdapter = new AdapterDrops(this, mRealm, mResults, mAddListener, mMarkListener, mResetListener);
        mAdapter.setHasStableIds(true);
        mRecycler.setAdapter(mAdapter);
        mBtnAdd = (Button) findViewById(R.id.btn_addd);
        mDBtnAdd = (Button) findViewById(R.id.btn_adddd);
        mBtnAdd.setOnClickListener(mBtnAddListener);
        mDBtnAdd.setOnClickListener(mBtnAddListener);
        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecycler);
        Util.schedulenoti(this);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int filterOption = Filter.MOST_DAYS_LEFT;
        boolean handled = true;
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;
            case R.id.action_sort_asc:
                filterOption = Filter.LEAST_DAYS_LEFT;
                break;
            case R.id.action_none:
                filterOption = Filter.NONE;
                break;
            case R.id.action_show_finished:
                filterOption = Filter.FINISHED;
                break;
            case R.id.action_show_remaining:
                filterOption = Filter.REMAINING;
                break;
            case R.id.action_donate_food:
                redirect();
                break;
            default:
                handled = false;
                break;
        }
        loadResults(filterOption);
        AppMyKitchen.save(this,filterOption);
        return handled;
    }

    private void redirect() {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=donate+food+near+me"));
        startActivity(browse);
    }

    private void loadResults(int filterOption) {
        switch (filterOption) {
            case Filter.NONE:
                mResults = mRealm.where(Drop.class).findAllAsync();
                break;
            case Filter.FINISHED:
                mResults = mRealm.where(Drop.class).equalTo("completed", true).findAllAsync();
                break;
            case Filter.LEAST_DAYS_LEFT:
                mResults = mRealm.where(Drop.class).sort("when").findAllAsync();
                break;
            case Filter.MOST_DAYS_LEFT:
                mResults = mRealm.where(Drop.class).sort("when", Sort.DESCENDING).findAllAsync();
                break;
            case Filter.REMAINING:
                mResults = mRealm.where(Drop.class).equalTo("completed", false).findAllAsync();
                break;

        }
        mResults.addChangeListener(mChangeListener);
    }



    @Override
    protected void onStart() {
        super.onStart();
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResults.removeChangeListener(mChangeListener);
    }
}