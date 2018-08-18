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
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.arpnetwork.arpdemo.data.AppInfo;
import org.arpnetwork.arpdemo.R;
import org.arpnetwork.arpdemo.protocol.ServerProtocol;

public class AppInfoItem extends LinearLayout {
    public static final int POSTER = 0;
    public static final int ICON = 1;

    private ImageView mPosterIV;
    private ImageView mIconIV;
    private TextView mTitleTV;
    private TextView mDescriptionTV;
    private LinearLayout mRatingLL;

    public AppInfoItem(Context context, AppInfo info, int type) {
        super(context);
        setAppInfo(info, type);
    }

    public void setAppInfo(AppInfo data, int type) {
        initWithData(type);
        if (mPosterIV != null) {
            Picasso.get().load(ServerProtocol.HOST + data.poster).into(mPosterIV);
        }
        Picasso.get().load(ServerProtocol.HOST + data.logo).into(mIconIV);

        mTitleTV.setText(data.title);
        mDescriptionTV.setText(data.description);
        setRating(data.rating);
    }

    private void initWithData(int type) {
        if (type == ICON) {
            LayoutInflater.from(getContext()).inflate(R.layout.item_app_icon, this, true);
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.item_app_poster, this, true);
            mPosterIV = findViewById(R.id.iv_poster);
        }
        mIconIV = findViewById(R.id.iv_icon);
        mTitleTV = findViewById(R.id.tv_title);
        mDescriptionTV = findViewById(R.id.tv_description);
        mRatingLL = findViewById(R.id.ll_rating);
    }

    private void setRating(int rating) {
        mRatingLL.removeAllViews();
        for (int i = 0; i < rating; i ++) {
            ImageView startImage = new ImageView(getContext());
            initStartImage(startImage);
            mRatingLL.addView(startImage);
        }
    }

    private void initStartImage(ImageView startImage) {
        startImage.setImageResource(R.mipmap.star);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(7, 0, 0, 0);
        startImage.setLayoutParams(lp);
    }
}
