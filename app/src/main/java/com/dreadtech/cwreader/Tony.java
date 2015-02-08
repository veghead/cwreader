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


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;


public class Tony extends Thread {
    private Runnable finishedSound;
    private boolean needToGenerate = false;
    private double wpm = 20;
    private double toneFreq = 800;
    private final int maxDuration = 3;
    private final int sampleRate = 8000;
    private final int maxSamples = maxDuration * sampleRate;
    private final double raw[] = new double[maxSamples];
    private int dotlen = 0;
    private static final int PARIS = 50;
    private static final int DASHLEN = 3;
    private static final int INTRACHAR = 1;
    private static final int INTERCHAR = 3;
    private static final int MAXVOL = 32767;
    private static Handler handler;

    static final Map<String , String> morseChars = new HashMap<String , String>() {{
        put("A", ".-");
        put("B","-...");
        put("C","-.-.");
        put("D","-..");
		put("E",".");
		put("F","..-.");
		put("G","--.");
		put("H","....");
		put("I","..");
		put("J",".---");
		put("K","-.-");
		put("L",".-..");
		put("M","--");
		put("N","-.");
		put("O","---");
		put("P",".--.");
		put("Q","--.-");
		put("R",".-.");
		put("S","...");
		put("T","-");
		put("U","..-");
		put("V","...-");
		put("W",".--");
		put("X","-..-");
		put("Y","-.--");
		put("Z","--..");
		put("0","-----");
		put("1",".----");
		put("2","..---");
		put("3","...--");
		put("4","....-");
		put("5",".....");
		put("6","-....");
		put("7","--...");
		put("8","---..");
		put("9","----.");
		put("?","..--..");
		put("/","-..-.");
		put(",","--..--");
		put(".",".-.-.-");
        /*
		put("-...-"); // BT (double dash)
		put(".-.-."); / AR (end of message)
		put("...-.-"); // SK (end of contact)
        */
    }};


    private final byte dot[] = new byte[2 * maxSamples];
    private final byte dash[] = new byte[2 * maxSamples];
    private final byte silent[] = new byte[2 * maxSamples];

    AudioTrack audioTrack = null;


    public void setToneFreq(double toneFreq) {
        this.toneFreq = toneFreq;
        needToGenerate = true;
    }

    public void setWpm(double wpm) {
        this.wpm = wpm;
        needToGenerate = true;
    }

    public void setFinishedSound(Runnable finishedSound) {
        this.finishedSound = finishedSound;
    }

    public Handler getHandler() {
        return handler;
    }

    private void makeTone(byte buffer[], int len, int vol, int fadelen) {
        int j = 0;
        int stdvol = (int)((float)vol * (float)MAXVOL / 100.0);
        int curvol = stdvol;
        for (int i = 0; i < len; i += 1) {
            if (i > (len - fadelen)) {
                curvol = (int)((float)(len - i) * curvol /(float)fadelen);
            }
            final short rawval = (short) ((raw[i] * curvol));
            buffer[j++] = (byte) (rawval & 0x00ff);
            buffer[j++] = (byte) ((rawval & 0xff00) >>> 8);
        }
    }

    public void makeTones() {
        // Work out samples per element (i.e. per dash)
        dotlen = (int)((60.0 * (double)sampleRate) / (wpm * (double)PARIS));

        // create a nice sine wave
        for (int i = 0; i < dotlen * 3; ++i) {
            raw[i] = Math.sin(2 * Math.PI * i / (sampleRate/toneFreq));
        }
        makeTone(dot, dotlen, 100, 5);
        makeTone(silent, dotlen * 3, 0, 5);
        makeTone(dash, dotlen * 3, 100, 5);
        needToGenerate = false;
    }

    public void run() {
        Looper.prepare();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                soundText(bundle.getString("word").toUpperCase());
            }
        };
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 80000,
                AudioTrack.MODE_STREAM);
        makeTones();
        Looper.loop();
    }

    private String morseCharAt(String word, int index) {
        char c = word.charAt(index);
        String morseChar = morseChars.get(c + "");
        if (null == morseChar) {
            morseChar = "";
        }
        return morseChar;
    }

    void writeChar(String morseChar) {
        for (int i = 0; i < morseChar.length(); i++) {
            switch (morseChar.charAt(i)) {
                case('.'):
                    audioTrack.write(dot, 0, dotlen);
                    break;
                case('-'):
                    audioTrack.write(dash, 0, dotlen * DASHLEN);
                    break;
            }
            audioTrack.write(silent, 0, dotlen * INTRACHAR);
        }
        audioTrack.write(silent, 0, dotlen * INTERCHAR);
    }

    void soundText(String text) {
        if (needToGenerate) {
            makeTones();
        }
        audioTrack.play();
        String words[] = text.split(" ");
        for (String word : words) {
            writeWord(word);
        }
        audioTrack.stop();
        finishedSound.run();
    }

    void writeWord(String word) {
        final int wordlen = word.length();

        for (int i = 0 ; i < wordlen; i++) {
            writeChar(morseCharAt(word, i));
        }

        // Two INTERCHARS between words
        audioTrack.write(silent, 0, dotlen * INTERCHAR);
        audioTrack.write(silent, 0, dotlen * INTERCHAR);
    }
}
