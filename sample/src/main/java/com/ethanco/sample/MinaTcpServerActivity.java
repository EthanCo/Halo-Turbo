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
                .addHandler(new StringLogHandler(TAG))
                .addHandler(new DemoHandler())
                .build();

        binding.btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        if (halo.isRunning()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplication(), "已启动", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        final boolean startSuccess = halo.start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String startResult = startSuccess ? "启动成功" : "启动失败";
                                binding.tvInfo.append(startResult + "\r\n");
                            }
                        });
                    }
                }.start();
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
        public void messageReceived(ISession session, Object message) {
            session.write("这是服务端，我已经收到数据了 --->>>666");
            binding.tvInfo.append("接收:" + message + "\r\n");
        }

        @Override
        public void messageSent(ISession session, Object message) {
            super.messageSent(session, message);
            binding.tvInfo.append("发送:" + message + "\r\n");
        }
    }
}
