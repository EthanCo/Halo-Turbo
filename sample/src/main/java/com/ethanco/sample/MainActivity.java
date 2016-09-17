package com.ethanco.sample;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.HexUtil;
import com.ethanco.halo.turbo.ISocket;
import com.ethanco.halo.turbo.Mode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Z-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Halo.Builder builder = new Halo.Builder()
                .setMode(Mode.TCP_SERVICE)
                .setPort(8881)
                .setBufferSize(1024);

        Halo server = builder.build();
        server.addSocketListener(new ISocket.SocketListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart : ");
            }

            @Override
            public void onStop() {
                Log.i(TAG, "onStop : ");
            }

            @Override
            public void onReceive(Object buffer) {
                Log.i(TAG, "onReceive : " + HexUtil.bytesToHexString((byte[]) buffer));
            }
        });
        server.start();


        SystemClock.sleep(1000);
        clienttest();
    }

    public void clienttest(){
        Halo.Builder builder = new Halo.Builder()
                .setMode(Mode.TCP_CLIENT)
                .setIp("127.0.0.1")
                .setPort(8881)
                .setBufferSize(1024);

        Halo client = builder.build();
        client.start();
        //SystemClock.sleep(1000);
        client.send(new byte[]{11,22,33,44});

    }
}
