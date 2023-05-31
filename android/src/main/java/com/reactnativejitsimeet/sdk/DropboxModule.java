// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.users.SpaceAllocation;
import com.facebook.react.bridge.WritableMap;
import com.dropbox.core.v2.users.SpaceUsage;
import com.facebook.react.bridge.Arguments;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import java.util.HashMap;
import java.util.Map;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.facebook.react.bridge.ReactMethod;
import android.content.Context;
import com.dropbox.core.android.Auth;
import com.dropbox.core.DbxRequestConfig;
import android.text.TextUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "Dropbox")
class DropboxModule extends ReactContextBaseJavaModule implements LifecycleEventListener
{
    public static final String NAME = "Dropbox";
    private String appKey;
    private String clientId;
    private final boolean isEnabled;
    private Promise promise;
    
    public DropboxModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        final String pkg = reactContext.getApplicationContext().getPackageName();
        final int resId = reactContext.getResources().getIdentifier("dropbox_app_key", "string", pkg);
        this.appKey = reactContext.getString(resId);
        this.isEnabled = !TextUtils.isEmpty((CharSequence)this.appKey);
        this.clientId = this.generateClientId();
        reactContext.addLifecycleEventListener((LifecycleEventListener)this);
    }
    
    @ReactMethod
    public void authorize(final Promise promise) {
        if (this.isEnabled) {
            Auth.startOAuth2PKCE((Context)this.getCurrentActivity(), this.appKey, DbxRequestConfig.newBuilder(this.clientId).build());
            this.promise = promise;
        }
        else {
            promise.reject((Throwable)new Exception("Dropbox integration isn't configured."));
        }
    }
    
    private String generateClientId() {
        final Context context = (Context)this.getReactApplicationContext();
        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        try {
            final String packageName = context.getPackageName();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        }
        catch (PackageManager.NameNotFoundException ex) {}
        final String applicationLabel = (applicationInfo == null) ? "JitsiMeet" : packageManager.getApplicationLabel(applicationInfo).toString().replaceAll("\\s", "");
        final String version = (packageInfo == null) ? "dev" : packageInfo.versionName;
        return applicationLabel + "/" + version;
    }
    
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("ENABLED", this.isEnabled);
        return constants;
    }
    
    @ReactMethod
    public void getDisplayName(final String token, final Promise promise) {
        final DbxRequestConfig config = DbxRequestConfig.newBuilder(this.clientId).build();
        final DbxClientV2 client = new DbxClientV2(config, token);
        try {
            final FullAccount account = client.users().getCurrentAccount();
            promise.resolve((Object)account.getName().getDisplayName());
        }
        catch (DbxException e) {
            promise.reject((Throwable)e);
        }
    }
    
    public String getName() {
        return "Dropbox";
    }
    
    @ReactMethod
    public void getSpaceUsage(final String token, final Promise promise) {
        final DbxRequestConfig config = DbxRequestConfig.newBuilder(this.clientId).build();
        final DbxClientV2 client = new DbxClientV2(config, token);
        try {
            final SpaceUsage spaceUsage = client.users().getSpaceUsage();
            final WritableMap map = Arguments.createMap();
            map.putString("used", String.valueOf(spaceUsage.getUsed()));
            final SpaceAllocation allocation = spaceUsage.getAllocation();
            long allocated = 0L;
            if (allocation.isIndividual()) {
                allocated += allocation.getIndividualValue().getAllocated();
            }
            if (allocation.isTeam()) {
                allocated += allocation.getTeamValue().getAllocated();
            }
            map.putString("allocated", String.valueOf(allocated));
            promise.resolve((Object)map);
        }
        catch (DbxException e) {
            promise.reject((Throwable)e);
        }
    }
    
    public void onHostDestroy() {
    }
    
    public void onHostPause() {
    }
    
    public void onHostResume() {
        final DbxCredential credential = Auth.getDbxCredential();
        if (this.promise != null) {
            if (credential != null) {
                final WritableMap result = Arguments.createMap();
                result.putString("token", credential.getAccessToken());
                result.putString("rToken", credential.getRefreshToken());
                result.putDouble("expireDate", (double)credential.getExpiresAt());
                this.promise.resolve((Object)result);
                this.promise = null;
            }
            else {
                this.promise.reject("Invalid dropbox credentials");
            }
            this.promise = null;
        }
    }
}
