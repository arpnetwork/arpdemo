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

package org.arpnetwork.arpdemo.data;

import android.content.Context;

import org.arpnetwork.arpclient.data.ErrorInfo;
import org.arpnetwork.arpdemo.R;

public class ErrorMessage {
    public static final int NETWORK_ERROR = -1;
    private static final int NO_IDLE_DEVICE = 201;
    private static final int FAILED_LAUNCHING_APP = 202;
    private static final int PARAM_ERROR = 101;

    public static String getDemoErrorMessage(Context context, int errorCode) {
        switch (errorCode) {
            case NETWORK_ERROR:
                return context.getString(R.string.network_error);

            case NO_IDLE_DEVICE:
                return context.getString(R.string.no_idle_device);

            case FAILED_LAUNCHING_APP:
                return context.getString(R.string.failed_launching);

            case PARAM_ERROR:
                return context.getString(R.string.version_error);

            default:
                return context.getString(R.string.unknown_error);
        }
    }

    public static String getSDKErrorMessage(Context context, int errorCode) {
        switch (errorCode) {
            case ErrorInfo.ERROR_NETWORK:
                return context.getString(R.string.network_error);

            case ErrorInfo.ERROR_DISCONNECTED_BY_DEVICE:
                return context.getString(R.string.remote_disconnect);

            case ErrorInfo.ERROR_UNKNOWN:
            case ErrorInfo.ERROR_PARAM:
            case ErrorInfo.ERROR_NO_DEVICES:
                return context.getString(R.string.server_reject);

            case ErrorInfo.ERROR_MEDIA:
                return context.getString(R.string.server_media);

            case ErrorInfo.ERROR_PROTOCOL_TOUCH_SETTING:
            case ErrorInfo.ERROR_PROTOCOL_VIDEO_INFO:
            case ErrorInfo.ERROR_CONNECTION_RESULT:
                return context.getString(R.string.remote_error);

            case ErrorInfo.ERROR_CONNECTION_REFUSED_VERSION:
                return context.getString(R.string.version_error);

            default:
                return context.getString(R.string.unknown_error);
        }
    }
}
