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
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CWReader.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dreadtech.cwreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.Button;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public class PCBButton extends Button {
    private int width = 30;

    public PCBButton(Context context) {
        super(context);
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.pcb_button));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels / 12;
        setWidth(width);
        setHeight(width);
    }


    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(makeMeasureSpec(width,MeasureSpec.EXACTLY), makeMeasureSpec(width,MeasureSpec.EXACTLY));
    }

}
