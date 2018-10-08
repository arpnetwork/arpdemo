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

package org.arpnetwork.arpdemo.protocol;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.arpnetwork.arpclient.data.ErrorInfo;
import org.arpnetwork.arpdemo.data.DeviceInfo;
import org.arpnetwork.arpdemo.data.DeviceInfoResponse;
import org.arpnetwork.arpdemo.volley.GsonRequest;
import org.arpnetwork.arpdemo.volley.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerProtocol {
    public static final String HOST = "http://dev.arpnetwork.org:33224";

    public interface OnReceiveDeviceInfo {
        /**
         * Called when receive deviceInfo from server
         *
         * @param info deviceInfo
         */
        void onReceiveDeviceInfo(DeviceInfo info);
    }

    public interface OnServerProtocolError {
        /**
         * Called when error occurred
         *
         * @param code
         * @param msg
         */
        void onServerProtocolError(int code, String msg);
    }

    /**
     * Post http request to get userInfo from server
     *
     * @param context
     * @param packageName         name of package to run on remote device
     * @param onReceiveDeviceInfo callback with userInfo
     * @param onError             callback with error
     */
    public static void getDeviceInfo(Context context, String packageName,
            final OnReceiveDeviceInfo onReceiveDeviceInfo, final OnServerProtocolError onError) {
        String url = HOST + "/device/request";
        JSONObject params = new JSONObject();
        int width = getWidthNoVirtualBar(context);
        int height = getHeightNoVirtualBar(context);
        try {
            params.put("package", packageName);
            params.put("width", width);
            params.put("height", height);
        } catch (JSONException ignore) {
        }
        GsonRequest request = new GsonRequest<DeviceInfoResponse>(Request.Method.POST, url, params.toString(),
                DeviceInfoResponse.class, new Response.Listener<DeviceInfoResponse>() {
            @Override
            public void onResponse(DeviceInfoResponse response) {
                if (response.code == 0) {
                    onReceiveDeviceInfo.onReceiveDeviceInfo(response.data);
                } else {
                    onError.onServerProtocolError(response.code, response.message);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                        onError.onServerProtocolError(errorResponse.getInt("code"),
                                errorResponse.getString("message"));
                        return;
                    } catch (Exception e) {
                    }
                }
                onError.onServerProtocolError(ErrorInfo.ERROR_NETWORK, ErrorInfo.getErrorMessage(ErrorInfo.ERROR_NETWORK));
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Report connection state
     *
     * @param context
     * @param session session from DeviceInfo
     * @param state   see {@link DeviceInfo}
     */
    public static void setConnectionState(Context context, String session, int state) {
        String url = HOST + "/device/report";
        JSONObject params = new JSONObject();
        try {
            params.put("session", session);
            params.put("state", state);
        } catch (JSONException ignore) {
        }
        GsonRequest request = new GsonRequest<Void>(Request.Method.POST, url, params.toString(),
                Void.class, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private static int getHeightNoVirtualBar(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private static int getWidthNoVirtualBar(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
