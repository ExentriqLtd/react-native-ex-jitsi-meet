// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import android.content.Intent;

public class BroadcastIntentHelper
{
    public static Intent buildSetAudioMutedIntent(final boolean muted) {
        final Intent intent = new Intent(BroadcastAction.Type.SET_AUDIO_MUTED.getAction());
        intent.putExtra("muted", muted);
        return intent;
    }
    
    public static Intent buildHangUpIntent() {
        return new Intent(BroadcastAction.Type.HANG_UP.getAction());
    }
    
    public static Intent buildSendEndpointTextMessageIntent(final String to, final String message) {
        final Intent intent = new Intent(BroadcastAction.Type.SEND_ENDPOINT_TEXT_MESSAGE.getAction());
        intent.putExtra("to", to);
        intent.putExtra("message", message);
        return intent;
    }
    
    public static Intent buildToggleScreenShareIntent(final boolean enabled) {
        final Intent intent = new Intent(BroadcastAction.Type.TOGGLE_SCREEN_SHARE.getAction());
        intent.putExtra("enabled", enabled);
        return intent;
    }
    
    public static Intent buildOpenChatIntent(final String participantId) {
        final Intent intent = new Intent(BroadcastAction.Type.OPEN_CHAT.getAction());
        intent.putExtra("to", participantId);
        return intent;
    }
    
    public static Intent buildCloseChatIntent() {
        return new Intent(BroadcastAction.Type.CLOSE_CHAT.getAction());
    }
    
    public static Intent buildSendChatMessageIntent(final String participantId, final String message) {
        final Intent intent = new Intent(BroadcastAction.Type.SEND_CHAT_MESSAGE.getAction());
        intent.putExtra("to", participantId);
        intent.putExtra("message", message);
        return intent;
    }
    
    public static Intent buildSetVideoMutedIntent(final boolean muted) {
        final Intent intent = new Intent(BroadcastAction.Type.SET_VIDEO_MUTED.getAction());
        intent.putExtra("muted", muted);
        return intent;
    }
    
    public static Intent buildSetClosedCaptionsEnabledIntent(final boolean enabled) {
        final Intent intent = new Intent(BroadcastAction.Type.SET_CLOSED_CAPTIONS_ENABLED.getAction());
        intent.putExtra("enabled", enabled);
        return intent;
    }
}
