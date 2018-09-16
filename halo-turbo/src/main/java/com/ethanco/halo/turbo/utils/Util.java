package com.ethanco.halo.turbo.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class Util {
    public static String HALO = "halo";

    public static void println(String tag, String message) {
        System.out.println(tag + ": " + message);
    }

    public static void println(String message) {
        println("ethanco", message);
    }

    /**
     * 获取本地地址
     *
     * @return bytes
     */
    public static byte[] getLocalIP() {
        try {
            for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                    .getNetworkInterfaces(); mEnumeration.hasMoreElements(); ) {

                NetworkInterface intf = mEnumeration.nextElement();

                for (Enumeration<InetAddress> enumIPAddress = intf
                        .getInetAddresses(); enumIPAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIPAddress.nextElement();

                    // 如果不是回环地址
                    if (!inetAddress.isLoopbackAddress() && Inet4Address.class.isInstance(inetAddress)) {

                        // 直接返回本地IP地址
                        return inetAddress.getAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            System.err.print("error");
        }
        return new byte[4];
    }
}
