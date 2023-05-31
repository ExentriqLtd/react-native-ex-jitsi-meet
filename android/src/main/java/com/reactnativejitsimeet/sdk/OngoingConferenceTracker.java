// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import java.util.Iterator;
import com.facebook.react.bridge.ReadableMap;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;

class OngoingConferenceTracker
{
    private static final OngoingConferenceTracker instance;
    private static final String CONFERENCE_WILL_JOIN = "CONFERENCE_WILL_JOIN";
    private static final String CONFERENCE_TERMINATED = "CONFERENCE_TERMINATED";
    private final Collection<OngoingConferenceListener> listeners;
    private String currentConference;
    
    public OngoingConferenceTracker() {
        this.listeners = (Collection<OngoingConferenceListener>)Collections.synchronizedSet(new HashSet<Object>());
    }
    
    public static OngoingConferenceTracker getInstance() {
        return OngoingConferenceTracker.instance;
    }
    
    synchronized String getCurrentConference() {
        return this.currentConference;
    }
    
    synchronized void onExternalAPIEvent(final String name, final ReadableMap data) {
        if (!data.hasKey("url")) {
            return;
        }
        final String url = data.getString("url");
        if (url == null) {
            return;
        }
        switch (name) {
            case "CONFERENCE_WILL_JOIN": {
                this.currentConference = url;
                this.updateListeners();
                break;
            }
            case "CONFERENCE_TERMINATED": {
                if (url.equals(this.currentConference)) {
                    this.currentConference = null;
                    this.updateListeners();
                    break;
                }
                break;
            }
        }
    }
    
    void addListener(final OngoingConferenceListener listener) {
        this.listeners.add(listener);
    }
    
    void removeListener(final OngoingConferenceListener listener) {
        this.listeners.remove(listener);
    }
    
    private void updateListeners() {
        synchronized (this.listeners) {
            for (final OngoingConferenceListener listener : this.listeners) {
                listener.onCurrentConferenceChanged(this.currentConference);
            }
        }
    }
    
    static {
        instance = new OngoingConferenceTracker();
    }
    
    public interface OngoingConferenceListener
    {
        void onCurrentConferenceChanged(final String p0);
    }
}
