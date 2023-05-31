// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.content.Intent;
import com.facebook.react.bridge.ReadableMap;
import android.content.Context;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BroadcastEmitter
{
    private final LocalBroadcastManager localBroadcastManager;
    
    public BroadcastEmitter(final Context context) {
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }
    
    public void sendBroadcast(final String name, final ReadableMap data) {
        final BroadcastEvent event = new BroadcastEvent(name, data);
        final Intent intent = event.buildIntent();
        if (intent != null) {
            this.localBroadcastManager.sendBroadcast(intent);
        }
    }
}