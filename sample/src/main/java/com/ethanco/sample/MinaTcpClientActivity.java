package com.ethanco.sample;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.type.Mode;

import java.io.IOException;

public class MinaTcpClientActivity extends AppCompatActivity {

    private static final String TAG = "Z-MinaTcpClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mina_tcp_client);

        new Thread() {
            @Override
            public void run() {
                Halo halo = new Halo.Builder()
                        .setMode(Mode.NIO_TCP_CLIENT)
                        .setBufferSize(2048)
                        .setTargetIP("192.168.39.103")
                        .setTargetPort(19701)
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
        public void sessionOpened(final ISession session) {
            Log.i(TAG, "sessionOpened");
            session.write("hello aaabbb");
            new Thread() {
                @Override
                public void run() {
                    SystemClock.sleep(5000);
                    Log.i(TAG, "write bbccc");
                    session.write("hello bbbccc");
                }
            }.start();
        }

        @Override
        public void sessionClosed(ISession session) {
            Log.i(TAG, "sessionClosed");
        }

        @Override
        public void messageReceived(ISession session, Object message) {
            Log.i(TAG, "messageReceived data:" + message);
        }

        @Override
        public void messageSent(ISession session, Object message) {
            Log.i(TAG, "messageSent data:" + message);
        }
    }
}
