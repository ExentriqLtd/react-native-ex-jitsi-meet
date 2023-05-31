// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk.net;

import com.facebook.react.bridge.ReactMethod;
import java.net.UnknownHostException;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

@ReactModule(name = "NAT64AddrInfo")
public class NAT64AddrInfoModule extends ReactContextBaseJavaModule
{
    public static final String NAME = "NAT64AddrInfo";
    private static final String HOST = "ipv4only.arpa";
    private static final long INFO_LIFETIME = 60000L;
    private static final String TAG = "NAT64AddrInfo";
    private NAT64AddrInfo info;
    private long infoTimestamp;
    
    public NAT64AddrInfoModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }
    
    @ReactMethod
    public void getIPv6Address(final String ipv4Address, final Promise promise) {
        if (System.currentTimeMillis() - this.infoTimestamp > 60000L) {
            this.info = null;
        }
        if (this.info == null) {
            final String host = "ipv4only.arpa";
            try {
                this.info = NAT64AddrInfo.discover(host);
            }
            catch (UnknownHostException e) {
                JitsiMeetLogger.e(e, "NAT64AddrInfo NAT64AddrInfo.discover: " + host, new Object[0]);
            }
            this.infoTimestamp = System.currentTimeMillis();
        }
        String result;
        try {
            result = ((this.info == null) ? null : this.info.getIPv6Address(ipv4Address));
        }
        catch (IllegalArgumentException exc) {
            JitsiMeetLogger.e(exc, "NAT64AddrInfo Failed to get IPv6 address for: " + ipv4Address, new Object[0]);
            result = null;
        }
        promise.resolve((Object)result);
    }
    
    public String getName() {
        return "NAT64AddrInfo";
    }
}
