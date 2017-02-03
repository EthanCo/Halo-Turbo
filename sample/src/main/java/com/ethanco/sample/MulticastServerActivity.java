package com.ethanco.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandlerAdapter;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.impl.handler.StringLogHandler;
import com.ethanco.halo.turbo.type.Mode;
import com.ethanco.sample.databinding.ActivityMulticastServerBinding;

import java.util.concurrent.Executors;

public class MulticastServerActivity extends AppCompatActivity {

    public static final String TAG = "Z-Server";
    private ActivityMulticastServerBinding binding;
    private Halo halo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_multicast_server);

        halo = new Halo.Builder()
                .setMode(Mode.MULTICAST)
                .setSourcePort(19602)
                .setTargetPort(19601)
                .setTargetIP("224.0.0.1")
                .setBufferSize(512)
                //.addHandler(new ByteLogHandler(TAG))
                .addHandler(new StringLogHandler(TAG))
                .addHandler(new DemoHandler())
                .setThreadPool(Executors.newCachedThreadPool())
                .build();

        binding.btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (halo.isRunning()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MulticastServerActivity.this, "已启动", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                boolean result = halo.start();
                String startSuccess = result ? "启动成功" : "启动失败";
                binding.tvInfo.append(startSuccess + "\r\n");
            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (halo != null) {
                    halo.stop();
                    binding.tvInfo.append("停止启动" + "\r\n");
                }
            }
        });

        binding.tvInfo.addTextChangedListener(new ScrollBottomTextWatcher(binding.scrollView));
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
        public void sessionCreated(ISession session) {
            binding.tvInfo.append("sessionCreated\r\n");
        }

        @Override
        public void sessionOpened(ISession session) {
            binding.tvInfo.append("sessionOpened\r\n");
        }

        @Override
        public void sessionClosed(ISession session) {
            binding.tvInfo.append("sessionClosed\r\n");
        }

        @Override
        public void messageReceived(ISession session, Object message) {
            binding.tvInfo.append("messageReceived data:" + message + "\r\n");
            session.write("------>>>> 回复你的");
        }

        @Override
        public void messageSent(ISession session, Object message) {
            binding.tvInfo.append("messageSent data:" + message + "\r\n");
        }
    }
}
