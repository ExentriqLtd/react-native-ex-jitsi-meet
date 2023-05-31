// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Bundle;
import java.net.URL;
import android.os.Parcelable;

public class JitsiMeetConferenceOptions implements Parcelable
{
    private URL serverURL;
    private String room;
    private String token;
    private Bundle config;
    private Bundle featureFlags;
    private JitsiMeetUserInfo userInfo;
    public static final Parcelable.Creator<JitsiMeetConferenceOptions> CREATOR;
    
    public URL getServerURL() {
        return this.serverURL;
    }
    
    public String getRoom() {
        return this.room;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public Bundle getFeatureFlags() {
        return this.featureFlags;
    }
    
    public JitsiMeetUserInfo getUserInfo() {
        return this.userInfo;
    }
    
    private JitsiMeetConferenceOptions() {
    }
    
    private JitsiMeetConferenceOptions(final Parcel in) {
        this.serverURL = (URL)in.readSerializable();
        this.room = in.readString();
        this.token = in.readString();
        this.config = in.readBundle();
        this.featureFlags = in.readBundle();
        this.userInfo = new JitsiMeetUserInfo(in.readBundle());
    }
    
    Bundle asProps() {
        final Bundle props = new Bundle();
        if (!this.featureFlags.containsKey("pip.enabled")) {
            this.featureFlags.putBoolean("pip.enabled", true);
        }
        props.putBundle("flags", this.featureFlags);
        final Bundle urlProps = new Bundle();
        if (this.room != null && this.room.contains("://")) {
            urlProps.putString("url", this.room);
        }
        else {
            if (this.serverURL != null) {
                urlProps.putString("serverURL", this.serverURL.toString());
            }
            if (this.room != null) {
                urlProps.putString("room", this.room);
            }
        }
        if (this.token != null) {
            urlProps.putString("jwt", this.token);
        }
        if (this.userInfo != null) {
            props.putBundle("userInfo", this.userInfo.asBundle());
        }
        urlProps.putBundle("config", this.config);
        props.putBundle("url", urlProps);
        return props;
    }
    
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeSerializable((Serializable)this.serverURL);
        dest.writeString(this.room);
        dest.writeString(this.token);
        dest.writeBundle(this.config);
        dest.writeBundle(this.featureFlags);
        dest.writeBundle((this.userInfo != null) ? this.userInfo.asBundle() : new Bundle());
    }
    
    public int describeContents() {
        return 0;
    }
    
    static {
        CREATOR = (Parcelable.Creator)new Parcelable.Creator<JitsiMeetConferenceOptions>() {
            public JitsiMeetConferenceOptions createFromParcel(final Parcel in) {
                return new JitsiMeetConferenceOptions(in, null);
            }
            
            public JitsiMeetConferenceOptions[] newArray(final int size) {
                return new JitsiMeetConferenceOptions[size];
            }
        };
    }
    
    public static class Builder
    {
        private URL serverURL;
        private String room;
        private String token;
        private Bundle config;
        private Bundle featureFlags;
        private JitsiMeetUserInfo userInfo;
        
        public Builder() {
            this.config = new Bundle();
            this.featureFlags = new Bundle();
        }
        
        public Builder setServerURL(final URL url) {
            this.serverURL = url;
            return this;
        }
        
        public Builder setRoom(final String room) {
            this.room = room;
            return this;
        }
        
        public Builder setSubject(final String subject) {
            this.setConfigOverride("subject", subject);
            return this;
        }
        
        public Builder setToken(final String token) {
            this.token = token;
            return this;
        }
        
        public Builder setAudioMuted(final boolean audioMuted) {
            this.setConfigOverride("startWithAudioMuted", audioMuted);
            return this;
        }
        
        public Builder setAudioOnly(final boolean audioOnly) {
            this.setConfigOverride("startAudioOnly", audioOnly);
            return this;
        }
        
        public Builder setVideoMuted(final boolean videoMuted) {
            this.setConfigOverride("startWithVideoMuted", videoMuted);
            return this;
        }
        
        public Builder setFeatureFlag(final String flag, final boolean value) {
            this.featureFlags.putBoolean(flag, value);
            return this;
        }
        
        public Builder setFeatureFlag(final String flag, final String value) {
            this.featureFlags.putString(flag, value);
            return this;
        }
        
        public Builder setFeatureFlag(final String flag, final int value) {
            this.featureFlags.putInt(flag, value);
            return this;
        }
        
        public Builder setUserInfo(final JitsiMeetUserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }
        
        public Builder setConfigOverride(final String config, final String value) {
            this.config.putString(config, value);
            return this;
        }
        
        public Builder setConfigOverride(final String config, final int value) {
            this.config.putInt(config, value);
            return this;
        }
        
        public Builder setConfigOverride(final String config, final boolean value) {
            this.config.putBoolean(config, value);
            return this;
        }
        
        public Builder setConfigOverride(final String config, final Bundle bundle) {
            this.config.putBundle(config, bundle);
            return this;
        }
        
        public Builder setConfigOverride(final String config, final String[] list) {
            this.config.putStringArray(config, list);
            return this;
        }
        
        public JitsiMeetConferenceOptions build() {
            final JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions((JitsiMeetConferenceOptions$1)null);
            options.serverURL = this.serverURL;
            options.room = this.room;
            options.token = this.token;
            options.config = this.config;
            options.featureFlags = this.featureFlags;
            options.userInfo = this.userInfo;
            return options;
        }
    }
}
