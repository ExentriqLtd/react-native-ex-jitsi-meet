package com.reactnativejitsimeet;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.UiThreadUtil;

import java.util.HashMap;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

public class MyJitsiMeetActivity extends JitsiMeetActivity {

    public static void launch2(Context context, JitsiMeetConferenceOptions options, Callback _callback) {
        Intent intent = new Intent(context, MyJitsiMeetActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_custom_jitsi);
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jitsi_menu, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.addmembers) {
            return true;
        } else if (itemId == R.id.chat) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

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
