package com.ethanco.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.type.Mode;
import com.ethanco.sample.databinding.ActivityMainBinding;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Z-MainActivity";
    private ActivityMainBinding binding;
    private Halo server;
    private Halo client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        binding.btnServerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServer();
            }
        });
        binding.btnServerSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.send("server:" + new Date().toString());
            }
        });
        binding.btnServerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.stop();
            }
        });


        binding.btnClientConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientConnect();
            }
        });
        binding.btnClientSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.send("client:" + new Date().toString());
            }
        });
        binding.btnClientStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.stop();
            }
        });
    }

    private void startServer() {
        Halo.Builder builder = new Halo.Builder()
                .setMode(Mode.TCP_SERVICE)
                .setPort(8881)
                .setBufferSize(1024);

        server = builder.build();
        server.addStateListener(new ISocket.StateListener() {
            @Override
            public void onStarted() {
                Log.i(TAG, "server onStarted : ");
            }

            @Override
            public void onStoped() {
                Log.i(TAG, "server onStoped : ");
            }
        });
        server.addReceiveListener(new ISocket.ReceiveListener() {
            @Override
            public void onReceive(Object buffer) {
                Log.i(TAG, "server onReceive : " + new String((byte[]) buffer));
                binding.tvServer.append(new String((byte[]) buffer)+"\n");
            }
        });
        server.start();
    }

    public void clientConnect() {
        Halo.Builder builder = new Halo.Builder()
                .setMode(Mode.TCP_CLIENT)
                .setIp("127.0.0.1")
                .setPort(8881)
                .setBufferSize(1024);

        client = builder.build();
        client.addStateListener(new ISocket.StateListener() {
            @Override
            public void onStarted() {
                Log.i(TAG, "client onStarted : ");
            }

            @Override
            public void onStoped() {
                Log.i(TAG, "client onStoped : ");
            }
        });
        client.addReceiveListener(new ISocket.ReceiveListener() {
            @Override
            public void onReceive(Object buffer) {
                Log.i(TAG, "client onReceive : " + new String((byte[]) buffer));
                binding.tvClient.append(new String((byte[]) buffer) + "\n");
            }
        });
        client.start();
        //client.send(new byte[]{11, 22, 33, 44});
    }
}
