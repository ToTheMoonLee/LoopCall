package com.lxy.moonight.loopcalldemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button mBindService;
    private Button mStartCall;
    private LoopCallService.MyBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mBindService = ((Button) findViewById(R.id.bt_bind_service));
        mStartCall = ((Button) findViewById(R.id.bt_start_call));
        mBindService.setOnClickListener(this);
        mStartCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_bind_service:
                bindServer();
                break;
            case R.id.bt_start_call:
                startCall();
                break;
            default:
                break;
        }
    }

    private void startCall() {
        mBinder.call();
    }

    private void bindServer() {
        Intent intent = new Intent(this, LoopCallService.class);

        bindService(intent, new MyConn(), BIND_AUTO_CREATE);
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (LoopCallService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
