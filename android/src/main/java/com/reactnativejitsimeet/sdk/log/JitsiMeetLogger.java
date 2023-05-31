// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk.log;

import timber.log.Timber;

public class JitsiMeetLogger
{
    public static void addHandler(final JitsiMeetBaseLogHandler handler) {
        if (!Timber.forest().contains(handler)) {
            try {
                Timber.plant((Timber.Tree)handler);
            }
            catch (Throwable t) {
                Timber.w(t, "Couldn't add log handler", new Object[0]);
            }
        }
    }
    
    public static void removeHandler(final JitsiMeetBaseLogHandler handler) {
        if (Timber.forest().contains(handler)) {
            try {
                Timber.uproot((Timber.Tree)handler);
            }
            catch (Throwable t) {
                Timber.w(t, "Couldn't remove log handler", new Object[0]);
            }
        }
    }
    
    public static void v(final String message, final Object... args) {
        Timber.v(message, args);
    }
    
    public static void v(final Throwable t, final String message, final Object... args) {
        Timber.v(t, message, args);
    }
    
    public static void v(final Throwable t) {
        Timber.v(t);
    }
    
    public static void d(final String message, final Object... args) {
        Timber.d(message, args);
    }
    
    public static void d(final Throwable t, final String message, final Object... args) {
        Timber.d(t, message, args);
    }
    
    public static void d(final Throwable t) {
        Timber.d(t);
    }
    
    public static void i(final String message, final Object... args) {
        Timber.i(message, args);
    }
    
    public static void i(final Throwable t, final String message, final Object... args) {
        Timber.i(t, message, args);
    }
    
    public static void i(final Throwable t) {
        Timber.i(t);
    }
    
    public static void w(final String message, final Object... args) {
        Timber.w(message, args);
    }
    
    public static void w(final Throwable t, final String message, final Object... args) {
        Timber.w(t, message, args);
    }
    
    public static void w(final Throwable t) {
        Timber.w(t);
    }
    
    public static void e(final String message, final Object... args) {
        Timber.e(message, args);
    }
    
    public static void e(final Throwable t, final String message, final Object... args) {
        Timber.e(t, message, args);
    }
    
    public static void e(final Throwable t) {
        Timber.e(t);
    }
    
    static {
        addHandler(new JitsiMeetDefaultLogHandler());
    }
}
