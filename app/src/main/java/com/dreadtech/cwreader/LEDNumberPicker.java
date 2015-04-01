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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class LEDNumberPicker extends LinearLayout {

    public interface OnValueChangeListener {
        public abstract void onValueChange(LEDNumberPicker picker, int oldValue, int newValue);
    }

    private int maxValue = 99;
    private int minValue = 0;
    private int digits = 1;
    private int value = 0;
    private Button up, down;
    private ArrayList<SevenSegmentLED> digitArray;
    private OnValueChangeListener listener = null;
    private String label;


    public LEDNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LEDNumberPicker,
                0, 0);

        this.maxValue = a.getInteger(R.styleable.LEDNumberPicker_maxValue,99);
        this.minValue = a.getInteger(R.styleable.LEDNumberPicker_minValue,0);
        this.label = a.getString(R.styleable.LEDNumberPicker_label);

        digits = (int)Math.log10(this.maxValue) + 1;
        digitArray = new ArrayList();

        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout ledLayout = new LinearLayout(context);
        ledLayout.setOrientation(HORIZONTAL);
        ledLayout.setGravity(Gravity.CENTER_HORIZONTAL);


        for (int i = 0; i < digits; i++) {
            SevenSegmentLED seven = new SevenSegmentLED(context);
            digitArray.add(seven);
            ledLayout.addView(seven);
        }
        this.addView(ledLayout);
        TextView labelView = new TextView(context);
        labelView.setText(label);
        labelView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.addView(labelView);


        down = new PCBButton(context);
        down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setValue(getValue() - 1);
            }
        });
        up = new PCBButton(context);
        up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setValue(getValue() + 1);
            }
        });

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonLayout.setOrientation(HORIZONTAL);
        buttonLayout.addView(down);
        buttonLayout.addView(up);
        this.addView(buttonLayout);
    }

    public void setValue(int v) {
        int old = value;
        value = v;
        if ((v > maxValue) || (v < minValue)) return;
        for (int d = 0; d < digits; d++) {
            digitArray.get(digits - d - 1).setDigit((int)(v / (Math.pow(10,d))) % 10);
        }
        if (listener != null) {
            listener.onValueChange(this, old, v);
        }
    }

    public int getValue() {
        return value;
    }

    public void setOnValueChangedListener(OnValueChangeListener listener) {
        this.listener = listener;
    }
}
