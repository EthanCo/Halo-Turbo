package com.ethanco.sample;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ethanco.sample.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.tvMulServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionStartAty(MulticastServerActivity.class);
            }
        });

        binding.tvMulClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionStartAty(MulticastClientActivity.class);
            }
        });

        binding.tvMinaTcpClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionStartAty(MinaTcpClientActivity.class);
            }
        });

        binding.tvMinaTcpServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionStartAty(MinaTcpServerActivity.class);
            }
        });
    }

    private void actionStartAty(Class<? extends Activity> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }
}