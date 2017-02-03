package com.ethanco.sample;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;

/**
 * TODO
 *
 * @author EthanCo
 * @since 2017/1/24
 */

public class ScrollBottomTextWatcher implements TextWatcher {

    private WeakReference<ScrollView> scrollViewRef;

    public ScrollBottomTextWatcher(ScrollView scrollView) {
        this.scrollViewRef = new WeakReference(scrollView);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        final ScrollView scrollview = scrollViewRef.get();
        if (scrollview == null) {
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
