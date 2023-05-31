// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

class LocaleDetector extends ReactContextBaseJavaModule
{
    public LocaleDetector(final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    public Map<String, Object> getConstants() {
        final Context context = (Context)this.getReactApplicationContext();
        final HashMap<String, Object> constants = new HashMap<String, Object>();
        constants.put("locale", context.getResources().getConfiguration().locale.toLanguageTag());
        return constants;
    }
    
    public String getName() {
        return "LocaleDetector";
    }
}
