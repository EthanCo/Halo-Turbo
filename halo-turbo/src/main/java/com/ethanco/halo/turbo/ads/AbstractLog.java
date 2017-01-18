package com.ethanco.halo.turbo.ads;

/**
 * @author EthanCo
 * @since 2017/1/18
 */

public abstract class AbstractLog implements ILog {
    protected String prefix = "";

    protected String getPrefix() {
        return prefix;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
