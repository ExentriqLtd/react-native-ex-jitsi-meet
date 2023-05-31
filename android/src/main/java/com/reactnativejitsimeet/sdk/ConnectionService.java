// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import android.telecom.CallAudioState;
import com.facebook.react.bridge.WritableNativeMap;
import android.telecom.PhoneAccount;
import android.content.ComponentName;
import android.net.Uri;
import android.content.Context;
import android.telecom.TelecomManager;
import java.util.Objects;
import android.os.Parcelable;
import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.PhoneAccountHandle;
import com.facebook.react.bridge.ReadableMap;
import android.telecom.DisconnectCause;
import com.reactnativejitsimeet.sdk.log.JitsiMeetLogger;
import android.os.Build;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.facebook.react.bridge.Promise;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.RequiresApi;

@RequiresApi(api = 26)
public class ConnectionService extends android.telecom.ConnectionService
{
    static final String TAG = "JitsiConnectionService";
    static final String EXTRA_PHONE_ACCOUNT_HANDLE = "com.reactnativejitsimeet.sdk.connection_service.PHONE_ACCOUNT_HANDLE";
    private static final Map<String, ConnectionImpl> connections;
    private static final HashMap<String, Promise> startCallPromises;
    
    static void abortConnections() {
        for (final ConnectionImpl connection : getConnections()) {
            connection.onAbort();
        }
    }
    
    static void addConnection(final ConnectionImpl connection) {
        ConnectionService.connections.put(connection.getCallUUID(), connection);
    }
    
    static List<ConnectionImpl> getConnections() {
        return new ArrayList<ConnectionImpl>(ConnectionService.connections.values());
    }
    
    static boolean isSamsungDevice() {
        return Build.MANUFACTURER.toLowerCase().contains("samsung");
    }
    
    static void registerStartCallPromise(final String uuid, final Promise promise) {
        ConnectionService.startCallPromises.put(uuid, promise);
    }
    
    static void removeConnection(final ConnectionImpl connection) {
        ConnectionService.connections.remove(connection.getCallUUID());
    }
    
    static boolean setConnectionActive(final String callUUID) {
        final ConnectionImpl connection = ConnectionService.connections.get(callUUID);
        if (connection != null) {
            connection.setActive();
            return true;
        }
        JitsiMeetLogger.w("%s setConnectionActive - no connection for UUID: %s", "JitsiConnectionService", callUUID);
        return false;
    }
    
    static void setConnectionDisconnected(final String callUUID, final DisconnectCause cause) {
        final ConnectionImpl connection = ConnectionService.connections.get(callUUID);
        if (connection != null) {
            if (isSamsungDevice()) {
                connection.setOnHold();
                connection.setConnectionProperties(144);
            }
            connection.setDisconnected(cause);
            connection.destroy();
        }
        else {
            JitsiMeetLogger.e("JitsiConnectionService endCall no connection for UUID: " + callUUID, new Object[0]);
        }
    }
    
    static Promise unregisterStartCallPromise(final String uuid) {
        return ConnectionService.startCallPromises.remove(uuid);
    }
    
    static void updateCall(final String callUUID, final ReadableMap callState) {
        final ConnectionImpl connection = ConnectionService.connections.get(callUUID);
        if (connection != null) {
            if (callState.hasKey("hasVideo")) {
                final boolean hasVideo = callState.getBoolean("hasVideo");
                JitsiMeetLogger.i(" %s updateCall: %s hasVideo: %s", "JitsiConnectionService", callUUID, hasVideo);
                connection.setVideoState(hasVideo ? 3 : 0);
            }
        }
        else {
            JitsiMeetLogger.e("JitsiConnectionService updateCall no connection for UUID: " + callUUID, new Object[0]);
        }
    }
    
    public Connection onCreateOutgoingConnection(final PhoneAccountHandle accountHandle, final ConnectionRequest request) {
        final ConnectionImpl connection = new ConnectionImpl();
        connection.setConnectionProperties(128);
        connection.setAddress(request.getAddress(), 3);
        connection.setExtras(request.getExtras());
        connection.setAudioModeIsVoip(true);
        connection.setVideoState(request.getVideoState());
        final Bundle moreExtras = new Bundle();
        moreExtras.putParcelable("com.reactnativejitsimeet.sdk.connection_service.PHONE_ACCOUNT_HANDLE", (Parcelable)Objects.requireNonNull((Parcelable)request.getAccountHandle(), "accountHandle"));
        connection.putExtras(moreExtras);
        addConnection(connection);
        final Promise startCallPromise = unregisterStartCallPromise(connection.getCallUUID());
        if (startCallPromise != null) {
            JitsiMeetLogger.d("JitsiConnectionService onCreateOutgoingConnection " + connection.getCallUUID(), new Object[0]);
            startCallPromise.resolve((Object)null);
        }
        else {
            JitsiMeetLogger.e("JitsiConnectionService onCreateOutgoingConnection: no start call Promise for " + connection.getCallUUID(), new Object[0]);
        }
        return connection;
    }
    
    public Connection onCreateIncomingConnection(final PhoneAccountHandle accountHandle, final ConnectionRequest request) {
        throw new RuntimeException("Not implemented");
    }
    
