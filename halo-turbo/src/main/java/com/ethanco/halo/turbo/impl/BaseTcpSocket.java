package com.ethanco.halo.turbo.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ethanco.halo.turbo.ads.absSocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.utils.HexUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class BaseTcpSocket<T> extends absSocket<T> {

    public static final int WHAT_ONREVEIVE = 3435;
    protected final ExecutorService threadPool;
    protected final int bufferSize;

    protected Handler H = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_ONREVEIVE) {
                T t = (T) msg.obj;
                for (ReceiveListener<T> mReceiveListener : mReceiveListeners) {
                    mReceiveListener.onReceive(t);
                }

                for (SocketListener<T> mSocketListener : mSocketListeners) {
                    mSocketListener.onReceive(t);
                }
            }
        }
    };

    @Override
    public void stop() {
        if (H != null) {
            H.removeCallbacksAndMessages(null);
        }
    }

    public BaseTcpSocket(Config config) {
        super(config);
        this.threadPool = config.threadPool;
        this.bufferSize = config.bufferSize;
    }

    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    protected void readStream(final InputStream inStream) {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        DataInputStream dis = new DataInputStream(inStream);
        byte[] buffer = new byte[bufferSize];
        int len = -1;
        try {

            Log.i("Z-", "run read: ");
            while ((len = dis.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                byte[] data = outSteam.toByteArray();

                Log.i("Z-", "run : " + HexUtil.bytesToHexString(data));

                T t = convert(data);
                Message.obtain(H, WHAT_ONREVEIVE, t).sendToTarget();

                outSteam.flush();
                outSteam.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO error处理
        }
    }

    abstract T convert(byte[] data);
}
