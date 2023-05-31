// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk.net;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class NAT64AddrInfo
{
    private final String prefix;
    private final String suffix;
    
    static String bytesToHexString(final byte[] bytes) {
        final StringBuilder hexStr = new StringBuilder();
        for (final byte b : bytes) {
            hexStr.append(String.format("%02X", b));
        }
        return hexStr.toString();
    }
    
    public static NAT64AddrInfo discover(final String host) throws UnknownHostException {
        InetAddress ipv4 = null;
        InetAddress ipv5 = null;
        for (final InetAddress addr : InetAddress.getAllByName(host)) {
            final byte[] bytes = addr.getAddress();
            if (bytes.length == 4) {
                ipv4 = addr;
            }
            else if (bytes.length == 16) {
                ipv5 = addr;
            }
        }
        if (ipv4 != null && ipv5 != null) {
            return figureOutNAT64AddrInfo(ipv4.getAddress(), ipv5.getAddress());
        }
        return null;
    }
    
    static NAT64AddrInfo figureOutNAT64AddrInfo(final byte[] ipv4AddrBytes, final byte[] ipv6AddrBytes) {
        String ipv6Str = bytesToHexString(ipv6AddrBytes);
        final String ipv4Str = bytesToHexString(ipv4AddrBytes);
        int prefixLength = 96;
        int suffixLength = 0;
        String prefix = null;
        String suffix = null;
        if (ipv4Str.equalsIgnoreCase(ipv6Str.substring(prefixLength / 4))) {
            prefix = ipv6Str.substring(0, prefixLength / 4);
        }
        else {
            ipv6Str = ipv6Str.substring(0, 16) + ipv6Str.substring(18);
            for (prefixLength = 64, suffixLength = 6; prefixLength >= 32; prefixLength -= 8, suffixLength += 2) {
                if (ipv4Str.equalsIgnoreCase(ipv6Str.substring(prefixLength / 4, prefixLength / 4 + 8))) {
                    prefix = ipv6Str.substring(0, prefixLength / 4);
                    suffix = ipv6Str.substring(ipv6Str.length() - suffixLength);
                    break;
                }
            }
        }
        return (prefix != null) ? new NAT64AddrInfo(prefix, suffix) : null;
    }
    
    static String hexStringToIPv6String(final String hexStr) {
        return hexStringToIPv6String(new StringBuilder(hexStr));
    }
    
    static String hexStringToIPv6String(final StringBuilder str) {
        for (int i = 28; i > 0; i -= 4) {
            str.insert(i, ":");
        }
        return str.toString().toUpperCase();
    }
    
    static byte[] ipv4AddressStringToBytes(final String ipv4Address) {
        InetAddress address;
        try {
            address = InetAddress.getByName(ipv4Address);
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + ipv4Address, e);
        }
        final byte[] bytes = address.getAddress();
        if (bytes.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address: " + ipv4Address);
        }
        return bytes;
    }
    
    private NAT64AddrInfo(final String prefix, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    public String getIPv6Address(final String ipv4Address) {
        final byte[] ipv4AddressBytes = ipv4AddressStringToBytes(ipv4Address);
        final StringBuilder newIPv6Str = new StringBuilder();
        newIPv6Str.append(this.prefix);
        newIPv6Str.append(bytesToHexString(ipv4AddressBytes));
        if (this.suffix != null) {
            newIPv6Str.insert(16, "00");
            newIPv6Str.append(this.suffix);
        }
        return hexStringToIPv6String(newIPv6Str);
    }
}
