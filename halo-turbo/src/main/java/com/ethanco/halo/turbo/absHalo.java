package com.ethanco.halo.turbo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanCo on 2016/9/14.
 */
public abstract class absHalo<T> implements IHalo {

    protected List<ReceiveListener<T>> mReceiveListeners = new ArrayList<>();

    interface ReceiveListener<T> {
        void onReceive(T buffer);
    }

    public void addReceiveListener(ReceiveListener<T> receiveListener) {
        if (!mReceiveListeners.contains(receiveListener)) {
            mReceiveListeners.add(receiveListener);
        }
    }
}
