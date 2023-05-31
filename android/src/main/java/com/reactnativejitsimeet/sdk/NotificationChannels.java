// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

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
