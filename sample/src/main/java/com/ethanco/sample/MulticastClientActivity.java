package com.ethanco.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.type.Mode;

import java.io.IOException;
import java.util.concurrent.Executors;

public class MulticastClientActivity extends AppCompatActivity {

    private static final String TAG = "Z-Client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multicast_client);

        new Thread() {
            @Override
            public void run() {
                Halo halo = new Halo.Builder()
                        .setMode(Mode.MULTICAST)
                        .setSourcePort(19601)
                        .setTargetPort(19602)
                        .setTargetIP("224.0.0.1")
                        .setBufferSize(512)
                        .setHandler(new DemoHandler())
                        .setThreadPool(Executors.newCachedThreadPool())
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
        public void sessionCreated(ISession var1) {
            Log.i(TAG, "sessionCreated");
        }

        @Override
        public void sessionOpened(ISession var1) {
            Log.i(TAG, "sessionOpened");
            var1.write("hello");
        }

        @Override
        public void sessionClosed(ISession var1) {
            Log.i(TAG, "sessionClosed");
        }

        @Override
        public void messageReceived(ISession var1, Object var2) {
            Log.i(TAG, "messageReceived data:" + new String((byte[]) var2).trim());
        }

        @Override
        public void messageSent(ISession var1, Object var2) {
            Log.i(TAG, "messageSent");
        }
    }
}
