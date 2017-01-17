package com.ethanco.halo.turbo.ads;

import com.ethanco.halo.turbo.bean.Config;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class AbstractSocket implements ISocket {
    protected Config config;


    public AbstractSocket(Config config) {
        this.config = config;
        //init(config);
    }

}
