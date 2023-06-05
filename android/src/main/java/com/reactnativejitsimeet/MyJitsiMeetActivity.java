package com.reactnativejitsimeet;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UiThreadUtil;

import java.util.HashMap;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

public class MyJitsiMeetActivity extends JitsiMeetActivity {

    public static void launch2(Context context, JitsiMeetConferenceOptions options, Callback _callback) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, MyJitsiMeetActivity.class);
                intent.setAction("org.jitsi.meet.CONFERENCE");
                intent.putExtra("JitsiMeetConferenceOptions", options);
                if (!(context instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    @Override
    protected void onConferenceJoined(HashMap<String, Object> extraData) {
        super.onConferenceJoined(extraData);
        Log.d("MyJitsiMeetActivity:::onConferenceJoined", "start");
    }

    @Override
    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
        super.onConferenceTerminated(extraData);
        Log.d("MyJitsiMeetActivity:::onConferenceTerminated", "start");
        JitsiMeetModule jitsiMeetModule = JitsiMeetModule.getCurrentInstance();
        if (jitsiMeetModule != null) {
            Callback callbackMain = jitsiMeetModule.getCallBackMain();
            callbackMain.invoke("ExJitsiMeetActivity", "CONFERENCE_TERMINATED");
        }
    }

    @Override
    protected void onConferenceWillJoin(HashMap<String, Object> extraData) {
        super.onConferenceWillJoin(extraData);
        Log.d("MyJitsiMeetActivity:::onConferenceWillJoin", "start");
    }
}
