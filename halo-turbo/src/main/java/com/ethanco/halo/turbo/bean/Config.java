package com.ethanco.halo.turbo.bean;

import com.ethanco.halo.turbo.type.Mode;

import java.util.concurrent.ExecutorService;

/**
 * Created by EthanCo on 2016/9/16.
 */
public class Config {
    //protected Type type;
    public Mode mode;
    public String ip;
    public int port;
    public int bufferSize;
    public ExecutorService threadPool;
}
