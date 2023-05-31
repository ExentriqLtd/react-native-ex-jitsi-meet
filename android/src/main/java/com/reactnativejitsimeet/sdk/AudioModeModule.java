// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import java.util.concurrent.Executors;
import android.os.Build;
import android.app.Activity;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import java.util.Iterator;
import com.facebook.react.bridge.WritableArray;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import java.util.HashMap;
import java.util.Map;
import com.facebook.react.bridge.ReactMethod;
import java.util.HashSet;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import android.media.AudioManager;
import android.annotation.SuppressLint;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "AudioMode")
@SuppressLint({ "AnnotateVersionCheck" })
class AudioModeModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "AudioMode";
    static final int DEFAULT = 0;
    static final int AUDIO_CALL = 1;
    static final int VIDEO_CALL = 2;
    static final String TAG = "AudioMode";
    private static final boolean supportsConnectionService;
    private static boolean useConnectionService_;
    private AudioManager audioManager;
    private AudioDeviceHandlerInterface audioDeviceHandler;
    private static final ExecutorService executor;
    private int mode;
    static final String DEVICE_BLUETOOTH = "BLUETOOTH";
    static final String DEVICE_EARPIECE = "EARPIECE";
    static final String DEVICE_HEADPHONES = "HEADPHONES";
    static final String DEVICE_SPEAKER = "SPEAKER";
    private static final String DEVICE_CHANGE_EVENT = "org.jitsi.meet:features/audio-mode#devices-update";
    private Set<String> availableDevices;
    private String selectedDevice;
    private String userSelectedDevice;
    
    static boolean useConnectionService() {
        return AudioModeModule.supportsConnectionService && AudioModeModule.useConnectionService_;
    }
    
    public AudioModeModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.mode = -1;
        this.availableDevices = new HashSet<String>();
        this.audioManager = (AudioManager)reactContext.getSystemService("audio");
    }
    
    @ReactMethod
    public void addListener(final String eventName) {
    }
    
    @ReactMethod
    public void removeListeners(final Integer count) {
    }
    
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("DEVICE_CHANGE_EVENT", "org.jitsi.meet:features/audio-mode#devices-update");
        constants.put("AUDIO_CALL", 1);
        constants.put("DEFAULT", 0);
        constants.put("VIDEO_CALL", 2);
        return constants;
    }
    
    private void notifyDevicesChanged() {
        this.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                final WritableArray data = Arguments.createArray();
                final boolean hasHeadphones = AudioModeModule.this.availableDevices.contains("HEADPHONES");
                for (final String device : AudioModeModule.this.availableDevices) {
                    if (hasHeadphones && device.equals("EARPIECE")) {
                        continue;
                    }
                    final WritableMap deviceInfo = Arguments.createMap();
                    deviceInfo.putString("type", device);
                    deviceInfo.putBoolean("selected", device.equals(AudioModeModule.this.selectedDevice));
                    data.pushMap((ReadableMap)deviceInfo);
                }
                ((DeviceEventManagerModule.RCTDeviceEventEmitter)AudioModeModule.this.getContext().getJSModule((Class)DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("org.jitsi.meet:features/audio-mode#devices-update", (Object)data);
                JitsiMeetLogger.i("AudioMode Updating audio device list", new Object[0]);
            }
        });
    }
    
    public String getName() {
        return "AudioMode";
    }
    
    public ReactContext getContext() {
        return (ReactContext)this.getReactApplicationContext();
    }
    
    public void initialize() {
        this.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                AudioModeModule.this.setAudioDeviceHandler();
            }
        });
    }
    
    private void setAudioDeviceHandler() {
        if (this.audioDeviceHandler != null) {
            this.audioDeviceHandler.stop();
        }
        if (useConnectionService()) {
            this.audioDeviceHandler = new AudioDeviceHandlerConnectionService(this.audioManager);
        }
        else {
            this.audioDeviceHandler = new AudioDeviceHandlerGeneric(this.audioManager);
        }
        this.audioDeviceHandler.start(this);
    }
    
    void runInAudioThread(final Runnable runnable) {
        AudioModeModule.executor.execute(runnable);
    }
    
    @ReactMethod
    public void setAudioDevice(final String device) {
        this.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                if (!AudioModeModule.this.availableDevices.contains(device)) {
                    JitsiMeetLogger.w("AudioMode Audio device not available: " + device, new Object[0]);
                    AudioModeModule.this.userSelectedDevice = null;
                    return;
                }
                if (AudioModeModule.this.mode != -1) {
                    JitsiMeetLogger.i("AudioMode User selected device set to: " + device, new Object[0]);
                    AudioModeModule.this.userSelectedDevice = device;
                    AudioModeModule.this.updateAudioRoute(AudioModeModule.this.mode, false);
                }
            }
        });
    }
    
    @ReactMethod
    public void setMode(final int mode, final Promise promise) {
        if (mode != 0 && mode != 1 && mode != 2) {
            promise.reject("setMode", "Invalid audio mode " + mode);
            return;
        }
        final Activity currentActivity = this.getCurrentActivity();
        if (currentActivity != null) {
            if (mode == 0) {
                currentActivity.setVolumeControlStream(Integer.MIN_VALUE);
            }
            else {
                currentActivity.setVolumeControlStream(0);
            }
        }
        this.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                boolean success;
                try {
                    success = AudioModeModule.this.updateAudioRoute(mode, false);
                }
                catch (Throwable e) {
                    success = false;
                    JitsiMeetLogger.e(e, "AudioMode Failed to update audio route for mode: " + mode, new Object[0]);
                }
                if (success) {
                    AudioModeModule.this.mode = mode;
                    promise.resolve((Object)null);
                }
                else {
                    promise.reject("setMode", "Failed to set audio mode to " + mode);
                }
            }
        });
    }
    
    @ReactMethod
    public void setUseConnectionService(final boolean use) {
        this.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                AudioModeModule.useConnectionService_ = use;
                AudioModeModule.this.setAudioDeviceHandler();
            }
        });
    }
    
    private boolean updateAudioRoute(final int mode, final boolean force) {
        JitsiMeetLogger.i("AudioMode Update audio route for mode: " + mode, new Object[0]);
        if (!this.audioDeviceHandler.setMode(mode)) {
            return false;
        }
        if (mode == 0) {
            this.selectedDevice = null;
            this.userSelectedDevice = null;
            this.notifyDevicesChanged();
            return true;
        }
        final boolean bluetoothAvailable = this.availableDevices.contains("BLUETOOTH");
        final boolean headsetAvailable = this.availableDevices.contains("HEADPHONES");
        String audioDevice;
        if (bluetoothAvailable) {
            audioDevice = "BLUETOOTH";
        }
        else if (headsetAvailable) {
            audioDevice = "HEADPHONES";
        }
        else {
            audioDevice = "SPEAKER";
        }
        if (this.userSelectedDevice != null && this.availableDevices.contains(this.userSelectedDevice)) {
            audioDevice = this.userSelectedDevice;
        }
        if (!force && this.selectedDevice != null && this.selectedDevice.equals(audioDevice)) {
            return true;
        }
        this.selectedDevice = audioDevice;
        JitsiMeetLogger.i("AudioMode Selected audio device: " + audioDevice, new Object[0]);
        this.audioDeviceHandler.setAudioRoute(audioDevice);
        this.notifyDevicesChanged();
        return true;
    }
    
    String getSelectedDevice() {
        return this.selectedDevice;
    }
    
    void resetSelectedDevice() {
        this.selectedDevice = null;
        this.userSelectedDevice = null;
    }
    
    void addDevice(final String device) {
        this.availableDevices.add(device);
        this.resetSelectedDevice();
    }
    
    void removeDevice(final String device) {
        this.availableDevices.remove(device);
        this.resetSelectedDevice();
    }
    
    void replaceDevices(final Set<String> devices) {
        this.availableDevices = devices;
        this.resetSelectedDevice();
    }
    
    void updateAudioRoute() {
        if (this.mode != -1) {
            this.updateAudioRoute(this.mode, false);
        }
    }
    
    void resetAudioRoute() {
        if (this.mode != -1) {
            this.updateAudioRoute(this.mode, true);
        }
    }
    
    static {
        supportsConnectionService = (Build.VERSION.SDK_INT >= 26);
        AudioModeModule.useConnectionService_ = AudioModeModule.supportsConnectionService;
        executor = Executors.newSingleThreadExecutor();
    }
    
    interface AudioDeviceHandlerInterface
    {
        void start(final AudioModeModule p0);
        
        void stop();
        
        void setAudioRoute(final String p0);
        
        boolean setMode(final int p0);
    }
}
