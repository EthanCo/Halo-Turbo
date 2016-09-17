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
                    synchronized (TcpClientSocket.this) {
                        init();
                        outputStream = socket.getOutputStream();
                    }
                    readStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO 重试机制
                }
                //}
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if (socket == null) {
            return;
        }

        onStop();

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
                synchronized (TcpClientSocket.this) {
                    if (outputStream == null) {
                        try {
                            TcpClientSocket.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != outputStream) {
                        try {
                            DataOutputStream out = new DataOutputStream(outputStream);
                            out.write(buffer, offset, length);
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                            //TODO 重试机制
                        }
                    }
                }
            }
        });
    }
}
