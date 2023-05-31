// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.os.Build;
import android.os.Handler;
import android.media.AudioDeviceInfo;
import java.util.Set;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import java.util.HashSet;
import android.media.AudioDeviceCallback;
import android.media.AudioManager;

class AudioDeviceHandlerGeneric implements AudioModeModule.AudioDeviceHandlerInterface, AudioManager.OnAudioFocusChangeListener
{
    private static final String TAG;
    private AudioModeModule module;
    private static final int TYPE_HEARING_AID = 23;
    private static final int TYPE_USB_HEADSET = 22;
    private boolean audioFocusLost;
    private AudioManager audioManager;
    private final Runnable onAudioDeviceChangeRunner;
    private final AudioDeviceCallback audioDeviceCallback;
    
    public AudioDeviceHandlerGeneric(final AudioManager audioManager) {
        this.audioFocusLost = false;
        this.onAudioDeviceChangeRunner = new Runnable() {
            @Override
            public void run() {
                final Set<String> devices = new HashSet<String>();
                final AudioDeviceInfo[] devices2;
                final AudioDeviceInfo[] deviceInfos = devices2 = AudioDeviceHandlerGeneric.this.audioManager.getDevices(3);
                for (final AudioDeviceInfo info : devices2) {
                    switch (info.getType()) {
                        case 7: {
                            devices.add("BLUETOOTH");
                            break;
                        }
                        case 1: {
                            devices.add("EARPIECE");
                            break;
                        }
                        case 2: {
                            devices.add("SPEAKER");
                            break;
                        }
                        case 3:
                        case 4:
                        case 22:
                        case 23: {
                            devices.add("HEADPHONES");
                            break;
                        }
                    }
                }
                AudioDeviceHandlerGeneric.this.module.replaceDevices(devices);
                JitsiMeetLogger.i(AudioDeviceHandlerGeneric.TAG + " Available audio devices: " + devices.toString(), new Object[0]);
                AudioDeviceHandlerGeneric.this.module.updateAudioRoute();
            }
        };
        this.audioDeviceCallback = new AudioDeviceCallback() {
            public void onAudioDevicesAdded(final AudioDeviceInfo[] addedDevices) {
                JitsiMeetLogger.d(AudioDeviceHandlerGeneric.TAG + " Audio devices added", new Object[0]);
                AudioDeviceHandlerGeneric.this.onAudioDeviceChange();
            }
            
            public void onAudioDevicesRemoved(final AudioDeviceInfo[] removedDevices) {
                JitsiMeetLogger.d(AudioDeviceHandlerGeneric.TAG + " Audio devices removed", new Object[0]);
                AudioDeviceHandlerGeneric.this.onAudioDeviceChange();
            }
        };
        this.audioManager = audioManager;
    }
    
    private void onAudioDeviceChange() {
        this.module.runInAudioThread(this.onAudioDeviceChangeRunner);
    }
    
    public void onAudioFocusChange(final int focusChange) {
        this.module.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                switch (focusChange) {
                    case 1: {
                        JitsiMeetLogger.d(AudioDeviceHandlerGeneric.TAG + " Audio focus gained", new Object[0]);
                        if (AudioDeviceHandlerGeneric.this.audioFocusLost) {
                            AudioDeviceHandlerGeneric.this.module.resetAudioRoute();
                        }
                        AudioDeviceHandlerGeneric.this.audioFocusLost = false;
                        break;
                    }
                    case -3:
                    case -2:
                    case -1: {
                        JitsiMeetLogger.d(AudioDeviceHandlerGeneric.TAG + " Audio focus lost", new Object[0]);
                        AudioDeviceHandlerGeneric.this.audioFocusLost = true;
                        break;
                    }
                }
            }
        });
    }
    
    private void setBluetoothAudioRoute(final boolean enabled) {
        if (enabled) {
            this.audioManager.startBluetoothSco();
            this.audioManager.setBluetoothScoOn(true);
        }
        else {
            this.audioManager.setBluetoothScoOn(false);
            this.audioManager.stopBluetoothSco();
        }
    }
    
    @Override
    public void start(final AudioModeModule audioModeModule) {
        JitsiMeetLogger.i("Using " + AudioDeviceHandlerGeneric.TAG + " as the audio device handler", new Object[0]);
        this.module = audioModeModule;
        this.audioManager.registerAudioDeviceCallback(this.audioDeviceCallback, (Handler)null);
        this.onAudioDeviceChange();
    }
    
    @Override
    public void stop() {
        this.audioManager.unregisterAudioDeviceCallback(this.audioDeviceCallback);
    }
    
    @Override
    public void setAudioRoute(final String device) {
        this.audioManager.setSpeakerphoneOn(device.equals("SPEAKER"));
        this.setBluetoothAudioRoute(device.equals("BLUETOOTH"));
    }
    
    @Override
    public boolean setMode(final int mode) {
        if (mode == 0) {
            this.audioFocusLost = false;
            this.audioManager.setMode(0);
            this.audioManager.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener)this);
            this.audioManager.setSpeakerphoneOn(false);
            this.setBluetoothAudioRoute(false);
            return true;
        }
        this.audioManager.setMode(3);
        this.audioManager.setMicrophoneMute(false);
        int gotFocus;
        if (Build.VERSION.SDK_INT >= 26) {
            gotFocus = this.audioManager.requestAudioFocus(new AudioFocusRequest.Builder(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(2).setContentType(1).build()).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener((AudioManager.OnAudioFocusChangeListener)this).build());
        }
        else {
            gotFocus = this.audioManager.requestAudioFocus((AudioManager.OnAudioFocusChangeListener)this, 0, 1);
        }
        if (gotFocus == 0) {
            JitsiMeetLogger.w(AudioDeviceHandlerGeneric.TAG + " Audio focus request failed", new Object[0]);
            return false;
        }
        return true;
    }
    
    static {
        TAG = AudioDeviceHandlerGeneric.class.getSimpleName();
    }
}
