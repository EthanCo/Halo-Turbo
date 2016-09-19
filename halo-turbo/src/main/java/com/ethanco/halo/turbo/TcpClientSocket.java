package com.ethanco.halo.turbo;

import android.os.SystemClock;

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
        onStarted();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    readStream(socket.getInputStream());
                    state = State.STARTED;
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(Error.CLIENT_START, e);
                }
            }
        });
    }

    private void init() {
        if (!isRunning()) {
            try {
                state = State.STARTING;
                socket = new Socket(config.ip, config.port);
                state = State.STARTED;
            } catch (IOException e) {
                e.printStackTrace();
                onError(Error.CLIENT_INIT, e);
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
                    if (!reSleep(50, 0, 1000)) return;
                    OutputStream outputStream = socket.getOutputStream();
                    if (null != outputStream) {
                        DataOutputStream out = new DataOutputStream(outputStream);
                        out.write(buffer, offset, length);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(Error.CLIENT_SEND, e);
                }
            }
        });
    }

    private boolean reSleep(int interval, int currBlock, final int maxBlock) {
        if (isRunning()) {
            return true;
        }
        SystemClock.sleep(interval);
        currBlock += interval;
        if (currBlock >= maxBlock) {
            return false;
        } else {
            return reSleep(interval, currBlock, maxBlock);
        }
    }
}
