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

package org.arpnetwork.arpdemo.page.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import org.arpnetwork.arpclient.ARPClient;
import org.arpnetwork.arpclient.data.ErrorInfo;
import org.arpnetwork.arpclient.data.Quality;

public class H264RawView extends TextureView implements ARPClient.ARPClientListener {
    private ARPClient mARPClient;

    private OnRenderListener mRenderListener;

    public interface OnRenderListener {
        /**
         * Called when the video is ready for play.
         */
        void onPrepared();

        /**
         * Socket closed
         */
        void onClosed();

        /**
         * Play error
         *
         * @param errorCode See {@link ErrorInfo}
         * @param msg       Error details
         */
        void onError(int errorCode, String msg);
    }

    public H264RawView(Context context) {
        this(context, null);
    }

    public H264RawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public H264RawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Connect remote device
     *
     * @param host
     * @param port
     * @param session
     * @param packageName
     */
    public void connectRemoteDevice(String host, int port, String session, String packageName) {
        mARPClient = new ARPClient(getContext(), this);
        ARPClient.setQuality(Quality.HIGH);
        mARPClient.setSurfaceView(this);
        mARPClient.start(host, port, session, packageName);
    }

    /**
     * Stop media player
     */
    public void stop() {
        mARPClient.stop();
    }

    /**
     * Pass press back key event
     */
    public void onBackPressed() {
        mARPClient.onBackPressed();
    }

    /**
     * Set render listener
     *
     * @param listener
     */
    public void setRenderListener(OnRenderListener listener) {
        mRenderListener = listener;
    }

    @Override
    public void onPrepared() {
        if (mRenderListener != null) {
            mRenderListener.onPrepared();
        }
    }

    @Override
    public void onClosed() {
        mRenderListener.onClosed();
    }

    @Override
    public void onError(int code, String msg) {
        if (mRenderListener != null) {
            mRenderListener.onError(code, msg);
        }
    }
}
