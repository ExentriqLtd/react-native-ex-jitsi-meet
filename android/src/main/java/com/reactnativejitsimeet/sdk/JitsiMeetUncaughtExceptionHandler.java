// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import org.jitsi.meet.sdk.log.JitsiMeetLogger;

class JitsiMeetUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    
    public static void register() {
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        final JitsiMeetUncaughtExceptionHandler uncaughtExceptionHandler = new JitsiMeetUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
    
    private JitsiMeetUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
        this.defaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
    }
    
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        JitsiMeetLogger.e(e, this.getClass().getSimpleName() + " FATAL ERROR", new Object[0]);
        if (AudioModeModule.useConnectionService()) {
            ConnectionService.abortConnections();
        }
        if (this.defaultUncaughtExceptionHandler != null) {
            this.defaultUncaughtExceptionHandler.uncaughtException(t, e);
        }
    }
}
