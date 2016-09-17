package com.ethanco.halo.turbo;

import android.util.Log;

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
        Log.e("Z-TcpClientSocket", "TcpClientSocket : ");

        Log.w("Z-TcpClientSocket", "init");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (TcpClientSocket.this) {
                    try {
                        socket = new Socket(config.ip, config.port);
                        Log.w("Z-TcpClientSocket", "init");
                        TcpClientSocket.this.notify();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    @Override
//    public void init(Config config) {
//        try {
//            socket = new Socket(config.ip, config.port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void start() {
        Log.e("Z-TcpClientSocket", "start : ");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.w("Z-TcpClientSocket", "run start: ");
                //if (socket != null && socket.isConnected()) {
                try {
                    synchronized (TcpClientSocket.this) {
                        Log.w("Z-TcpClientSocket", "run start sync: ");
                        if (socket == null) {
                            try {
                                Log.w("Z-TcpClientSocket", "run socket==null ");
                                TcpClientSocket.this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            /*try {
                                socket = new Socket(config.ip, config.port);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                        Log.w("Z-TcpClientSocket", "run start1: ");
//                        if (socket == null) {
//                            Log.w("Z-TcpClientSocket", "run start2: ");
//
//                        }
                        Log.w("Z-TcpClientSocket", "run start4: ");
                        outputStream = socket.getOutputStream();
                    }
                    //TcpClientSocket.this.notify();
                    //notifyAll();
                    Log.w("Z-TcpClientSocket", "run start5: ");
                    readStream(socket.getInputStream());
                    Log.w("Z-TcpClientSocket", "run start6: ");
                } catch (IOException e) {
                    Log.e("Z-TcpClientSocket", "run : " + e.getMessage());
                        /*if (clientCanReceive) {
                            clientCanReceive = false;
                            beginReceive(maxBufferSize);
                        }*/
                }
                //}
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
    public void send(final byte[] buffer, final int offset, final int length) {
        Log.e("Z-TcpClientSocket", "send : ");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.w("Z-TcpClientSocket", "run send: ");
                synchronized (TcpClientSocket.this) {
                    if (outputStream == null) {
                        try {
                            TcpClientSocket.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.w("Z-TcpClientSocket", "run send1: ");
                    if (null != outputStream) {
                        Log.w("Z-TcpClientSocket", "run send2: ");
                        try {
                            DataOutputStream out = new DataOutputStream(outputStream);
                            out.write(buffer, offset, length);
                            out.flush();
                            Log.w("Z-TcpClientSocket", "run send4: ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            //CloseUtils.closeQuietly(out);
                        }
                    }
                }
            }
        });
    }
}
