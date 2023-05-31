// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import org.devio.rn.splashscreen.SplashScreen;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Context;
import com.facebook.react.ReactInstanceManager;
import android.os.Bundle;

public class JitsiMeet
{
    private static JitsiMeetConferenceOptions defaultConferenceOptions;
    
    public static JitsiMeetConferenceOptions getDefaultConferenceOptions() {
        return JitsiMeet.defaultConferenceOptions;
    }
    
    public static void setDefaultConferenceOptions(final JitsiMeetConferenceOptions options) {
        if (options != null && options.getRoom() != null) {
            throw new RuntimeException("'room' must be null in the default conference options");
        }
        JitsiMeet.defaultConferenceOptions = options;
    }
    
    public static String getCurrentConference() {
        return OngoingConferenceTracker.getInstance().getCurrentConference();
    }
    
    static Bundle getDefaultProps() {
        if (JitsiMeet.defaultConferenceOptions != null) {
            return JitsiMeet.defaultConferenceOptions.asProps();
        }
        return new Bundle();
    }
    
    public static void showDevOptions() {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.showDevOptionsDialog();
        }
    }
    
    public static boolean isCrashReportingDisabled(final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences("jitsi-default-preferences", 0);
        final String value = preferences.getString("isCrashReportingDisabled", "");
        return Boolean.parseBoolean(value);
    }
    
    public static void showSplashScreen(final Activity activity) {
        try {
            SplashScreen.show(activity);
        }
        catch (Exception e) {
            JitsiMeetLogger.e(e, "Failed to show splash screen", new Object[0]);
        }
    }
}
