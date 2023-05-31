package com.reactnativejitsimeet;

import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

public class JitsiMeetModule extends ReactContextBaseJavaModule {

    public JitsiMeetModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "JitsiMeetModule";
    }

    @ReactMethod
    public void call(String domain, String roomName, ReadableMap userInfo, ReadableMap meetOptions, ReadableMap meetFeatureFlags, Callback callBack) {
        Log.d("domain::", domain);
        Log.d("roomName::", roomName);
        try {
            URL _url = new URL(domain);
            JitsiMeetConferenceOptions defaultOptions
                    = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(_url)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(roomName)
                    .build();
            JitsiMeetActivity.launch(this.getReactApplicationContext(), options);

            /*JitsiMeetUserInfo _userInfo = new JitsiMeetUserInfo();
            if (userInfo != null) {
                if (userInfo.hasKey("displayName")) {
                    _userInfo.setDisplayName(userInfo.getString("displayName"));
                }
                if (userInfo.hasKey("email")) {
                    _userInfo.setEmail(userInfo.getString("email"));
                }
                if (userInfo.hasKey("avatar")) {
                    String avatarURL = userInfo.getString("avatar");
                    try {
                        _userInfo.setAvatar(new URL(avatarURL));
                    } catch (MalformedURLException e) {
                    }
                }
            }
            URL _url = new URL(domain);
            System.out.println("call::::URL::" + _url.toString());
            // Somewhere early in your app.
            JitsiMeetConferenceOptions defaultOptions
                    = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(_url)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(roomName)
                    .setToken(meetOptions.hasKey("token") ? meetOptions.getString("token") : "")
                    .setSubject(meetOptions.hasKey("subject") ? meetOptions.getString("subject") : "")
                    .setAudioMuted(meetOptions.hasKey("audioMuted") ? meetOptions.getBoolean("audioMuted") : false)
                    .setAudioOnly(meetOptions.hasKey("audioOnly") ? meetOptions.getBoolean("audioOnly") : false)
                    .setVideoMuted(meetOptions.hasKey("videoMuted") ? meetOptions.getBoolean("videoMuted") : false)
                    .setUserInfo(_userInfo)
                    /*.setFeatureFlag("add-people.enabled", meetFeatureFlags.hasKey("addPeopleEnabled") ? meetFeatureFlags.getBoolean("addPeopleEnabled") : true)
                    .setFeatureFlag("calendar.enabled", meetFeatureFlags.hasKey("calendarEnabled") ?meetFeatureFlags.getBoolean("calendarEnabled") : true)
                    .setFeatureFlag("call-integration.enabled", meetFeatureFlags.hasKey("callIntegrationEnabled") ?meetFeatureFlags.getBoolean("callIntegrationEnabled") : true)
                    .setFeatureFlag("chat.enabled", meetFeatureFlags.hasKey("chatEnabled") ?meetFeatureFlags.getBoolean("chatEnabled") : true)
                    .setFeatureFlag("close-captions.enabled", meetFeatureFlags.hasKey("closeCaptionsEnabled") ?meetFeatureFlags.getBoolean("closeCaptionsEnabled") : true)
                    .setFeatureFlag("invite.enabled", meetFeatureFlags.hasKey("inviteEnabled") ?meetFeatureFlags.getBoolean("inviteEnabled") : true)
                    .setFeatureFlag("android.screensharing.enabled", meetFeatureFlags.hasKey("androidScreenSharingEnabled") ?meetFeatureFlags.getBoolean("androidScreenSharingEnabled") : true)
                    .setFeatureFlag("live-streaming.enabled", meetFeatureFlags.hasKey("liveStreamingEnabled") ?meetFeatureFlags.getBoolean("liveStreamingEnabled") : true)
                    .setFeatureFlag("meeting-name.enabled", meetFeatureFlags.hasKey("meetingNameEnabled") ?meetFeatureFlags.getBoolean("meetingNameEnabled") : true)
                    .setFeatureFlag("meeting-password.enabled", meetFeatureFlags.hasKey("meetingPasswordEnabled") ?meetFeatureFlags.getBoolean("meetingPasswordEnabled") : true)
                    .setFeatureFlag("pip.enabled", meetFeatureFlags.hasKey("pipEnabled") ?meetFeatureFlags.getBoolean("pipEnabled") : true)
                    .setFeatureFlag("kick-out.enabled", meetFeatureFlags.hasKey("kickOutEnabled") ?meetFeatureFlags.getBoolean("kickOutEnabled") : true)
                    .setFeatureFlag("conference-timer.enabled", meetFeatureFlags.hasKey("conferenceTimerEnabled") ?meetFeatureFlags.getBoolean("conferenceTimerEnabled") : true)
                    .setFeatureFlag("video-share.enabled", meetFeatureFlags.hasKey("videoShareEnabled") ?meetFeatureFlags.getBoolean("videoShareEnabled") : true)
                    .setFeatureFlag("recording.enabled", meetFeatureFlags.hasKey("recordingEnabled") ?meetFeatureFlags.getBoolean("recordingEnabled") : true)
                    .setFeatureFlag("reactions.enabled", meetFeatureFlags.hasKey("reactionsEnabled") ?meetFeatureFlags.getBoolean("reactionsEnabled") : true)
                    .setFeatureFlag("raise-hand.enabled", meetFeatureFlags.hasKey("raiseHandEnabled") ?meetFeatureFlags.getBoolean("raiseHandEnabled") : true)
                    .setFeatureFlag("tile-view.enabled", meetFeatureFlags.hasKey("tileViewEnabled") ?meetFeatureFlags.getBoolean("tileViewEnabled") : true)
                    .setFeatureFlag("toolbox.alwaysVisible", meetFeatureFlags.hasKey("toolboxAlwaysVisible") ?meetFeatureFlags.getBoolean("toolboxAlwaysVisible") : false)
                    .setFeatureFlag("toolbox.enabled", meetFeatureFlags.hasKey("toolboxEnabled") ?meetFeatureFlags.getBoolean("toolboxEnabled") : true)
                    .setFeatureFlag("welcomepage.enabled", meetFeatureFlags.hasKey("welcomePageEnabled") ?meetFeatureFlags.getBoolean("welcomePageEnabled") : false)
                    .setFeatureFlag("prejoinpage.enabled", meetFeatureFlags.hasKey("prejoinPageEnabled") ?meetFeatureFlags.getBoolean("prejoinPageEnabled") : false)
                    .build();
            JitsiMeetActivity.launch(this.getReactApplicationContext(), options);*/
            callBack.invoke("ExJitsiMeetActivity", "CONFERENCE_LAUNCH");
        } catch (Exception e) {
            Log.d("Error::", e.getMessage());
        }
    }

    @ReactMethod
    public void endCall() {
        try {
            Log.d("END-CALL", "START");
            this.getCurrentActivity().finish();
        } catch (Exception e) {
            Log.d("Error::", e.getMessage());
        }
    }

}
