// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import android.os.Bundle;
import java.util.Iterator;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import com.facebook.react.bridge.WritableNativeMap;
import android.content.Intent;
import java.util.HashMap;

public class BroadcastAction
{
    private static final String TAG;
    private final Type type;
    private final HashMap<String, Object> data;
    
    public BroadcastAction(final Intent intent) {
        this.type = buildTypeFromAction(intent.getAction());
        this.data = buildDataFromBundle(intent.getExtras());
    }
    
    public Type getType() {
        return this.type;
    }
    
    public HashMap<String, Object> getData() {
        return this.data;
    }
    
    public WritableNativeMap getDataAsWritableNativeMap() {
        final WritableNativeMap nativeMap = new WritableNativeMap();
        for (final String key : this.data.keySet()) {
            try {
                if (this.data.get(key) instanceof Boolean) {
                    nativeMap.putBoolean(key, (boolean)this.data.get(key));
                }
                else if (this.data.get(key) instanceof Integer) {
                    nativeMap.putInt(key, (int)this.data.get(key));
                }
                else if (this.data.get(key) instanceof Double) {
                    nativeMap.putDouble(key, (double)this.data.get(key));
                }
                else {
                    if (!(this.data.get(key) instanceof String)) {
                        throw new Exception("Unsupported extra data type");
                    }
                    nativeMap.putString(key, (String)this.data.get(key));
                }
            }
            catch (Exception e) {
                JitsiMeetLogger.w(BroadcastAction.TAG + " invalid extra data in event", e);
            }
        }
        return nativeMap;
    }
    
    private static HashMap<String, Object> buildDataFromBundle(final Bundle bundle) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        if (bundle != null) {
            for (final String key : bundle.keySet()) {
                map.put(key, bundle.get(key));
            }
        }
        return map;
    }
    
    static {
        TAG = BroadcastAction.class.getSimpleName();
    }
    
    enum Type
    {
        SET_AUDIO_MUTED("org.jitsi.meet.SET_AUDIO_MUTED"), 
        HANG_UP("org.jitsi.meet.HANG_UP"), 
        SEND_ENDPOINT_TEXT_MESSAGE("org.jitsi.meet.SEND_ENDPOINT_TEXT_MESSAGE"), 
        TOGGLE_SCREEN_SHARE("org.jitsi.meet.TOGGLE_SCREEN_SHARE"), 
        RETRIEVE_PARTICIPANTS_INFO("org.jitsi.meet.RETRIEVE_PARTICIPANTS_INFO"), 
        OPEN_CHAT("org.jitsi.meet.OPEN_CHAT"), 
        CLOSE_CHAT("org.jitsi.meet.CLOSE_CHAT"), 
        SEND_CHAT_MESSAGE("org.jitsi.meet.SEND_CHAT_MESSAGE"), 
        SET_VIDEO_MUTED("org.jitsi.meet.SET_VIDEO_MUTED"), 
        SET_CLOSED_CAPTIONS_ENABLED("org.jitsi.meet.SET_CLOSED_CAPTIONS_ENABLED");
        
        private final String action;
        
        private Type(final String action) {
            this.action = action;
        }
        
        public String getAction() {
            return this.action;
        }
        
        private static Type buildTypeFromAction(final String action) {
            for (final Type type : values()) {
                if (type.action.equalsIgnoreCase(action)) {
                    return type;
                }
            }
            return null;
        }
    }
}
