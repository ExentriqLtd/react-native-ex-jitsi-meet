// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import java.util.Random;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.Notification;
import android.content.Context;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import android.os.Build;

class OngoingNotification
{
    private static final String TAG;
    static final int NOTIFICATION_ID;
    private static long startingTime;
    
    static void createOngoingConferenceNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        final Context context = (Context)ReactInstanceManagerHolder.getCurrentActivity();
        if (context == null) {
            JitsiMeetLogger.w(OngoingNotification.TAG + " Cannot create notification channel: no current context", new Object[0]);
            return;
        }
        final NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
        NotificationChannel channel = notificationManager.getNotificationChannel("JitsiOngoingConferenceChannel");
        if (channel != null) {
            return;
        }
        channel = new NotificationChannel("JitsiOngoingConferenceChannel", (CharSequence)context.getString(R.string.ongoing_notification_action_unmute), 3);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }
    
    static Notification buildOngoingConferenceNotification(final boolean isMuted) {
        final Context context = (Context)ReactInstanceManagerHolder.getCurrentActivity();
        if (context == null) {
            JitsiMeetLogger.w(OngoingNotification.TAG + " Cannot create notification: no current context", new Object[0]);
            return null;
        }
        final Intent notificationIntent = new Intent(context, (Class)context.getClass());
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 67108864);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "JitsiOngoingConferenceChannel");
        if (OngoingNotification.startingTime == 0L) {
            OngoingNotification.startingTime = System.currentTimeMillis();
        }
        builder.setCategory("call").setContentTitle((CharSequence)context.getString(R.string.ongoing_notification_title)).setContentText((CharSequence)context.getString(R.string.ongoing_notification_text)).setPriority(0).setContentIntent(pendingIntent).setOngoing(true).setWhen(OngoingNotification.startingTime).setUsesChronometer(true).setAutoCancel(false).setVisibility(1).setOnlyAlertOnce(true).setSmallIcon(context.getResources().getIdentifier("ic_notification", "drawable", context.getPackageName()));
        final NotificationCompat.Action hangupAction = createAction(context, JitsiMeetOngoingConferenceService.Action.HANGUP, R.string.ongoing_notification_action_hang_up);
        final JitsiMeetOngoingConferenceService.Action toggleAudioAction = isMuted ? JitsiMeetOngoingConferenceService.Action.UNMUTE : JitsiMeetOngoingConferenceService.Action.MUTE;
        final int toggleAudioTitle = isMuted ? R.string.ongoing_notification_action_unmute : R.string.ongoing_notification_action_mute;
        final NotificationCompat.Action audioAction = createAction(context, toggleAudioAction, toggleAudioTitle);
        builder.addAction(hangupAction);
        builder.addAction(audioAction);
        return builder.build();
    }
    
    static void resetStartingtime() {
        OngoingNotification.startingTime = 0L;
    }
    
    private static NotificationCompat.Action createAction(final Context context, final JitsiMeetOngoingConferenceService.Action action, @StringRes final int titleId) {
        final Intent intent = new Intent(context, (Class)JitsiMeetOngoingConferenceService.class);
        intent.setAction(action.getName());
        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 67108864);
        final String title = context.getString(titleId);
        return new NotificationCompat.Action(0, (CharSequence)title, pendingIntent);
    }
    
    static {
        TAG = OngoingNotification.class.getSimpleName();
        NOTIFICATION_ID = new Random().nextInt(99999) + 10000;
        OngoingNotification.startingTime = 0L;
    }
}
