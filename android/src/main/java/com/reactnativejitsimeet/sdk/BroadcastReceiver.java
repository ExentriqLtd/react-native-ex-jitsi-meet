// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.Context;

public class BroadcastReceiver extends android.content.BroadcastReceiver
{
    public BroadcastReceiver(final Context context) {
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        final IntentFilter intentFilter = new IntentFilter();
        for (final BroadcastAction.Type type : BroadcastAction.Type.values()) {
            intentFilter.addAction(type.getAction());
        }
        localBroadcastManager.registerReceiver((android.content.BroadcastReceiver)this, intentFilter);
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final BroadcastAction action = new BroadcastAction(intent);
        final String actionName = action.getType().getAction();
        ReactInstanceManagerHolder.emitEvent(actionName, action.getDataAsWritableNativeMap());
    }
}
