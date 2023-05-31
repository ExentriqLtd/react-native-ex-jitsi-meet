// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import com.facebook.react.ReactInstanceManager;
import android.content.Intent;
import android.app.Activity;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;

public class JitsiMeetActivityDelegate
{
    private static PermissionListener permissionListener;
    private static Callback permissionsCallback;
    
    static boolean arePermissionsBeingRequested() {
        return JitsiMeetActivityDelegate.permissionListener != null;
    }
    
    public static void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent data) {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onActivityResult(activity, requestCode, resultCode, data);
        }
    }
    
    public static void onBackPressed() {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onBackPressed();
        }
    }
    
    public static void onHostDestroy(final Activity activity) {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostDestroy(activity);
        }
    }
    
    public static void onHostPause(final Activity activity) {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            try {
                reactInstanceManager.onHostPause(activity);
            }
            catch (AssertionError e) {
                JitsiMeetLogger.e(e, "Error running onHostPause, ignoring", new Object[0]);
            }
        }
    }
    
    public static void onHostResume(final Activity activity) {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostResume(activity, (DefaultHardwareBackBtnHandler)new DefaultHardwareBackBtnHandlerImpl(activity));
        }
        if (JitsiMeetActivityDelegate.permissionsCallback != null) {
            JitsiMeetActivityDelegate.permissionsCallback.invoke(new Object[0]);
            JitsiMeetActivityDelegate.permissionsCallback = null;
        }
    }
    
    public static void onNewIntent(final Intent intent) {
        final ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onNewIntent(intent);
        }
    }
    
    public static void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        JitsiMeetActivityDelegate.permissionsCallback = (Callback)new Callback() {
            public void invoke(final Object... args) {
                if (JitsiMeetActivityDelegate.permissionListener != null && JitsiMeetActivityDelegate.permissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    JitsiMeetActivityDelegate.permissionListener = null;
                }
            }
        };
    }
    
    public static void requestPermissions(final Activity activity, final String[] permissions, final int requestCode, final PermissionListener listener) {
        JitsiMeetActivityDelegate.permissionListener = listener;
        try {
            activity.requestPermissions(permissions, requestCode);
        }
        catch (Exception e) {
            JitsiMeetLogger.e(e, "Error requesting permissions", new Object[0]);
            onRequestPermissionsResult(requestCode, permissions, new int[0]);
        }
    }
}
