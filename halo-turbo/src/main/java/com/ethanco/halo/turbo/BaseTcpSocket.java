package com.ethanco.halo.turbo;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class BaseTcpSocket<T> extends absSocket<T> {

    protected final ExecutorService threadPool;
    protected final int bufferSize;

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
        //threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
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

                        for (ReceiveListener<T> mReceiveListener : mReceiveListeners) {
                            mReceiveListener.onReceive(t);
                        }

                        for (SocketListener<T> mSocketListener : mSocketListeners) {
                            mSocketListener.onReceive(t);
                        }

                        outSteam.flush();
                        outSteam.reset();
                    }
                } catch (IOException e) {
                    /*if (clientCanReceive) {
                        clientCanReceive = false;
                        beginReceive(MAX_BUFFER_SIZE);
                    }*/
                }
            }
        //});
    //}

    abstract T convert(byte[] data);
}
