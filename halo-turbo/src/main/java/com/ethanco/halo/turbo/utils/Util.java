package com.ethanco.halo.turbo.utils;

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
}
