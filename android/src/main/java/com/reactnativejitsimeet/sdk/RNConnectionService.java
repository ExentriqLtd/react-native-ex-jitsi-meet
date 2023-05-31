// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.telecom.CallAudioState;
import com.facebook.react.bridge.ReadableMap;
import android.telecom.DisconnectCause;
import android.annotation.SuppressLint;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.os.Parcelable;
import android.os.Bundle;
import android.content.Context;
import android.net.Uri;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Iterator;
import androidx.annotation.RequiresApi;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "ConnectionService")
@RequiresApi(api = 26)
class RNConnectionService extends ReactContextBaseJavaModule
{
    public static final String NAME = "ConnectionService";
    private static final String TAG = "JitsiConnectionService";
    private CallAudioStateListener callAudioStateListener;
    
    @RequiresApi(api = 26)
    static void setAudioRoute(final int audioRoute) {
        for (final ConnectionService.ConnectionImpl c : ConnectionService.getConnections()) {
            c.setAudioRoute(audioRoute);
        }
    }
    
    RNConnectionService(final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    @ReactMethod
    public void addListener(final String eventName) {
    }
    
    @ReactMethod
    public void removeListeners(final Integer count) {
    }
    
    @ReactMethod
    @SuppressLint({ "MissingPermission" })
    public void startCall(final String callUUID, final String handle, final boolean hasVideo, final Promise promise) {
        JitsiMeetLogger.d("%s startCall UUID=%s, h=%s, v=%s", "JitsiConnectionService", callUUID, handle, hasVideo);
        final ReactApplicationContext ctx = this.getReactApplicationContext();
        final Uri address = Uri.fromParts("sip", handle, (String)null);
        PhoneAccountHandle accountHandle;
        try {
            accountHandle = ConnectionService.registerPhoneAccount((Context)this.getReactApplicationContext(), address, callUUID);
        }
        catch (Throwable tr) {
            JitsiMeetLogger.e(tr, "JitsiConnectionService error in startCall", new Object[0]);
            promise.reject(tr);
            return;
        }
        final Bundle extras = new Bundle();
        extras.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", (Parcelable)accountHandle);
        extras.putInt("android.telecom.extra.START_CALL_WITH_VIDEO_STATE", hasVideo ? 3 : 0);
        ConnectionService.registerStartCallPromise(callUUID, promise);
        TelecomManager tm = null;
        try {
            tm = (TelecomManager)ctx.getSystemService("telecom");
            tm.placeCall(address, extras);
        }
        catch (Throwable tr2) {
            JitsiMeetLogger.e(tr2, "JitsiConnectionService error in startCall", new Object[0]);
            if (tm != null) {
                try {
                    tm.unregisterPhoneAccount(accountHandle);
                }
                catch (Throwable t) {}
            }
            ConnectionService.unregisterStartCallPromise(callUUID);
            promise.reject(tr2);
        }
    }
    
    @ReactMethod
    public void reportCallFailed(final String callUUID) {
        JitsiMeetLogger.d("JitsiConnectionService reportCallFailed " + callUUID, new Object[0]);
        ConnectionService.setConnectionDisconnected(callUUID, new DisconnectCause(1));
    }
    
    @ReactMethod
    public void endCall(final String callUUID) {
        JitsiMeetLogger.d("JitsiConnectionService endCall " + callUUID, new Object[0]);
        ConnectionService.setConnectionDisconnected(callUUID, new DisconnectCause(2));
    }
    
    @ReactMethod
    public void reportConnectedOutgoingCall(final String callUUID, final Promise promise) {
        JitsiMeetLogger.d("JitsiConnectionService reportConnectedOutgoingCall " + callUUID, new Object[0]);
        if (ConnectionService.setConnectionActive(callUUID)) {
            promise.resolve((Object)null);
        }
        else {
            promise.reject("CONNECTION_NOT_FOUND_ERROR", "Connection wasn't found.");
        }
    }
    
    public String getName() {
        return "ConnectionService";
    }
    
    @ReactMethod
    public void updateCall(final String callUUID, final ReadableMap callState) {
        ConnectionService.updateCall(callUUID, callState);
    }
    
    public CallAudioStateListener getCallAudioStateListener() {
        return this.callAudioStateListener;
    }
    
    public void setCallAudioStateListener(final CallAudioStateListener callAudioStateListener) {
        this.callAudioStateListener = callAudioStateListener;
    }
    
    void onCallAudioStateChange(final CallAudioState callAudioState) {
        if (this.callAudioStateListener != null) {
            this.callAudioStateListener.onCallAudioStateChange(callAudioState);
        }
    }
    
    interface CallAudioStateListener
    {
        void onCallAudioStateChange(final CallAudioState p0);
    }
}
