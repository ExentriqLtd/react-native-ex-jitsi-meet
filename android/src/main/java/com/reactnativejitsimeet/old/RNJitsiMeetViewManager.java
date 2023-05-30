package com.reactnativejitsimeet;

import android.app.Activity;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.module.annotations.ReactModule;

import androidx.annotation.NonNull;

import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetView;
@ReactModule(name = RNJitsiMeetViewManager.REACT_CLASS)
public class RNJitsiMeetViewManager extends SimpleViewManager<JitsiMeetView>  {
    public static final String REACT_CLASS = "RNJitsiMeetView";
    private ReactApplicationContext mReactContext;

    public RNJitsiMeetViewManager(ReactApplicationContext reactContext) {
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected JitsiMeetView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new MainActivity(reactContext);
    }
}