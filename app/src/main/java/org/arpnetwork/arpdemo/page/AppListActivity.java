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

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.arpnetwork.arpdemo.R;
import org.arpnetwork.arpdemo.data.AppInfo;
import org.arpnetwork.arpdemo.data.AppInfoResponse;
import org.arpnetwork.arpdemo.data.DeviceInfo;
import org.arpnetwork.arpdemo.protocol.ServerProtocol;
import org.arpnetwork.arpdemo.volley.GsonRequest;
import org.arpnetwork.arpdemo.volley.VolleySingleton;

import java.util.List;

public class AppListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_app_list);
        getAppInfoList(this);
    }

    private void initViews(final List<AppInfo> list) {
        ListView listView = findViewById(R.id.list_view);
        AppListAdapter adapter = new AppListAdapter(this, list);
        listView.setAdapter(adapter);
        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInfo appInfo = list.get(i);
                ServerProtocol.getDeviceInfo(context, appInfo.packageName, new ServerProtocol.OnReceiveDeviceInfo() {
                    @Override
                    public void onReceiveDeviceInfo(DeviceInfo info) {
                        // TODO: connect remote device
                    }
                }, new ServerProtocol.OnServerProtocolError() {
                    @Override
                    public void onServerProtocolError(int code, String msg) {
                        // TODO: show error tips
                    }
                });
            }
        });
    }

    private void getAppInfoList(Context context) {
        String url = AppInfo.HOST + "/app/list";
        GsonRequest request = new GsonRequest<AppInfoResponse>(Request.Method.GET, url, null,
                AppInfoResponse.class, new Response.Listener<AppInfoResponse>() {
            @Override
            public void onResponse(AppInfoResponse response) {
                initViews(response.data);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
