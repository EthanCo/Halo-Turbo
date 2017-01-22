package com.ethanco.halo.turbo;

import com.ethanco.halo.turbo.ads.AbstractHalo;
import com.ethanco.halo.turbo.ads.IConvertor;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.type.Mode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class Halo extends AbstractHalo {
    private ISocket haloImpl;

    public Halo() {
        this(new Builder());
    }

    public Halo(Builder builder) {
        this.haloImpl = SocketFactory.create(builder);
    }


    @Override
    public boolean start() {
        return this.haloImpl.start();
    }

    @Override
    public void stop() {
        haloImpl.stop();
    }

    @Override
    public List<IHandler> getHandlers() {
        return this.haloImpl.getHandlers();
    }

    @Override
    public void addHandler(IHandler handler) {
        this.haloImpl.addHandler(handler);
    }

    @Override
    public boolean removeHandler(IHandler handler) {
        return this.haloImpl.removeHandler(handler);
    }

    @Override
    public boolean isRunning() {
        return haloImpl.isRunning();
    }

    public static class Builder extends Config {

        private ISocket ihalo;

        public Builder() {
            this.mode = Mode.MINA_NIO_TCP_CLIENT;
            this.targetIP = "192.168.1.1";
            this.targetPort = 19600;
            //this.sourceIP = "192.168.1.1";
            this.sourcePort = 19700;
            this.bufferSize = 1024;
            this.handlers = new ArrayList<>();
            this.convertors = new ArrayList<>();
            //需要的自行进行初始化
            //this.threadPool = Executors.newCachedThreadPool();
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

        public Builder addHandler(IHandler handler) {
            if (!this.handlers.contains(handler)) {
                this.handlers.add(handler);
            }
            return this;
        }

        //添加转换器
        public Builder addConvert(IConvertor convertor) {
            if (!convertors.contains(convertor)) {
                this.convertors.add(convertor);
            }
            return this;
        }

        //这是自定义的转换器列表
        public Builder setConverts(List<IConvertor> convertors) {
            this.convertors = convertors;
            return this;
        }

        public Halo build() {
            return new Halo(this);
        }
    }
}
