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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;
import java.util.Random;


public class MainActivity extends Activity {
    private static final String TAG = "CWReader";
    TextToSpeech ttobj;
    boolean ttsReady = false;
    boolean stopped = true;
    boolean settingsDisplayed = false;
    EditText readSource;
    LEDNumberPicker wpmPicker;
    LEDNumberPicker freqPicker;
    LEDNumberPicker lettersPerGroupPicker;
    LEDNumberPicker numberOfGroupsPicker;
    ToggleSwitch farnsworthSwitch;
    String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String digits = "123456789";
    PushButton playButton;
    PushButton stopButton;
    PushButton groupsButton;
    PushButton settingsButton;
    String currentWord = "";
    Tony tony;
    View settingsView;
    View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.wood);
        tony = new Tony();
        farnsworthSwitch = findViewById(R.id.farnsworthSwitch);
        wpmPicker = findViewById(R.id.wpm);
        freqPicker = findViewById(R.id.freq);
        lettersPerGroupPicker = findViewById(R.id.lettersPerGroup);
        numberOfGroupsPicker = findViewById(R.id.numberOfGroups);
        readSource = findViewById(R.id.readsource);
        settingsView = findViewById(R.id.settings_view);
        mainView = findViewById(R.id.main_view);

        findViewById(R.id.backgroundView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSource.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        numberOfGroupsPicker.setValue(1);
        lettersPerGroupPicker.setValue(5);

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
        wpmPicker.setValue(20);

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
                    ttobj.speak(makeReadable(currentWord), TextToSpeech.QUEUE_FLUSH, null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopThat();
                            readSource.setText(currentWord);
                        }
                    });
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

        currentWord = readSource.getText().toString();

        playButton = findViewById(R.id.play_button);
        stopButton = findViewById(R.id.stop_button);
        groupsButton = findViewById(R.id.groups_button);
        settingsButton = findViewById(R.id.settings_button);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSource.onEditorAction(EditorInfo.IME_ACTION_DONE);
                if (ttsReady) {
                    v.setEnabled(false);
                    groupsButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    currentWord = readSource.getText().toString();
                    bundle.putString("word", currentWord);
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
                readSource.onEditorAction(EditorInfo.IME_ACTION_DONE);
                stopThat();
            }
        });

        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsReady) {
                    currentWord = makeWord();
                    playButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    readSource.setText("");
                    bundle.putString("word", currentWord);
                    msg.setData(bundle);
                    stopped = false;
                    tony.getHandler().sendMessage(msg);
                }
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSource.onEditorAction(EditorInfo.IME_ACTION_DONE);
                runOnUiThread(new Runnable() {
                    public void run() {
                        settingsView.setVisibility(View.VISIBLE);
                        mainView.setVisibility(View.GONE);
                        settingsDisplayed = true;
                    }
                });
            }
        });
        settingsView.setVisibility(View.GONE);
    }

    public void stopThat() {
        playButton.setEnabled(true);
        groupsButton.setEnabled(true);
        tony.setStopped();
        stopButton.setEnabled(false);
        stopped = true;
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

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String makeWord() {
        Random r = new Random();
        int numLetters = lettersPerGroupPicker.getValue();
        String set = letters;//+digits;
        String group = "";
        for(int i = 0; i< numLetters; i++) {
            int index = r.nextInt(set.length() - 1);
            group += set.substring(index, index+1);
        }
        return group;
    }

    private String makeReadable(String word) {
        if (!word.matches("^[A-Z\\d]+$")) {
            return word;
        }
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < word.length() ; i++) {
            char c = word.charAt(i);
            sb.append(phonetic(c) + ", ");
        }

        Log.d(TAG, "Before: " + word + "  After:" + sb.toString());
        return sb.toString();
    }

    private String phonetic(char c) {
        switch (c) {
            case 'A':
                return "ay";
            case 'B':
                return "bee";
            case 'C':
                return "see";
            case 'D':
                return "dee";
            case 'E':
                return "ee";
            case 'F':
                return "ef";
            case 'G':
                return "gee";
            case 'H':
                return "aitch";
            case 'I':
                return "eye";
            case 'J':
                return "jay";
            case 'K':
                return "kay";
            case 'L':
                return "el";
            case 'M':
                return "em";
            case 'N':
                return "en";
            case 'O':
                return "oh";
            case 'P':
                return "pee";
            case 'Q':
                return "cue";
            case 'R':
                return "ar";
            case 'S':
                return "ess";
            case 'T':
                return "tee";
            case 'U':
                return "you";
            case 'V':
                return "vee";
            case 'W':
                return "double you";
            case 'X':
                return "ex";
            case 'Y':
                return "wy";
            case 'Z':
                return "zed";
        }
        return String.valueOf(c);
    }


    public void onBackPressed () {
        if (settingsDisplayed) {
            settingsView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
            settingsDisplayed = false;
            return;
        }
        super.onBackPressed();
    }
}

