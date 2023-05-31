// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import com.facebook.react.bridge.ReadableMap;
import java.util.HashMap;
import java.util.Map;
import com.facebook.react.bridge.ReactMethod;
import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "ExternalAPI")
class ExternalAPIModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "ExternalAPI";
    private static final String TAG = "ExternalAPI";
    private final BroadcastEmitter broadcastEmitter;
    private final BroadcastReceiver broadcastReceiver;
    
    public ExternalAPIModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.broadcastEmitter = new BroadcastEmitter((Context)reactContext);
        this.broadcastReceiver = new BroadcastReceiver((Context)reactContext);
        ParticipantsService.init((Context)reactContext);
    }
    
    @ReactMethod
    public void addListener(final String eventName) {
    }
    
    @ReactMethod
    public void removeListeners(final Integer count) {
    }
    
    public String getName() {
        return "ExternalAPI";
    }
    
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("SET_AUDIO_MUTED", BroadcastAction.Type.SET_AUDIO_MUTED.getAction());
        constants.put("HANG_UP", BroadcastAction.Type.HANG_UP.getAction());
        constants.put("SEND_ENDPOINT_TEXT_MESSAGE", BroadcastAction.Type.SEND_ENDPOINT_TEXT_MESSAGE.getAction());
        constants.put("TOGGLE_SCREEN_SHARE", BroadcastAction.Type.TOGGLE_SCREEN_SHARE.getAction());
        constants.put("RETRIEVE_PARTICIPANTS_INFO", BroadcastAction.Type.RETRIEVE_PARTICIPANTS_INFO.getAction());
        constants.put("OPEN_CHAT", BroadcastAction.Type.OPEN_CHAT.getAction());
        constants.put("CLOSE_CHAT", BroadcastAction.Type.CLOSE_CHAT.getAction());
        constants.put("SEND_CHAT_MESSAGE", BroadcastAction.Type.SEND_CHAT_MESSAGE.getAction());
        constants.put("SET_VIDEO_MUTED", BroadcastAction.Type.SET_VIDEO_MUTED.getAction());
        constants.put("SET_CLOSED_CAPTIONS_ENABLED", BroadcastAction.Type.SET_CLOSED_CAPTIONS_ENABLED.getAction());
        return constants;
    }
    
    @ReactMethod
    public void sendEvent(final String name, final ReadableMap data) {
        OngoingConferenceTracker.getInstance().onExternalAPIEvent(name, data);
        JitsiMeetLogger.d("ExternalAPI Sending event: " + name + " with data: " + data, new Object[0]);
        this.broadcastEmitter.sendBroadcast(name, data);
    }
}
