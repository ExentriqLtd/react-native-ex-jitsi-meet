// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;

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
