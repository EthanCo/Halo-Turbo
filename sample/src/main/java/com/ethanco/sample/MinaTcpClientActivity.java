package com.ethanco.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandlerAdapter;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.impl.LogHandler;
import com.ethanco.halo.turbo.type.Mode;

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
                        .setMode(Mode.MINA_NIO_TCP_CLIENT)
                        .setBufferSize(2048)
                        .setTargetIP("192.168.39.103")
                        .setTargetPort(19701)
                        .addHandler(new LogHandler(TAG))
                        .addHandler(new DemoHandler())
                        .build();

                halo.start();
            }
        }.start();
    }

    class DemoHandler extends IHandlerAdapter {

        @Override
        public void sessionOpened(final ISession session) {
            session.write("hello aaabbb");
        }
    }
}
