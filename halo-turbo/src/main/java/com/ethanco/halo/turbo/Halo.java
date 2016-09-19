package com.ethanco.halo.turbo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class Halo extends absHalo {

    private ISocket haloImpl;

    public Halo() {
        this(new Builder());
    }

    private Halo(Builder builder) {
        this.haloImpl = SocketFactory.create(builder);
    }

    @Override
    public void start() {
        haloImpl.start();
    }

    @Override
    public void stop() {
        haloImpl.stop();
    }

    @Override
    public void send(byte[] buffer, int offset, int length) {
        haloImpl.send(buffer, offset, length);
    }

    @Override
    public void send(byte[] buffer) {
        haloImpl.send(buffer);
    }

    @Override
    public void send(String str) {
        haloImpl.send(str);
    }

    @Override
    public boolean isRunning() {
        return haloImpl.isRunning();
    }

    @Override
    public void addReceiveListener(ReceiveListener receiveListener) {
        haloImpl.addReceiveListener(receiveListener);
    }

    @Override
    public void addSocketListener(SocketListener socketListener) {
        haloImpl.addSocketListener(socketListener);
    }

    public static class Builder extends Config {

        private ISocket ihalo;

        public Builder() {
            //this.type = Type.TCP;
            this.mode = Mode.TCP_CLIENT;
            this.ip = "192.168.1.1";
            this.port = 8800;
            this.bufferSize = 1024;
            this.threadPool = Executors.newCachedThreadPool();
        }

        /*public Builder setType(Type type) {
            //this.type = type;
            return this;
        }*/

        public Builder setMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
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

        public Halo build() {
            return new Halo(this);
        }
    }
}
