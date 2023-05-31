package com.reactnativejitsimeet;

import android.content.IntentFilter;
import android.annotation.SuppressLint;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.modules.core.PermissionListener;
import java.util.HashMap;
import android.net.Uri;

import com.reactnativejitsimeet.R;
import com.reactnativejitsimeet.sdk.BroadcastEvent;
import com.reactnativejitsimeet.sdk.BroadcastIntentHelper;
import com.reactnativejitsimeet.sdk.JitsiMeetActivityDelegate;
import com.reactnativejitsimeet.sdk.JitsiMeetActivityInterface;
import com.reactnativejitsimeet.sdk.JitsiMeetConferenceOptions;
import com.reactnativejitsimeet.sdk.JitsiMeetOngoingConferenceService;
import com.reactnativejitsimeet.sdk.JitsiMeetView;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.content.res.Configuration;
import android.app.Activity;
import android.os.Parcelable;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


public class ExJitsiMeetActivity extends AppCompatActivity implements JitsiMeetActivityInterface {
    protected static final String TAG = ExJitsiMeetActivity.class.getSimpleName();
    private static final String ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE";
    private static final String JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions";
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ExJitsiMeetActivity.this.onBroadcastReceived(intent);
        }
    };
    private JitsiMeetView jitsiView;

    private ReactInstanceManager reactInstanceManager;

    public ExJitsiMeetActivity() {
    }

    public static void launch(Context context, JitsiMeetConferenceOptions options) {
        Intent intent = new Intent(context, ExJitsiMeetActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_jitsi_meet);
        this.jitsiView = (JitsiMeetView)this.findViewById(R.id.jitsiView);
        this.registerForBroadcastMessages();
        if (!this.extraInitialize()) {
            this.initialize();
        }

        ReactInstanceManagerBuilder reactInstanceManagerBuilder = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this);

        reactInstanceManager = reactInstanceManagerBuilder.build();

    }

    public void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    public void onStop() {
        JitsiMeetActivityDelegate.onHostPause(this);
        super.onStop();
    }

    public void onDestroy() {
        Log.d("ExJitsiMeetActivity::", "start destroy");
        this.leave();
        this.jitsiView = null;
       /* if (AudioModeModule.useConnectionService()) {
            ConnectionService.abortConnections();
        }*/

        JitsiMeetOngoingConferenceService.abort(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.broadcastReceiver);
        JitsiMeetActivityDelegate.onHostDestroy(this);
        super.onDestroy();
    }

    public void finish() {
        Log.d("ExJitsiMeetActivity::", "start finish");

        this.leave();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }

    protected JitsiMeetView getJitsiView() {
        return this.jitsiView;
    }

    public void join(@Nullable String url) {
        JitsiMeetConferenceOptions options = (new JitsiMeetConferenceOptions.Builder()).setRoom(url).build();
        this.join(options);
    }

    public void join(JitsiMeetConferenceOptions options) {
        Log.d("ExJitsiMeetActivity::", "start join");

        if (this.jitsiView != null) {
            this.jitsiView.join(options);
        } else {
            JitsiMeetLogger.w("Cannot join, view is null", new Object[0]);
        }
    }

    protected void leave() {
        Log.d("ExJitsiMeetActivity::", "start leave");

        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    @Nullable
    private JitsiMeetConferenceOptions getConferenceOptions(Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.VIEW".equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                return (new JitsiMeetConferenceOptions.Builder()).setRoom(uri.toString()).build();
            }
        } else if ("org.jitsi.meet.CONFERENCE".equals(action)) {
            return (JitsiMeetConferenceOptions)intent.getParcelableExtra("JitsiMeetConferenceOptions");
        }

        return null;
    }

    protected boolean extraInitialize() {
        return false;
    }

    protected void initialize() {
        this.join(this.getConferenceOptions(this.getIntent()));
    }

    protected void onConferenceJoined(HashMap<String, Object> extraData) {
        Log.d("ExJitsiMeetActivity::", "start onConferenceJoined");

        JitsiMeetLogger.i("Conference joined: " + extraData, new Object[0]);
        JitsiMeetOngoingConferenceService.launch(this, extraData);
    }

    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
        Log.d("ExJitsiMeetActivity::", "start onConferenceTerminated");

        JitsiMeetLogger.i("Conference terminated: " + extraData, new Object[0]);
    }

    protected void onConferenceWillJoin(HashMap<String, Object> extraData) {
        Log.d("ExJitsiMeetActivity::", "start onConferenceWillJoin");

        JitsiMeetLogger.i("Conference will join: " + extraData, new Object[0]);
    }

    protected void onParticipantJoined(HashMap<String, Object> extraData) {
        Log.d("ExJitsiMeetActivity::", "start onParticipantJoined");

        try {
            JitsiMeetLogger.i("Participant joined: ", new Object[]{extraData});
        } catch (Exception var3) {
            JitsiMeetLogger.w("Invalid participant joined extraData", new Object[]{var3});
        }

    }

    protected void onParticipantLeft(HashMap<String, Object> extraData) {
        Log.d("ExJitsiMeetActivity::", "start onParticipantLeft");
        try {
            JitsiMeetLogger.i("Participant left: ", new Object[]{extraData});
        } catch (Exception var3) {
            JitsiMeetLogger.w("Invalid participant left extraData", new Object[]{var3});
        }

    }

    protected void onReadyToClose() {
        JitsiMeetLogger.i("SDK is ready to close", new Object[0]);
        this.finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
    }

    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();

    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetConferenceOptions options;
        if ((options = this.getConferenceOptions(intent)) != null) {
            this.join(options);
        } else {
            JitsiMeetActivityDelegate.onNewIntent(intent);
        }
    }

    protected void onUserLeaveHint() {
        if (this.jitsiView != null) {
            this.jitsiView.enterPictureInPicture();
        }

    }

    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();
        BroadcastEvent.Type[] var2 = BroadcastEvent.Type.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            BroadcastEvent.Type type = var2[var4];
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiver, intentFilter);
    }

    private void onBroadcastReceived(Intent intent) {
        Log.d("ExJitsiMeetActivity::", "start onBroadcastReceived");

        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);
            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    this.onConferenceJoined(event.getData());
                    break;
                case CONFERENCE_WILL_JOIN:
                    this.onConferenceWillJoin(event.getData());
                    break;
                case CONFERENCE_TERMINATED:
                    this.onConferenceTerminated(event.getData());
                    break;
                case PARTICIPANT_JOINED:
                    this.onParticipantJoined(event.getData());
                    break;
                case PARTICIPANT_LEFT:
                    this.onParticipantLeft(event.getData());
                    break;
                case READY_TO_CLOSE:
                    this.onReadyToClose();
            }
        }

    }
}
