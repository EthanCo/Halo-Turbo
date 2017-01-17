package com.ethanco.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.type.Mode;

import java.io.IOException;

public class MinaTcpServerActivity extends AppCompatActivity {

    private static final String TAG = "Z-MinaTcpServerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mina_tcp_server);

        new Thread() {
            @Override
            public void run() {
                Halo halo = new Halo.Builder()
                        .setMode(Mode.NIO_TCP_SERVER)
                        .setBufferSize(2048)
                        .setSourcePort(19701)
                        .setHandler(new DemoHandler())
                        .build();
                try {
                    halo.connected();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class DemoHandler implements IHandler {

        @Override
        public void sessionCreated(ISession session) {
            Log.i(TAG, "sessionCreated");
        }

        @Override
        public void sessionOpened(ISession session) {
            Log.i(TAG, "sessionOpened");
            //session.write("hello bbb");
        }

        @Override
        public void sessionClosed(ISession session) {
            Log.i(TAG, "sessionClosed");
        }

        @Override
        public void messageReceived(ISession session, Object message) {
            Log.i(TAG, "messageReceived data:" + message);

            session.write("---==>666哈哈");
        }

        @Override
        public void messageSent(ISession session, Object message) {
            Log.i(TAG, "messageSent data:" + message);
        }
    }
}