    public void onCreateIncomingConnectionFailed(final PhoneAccountHandle accountHandle, final ConnectionRequest request) {
        throw new RuntimeException("Not implemented");
    }
    
    public void onCreateOutgoingConnectionFailed(final PhoneAccountHandle accountHandle, final ConnectionRequest request) {
        final PhoneAccountHandle theAccountHandle = request.getAccountHandle();
        final String callUUID = theAccountHandle.getId();
        JitsiMeetLogger.e("JitsiConnectionService onCreateOutgoingConnectionFailed " + callUUID, new Object[0]);
        if (callUUID != null) {
            final Promise startCallPromise = unregisterStartCallPromise(callUUID);
            if (startCallPromise != null) {
                startCallPromise.reject("CREATE_OUTGOING_CALL_FAILED", "The request has been denied by the system");
            }
            else {
                JitsiMeetLogger.e("JitsiConnectionService startCallFailed - no start call Promise for UUID: " + callUUID, new Object[0]);
            }
        }
        else {
            JitsiMeetLogger.e("JitsiConnectionService onCreateOutgoingConnectionFailed - no call UUID", new Object[0]);
        }
        this.unregisterPhoneAccount(theAccountHandle);
    }
    
    private void unregisterPhoneAccount(final PhoneAccountHandle phoneAccountHandle) {
        final TelecomManager telecom = (TelecomManager)this.getSystemService((Class)TelecomManager.class);
        if (telecom != null) {
            if (phoneAccountHandle != null) {
                telecom.unregisterPhoneAccount(phoneAccountHandle);
            }
            else {
                JitsiMeetLogger.e("JitsiConnectionService unregisterPhoneAccount - account handle is null", new Object[0]);
            }
        }
        else {
            JitsiMeetLogger.e("JitsiConnectionService unregisterPhoneAccount - telecom is null", new Object[0]);
        }
    }
    
    static PhoneAccountHandle registerPhoneAccount(final Context context, final Uri address, final String callUUID) {
        final PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(new ComponentName(context, (Class)ConnectionService.class), callUUID);
        final PhoneAccount.Builder builder = PhoneAccount.builder(phoneAccountHandle, (CharSequence)address.toString()).setAddress(address).setCapabilities(3080).addSupportedUriScheme("sip");
        final PhoneAccount account = builder.build();
        final TelecomManager telecomManager = (TelecomManager)context.getSystemService((Class)TelecomManager.class);
        telecomManager.registerPhoneAccount(account);
        return phoneAccountHandle;
    }
    
    static {
        connections = new HashMap<String, ConnectionImpl>();
        startCallPromises = new HashMap<String, Promise>();
    }
    
    class ConnectionImpl extends Connection
    {
        static final String KEY_HAS_VIDEO = "hasVideo";
        
        public void onDisconnect() {
            JitsiMeetLogger.i("JitsiConnectionService onDisconnect " + this.getCallUUID(), new Object[0]);
            final WritableNativeMap data = new WritableNativeMap();
            data.putString("callUUID", this.getCallUUID());
            ReactInstanceManagerHolder.emitEvent("org.jitsi.meet:features/connection_service#disconnect", data);
            ConnectionService.setConnectionDisconnected(this.getCallUUID(), new DisconnectCause(2));
        }
        
        public void onAbort() {
            JitsiMeetLogger.i("JitsiConnectionService onAbort " + this.getCallUUID(), new Object[0]);
            final WritableNativeMap data = new WritableNativeMap();
            data.putString("callUUID", this.getCallUUID());
            ReactInstanceManagerHolder.emitEvent("org.jitsi.meet:features/connection_service#abort", data);
            ConnectionService.setConnectionDisconnected(this.getCallUUID(), new DisconnectCause(4));
        }
        
        public void onHold() {
            JitsiMeetLogger.w("JitsiConnectionService onHold %s - HOLD is not supported, aborting the call...", this.getCallUUID());
            this.onAbort();
        }
        
        public void onCallAudioStateChanged(final CallAudioState state) {
            JitsiMeetLogger.d("JitsiConnectionService onCallAudioStateChanged: " + state, new Object[0]);
            final RNConnectionService module = ReactInstanceManagerHolder.getNativeModule(RNConnectionService.class);
            if (module != null) {
                module.onCallAudioStateChange(state);
            }
        }
        
        public void onStateChanged(final int state) {
            JitsiMeetLogger.d("%s onStateChanged: %s %s", "JitsiConnectionService", Connection.stateToString(state), this.getCallUUID());
            if (state == 6) {
                ConnectionService.removeConnection(this);
                ConnectionService.this.unregisterPhoneAccount(this.getPhoneAccountHandle());
            }
        }
        
        String getCallUUID() {
            return this.getPhoneAccountHandle().getId();
        }
        
        private PhoneAccountHandle getPhoneAccountHandle() {
            return (PhoneAccountHandle)this.getExtras().getParcelable("com.reactnativejitsimeet.sdk.connection_service.PHONE_ACCOUNT_HANDLE");
        }
        
        public String toString() {
            return String.format("ConnectionImpl[address=%s, uuid=%s]@%d", this.getAddress(), this.getCallUUID(), this.hashCode());
        }
    }
}
