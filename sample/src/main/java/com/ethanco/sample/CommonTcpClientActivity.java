package com.ethanco.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ethanco.halo.turbo.utils.HexUtil;
import com.ethanco.sample.databinding.ActivityMinaTcpClientBinding;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class CommonTcpClientActivity extends AppCompatActivity {

    private static final String TAG = "Z-MinaTcpClientActivity";
    private ActivityMinaTcpClientBinding binding;
    private ScrollBottomTextWatcher watcher;
    private Socket client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mina_tcp_client);

        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String targetIP = binding.etTargetIp.getText().toString();
                if (TextUtils.isEmpty(targetIP)) {
                    Toast.makeText(CommonTcpClientActivity.this, "目标IP不能为空", Toast.LENGTH_SHORT).show();
                }

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            client = new Socket(targetIP, 19701);
                            //client = new Socket(targetIP, 21501);

                            InputStream in = client.getInputStream();
                            final byte b[] = new byte[1024];
                            int len = 0;
                            int temp = -1;
                            Log.i(TAG, "read--->>>");
                            while ((temp = in.read()) != -1) {
                                Log.i(TAG, "read...:" + HexUtil.bytesToHexString(new byte[]{(byte) temp}));
                                b[len] = (byte) temp;
                                String s = new String(b);
                                Log.i(TAG, "read1...:" + s + " indexOf:" + s.indexOf("\n"));
                                len++;
                            }
                            in.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvInfo.append(new String(b));
                                }
                            });

                            /*String line;
                            StringBuffer stringBuffer = new StringBuffer();
                            InputStream in = client.getInputStream();
                            Reader reader = new InputStreamReader(in, "UTF-8");
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            Log.i(TAG, "pre read");
                            while ((line = bufferedReader.readLine()) != null) {
                                Log.i(TAG, "readLine...:" + line + " concat:" + line.indexOf("\r\n"));
                                stringBuffer.append(line);
                                final String finalLine = line;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.tvInfo.append("接收:" + finalLine);
                                    }
                                });
                            }
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            final String content = stringBuffer.toString();*/

                        } catch (final IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toast(e);
                                }
                            });
                        }
                    }
                }.start();
            }
        });

        binding.btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning()) {
                    Toast.makeText(CommonTcpClientActivity.this, "未建立连接", Toast.LENGTH_SHORT).show();
                }

                try {
                    BufferedWriter myBufferedWriter = new BufferedWriter(
                            new OutputStreamWriter(client.getOutputStream()));
                    //final String data = "hello world!333\r\n";
                    //final String data = "hello world!666\n";
                    //final String data = "hello world!999";
                    final String data = "{\"cmd\": \"control\",\"params\": {\"playstate\": \"next\"}}\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvInfo.append("发送:" + data);
                        }
                    });
                    myBufferedWriter.write(data, 0, data.length());
                    myBufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.btnSendDataEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning()) {
                    Toast.makeText(CommonTcpClientActivity.this, "未建立连接", Toast.LENGTH_SHORT).show();
                }

                try {
                    BufferedWriter myBufferedWriter = new BufferedWriter(
                            new OutputStreamWriter(client.getOutputStream()));
                    final String data = "\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvInfo.append("发送:" + data);
                        }
                    });
                    myBufferedWriter.write(data, 0, data.length());
                    myBufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning()) {
                    try {
                        client.close();
                        client = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        watcher = new ScrollBottomTextWatcher(binding.scrollView);
        binding.tvInfo.addTextChangedListener(watcher);
    }

    private boolean isRunning() {
        return client != null && client.isConnected();
    }

    private void toast(IOException e) {
        Toast.makeText(CommonTcpClientActivity.this, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (watcher != null) {
            binding.tvInfo.removeTextChangedListener(watcher);
            watcher = null;
        }
    }
}
