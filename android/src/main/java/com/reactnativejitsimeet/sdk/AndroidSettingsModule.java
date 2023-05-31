// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactMethod;
import android.content.Context;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.content.Intent;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "AndroidSettings")
class AndroidSettingsModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "AndroidSettings";
    
    public AndroidSettingsModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    public String getName() {
        return "AndroidSettings";
    }
    
    @ReactMethod
    public void open(final Promise promise) {
        final Context context = (Context)this.getReactApplicationContext();
        final Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), (String)null));
        try {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            promise.reject((Throwable)e);
            return;
        }
        promise.resolve((Object)null);
    }
}
