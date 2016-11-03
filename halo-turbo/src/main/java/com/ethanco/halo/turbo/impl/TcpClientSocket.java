package com.ethanco.halo.turbo.impl;

import android.os.SystemClock;

import com.ethanco.halo.turbo.bean.Config;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by EthanCo on 2016/9/14.
 */
public abstract class TcpClientSocket<T> extends BaseTcpSocket<T> {
    private Socket socket;

    public TcpClientSocket(final Config config) {
        super(config);
    }

    @Override
    public void start() {
        onStart();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    readStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO 重试机制
                }
            }
        });
    }

    private void onStart() {
        for (SocketListener<T> mSocketListener : mSocketListeners) {
            mSocketListener.onStart();
        }
    }

    private void init() {
        if (socket == null) {
            try {
                socket = new Socket(config.ip, config.port);
                runningFlag = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (socket == null) {
            return;
        }

        onStop();

        try {
            runningFlag = false;
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onStop() {
        for (SocketListener<T> mSocketListener : mSocketListeners) {
            mSocketListener.onStop();
        }
    }

    @Override
    public void send(final byte[] buffer, final int offset, final int length) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null)
                        SystemClock.sleep(200);
                    OutputStream outputStream = socket.getOutputStream();
                    if (null != outputStream) {
                        DataOutputStream out = new DataOutputStream(outputStream);
                        out.write(buffer, offset, length);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO 重试机制
                }
            }
        });
    }
}
