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
import com.ethanco.sample.databinding.ActivityMinaTcpServerBinding;

public class MinaTcpServerActivity extends AppCompatActivity {

    private static final String TAG = "Z-MinaTcpServerActivity";
    private ActivityMinaTcpServerBinding binding;
    private Halo halo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mina_tcp_server);

        halo = new Halo.Builder()
                .setMode(Mode.MINA_NIO_TCP_SERVER)
                .setBufferSize(2048)
                .setSourcePort(19701)
                .addHandler(new LogHandler(TAG))
                .addHandler(new DemoHandler())
                .build();

        binding.btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        halo.start();
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (halo != null) {
            halo.stop();
        }
    }

    class DemoHandler extends IHandlerAdapter {
        @Override
        public void messageReceived(ISession session, Object message) {
            session.write("---==>666哈哈 00-00");
        }
    }
}
