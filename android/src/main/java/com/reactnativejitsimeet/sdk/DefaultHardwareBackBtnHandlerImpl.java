// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

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
