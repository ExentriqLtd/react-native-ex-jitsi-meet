package com.reactnativejitsimeet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.PermissionListener;
import java.util.HashMap;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastEvent.Type;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.R.id;
import org.jitsi.meet.sdk.R.layout;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class ExJitsiMeetActivity extends AppCompatActivity implements JitsiMeetActivityInterface {
    protected static final String TAG = JitsiMeetActivity.class.getSimpleName();
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
        Intent intent = new Intent(context, JitsiMeetActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        if (!(context instanceof Activity)) {
            intent.setFlags(268435456);
        }

        context.startActivity(intent);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_jitsi_meet);
        this.jitsiView = (JitsiMeetView)this.findViewById(id.jitsiView);
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
        if (this.jitsiView != null) {
            this.jitsiView.join(options);
        } else {
            JitsiMeetLogger.w("Cannot join, view is null", new Object[0]);
        }
    }

    protected void leave() {
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
        JitsiMeetLogger.i("Conference joined: " + extraData, new Object[0]);
        JitsiMeetOngoingConferenceService.launch(this, extraData);
    }

    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference terminated: " + extraData, new Object[0]);
    }

    protected void onConferenceWillJoin(HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference will join: " + extraData, new Object[0]);
    }

    protected void onParticipantJoined(HashMap<String, Object> extraData) {
        try {
            JitsiMeetLogger.i("Participant joined: ", new Object[]{extraData});
        } catch (Exception var3) {
            JitsiMeetLogger.w("Invalid participant joined extraData", new Object[]{var3});
        }

    }

    protected void onParticipantLeft(HashMap<String, Object> extraData) {
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
        BroadcastEvent.Type[] var2 = Type.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            BroadcastEvent.Type type = var2[var4];
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiver, intentFilter);
    }

    private void onBroadcastReceived(Intent intent) {
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
