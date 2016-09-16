package com.ethanco.halo.turbo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by EthanCo on 2016/9/14.
 */
public abstract class TcpClientSocket<T> extends BaseTcpSocket<T> {

    private Socket socket;
    private OutputStream outputStream;

    public TcpClientSocket(Config config) {
        super(config);
    }

    @Override
    public void init(Config config) {
        try {
            socket = new Socket(config.ip, config.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (socket != null && socket.isConnected()) {
                    try {
                        readStream(socket.getInputStream());
                        synchronized (this) {
                            outputStream = socket.getOutputStream();
                        }
                    } catch (IOException e) {
                        /*if (clientCanReceive) {
                            clientCanReceive = false;
                            beginReceive(maxBufferSize);
                        }*/
                    }
                }
            }
        });
    }

    @Override
    public void stop() {
        if (socket == null) {
            return;
        }

        try {
            if (outputStream != null) {
                outputStream.close();
            }
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(byte[] buffer, int offset, int length) {
        synchronized (this) {
            if (null != outputStream) {
                try {
                    DataOutputStream out = new DataOutputStream(outputStream);
                    out.write(buffer, offset, length);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //CloseUtils.closeQuietly(out);
                }
            }
        }
    }
}
