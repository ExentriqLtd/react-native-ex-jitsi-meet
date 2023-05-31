// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.app.Activity;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

class DefaultHardwareBackBtnHandlerImpl implements DefaultHardwareBackBtnHandler
{
    private final Activity activity;
    
    public DefaultHardwareBackBtnHandlerImpl(final Activity activity) {
        this.activity = activity;
    }
    
    public void invokeDefaultOnBackPressed() {
        this.activity.finish();
    }
}
