// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.app.NotificationManager;
import android.os.IBinder;
import android.app.Notification;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.IntentFilter;
import android.content.ComponentName;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import android.os.Build;
import java.io.Serializable;
import android.os.Bundle;
import android.content.Intent;
import java.util.HashMap;
import android.content.Context;
import android.app.Service;

public class JitsiMeetOngoingConferenceService extends Service implements OngoingConferenceTracker.OngoingConferenceListener
{
    private static final String TAG;
    private static final String EXTRA_DATA_KEY = "extraDataKey";
    private static final String EXTRA_DATA_BUNDLE_KEY = "extraDataBundleKey";
    private static final String IS_AUDIO_MUTED_KEY = "isAudioMuted";
    private final BroadcastReceiver broadcastReceiver;
    private boolean isAudioMuted;
    
    public JitsiMeetOngoingConferenceService() {
        this.broadcastReceiver = new BroadcastReceiver();
    }
    
    public static void launch(final Context context, final HashMap<String, Object> extraData) {
        OngoingNotification.createOngoingConferenceNotificationChannel();
        final Intent intent = new Intent(context, (Class)JitsiMeetOngoingConferenceService.class);
        final Bundle extraDataBundle = new Bundle();
        extraDataBundle.putSerializable("extraDataKey", (Serializable)extraData);
        intent.putExtra("extraDataBundleKey", extraDataBundle);
        ComponentName componentName;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                componentName = context.startForegroundService(intent);
            }
            else {
                componentName = context.startService(intent);
            }
        }
        catch (RuntimeException e) {
            JitsiMeetLogger.w(JitsiMeetOngoingConferenceService.TAG + " Ongoing conference service not started", e);
            return;
        }
        if (componentName == null) {
            JitsiMeetLogger.w(JitsiMeetOngoingConferenceService.TAG + " Ongoing conference service not started", new Object[0]);
        }
    }
    
    public static void abort(final Context context) {
        final Intent intent = new Intent(context, (Class)JitsiMeetOngoingConferenceService.class);
        context.stopService(intent);
    }
    
    public void onCreate() {
        super.onCreate();
        final Notification notification = OngoingNotification.buildOngoingConferenceNotification(this.isAudioMuted);
        if (notification == null) {
            this.stopSelf();
            JitsiMeetLogger.w(JitsiMeetOngoingConferenceService.TAG + " Couldn't start service, notification is null", new Object[0]);
        }
        else {
            this.startForeground(OngoingNotification.NOTIFICATION_ID, notification);
            JitsiMeetLogger.i(JitsiMeetOngoingConferenceService.TAG + " Service started", new Object[0]);
        }
        OngoingConferenceTracker.getInstance().addListener(this);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver((android.content.BroadcastReceiver)this.broadcastReceiver, intentFilter);
    }
    
    public void onDestroy() {
        OngoingConferenceTracker.getInstance().removeListener(this);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver((android.content.BroadcastReceiver)this.broadcastReceiver);
        super.onDestroy();
    }
    
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final Boolean isAudioMuted = this.tryParseIsAudioMuted(intent);
        if (isAudioMuted != null) {
            this.isAudioMuted = Boolean.parseBoolean(intent.getStringExtra("muted"));
            final Notification notification = OngoingNotification.buildOngoingConferenceNotification(isAudioMuted);
            if (notification == null) {
                this.stopSelf();
                JitsiMeetLogger.w(JitsiMeetOngoingConferenceService.TAG + " Couldn't start service, notification is null", new Object[0]);
            }
            else {
                final NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(OngoingNotification.NOTIFICATION_ID, notification);
            }
        }
        final String actionName = intent.getAction();
        final Action action = Action.fromName(actionName);
        if (action != null) {
            switch (action) {
                case UNMUTE:
                case MUTE: {
                    final Intent muteBroadcastIntent = BroadcastIntentHelper.buildSetAudioMutedIntent(action == Action.MUTE);
                    LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(muteBroadcastIntent);
                    break;
                }
                case HANGUP: {
                    JitsiMeetLogger.i(JitsiMeetOngoingConferenceService.TAG + " Hangup requested", new Object[0]);
                    final Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
                    LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
                    this.stopSelf();
                    break;
                }
                default: {
                    JitsiMeetLogger.w(JitsiMeetOngoingConferenceService.TAG + " Unknown action received: " + action, new Object[0]);
                    break;
                }
            }
        }
        return Service.START_NOT_STICKY;
    }
    
    public void onCurrentConferenceChanged(final String conferenceUrl) {
        if (conferenceUrl == null) {
            this.stopSelf();
            OngoingNotification.resetStartingtime();
            JitsiMeetLogger.i(JitsiMeetOngoingConferenceService.TAG + "Service stopped", new Object[0]);
        }
    }
    
    private Boolean tryParseIsAudioMuted(final Intent intent) {
        try {
            final HashMap<String, Object> extraData = (HashMap<String, Object>)intent.getBundleExtra("extraDataBundleKey").getSerializable("extraDataKey");
            return Boolean.parseBoolean((String) extraData.get("isAudioMuted"));
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    static {
        TAG = JitsiMeetOngoingConferenceService.class.getSimpleName();
    }
    
    public enum Action
    {
        HANGUP(JitsiMeetOngoingConferenceService.TAG + ":HANGUP"), 
        MUTE(JitsiMeetOngoingConferenceService.TAG + ":MUTE"), 
        UNMUTE(JitsiMeetOngoingConferenceService.TAG + ":UNMUTE");
        
        private final String name;
        
        private Action(final String name) {
            this.name = name;
        }
        
        public static Action fromName(final String name) {
            for (final Action action : values()) {
                if (action.name.equalsIgnoreCase(name)) {
                    return action;
                }
            }
            return null;
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    private class BroadcastReceiver extends android.content.BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            JitsiMeetOngoingConferenceService.this.isAudioMuted = Boolean.parseBoolean(intent.getStringExtra("muted"));
            final Notification notification = OngoingNotification.buildOngoingConferenceNotification(JitsiMeetOngoingConferenceService.this.isAudioMuted);
            if (notification == null) {
                JitsiMeetOngoingConferenceService.this.stopSelf();
                JitsiMeetLogger.w(JitsiMeetOngoingConferenceService.TAG + " Couldn't update service, notification is null", new Object[0]);
            }
            else {
                final NotificationManager notificationManager = (NotificationManager)JitsiMeetOngoingConferenceService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(OngoingNotification.NOTIFICATION_ID, notification);
                JitsiMeetLogger.i(JitsiMeetOngoingConferenceService.TAG + " audio muted changed", new Object[0]);
            }
        }
    }
}
