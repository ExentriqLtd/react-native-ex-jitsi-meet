// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.ReactApplicationContext;
import android.os.PowerManager;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "Proximity")
class ProximityModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "Proximity";
    private final PowerManager.WakeLock wakeLock;
    
    public ProximityModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        final PowerManager powerManager = (PowerManager)reactContext.getSystemService("power");
        PowerManager.WakeLock wakeLock;
        try {
            wakeLock = powerManager.newWakeLock(32, "jitsi:Proximity");
        }
        catch (Throwable ignored) {
            wakeLock = null;
        }
        this.wakeLock = wakeLock;
    }
    
    public String getName() {
        return "Proximity";
    }
    
    @ReactMethod
    public void setEnabled(final boolean enabled) {
        if (this.wakeLock == null) {
            return;
        }
        UiThreadUtil.runOnUiThread((Runnable)new Runnable() {
            @Override
            public void run() {
                if (enabled) {
                    if (!ProximityModule.this.wakeLock.isHeld()) {
                        ProximityModule.this.wakeLock.acquire();
                    }
                }
                else if (ProximityModule.this.wakeLock.isHeld()) {
                    ProximityModule.this.wakeLock.release();
                }
            }
        });
    }
}
