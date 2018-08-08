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

package org.arpnetwork.arpdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.arpnetwork.arpclient.volley.GsonRequest;
import org.arpnetwork.arpclient.volley.VolleySingleton;

import java.util.List;

public class AppListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_app_list);
        getAppInfoList(this);
    }

    private void initViews(List<AppInfo> list) {
        ListView listView = findViewById(R.id.list_view);
        AppListAdapter adapter = new AppListAdapter(this, list);
        listView.setAdapter(adapter);
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
