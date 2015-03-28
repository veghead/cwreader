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

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ToggleButton;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public class ToggleSwitch extends ToggleButton {
    private int width = 30;

    public ToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels / 8;
        setWidth(width);
        setHeight(width);
        setTextOn("");
        setTextOff("");
    }


    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(makeMeasureSpec(width, View.MeasureSpec.EXACTLY), makeMeasureSpec(width,MeasureSpec.EXACTLY));
    }
}
