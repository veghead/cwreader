package com.dreadtech.cwreader;

import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    TextToSpeech ttobj;
    boolean ttsReady = false;
    EditText readSource;
    NumberPicker wpmPicker;
    NumberPicker freqPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Tony tony = new Tony();
        readSource = (EditText)findViewById(R.id.readsource);
        wpmPicker = (NumberPicker)findViewById(R.id.wpm);
        wpmPicker.setMaxValue(90);
        wpmPicker.setMinValue(5);
        wpmPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tony.setWpm(wpmPicker.getValue());
            }
        });
        freqPicker = (NumberPicker)findViewById(R.id.freq);
        freqPicker.setMaxValue(1100);
        freqPicker.setMinValue(200);
        freqPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tony.setToneFreq(freqPicker.getValue());
            }
        });
        freqPicker.setValue(800);
        wpmPicker.setValue(17);

        tony.setFinishedSound(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
                ttobj.speak(readSource.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
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

        Button playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsReady) {
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("word", readSource.getText().toString());
                    msg.setData(bundle);
                    tony.getHandler().sendMessage(msg);
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
