// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk.log;

import java.text.MessageFormat;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public abstract class JitsiMeetBaseLogHandler extends Timber.Tree
{
    protected void log(final int priority, @Nullable final String tag, @NotNull final String msg, @Nullable final Throwable t) {
        final String errmsg = Log.getStackTraceString(t);
        if (errmsg.isEmpty()) {
            this.doLog(priority, this.getDefaultTag(), msg);
        }
        else {
            this.doLog(priority, this.getDefaultTag(), MessageFormat.format("{0}\n{1}", msg, errmsg));
        }
    }
    
    protected abstract void doLog(final int p0, @NotNull final String p1, @NotNull final String p2);
    
    protected abstract String getDefaultTag();
}
