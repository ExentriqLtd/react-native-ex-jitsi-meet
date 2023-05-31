// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk.log;

import android.util.Log;
import org.jetbrains.annotations.NotNull;

public class JitsiMeetDefaultLogHandler extends JitsiMeetBaseLogHandler
{
    private static final String TAG = "JitsiMeetSDK";
    
    @Override
    protected void doLog(final int priority, @NotNull final String tag, @NotNull final String msg) {
        Log.println(priority, tag, msg);
    }
    
    @Override
    protected String getDefaultTag() {
        return "JitsiMeetSDK";
    }
}
