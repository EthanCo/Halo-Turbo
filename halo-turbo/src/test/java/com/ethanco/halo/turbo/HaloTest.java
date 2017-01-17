package com.ethanco.halo.turbo;

import com.ethanco.halo.turbo.type.Mode;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.Config;

/**
 * Created by EthanCo on 2016/9/14.
 */
@Config(constants = BuildConfig.class, sdk = 21)
public class HaloTest {
    private Halo halo;

    @Before
    public void setUp() {

        Halo.Builder builder = new Halo.Builder()
                .setTargetIP("192.168.2.1")
                .setTargetPort(8890)
                .setMode(Mode.NIO_TCP_CLIENT)
                //.setType(Type.UDP)
                .setBufferSize(1024 * 2);

        halo = builder.build();
    }

    @Test
    public void testLogic() {
        halo.connected();
        halo.send("hello");
        halo.dispose();
    }
}
