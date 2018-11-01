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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_app_list);
        initViews();
        getAppInfoList(this);
    }

    @Override
    public void onBackPressed() {
        long duration = System.currentTimeMillis() - mExitTime;
        if (duration > 2000) {
            Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }

    private void initViews() {
        TextView version = findViewById(R.id.tv_version);
        version.setText(getAppVersion(this));
    }

    private void setData(final List<AppInfo> list) {
        ListView listView = findViewById(R.id.list_view);
        AppListAdapter adapter = new AppListAdapter(this, list);
        listView.setAdapter(adapter);
        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInfo appInfo = list.get(i);
                final String packageName = appInfo.packageName;
                ServerProtocol.getDeviceInfo(context, packageName, new ServerProtocol.OnReceiveDeviceInfo() {
                    @Override
                    public void onReceiveDeviceInfo(DeviceInfo info) {
                        Intent intent = new Intent(AppListActivity.this, PlayActivity.class);
                        intent.putExtra("HOST", info.ip);
                        intent.putExtra("PORT", info.port);
                        intent.putExtra("SESSION", info.session);
                        intent.putExtra("PACKAGE", packageName);

                        startActivity(intent);
                    }
                }, new ServerProtocol.OnServerProtocolError() {
                    @Override
                    public void onServerProtocolError(int code, String msg) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getAppInfoList(final Context context) {
        String url = ServerProtocol.HOST + "/app/list";
        GsonRequest request = new GsonRequest<AppInfoResponse>(Request.Method.GET, url, null,
                AppInfoResponse.class, new Response.Listener<AppInfoResponse>() {
            @Override
            public void onResponse(AppInfoResponse response) {
                setData(response.data);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private static String getAppVersion(Context context) {
        String appVersion = "";
        try {
            appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return appVersion;
    }
}
