// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import java.net.MalformedURLException;
import android.os.Bundle;
import java.net.URL;

public class JitsiMeetUserInfo
{
    private String displayName;
    private String email;
    private URL avatar;
    
    public JitsiMeetUserInfo() {
    }
    
    public JitsiMeetUserInfo(final Bundle b) {
        if (b.containsKey("displayName")) {
            this.displayName = b.getString("displayName");
        }
        if (b.containsKey("email")) {
            this.email = b.getString("email");
        }
        if (b.containsKey("avatarURL")) {
            final String avatarURL = b.getString("avatarURL");
            try {
                this.avatar = new URL(avatarURL);
            }
            catch (MalformedURLException ex) {}
        }
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public URL getAvatar() {
        return this.avatar;
    }
    
    public void setAvatar(final URL avatar) {
        this.avatar = avatar;
    }
    
    Bundle asBundle() {
        final Bundle b = new Bundle();
        if (this.displayName != null) {
            b.putString("displayName", this.displayName);
        }
        if (this.email != null) {
            b.putString("email", this.email);
        }
        if (this.avatar != null) {
            b.putString("avatarURL", this.avatar.toString());
        }
        return b;
    }
}
