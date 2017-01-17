package com.ethanco.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.type.Mode;
import com.ethanco.sample.databinding.ActivityMulticastServerBinding;

import java.io.IOException;
import java.util.concurrent.Executors;

public class MulticastServerActivity extends AppCompatActivity {

    public static final String TAG = "Z-Server";
    private ActivityMulticastServerBinding binding;
    private Halo halo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_multicast_server);

        binding.btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halo = new Halo.Builder()
                        .setMode(Mode.MULTICAST)
                        .setSourcePort(19602)
                        .setTargetPort(19601)
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
        });
    }

    class DemoHandler implements IHandler {

        @Override
        public void sessionCreated(ISession session) {
            Log.i(TAG, "sessionCreated");
            binding.tvInfo.append("sessionCreated\r\n");
        }

        @Override
        public void sessionOpened(ISession session) {
            Log.i(TAG, "sessionOpened");
            binding.tvInfo.append("sessionOpened\r\n");
        }

        @Override
        public void sessionClosed(ISession session) {
            Log.i(TAG, "sessionClosed");
            binding.tvInfo.append("sessionClosed\r\n");
        }

        @Override
        public void messageReceived(ISession session, Object message) {
            String result = new String((byte[]) message).trim();
            binding.tvInfo.append("messageReceived data:" + result + "\r\n");

            session.write("------>>>> 回复你的");
        }

        @Override
        public void messageSent(ISession session, Object message) {
            String result = new String((byte[]) message).trim();
            Log.i(TAG, "messageSent data:" + result);
            binding.tvInfo.append("messageSent data:" + result + "\r\n");
        }
    }
}
