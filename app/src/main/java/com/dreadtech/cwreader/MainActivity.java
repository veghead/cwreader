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

import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    TextToSpeech ttobj;
    boolean ttsReady = false;
    boolean stopped = true;
    EditText readSource;
    LEDNumberPicker wpmPicker;
    LEDNumberPicker freqPicker;
    ToggleSwitch farnsworthSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Tony tony = new Tony();
        farnsworthSwitch = (ToggleSwitch)findViewById(R.id.farnsworthSwitch);
        wpmPicker = (LEDNumberPicker)findViewById(R.id.wpm);
        freqPicker = (LEDNumberPicker)findViewById(R.id.freq);

        readSource = (EditText)findViewById(R.id.readsource);

        wpmPicker.setOnValueChangedListener(new LEDNumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(LEDNumberPicker picker, int oldVal, int newVal) {
                tony.setWpm(picker.getValue());
            }
        });

        freqPicker.setOnValueChangedListener(new LEDNumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(LEDNumberPicker picker, int oldVal, int newVal) {
                tony.setToneFreq(picker.getValue());
            }
        });

        freqPicker.setValue(800);
        wpmPicker.setValue(17);

        farnsworthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tony.setFarnsworth(isChecked);
            }
        });

        farnsworthSwitch.setChecked(true);

        tony.setFinishedSound(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
                if (!stopped) {
                    ttobj.speak(readSource.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        tony.start();

        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    ttobj.setLanguage(Locale.UK);
                    ttsReady = true;
                }
            }
        });

        final Button playButton = (Button) findViewById(R.id.play_button);
        final Button stopButton = (Button) findViewById(R.id.stop_button);
        final Button groupsButton = (Button) findViewById(R.id.groups_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ttsReady) {
                v.setEnabled(false);
                groupsButton.setEnabled(false);
                stopButton.setEnabled(true);
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("word", readSource.getText().toString());
                msg.setData(bundle);
                tony.getHandler().sendMessage(msg);
                stopped = false;
            }
            }
        });

        stopButton.setEnabled(false);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                playButton.setEnabled(true);
                groupsButton.setEnabled(true);
                tony.setStopped();
                stopped = true;
            }
        });

        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsReady) {
                    v.setEnabled(false);
                    playButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("word", readSource.getText().toString());
                    msg.setData(bundle);
                    tony.getHandler().sendMessage(msg);
                    stopped = false;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
