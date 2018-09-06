/*
 * Copyright 2018 ARP Network
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arpnetwork.arpdemo.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.arpnetwork.arpdemo.R;
import org.arpnetwork.arpdemo.data.DeviceInfo;
import org.arpnetwork.arpdemo.page.view.H264RawView;
import org.arpnetwork.arpdemo.protocol.ServerProtocol;

public class PlayActivity extends Activity implements H264RawView.OnRenderListener {
    private static final int UPDATE_STATE_INTERVAL = 10000;

    private H264RawView mH264RawView;
    private ProgressBar mIndicatorView;

    private String mSession;

    private boolean mClosed = false;
    private Handler mUpdateConnectingStateHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initViews();
        getData();
    }

    @Override
    public void onBackPressed() {
        mH264RawView.stop();
        setDisconnectedState();
        super.onBackPressed();
    }

    @Override
    public void onPrepared() {
        mIndicatorView.setVisibility(View.GONE);
        updateConnectingState();
    }

    @Override
    public void onClosed() {
        setDisconnectedState();
    }

    @Override
    public void onError(int errorCode, String msg) {
        setDisconnectedState();
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getData() {
        Intent intent = getIntent();
        String host = intent.getStringExtra("HOST");
        int port = intent.getIntExtra("PORT", 0);
        mSession = intent.getStringExtra("SESSION");
        String packageName = intent.getStringExtra("PACKAGE");

        mH264RawView.connectRemoteDevice(host, port, mSession, packageName);
    }

    private void initViews() {
        mH264RawView = findViewById(R.id.video_view);
        mH264RawView.setRenderLister(this);
        addIndicatorView();
    }

    private void addIndicatorView() {
        mIndicatorView = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(80, 80);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mIndicatorView.setLayoutParams(layoutParams);

        ((ViewGroup) mH264RawView.getParent()).addView(mIndicatorView);
    }

    private void updateConnectingState() {
        ServerProtocol.setConnectionState(this, mSession, DeviceInfo.STATE_CONNECTING);
        mUpdateConnectingStateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateConnectingState();
            }
        }, UPDATE_STATE_INTERVAL);
    }

    private void setDisconnectedState() {
        if (mClosed) return;
        mUpdateConnectingStateHandler.removeCallbacksAndMessages(null);
        ServerProtocol.setConnectionState(this, mSession, DeviceInfo.STATE_DISCONNECTED);
        mClosed = true;
    }
}
