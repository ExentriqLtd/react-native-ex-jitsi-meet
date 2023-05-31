// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.rnimmersive.RNImmersiveModule;
import android.app.Activity;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import android.view.View;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import android.content.Context;
import java.util.Iterator;
import androidx.annotation.Nullable;
import android.os.Bundle;
import com.facebook.react.ReactRootView;
import android.widget.FrameLayout;

public class JitsiMeetView extends FrameLayout
{
    private static final int BACKGROUND_COLOR = -15658735;
    private ReactRootView reactRootView;
    
    private static Bundle mergeProps(@Nullable final Bundle a, @Nullable final Bundle b) {
        final Bundle result = new Bundle();
        if (a == null) {
            if (b != null) {
                result.putAll(b);
            }
            return result;
        }
        if (b == null) {
            result.putAll(a);
            return result;
        }
        result.putAll(a);
        for (final String key : b.keySet()) {
            final Object bValue = b.get(key);
            final Object aValue = a.get(key);
            final String valueType = bValue.getClass().getSimpleName();
            if (valueType.contentEquals("Boolean")) {
                result.putBoolean(key, (boolean)bValue);
            }
            else if (valueType.contentEquals("String")) {
                result.putString(key, (String)bValue);
            }
            else if (valueType.contentEquals("Integer")) {
                result.putInt(key, (int)bValue);
            }
            else {
                if (!valueType.contentEquals("Bundle")) {
                    throw new RuntimeException("Unsupported type: " + valueType);
                }
                result.putBundle(key, mergeProps((Bundle)aValue, (Bundle)bValue));
            }
        }
        return result;
    }
    
    public JitsiMeetView(@NonNull final Activity activity) {
        super(activity);
        this.initialize(activity);
    }
    
    public JitsiMeetView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context);
    }
    
    public JitsiMeetView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context);
    }
    
    public void dispose() {
        if (this.reactRootView != null) {
            this.removeView((View)this.reactRootView);
            this.reactRootView.unmountReactApplication();
            this.reactRootView = null;
        }
    }
    
    public void enterPictureInPicture() {
        final PictureInPictureModule pipModule = ReactInstanceManagerHolder.getNativeModule(PictureInPictureModule.class);
        if (pipModule != null && pipModule.isPictureInPictureSupported() && !JitsiMeetActivityDelegate.arePermissionsBeingRequested()) {
            try {
                pipModule.enterPictureInPicture();
            }
            catch (RuntimeException re) {
                JitsiMeetLogger.e(re, "Failed to enter PiP mode", new Object[0]);
            }
        }
    }
    
    public void join(@Nullable final JitsiMeetConferenceOptions options) {
        this.setProps((options != null) ? options.asProps() : new Bundle());
    }
    
    public void abort() {
        this.setProps(new Bundle());
    }
    
    private void createReactRootView(final String appName, @Nullable Bundle props) {
        if (props == null) {
            props = new Bundle();
        }
        if (this.reactRootView == null) {
            (this.reactRootView = new ReactRootView(this.getContext())).startReactApplication(ReactInstanceManagerHolder.getReactInstanceManager(), appName, props);
            this.reactRootView.setBackgroundColor(-15658735);
            this.addView((View)this.reactRootView);
        }
        else {
            this.reactRootView.setAppProperties(props);
        }
    }
    
    private void initialize(@NonNull final Context context) {
        this.setBackgroundColor(-15658735);
        ReactInstanceManagerHolder.initReactInstanceManager((Activity)context);
    }
    
    private void setProps(@NonNull final Bundle newProps) {
        final Bundle props = mergeProps(JitsiMeet.getDefaultProps(), newProps);
        props.putLong("timestamp", System.currentTimeMillis());
        this.createReactRootView("App", props);
    }
    
    protected void onDetachedFromWindow() {
        this.dispose();
        super.onDetachedFromWindow();
    }
    
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final RNImmersiveModule immersive = RNImmersiveModule.getInstance();
        if (hasFocus && immersive != null) {
            immersive.emitImmersiveStateChangeEvent();
        }
    }
}
