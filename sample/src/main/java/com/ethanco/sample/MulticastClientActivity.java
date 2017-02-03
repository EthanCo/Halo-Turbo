package com.ethanco.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.IHandlerAdapter;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.impl.handler.HexLogHandler;
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

        halo = new Halo.Builder()
                .setMode(Mode.MULTICAST)
                .setSourcePort(19601)
                .setTargetPort(19602)
                .setTargetIP("224.0.0.1")
                .setBufferSize(512)
                .addHandler(new HexLogHandler(TAG))
                //.addHandler(new StringLogHandler(TAG))
                .addHandler(new DemoHandler())
                .setThreadPool(Executors.newCachedThreadPool())
                .build();

        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (halo.isRunning()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MulticastClientActivity.this, "已启动", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                boolean result = halo.start();
                String startSuccess = result ? "连接成功" : "连接失败";
                binding.tvInfo.append(startSuccess + "\r\n");
            }
        });

        binding.btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.write("send-112233");
            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (halo != null) {
                    halo.stop();
                    binding.tvInfo.append("停止连接" + "\r\n");
                }
            }
        });

        binding.tvInfo.addTextChangedListener(new ScrollBottomTextWatcher(binding.scrollView));
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
            binding.tvInfo.append("messageReceived data:" + message + "\r\n");
        }

        @Override
        public void messageSent(ISession session, Object message) {
            binding.tvInfo.append("messageSent data:" + message + "\r\n");
        }
    }
}
