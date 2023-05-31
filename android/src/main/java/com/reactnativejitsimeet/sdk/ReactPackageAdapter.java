// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.facebook.react.uimanager.ViewManager;
import java.util.Collections;
import com.facebook.react.bridge.NativeModule;
import java.util.List;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.ReactPackage;

class ReactPackageAdapter implements ReactPackage
{
    public List<NativeModule> createNativeModules(final ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
    
    public List<ViewManager> createViewManagers(final ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
