// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.EglBase;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.oney.WebRTCModule.webrtcutils.H264AndSoftwareVideoEncoderFactory;
import com.oney.WebRTCModule.webrtcutils.H264AndSoftwareVideoDecoderFactory;
import com.oney.WebRTCModule.EglUtils;
import com.oney.WebRTCModule.WebRTCModuleOptions;
import android.app.Activity;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import androidx.annotation.Nullable;
import com.facebook.react.jscexecutor.JSCExecutorFactory;
import java.lang.reflect.Constructor;

import android.os.Build;
import android.util.Log;
import org.wonday.orientation.OrientationPackage;
import com.horcrux.svg.SvgPackage;
import com.th3rdwave.safeareacontext.SafeAreaContextPackage;
import com.zmxv.RNSound.RNSoundPackage;
import com.swmansion.rnscreens.RNScreensPackage;
import com.rnimmersive.RNImmersivePackage;
import org.linusu.RNGetRandomValuesPackage;
import com.swmansion.gesturehandler.RNGestureHandlerPackage;
import com.oney.WebRTCModule.WebRTCModulePackage;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.kevinresol.react_native_default_preference.RNDefaultPreferencePackage;
import com.reactnativecommunity.webview.RNCWebViewPackage;
import com.brentvatne.react.ReactVideoPackage;
import com.reactnativecommunity.slider.ReactSliderPackage;
import com.oblador.performance.PerformancePackage;
import com.reactnativepagerview.PagerViewPackage;
import com.reactnativecommunity.netinfo.NetInfoPackage;
import com.reactnativecommunity.clipboard.ClipboardPackage;
import com.facebook.react.shell.MainReactPackage;
import com.corbt.keepawake.KCKeepAwakePackage;
import com.calendarevents.RNCalendarEventsPackage;
import com.ocetnik.timer.BackgroundTimerPackage;
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage;
import com.facebook.react.ReactPackage;
import java.util.Collections;
import com.facebook.react.uimanager.ViewManager;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.reactnativejitsimeet.sdk.net.NAT64AddrInfoModule;
import org.devio.rn.splashscreen.SplashScreenModule;
import com.facebook.react.bridge.NativeModule;
import java.util.List;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.ReactInstanceManager;

class ReactInstanceManagerHolder
{
    private static final String TAG;
    private static ReactInstanceManager reactInstanceManager;
    
