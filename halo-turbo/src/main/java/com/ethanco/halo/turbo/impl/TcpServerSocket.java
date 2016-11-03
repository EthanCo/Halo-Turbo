package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.bean.Config;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class TcpServerSocket<T> extends BaseTcpSocket<T> {
    private ServerSocket socket;
    private Socket sendSocket;

    public TcpServerSocket(final Config config) {
        super(config);
    }

    @Override
    public void start() {
        onStarted();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                init();

                while (isRunning()) {
                    try {
                        sendSocket = socket.accept();
                        InputStream in = sendSocket.getInputStream();
                        readStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onError(Error.SERVER_RECEIVE, e);
                    }
                }
            }
        });
    }

    private void init() {
        if (!isRunning()) {
            try {
                state = State.STARTING;
                socket = new ServerSocket(config.port);
                state = State.STARTED;
            } catch (IOException e) {
                e.printStackTrace();
                onError(Error.SERVER_INIT, e);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (state == State.STOPED) {
            return;
        }

        onStoped();

        try {
            state = State.STOPING;
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            state = State.STOPED;
        }
    }

    @Override
    public void send(final byte[] buffer, final int offset, final int length) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    OutputStream outputStream = sendSocket.getOutputStream();
                    if (null != outputStream) {
                        DataOutputStream out = new DataOutputStream(outputStream);
                        out.write(buffer, offset, length);
                        out.flush();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(Error.SERVER_SEND, e);
                }
            }
        });
    }
}
