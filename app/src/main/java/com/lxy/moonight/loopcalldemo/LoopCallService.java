package com.lxy.moonight.loopcalldemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by MooNight on 2016/12/12.
 */

public class LoopCallService extends Service {

    private ArrayList<String> mPhoneNumList = new ArrayList<>();
    private int i = 0;
    private TelephonyManager tm = null;
    private boolean mIsLoopCall = false;   //确保只有真正调用startPhoneCall时才会循环拨打，否则当普通的电话挂断之后会继续调用startPhoneCall

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isLastCallSucceed() && mIsLoopCall) {
                startPhoneCall();
            } else {
                mIsLoopCall = false;
            }
        }
    };

    class MyBinder extends Binder {
        public void call() {
            mIsLoopCall = true;
            startPhoneCall();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        mPhoneNumList.add("xxxxxxxxxxx");   //TODO 本demo需要自己添加测试号码
        mPhoneNumList.add("xxxxxxxxxxx");
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();

    }

    private void startPhoneCall() {
        Intent intent_call = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + mPhoneNumList.get(i)));
        intent_call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i = (i + 1) % mPhoneNumList.size();
        startActivity(intent_call);
    }

    private boolean isLastCallSucceed() {
        String[] arrayOfString = {"number", "duration"};
        Cursor localCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, arrayOfString, null, null, "date DESC");
        return (localCursor.moveToFirst()) && (localCursor.getInt(1) > 0);
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("state", state + "");
                    mHandler.sendEmptyMessageDelayed(0, 3000);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
            }
        }
    }
}
