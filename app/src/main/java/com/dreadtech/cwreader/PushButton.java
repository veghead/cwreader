package com.dreadtech.cwreader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;


public class PushButton extends LinearLayout {
    private AppCompatButton button;
    private TextView caption;

    public PushButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PushButton,
                0, 0);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int bsize = dm.widthPixels / 8;

        //setMinimumWidth(dm.widthPixels / 2);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        button = new AppCompatButton(context);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //setValue(getValue() - 1);
            }
        });

        int drawable = Objects.equals(a.getString(R.styleable.PushButton_buttonType),"blue")
                ? R.drawable.bluepushbutton
                : R.drawable.pushbutton;
        button.setBackground(getResources().getDrawable(drawable));
        button.setLayoutParams(new LinearLayout.LayoutParams(bsize, bsize));

        caption = new TextView(context);
        //caption.setBackgroundColor(Color.WHITE);
        caption.setTextColor(Color.GRAY);

        caption.setText(a.getString(R.styleable.PushButton_buttonLabel));
        LayoutParams params = new LinearLayout.LayoutParams(bsize * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,10,0);
        caption.setLayoutParams(params);
        addView(button);
        addView(caption);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        button.setOnClickListener(l);
    }
}
