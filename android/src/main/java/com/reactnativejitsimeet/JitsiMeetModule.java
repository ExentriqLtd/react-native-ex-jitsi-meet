package com.reactnativejitsimeet;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.jitsi.meet.sdk.JitsiMeet;
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
    public void call() {
        try {
            URL _url = new URL("https://meet.jit.si/");
            // Somewhere early in your app.
            JitsiMeetConferenceOptions defaultOptions
                    = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(_url)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom("x1234xx")
                    .build();
            ExJitsiMeetActivity.launch(this.getReactApplicationContext(), options);
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
