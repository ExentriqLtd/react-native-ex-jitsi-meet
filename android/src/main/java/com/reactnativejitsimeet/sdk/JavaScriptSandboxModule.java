// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactMethod;
import com.squareup.duktape.Duktape;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "JavaScriptSandbox")
class JavaScriptSandboxModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "JavaScriptSandbox";
    
    public JavaScriptSandboxModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    @ReactMethod
    public void evaluate(final String code, final Promise promise) {
        final Duktape vm = Duktape.create();
        try {
            final Object res = vm.evaluate(code);
            promise.resolve((Object)res.toString());
        }
        catch (Throwable tr) {
            promise.reject(tr);
        }
        finally {
            vm.close();
        }
    }
    
    public String getName() {
        return "JavaScriptSandbox";
    }
}
