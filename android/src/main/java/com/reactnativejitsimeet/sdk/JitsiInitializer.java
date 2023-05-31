// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import java.util.Collections;
import java.util.List;
import org.wonday.orientation.OrientationActivityLifecycle;
import android.app.Application;
import com.facebook.soloader.SoLoader;
import android.util.Log;
import androidx.annotation.NonNull;
import android.content.Context;
import androidx.startup.Initializer;

public class JitsiInitializer implements Initializer<Boolean>
{
    @NonNull
    public Boolean create(@NonNull final Context context) {
        Log.d(this.getClass().getCanonicalName(), "create");
        SoLoader.init(context, false);
        JitsiMeetUncaughtExceptionHandler.register();
        ((Application)context).registerActivityLifecycleCallbacks((Application.ActivityLifecycleCallbacks)OrientationActivityLifecycle.getInstance());
        return true;
    }
    
    @NonNull
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }
}
