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
import com.ethanco.halo.turbo.utils.HexUtil;
import com.ethanco.sample.databinding.ActivityMinaTcpServerBinding;

import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;

public class MinaTcpServerByteActivity extends AppCompatActivity {

    private static final String TAG = "Z-MinaTcpServerByteActivity";
    private ActivityMinaTcpServerBinding binding;
    private ScrollBottomTextWatcher watcher;
    private Halo halo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mina_tcp_server);

        halo = new Halo.Builder()
                .setMode(Mode.MINA_NIO_TCP_SERVER)
                .setBufferSize(2048)
                .setSourcePort(19701)
                //.addHandler(new StringLogHandler(TAG))
                .addHandler(new HexLogHandler(TAG))
                .addHandler(new DemoHandler())
                .setCodec(new ObjectSerializationCodecFactory())
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

        watcher = new ScrollBottomTextWatcher(binding.scrollView);
        binding.tvInfo.addTextChangedListener(watcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (halo != null) {
            halo.stop();
        }

        if (watcher != null) {
            binding.tvInfo.removeTextChangedListener(watcher);
            watcher = null;
        }
    }

    class DemoHandler extends IHandlerAdapter {
        @Override
        public void messageReceived(ISession session, Object message) {
            //session.write("这是服务端，我已经收到数据了 --->>>666");
            session.write(new byte[]{0x09, 0x09, 0x09});
            printInfo(message);
        }

        @Override
        public void messageSent(ISession session, Object message) {
            super.messageSent(session, message);
            printInfo(message);
        }
    }

    private void printInfo(Object message) {
        if (message instanceof byte[] || message instanceof Byte[]) {
            binding.tvInfo.append("接收:" + HexUtil.bytesToHexString((byte[]) message) + "\r\n");
        } else {
            binding.tvInfo.append("接收:" + message + "\r\n");
        }
    }
}
