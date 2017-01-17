package com.ethanco.halo.turbo;

import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.ads.AbstractHalo;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.type.Mode;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class Halo extends AbstractHalo {

    private ISocket haloImpl;

    public Halo() {
        this(new Builder());
    }

    private Halo(Builder builder) {
        this.haloImpl = SocketFactory.create(builder);
    }


    @Override
    public void connected() throws IOException {
        this.haloImpl.connected();
    }

    @Override
    public void dispose() {
        haloImpl.dispose();
    }

    @Override
    public IHandler getHandler() {
        return this.haloImpl.getHandler();
    }

    @Override
    public void setHandler(IHandler handler) {
        this.haloImpl.setHandler(handler);
    }

    @Override
    public boolean isRunning() {
        return haloImpl.isRunning();
    }

    public static class Builder extends Config {

        private ISocket ihalo;

        public Builder() {
            this.mode = Mode.NIO_TCP_CLIENT;
            this.targetIP = "192.168.1.1";
            this.targetPort = 19600;
            //this.sourceIP = "192.168.1.1";
            this.sourcePort = 19700;
            this.bufferSize = 1024;
            this.threadPool = Executors.newCachedThreadPool();
        }

        public Builder setMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder setTargetIP(String targetIP) {
            this.targetIP = targetIP;
            return this;
        }

        public Builder setTargetPort(int targetPort) {
            this.targetPort = targetPort;
            return this;
        }

        /*public Builder setSourceIP(String sourceIP) {
            this.sourceIP = sourceIP;
            return this;
        }*/

        public Builder setSourcePort(int sourcePort) {
            this.sourcePort = sourcePort;
            return this;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setIhalo(ISocket ihalo) {
            this.ihalo = ihalo;
            return this;
        }

        public Builder setThreadPool(ExecutorService threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        public Builder setHandler(IHandler handler) {
            this.handler = handler;
            return this;
        }

        public Halo build() {
            return new Halo(this);
        }
    }
}
