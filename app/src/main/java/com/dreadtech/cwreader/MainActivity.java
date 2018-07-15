/*
 * Copyright (c) 2015 dreadtech.com.
 *
 *     This file is part of CWReader.
 *
 *     CWReader is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CWReader is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CWReader.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dreadtech.cwreader;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.ads.AdView;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "CWReader";


    AdView madView;
    CWReaderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MobileAds.initialize(this, "ca-app-pub-6877516850913018~2865964003");
        //madView = (AdView) findViewById(R.id.adView);
    }

    void loadAd() {
        //AdRequest adRequest = new AdRequest.Builder().build();
        //madView.loadAd(adRequest);
    }
}

