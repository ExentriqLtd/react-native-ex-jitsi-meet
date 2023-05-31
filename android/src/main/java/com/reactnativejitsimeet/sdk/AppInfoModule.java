// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.Context;
import java.util.HashMap;
import android.content.pm.PackageManager;
import java.util.Map;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "AppInfo")
class AppInfoModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "AppInfo";
    
    public AppInfoModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    public Map<String, Object> getConstants() {
        final Context context = (Context)this.getReactApplicationContext();
        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo;
        try {
            final String packageName = context.getPackageName();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        }
        catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
            packageInfo = null;
        }
        final Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("buildNumber", (packageInfo == null) ? "" : String.valueOf(packageInfo.versionCode));
        constants.put("name", (applicationInfo == null) ? "" : packageManager.getApplicationLabel(applicationInfo));
        constants.put("version", (packageInfo == null) ? "" : packageInfo.versionName);
        constants.put("LIBRE_BUILD", false);
        constants.put("GOOGLE_SERVICES_ENABLED", false);
        return constants;
    }
    
    public String getName() {
        return "AppInfo";
    }
}
