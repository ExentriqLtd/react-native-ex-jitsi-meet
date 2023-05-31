// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.os.Bundle;
import java.util.Iterator;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import android.content.Intent;
import com.facebook.react.bridge.ReadableMap;
import java.util.HashMap;

public class BroadcastEvent
{
    private static final String TAG;
    private final Type type;
    private final HashMap<String, Object> data;
    
    public BroadcastEvent(final String name, final ReadableMap data) {
        this.type = Type.buildTypeFromName(name);
        this.data = (HashMap<String, Object>)data.toHashMap();
    }
    
    public BroadcastEvent(final Intent intent) {
        this.type = Type.buildTypeFromAction(intent.getAction());
        this.data = buildDataFromBundle(intent.getExtras());
    }
    
    public Type getType() {
        return this.type;
    }
    
    public HashMap<String, Object> getData() {
        return this.data;
    }
    
    public Intent buildIntent() {
        if (this.type != null && this.type.action != null) {
            final Intent intent = new Intent(this.type.action);
            for (final String key : this.data.keySet()) {
                try {
                    intent.putExtra(key, this.data.get(key).toString());
                }
                catch (Exception e) {
                    JitsiMeetLogger.w(BroadcastEvent.TAG + " invalid extra data in event", e);
                }
            }
            return intent;
        }
        return null;
    }
    
    private static HashMap<String, Object> buildDataFromBundle(final Bundle bundle) {
        if (bundle != null) {
            try {
                final HashMap<String, Object> map = new HashMap<String, Object>();
                for (final String key : bundle.keySet()) {
                    map.put(key, bundle.get(key));
                }
                return map;
            }
            catch (Exception e) {
                JitsiMeetLogger.w(BroadcastEvent.TAG + " invalid extra data", e);
            }
        }
        return null;
    }
    
    static {
        TAG = BroadcastEvent.class.getSimpleName();
    }
    
    public enum Type
    {
        CONFERENCE_JOINED("org.jitsi.meet.CONFERENCE_JOINED"), 
        CONFERENCE_TERMINATED("org.jitsi.meet.CONFERENCE_TERMINATED"), 
        CONFERENCE_WILL_JOIN("org.jitsi.meet.CONFERENCE_WILL_JOIN"), 
        AUDIO_MUTED_CHANGED("org.jitsi.meet.AUDIO_MUTED_CHANGED"), 
        PARTICIPANT_JOINED("org.jitsi.meet.PARTICIPANT_JOINED"), 
        PARTICIPANT_LEFT("org.jitsi.meet.PARTICIPANT_LEFT"), 
        ENDPOINT_TEXT_MESSAGE_RECEIVED("org.jitsi.meet.ENDPOINT_TEXT_MESSAGE_RECEIVED"), 
        SCREEN_SHARE_TOGGLED("org.jitsi.meet.SCREEN_SHARE_TOGGLED"), 
        PARTICIPANTS_INFO_RETRIEVED("org.jitsi.meet.PARTICIPANTS_INFO_RETRIEVED"), 
        CHAT_MESSAGE_RECEIVED("org.jitsi.meet.CHAT_MESSAGE_RECEIVED"), 
        CHAT_TOGGLED("org.jitsi.meet.CHAT_TOGGLED"), 
        VIDEO_MUTED_CHANGED("org.jitsi.meet.VIDEO_MUTED_CHANGED"), 
        READY_TO_CLOSE("org.jitsi.meet.READY_TO_CLOSE");
        
        private static final String CONFERENCE_WILL_JOIN_NAME = "CONFERENCE_WILL_JOIN";
        private static final String CONFERENCE_JOINED_NAME = "CONFERENCE_JOINED";
        private static final String CONFERENCE_TERMINATED_NAME = "CONFERENCE_TERMINATED";
        private static final String AUDIO_MUTED_CHANGED_NAME = "AUDIO_MUTED_CHANGED";
        private static final String PARTICIPANT_JOINED_NAME = "PARTICIPANT_JOINED";
        private static final String PARTICIPANT_LEFT_NAME = "PARTICIPANT_LEFT";
        private static final String ENDPOINT_TEXT_MESSAGE_RECEIVED_NAME = "ENDPOINT_TEXT_MESSAGE_RECEIVED";
        private static final String SCREEN_SHARE_TOGGLED_NAME = "SCREEN_SHARE_TOGGLED";
        private static final String PARTICIPANTS_INFO_RETRIEVED_NAME = "PARTICIPANTS_INFO_RETRIEVED";
        private static final String CHAT_MESSAGE_RECEIVED_NAME = "CHAT_MESSAGE_RECEIVED";
        private static final String CHAT_TOGGLED_NAME = "CHAT_TOGGLED";
        private static final String VIDEO_MUTED_CHANGED_NAME = "VIDEO_MUTED_CHANGED";
        private static final String READY_TO_CLOSE_NAME = "READY_TO_CLOSE";
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
        
        private static Type buildTypeFromName(final String name) {
            switch (name) {
                case "CONFERENCE_WILL_JOIN": {
                    return Type.CONFERENCE_WILL_JOIN;
                }
                case "CONFERENCE_JOINED": {
                    return Type.CONFERENCE_JOINED;
                }
                case "CONFERENCE_TERMINATED": {
                    return Type.CONFERENCE_TERMINATED;
                }
                case "AUDIO_MUTED_CHANGED": {
                    return Type.AUDIO_MUTED_CHANGED;
                }
                case "PARTICIPANT_JOINED": {
                    return Type.PARTICIPANT_JOINED;
                }
                case "PARTICIPANT_LEFT": {
                    return Type.PARTICIPANT_LEFT;
                }
                case "ENDPOINT_TEXT_MESSAGE_RECEIVED": {
                    return Type.ENDPOINT_TEXT_MESSAGE_RECEIVED;
                }
                case "SCREEN_SHARE_TOGGLED": {
                    return Type.SCREEN_SHARE_TOGGLED;
                }
                case "PARTICIPANTS_INFO_RETRIEVED": {
                    return Type.PARTICIPANTS_INFO_RETRIEVED;
                }
                case "CHAT_MESSAGE_RECEIVED": {
                    return Type.CHAT_MESSAGE_RECEIVED;
                }
                case "CHAT_TOGGLED": {
                    return Type.CHAT_TOGGLED;
                }
                case "VIDEO_MUTED_CHANGED": {
                    return Type.VIDEO_MUTED_CHANGED;
                }
                case "READY_TO_CLOSE": {
                    return Type.READY_TO_CLOSE;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
