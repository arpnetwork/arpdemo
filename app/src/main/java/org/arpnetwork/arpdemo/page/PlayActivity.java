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
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.arpnetwork.arpclient.ARPClient;
import org.arpnetwork.arpclient.data.Quality;
import org.arpnetwork.arpdemo.R;
import org.arpnetwork.arpdemo.data.DeviceInfo;
import org.arpnetwork.arpdemo.protocol.ServerProtocol;
import org.arpnetwork.arpdemo.data.ErrorMessage;

public class PlayActivity extends Activity implements ARPClient.ARPClientListener {
    private static final String TAG = PlayActivity.class.getSimpleName();
    private static final int UPDATE_STATE_INTERVAL = 10000;

    private ARPClient mARPClient;
    private TextureView mRenderView;
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
        mARPClient.onBackPressed();
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
        Log.e(TAG, "onError: errorCode:" + errorCode + ", message:" + (msg == null ? "null" : msg));
        String showMsg = ErrorMessage.getSDKErrorMessage(this, errorCode);
        showAlert(showMsg, R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }, false);
    }

    private void endPlaying() {
        mARPClient.stop();
        setDisconnectedState();
        finish();
    }

    private void getData() {
        Intent intent = getIntent();
        String host = intent.getStringExtra("HOST");
        int port = intent.getIntExtra("PORT", 0);
        mSession = intent.getStringExtra("SESSION");
        String packageName = intent.getStringExtra("PACKAGE");

        connectRemoteDevice(host, port, mSession, packageName);
    }

    private void initViews() {
        mRenderView = findViewById(R.id.video_view);
        addIndicatorView();
        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(getString(R.string.exit_confirm), getString(R.string.ok), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                endPlaying();
                            }
                        }, null, true);
            }
        });
    }

    private void connectRemoteDevice(String host, int port, String session, String packageName) {
        mARPClient = new ARPClient(this, this);
        ARPClient.setQuality(Quality.HIGH);
        mARPClient.setSurfaceView(mRenderView);
        mARPClient.start(host, port, session, packageName);
    }

    private void addIndicatorView() {
        mIndicatorView = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(80, 80);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mIndicatorView.setLayoutParams(layoutParams);

        ((FrameLayout) findViewById(R.id.layout)).addView(mIndicatorView);
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

    private void showAlert(String title, int positiveBtnText,
            DialogInterface.OnClickListener onClickPositive, boolean cancelable) {
        showAlert(title, getString(positiveBtnText), null, onClickPositive, null, cancelable);
    }

    private void showAlert(String title, String positiveBtnText, String negativeBtnText,
            DialogInterface.OnClickListener onClickPositive,
            DialogInterface.OnClickListener onClickNegative, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title)
                .setPositiveButton(positiveBtnText, onClickPositive);
        if (negativeBtnText != null) {
            builder.setNegativeButton(negativeBtnText, onClickNegative);
        }
        AlertDialog dialog = builder.create();
        dialog.setCancelable(cancelable);
        dialog.show();
    }
}
