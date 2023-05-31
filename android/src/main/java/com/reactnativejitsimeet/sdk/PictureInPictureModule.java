// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Rational;
import android.app.PictureInPictureParams;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import java.util.HashMap;
import java.util.Map;
import android.os.Build;
import android.app.ActivityManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "PictureInPicture")
class PictureInPictureModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "PictureInPicture";
    private static final String TAG = "PictureInPicture";
    private static boolean isSupported;
    private boolean isEnabled;
    
    public PictureInPictureModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        final ActivityManager am = (ActivityManager)reactContext.getSystemService("activity");
        PictureInPictureModule.isSupported = (Build.VERSION.SDK_INT >= 26 && !am.isLowRamDevice());
    }
    
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("SUPPORTED", PictureInPictureModule.isSupported);
        return constants;
    }
    
    @TargetApi(26)
    public void enterPictureInPicture() {
        if (!this.isEnabled) {
            return;
        }
        if (!PictureInPictureModule.isSupported) {
            throw new IllegalStateException("Picture-in-Picture not supported");
        }
        final Activity currentActivity = this.getCurrentActivity();
        if (currentActivity == null) {
            throw new IllegalStateException("No current Activity!");
        }
        JitsiMeetLogger.i("PictureInPicture Entering Picture-in-Picture", new Object[0]);
        final PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder().setAspectRatio(new Rational(1, 1));
        if (!currentActivity.enterPictureInPictureMode(builder.build())) {
            throw new RuntimeException("Failed to enter Picture-in-Picture");
        }
    }
    
    @ReactMethod
    public void enterPictureInPicture(final Promise promise) {
        try {
            this.enterPictureInPicture();
            promise.resolve((Object)null);
        }
        catch (RuntimeException re) {
            promise.reject((Throwable)re);
        }
    }
    
    @ReactMethod
    public void setPictureInPictureEnabled(final Boolean enabled) {
        this.isEnabled = enabled;
    }
    
    public boolean isPictureInPictureSupported() {
        return PictureInPictureModule.isSupported;
    }
    
    public String getName() {
        return "PictureInPicture";
    }
}
