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
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public class SevenSegmentLED extends android.view.View {

    private static Map<Integer,String> digitMap = new HashMap<Integer, String>();
    private static Map<String, Bitmap> iconMap = new HashMap<String, Bitmap>();
    static {
        digitMap.put(0, "abcdef");
        digitMap.put(1, "bc");
        digitMap.put(2, "abdeg");
        digitMap.put(3, "abcdg");
        digitMap.put(4, "bcfg");
        digitMap.put(5, "acdfg");
        digitMap.put(6, "acdefg");
        digitMap.put(7, "abc");
        digitMap.put(8, "abcdefg");
        digitMap.put(9, "abcdfg");

    }

    private int digit = 0;
    private int width = 75;
    private double heightRatio = 1.35;
    private boolean ds = false;
    private String bars = "abcdefg";

    public SevenSegmentLED(Context context) {
        super(context);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels / 11;
        int height = (int) (width * 1.35);

        iconMap.put("a", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.a),width,height, true));
        iconMap.put("b", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.b),width,height, true));
        iconMap.put("c", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.c),width,height, true));
        iconMap.put("d", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.d),width,height, true));
        iconMap.put("e", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.e),width,height, true));
        iconMap.put("f", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.f),width,height, true));
        iconMap.put("g", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.g),width,height, true));
        iconMap.put("off", Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.off),width,height, true));


    }

    protected void onDraw(Canvas canvas) {
        String allBars = "abcdefg";
        canvas.drawBitmap(iconMap.get("off"), 0, 0, null);

        for (int i = 0; i < allBars.length(); i++) {
            String bar = allBars.substring(i,i+1);

            if (bars.contains(bar)) {
                canvas.drawBitmap(iconMap.get(bar), 0, 0, null);
            }
        }

    }

    synchronized public void setBars(String bars) {
        this.bars = bars;
        invalidate();
    }

    public void setDigit(int digit) {
        this.digit = digit;
        setBars(SevenSegmentLED.digitMap.get(digit));
    }

    private int getWidthMeasure() {

        return makeMeasureSpec(width,MeasureSpec.EXACTLY);
    }

    private int getHeightMeasure() {
        return makeMeasureSpec((int) (width * heightRatio), MeasureSpec.EXACTLY);
    }

    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getWidthMeasure(), getHeightMeasure());
    }
}
