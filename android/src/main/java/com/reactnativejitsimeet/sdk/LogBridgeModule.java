// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactMethod;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import javax.annotation.Nonnull;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "LogBridge")
class LogBridgeModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "LogBridge";
    
    public LogBridgeModule(@Nonnull final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    public String getName() {
        return "LogBridge";
    }
    
    @ReactMethod
    public void trace(final String message) {
        JitsiMeetLogger.v(message, new Object[0]);
    }
    
    @ReactMethod
    public void debug(final String message) {
        JitsiMeetLogger.d(message, new Object[0]);
    }
    
    @ReactMethod
    public void info(final String message) {
        JitsiMeetLogger.i(message, new Object[0]);
    }
    
    @ReactMethod
    public void log(final String message) {
        JitsiMeetLogger.i(message, new Object[0]);
    }
    
    @ReactMethod
    public void warn(final String message) {
        JitsiMeetLogger.w(message, new Object[0]);
    }
    
    @ReactMethod
    public void error(final String message) {
        JitsiMeetLogger.e(message, new Object[0]);
    }
}