    private static List<NativeModule> createNativeModules(final ReactApplicationContext reactContext) {
        final List<NativeModule> nativeModules = new ArrayList<NativeModule>(Arrays.asList((NativeModule)new AndroidSettingsModule(reactContext), (NativeModule)new AppInfoModule(reactContext), (NativeModule)new AudioModeModule(reactContext), (NativeModule)new DropboxModule(reactContext), (NativeModule)new ExternalAPIModule(reactContext), (NativeModule)new JavaScriptSandboxModule(reactContext), (NativeModule)new LocaleDetector(reactContext), (NativeModule)new LogBridgeModule(reactContext), (NativeModule)new SplashScreenModule(reactContext), (NativeModule)new PictureInPictureModule(reactContext), (NativeModule)new ProximityModule(reactContext), (NativeModule)new NAT64AddrInfoModule(reactContext)));
        if (AudioModeModule.useConnectionService()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nativeModules.add((NativeModule)new RNConnectionService(reactContext));
            }
        }
        return nativeModules;
    }
    
    private static List<ViewManager> createViewManagers(final ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
    
    static List<ReactPackage> getReactNativePackages() {
        final List<ReactPackage> packages = new ArrayList<ReactPackage>(
                Arrays.asList(
                        new AsyncStoragePackage(),
                        new BackgroundTimerPackage(),
                        new RNCalendarEventsPackage(),
                        new KCKeepAwakePackage(),
                        new MainReactPackage(),
                        new ClipboardPackage(),
                        new NetInfoPackage(),
                        new PagerViewPackage(),
                        new PerformancePackage(),
                        new ReactSliderPackage(),
                        new ReactVideoPackage(),
                        new RNCWebViewPackage(),
                        new RNDefaultPreferencePackage(),
                        new RNDeviceInfo(),
                        new WebRTCModulePackage(),
                        new RNGestureHandlerPackage(),
                        new RNGetRandomValuesPackage(),
                        new RNImmersivePackage(),
                        new RNScreensPackage(),
                        new RNSoundPackage(),
                        new SafeAreaContextPackage(),
                        new SvgPackage(),
                        new OrientationPackage(),
                        new ReactPackageAdapter() {
                           @Override
                            public List<NativeModule> createNativeModules(final ReactApplicationContext reactContext) {
                                return super.createNativeModules(reactContext);
                            }

                            @Override
                            public List<ViewManager> createViewManagers(final ReactApplicationContext reactContext) {
                                return super.createViewManagers(reactContext);
                            }
                        })
        );
        try {
            final Class<?> amplitudePackageClass = Class.forName("com.amplitude.reactnative.AmplitudeReactNativePackage");
            final Constructor constructor = amplitudePackageClass.getConstructor((Class<?>[])new Class[0]);
            packages.add((ReactPackage) constructor.newInstance(new Object[0]));
        }
        catch (Exception e) {
            Log.d(ReactInstanceManagerHolder.TAG, "Not loading AmplitudeReactNativePackage");
        }
        try {
            final Class<?> giphyPackageClass = Class.forName("com.giphyreactnativesdk.GiphyReactNativeSdkPackage");
            final Constructor constructor = giphyPackageClass.getConstructor((Class<?>[])new Class[0]);
            packages.add((ReactPackage) constructor.newInstance(new Object[0]));
        }
        catch (Exception e) {
            Log.d(ReactInstanceManagerHolder.TAG, "Not loading GiphyReactNativeSdkPackage");
        }
        try {
            final Class<?> googlePackageClass = Class.forName("com.reactnativegooglesignin.RNGoogleSigninPackage");
            final Constructor constructor = googlePackageClass.getConstructor((Class<?>[])new Class[0]);
            packages.add((ReactPackage) constructor.newInstance(new Object[0]));
        }
        catch (Exception e) {
            Log.d(ReactInstanceManagerHolder.TAG, "Not loading RNGoogleSignInPackage");
        }
        return packages;
    }
    
    static JSCExecutorFactory getReactNativeJSFactory() {
        return new JSCExecutorFactory("", "");
    }
    
    static void emitEvent(final String eventName, @Nullable final Object data) {
        final ReactInstanceManager reactInstanceManager = getReactInstanceManager();
        if (reactInstanceManager != null) {
            final ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
            if (reactContext != null) {
                ((DeviceEventManagerModule.RCTDeviceEventEmitter)reactContext.getJSModule((Class)DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(eventName, data);
            }
        }
    }
    
    static <T extends NativeModule> T getNativeModule(final Class<T> nativeModuleClass) {
        final ReactContext reactContext = (ReactInstanceManagerHolder.reactInstanceManager != null) ? ReactInstanceManagerHolder.reactInstanceManager.getCurrentReactContext() : null;
        return (T)((reactContext != null) ? reactContext.getNativeModule((Class)nativeModuleClass) : null);
    }
    
    static Activity getCurrentActivity() {
        final ReactContext reactContext = (ReactInstanceManagerHolder.reactInstanceManager != null) ? ReactInstanceManagerHolder.reactInstanceManager.getCurrentReactContext() : null;
        return (reactContext != null) ? reactContext.getCurrentActivity() : null;
    }
    
    static ReactInstanceManager getReactInstanceManager() {
        return ReactInstanceManagerHolder.reactInstanceManager;
    }
    
    static void initReactInstanceManager(final Activity activity) {
        if (ReactInstanceManagerHolder.reactInstanceManager != null) {
            return;
        }
        final WebRTCModuleOptions options = WebRTCModuleOptions.getInstance();
        final EglBase.Context eglContext = EglUtils.getRootEglBaseContext();
        options.videoDecoderFactory = (VideoDecoderFactory)new H264AndSoftwareVideoDecoderFactory(eglContext);
        options.videoEncoderFactory = (VideoEncoderFactory)new H264AndSoftwareVideoEncoderFactory(eglContext);
        Log.d(ReactInstanceManagerHolder.TAG, "initializing RN with Activity");
        ReactInstanceManagerHolder.reactInstanceManager = ReactInstanceManager.builder().setApplication(activity.getApplication()).setCurrentActivity(activity).setBundleAssetName("index.android.bundle").setJSMainModulePath("index.android").setJavaScriptExecutorFactory((JavaScriptExecutorFactory)getReactNativeJSFactory()).addPackages((List)getReactNativePackages()).setUseDeveloperSupport(false).setInitialLifecycleState(LifecycleState.RESUMED).build();
    }
    
    static {
        TAG = ReactInstanceManagerHolder.class.getSimpleName();
    }
}
