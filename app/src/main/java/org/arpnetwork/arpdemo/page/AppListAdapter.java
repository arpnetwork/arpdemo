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
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.arpnetwork.arpdemo.data.AppInfo;
import org.arpnetwork.arpdemo.page.view.AppInfoItem;

import java.util.List;

public class AppListAdapter implements ListAdapter {
    private List<AppInfo> mList;
    private Context mContext;

    public AppListAdapter(Context context, List<AppInfo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AppInfoItem item;
        if (view == null) {
            item = new AppInfoItem(mContext, mList.get(i), getItemViewType(i));
        } else {
            item = (AppInfoItem) view;
            item.setAppInfo(mList.get(i), getItemViewType(i));
        }
        return item;
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return AppInfoItem.POSTER;
        } else {
            return AppInfoItem.ICON;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }
}
