package com.reactnativejitsimeet;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class ExJitsiMeetViewManager extends SimpleViewManager<ExJitsiMeetView> {
    public static final String REACT_CLASS = "ExJitsiMeetView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ExJitsiMeetView createViewInstance(ThemedReactContext reactContext) {
        return new ExJitsiMeetView(reactContext);
    }
}