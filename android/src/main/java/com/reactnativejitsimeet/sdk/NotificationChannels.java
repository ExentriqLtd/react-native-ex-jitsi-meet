// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import java.util.ArrayList;
import java.util.List;

public class NotificationChannels
{
    static final String ONGOING_CONFERENCE_CHANNEL_ID = "JitsiOngoingConferenceChannel";
    public static List<String> allIds;
    
    static {
        NotificationChannels.allIds = new ArrayList<String>() {
            {
                this.add("JitsiOngoingConferenceChannel");
            }
        };
    }
}
