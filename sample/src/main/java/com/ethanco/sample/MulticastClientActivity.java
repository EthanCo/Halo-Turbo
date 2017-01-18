package com.ethanco.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandlerAdapter;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.impl.LogHandler;
import com.ethanco.halo.turbo.type.Mode;
import com.ethanco.sample.databinding.ActivityMulticastClientBinding;

import java.util.concurrent.Executors;

public class MulticastClientActivity extends AppCompatActivity {

    private static final String TAG = "Z-Client";
    private ActivityMulticastClientBinding binding;
    private Halo halo;
    private ISession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_multicast_client);

        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halo = new Halo.Builder()
                        .setMode(Mode.MULTICAST)
                        .setSourcePort(19601)
                        .setTargetPort(19602)
                        .setTargetIP("224.0.0.1")
                        .setBufferSize(512)
                        .addHandler(new LogHandler(TAG))
                        .addHandler(new DemoHandler())
                        .setThreadPool(Executors.newCachedThreadPool())
                        .build();

                halo.start();
            }
        });

        binding.btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.write("send-112233");
            }
        });
    }

    class DemoHandler extends IHandlerAdapter {

        @Override
        public void sessionCreated(ISession session) {
            binding.tvInfo.append("sessionCreated\r\n");
        }

        @Override
        public void sessionOpened(ISession session) {
            MulticastClientActivity.this.session = session;
            //session.write("hello");
            binding.tvInfo.append("sessionOpened\r\n");
        }

        @Override
        public void sessionClosed(ISession session) {
            binding.tvInfo.append("sessionClosed\r\n");
        }

        @Override
        public void messageReceived(ISession session, Object message) {
            String result = new String((byte[]) message).trim();
            binding.tvInfo.append("messageReceived data:" + result + "\r\n");
        }

        @Override
        public void messageSent(ISession session, Object message) {
            String sendData = new String((byte[]) message).trim();
            binding.tvInfo.append("messageSent data:" + sendData + "\r\n");
        }
    }
}
