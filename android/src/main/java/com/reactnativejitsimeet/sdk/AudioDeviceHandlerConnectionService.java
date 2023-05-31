// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import android.telecom.CallAudioState;
import java.util.HashSet;
import java.util.Set;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import android.media.AudioManager;
import androidx.annotation.RequiresApi;

@RequiresApi(26)
class AudioDeviceHandlerConnectionService implements AudioModeModule.AudioDeviceHandlerInterface, RNConnectionService.CallAudioStateListener
{
    private static final String TAG;
    private AudioManager audioManager;
    private AudioModeModule module;
    private RNConnectionService rcs;
    private int supportedRouteMask;
    
    private static int audioDeviceToRouteInt(final String audioDevice) {
        if (audioDevice == null) {
            return 8;
        }
        switch (audioDevice) {
            case "BLUETOOTH": {
                return 2;
            }
            case "EARPIECE": {
                return 1;
            }
            case "HEADPHONES": {
                return 4;
            }
            case "SPEAKER": {
                return 8;
            }
            default: {
                JitsiMeetLogger.e(AudioDeviceHandlerConnectionService.TAG + " Unsupported device name: " + audioDevice, new Object[0]);
                return 8;
            }
        }
    }
    
    private static Set<String> routesToDeviceNames(final int supportedRouteMask) {
        final Set<String> devices = new HashSet<String>();
        if ((supportedRouteMask & 0x1) == 0x1) {
            devices.add("EARPIECE");
        }
        if ((supportedRouteMask & 0x2) == 0x2) {
            devices.add("BLUETOOTH");
        }
        if ((supportedRouteMask & 0x8) == 0x8) {
            devices.add("SPEAKER");
        }
        if ((supportedRouteMask & 0x4) == 0x4) {
            devices.add("HEADPHONES");
        }
        return devices;
    }
    
    public AudioDeviceHandlerConnectionService(final AudioManager audioManager) {
        this.supportedRouteMask = -1;
        this.audioManager = audioManager;
    }
    
    @Override
    public void onCallAudioStateChange(final CallAudioState state) {
        this.module.runInAudioThread(new Runnable() {
            @Override
            public void run() {
                final boolean audioRouteChanged = audioDeviceToRouteInt(AudioDeviceHandlerConnectionService.this.module.getSelectedDevice()) != state.getRoute();
                final int newSupportedRoutes = state.getSupportedRouteMask();
                final boolean audioDevicesChanged = AudioDeviceHandlerConnectionService.this.supportedRouteMask != newSupportedRoutes;
                if (audioDevicesChanged) {
                    AudioDeviceHandlerConnectionService.this.supportedRouteMask = newSupportedRoutes;
                    final Set<String> devices = routesToDeviceNames(AudioDeviceHandlerConnectionService.this.supportedRouteMask);
                    AudioDeviceHandlerConnectionService.this.module.replaceDevices(devices);
                    JitsiMeetLogger.i(AudioDeviceHandlerConnectionService.TAG + " Available audio devices: " + devices.toString(), new Object[0]);
                }
                if (audioRouteChanged || audioDevicesChanged) {
                    AudioDeviceHandlerConnectionService.this.module.resetSelectedDevice();
                    AudioDeviceHandlerConnectionService.this.module.updateAudioRoute();
                }
            }
        });
    }
    
    @Override
    public void start(final AudioModeModule audioModeModule) {
        JitsiMeetLogger.i("Using " + AudioDeviceHandlerConnectionService.TAG + " as the audio device handler", new Object[0]);
        this.module = audioModeModule;
        this.rcs = (RNConnectionService)this.module.getContext().getNativeModule((Class)RNConnectionService.class);
        if (this.rcs != null) {
            this.rcs.setCallAudioStateListener(this);
        }
        else {
            JitsiMeetLogger.w(AudioDeviceHandlerConnectionService.TAG + " Couldn't set call audio state listener, module is null", new Object[0]);
        }
    }
    
    @Override
    public void stop() {
        if (this.rcs != null) {
            this.rcs.setCallAudioStateListener(null);
            this.rcs = null;
        }
        else {
            JitsiMeetLogger.w(AudioDeviceHandlerConnectionService.TAG + " Couldn't set call audio state listener, module is null", new Object[0]);
        }
    }
    
    @Override
    public void setAudioRoute(final String audioDevice) {
        final int newAudioRoute = audioDeviceToRouteInt(audioDevice);
        RNConnectionService.setAudioRoute(newAudioRoute);
    }
    
    @Override
    public boolean setMode(final int mode) {
        if (mode != 0) {
            try {
                this.audioManager.setMicrophoneMute(false);
            }
            catch (Throwable tr) {
                JitsiMeetLogger.w(tr, AudioDeviceHandlerConnectionService.TAG + " Failed to unmute the microphone", new Object[0]);
            }
        }
        return true;
    }
    
    static {
        TAG = AudioDeviceHandlerConnectionService.class.getSimpleName();
    }
}
