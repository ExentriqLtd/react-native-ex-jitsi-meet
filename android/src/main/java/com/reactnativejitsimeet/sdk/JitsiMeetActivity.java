// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.content.IntentFilter;
import android.annotation.SuppressLint;
import com.facebook.react.modules.core.PermissionListener;
import java.util.HashMap;
import android.net.Uri;

import com.reactnativejitsimeet.R;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Build;
import android.os.Bundle;
import android.content.res.Configuration;
import android.app.Activity;
import android.os.Parcelable;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import androidx.appcompat.app.AppCompatActivity;

import org.jitsi.meet.sdk.JitsiMeetActivityInterface;

public class JitsiMeetActivity extends AppCompatActivity implements JitsiMeetActivityInterface
{
    protected static final String TAG;
    private static final String ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE";
    private static final String JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions";
    private final BroadcastReceiver broadcastReceiver;
    private JitsiMeetView jitsiView;
    
    public JitsiMeetActivity() {
        this.broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                JitsiMeetActivity.this.onBroadcastReceived(intent);
            }
        };
    }
    
    public static void launch(final Context context, final JitsiMeetConferenceOptions options) {
        final Intent intent = new Intent(context, (Class)JitsiMeetActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", (Parcelable)options);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
    
    public static void launch(final Context context, final String url) {
        final JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder().setRoom(url).build();
        launch(context, options);
    }
    
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final Intent intent = new Intent("onConfigurationChanged");
        intent.putExtra("newConfig", (Parcelable)newConfig);
        this.sendBroadcast(intent);
    }
    
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_jitsi_meet);
        this.jitsiView = (JitsiMeetView)this.findViewById(R.id.jitsiView);
        this.registerForBroadcastMessages();
        if (!this.extraInitialize()) {
            this.initialize();
        }
    }
    
    public void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume((Activity)this);
    }
    
    public void onStop() {
        JitsiMeetActivityDelegate.onHostPause((Activity)this);
        super.onStop();
    }
    
    public void onDestroy() {
        this.leave();
        this.jitsiView = null;
        if (AudioModeModule.useConnectionService()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ConnectionService.abortConnections();
            }
        }
        JitsiMeetOngoingConferenceService.abort((Context)this);
        LocalBroadcastManager.getInstance((Context)this).unregisterReceiver(this.broadcastReceiver);
        JitsiMeetActivityDelegate.onHostDestroy((Activity)this);
        super.onDestroy();
    }
    
    public void finish() {
        this.leave();
        super.finish();
    }
    
    protected JitsiMeetView getJitsiView() {
        return this.jitsiView;
    }
    
    public void join(@Nullable final String url) {
        final JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder().setRoom(url).build();
        this.join(options);
    }
    
    public void join(final JitsiMeetConferenceOptions options) {
        if (this.jitsiView != null) {
            this.jitsiView.join(options);
        }
        else {
            JitsiMeetLogger.w("Cannot join, view is null", new Object[0]);
        }
    }
    
    protected void leave() {
        if (this.jitsiView != null) {
            this.jitsiView.abort();
        }
        else {
            JitsiMeetLogger.w("Cannot leave, view is null", new Object[0]);
        }
    }
    
    @Nullable
    private JitsiMeetConferenceOptions getConferenceOptions(final Intent intent) {
        final String action = intent.getAction();
        if ("android.intent.action.VIEW".equals(action)) {
            final Uri uri = intent.getData();
            if (uri != null) {
                return new JitsiMeetConferenceOptions.Builder().setRoom(uri.toString()).build();
            }
        }
        else if ("org.jitsi.meet.CONFERENCE".equals(action)) {
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
    
    protected void onConferenceJoined(final HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference joined: " + extraData, new Object[0]);
        JitsiMeetOngoingConferenceService.launch((Context)this, extraData);
    }
    
    protected void onConferenceTerminated(final HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference terminated: " + extraData, new Object[0]);
    }
    
    protected void onConferenceWillJoin(final HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference will join: " + extraData, new Object[0]);
    }
    
    protected void onParticipantJoined(final HashMap<String, Object> extraData) {
        try {
            JitsiMeetLogger.i("Participant joined: ", extraData);
        }
        catch (Exception e) {
            JitsiMeetLogger.w("Invalid participant joined extraData", e);
        }
    }
    
    protected void onParticipantLeft(final HashMap<String, Object> extraData) {
        try {
            JitsiMeetLogger.i("Participant left: ", extraData);
        }
        catch (Exception e) {
            JitsiMeetLogger.w("Invalid participant left extraData", e);
        }
    }
    
    protected void onReadyToClose() {
        JitsiMeetLogger.i("SDK is ready to close", new Object[0]);
        this.finish();
    }
    
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult((Activity)this, requestCode, resultCode, data);
    }
    
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }
    
    public void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        final JitsiMeetConferenceOptions options;
        if ((options = this.getConferenceOptions(intent)) != null) {
            this.join(options);
            return;
        }
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }
    
    protected void onUserLeaveHint() {
        if (this.jitsiView != null) {
            this.jitsiView.enterPictureInPicture();
        }
    }
    
    public void requestPermissions(final String[] permissions, final int requestCode, final PermissionListener listener) {
        JitsiMeetActivityDelegate.requestPermissions((Activity)this, permissions, requestCode, listener);
    }
    
    @SuppressLint({ "MissingSuperCall" })
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    private void registerForBroadcastMessages() {
        final IntentFilter intentFilter = new IntentFilter();
        for (final BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }
        LocalBroadcastManager.getInstance((Context)this).registerReceiver(this.broadcastReceiver, intentFilter);
    }
    
    private void onBroadcastReceived(final Intent intent) {
        if (intent != null) {
            final BroadcastEvent event = new BroadcastEvent(intent);
            switch (event.getType()) {
                case CONFERENCE_JOINED: {
                    this.onConferenceJoined(event.getData());
                    break;
                }
                case CONFERENCE_WILL_JOIN: {
                    this.onConferenceWillJoin(event.getData());
                    break;
                }
                case CONFERENCE_TERMINATED: {
                    this.onConferenceTerminated(event.getData());
                    break;
                }
                case PARTICIPANT_JOINED: {
                    this.onParticipantJoined(event.getData());
                    break;
                }
                case PARTICIPANT_LEFT: {
                    this.onParticipantLeft(event.getData());
                    break;
                }
                case READY_TO_CLOSE: {
                    this.onReadyToClose();
                    break;
                }
            }
        }
    }
    
    static {
        TAG = JitsiMeetActivity.class.getSimpleName();
    }
}
