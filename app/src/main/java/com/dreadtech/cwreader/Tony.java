package com.dreadtech.cwreader;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;


public class Tony extends Thread {
    private final int maxDuration = 3;
    private final int sampleRate = 8000;
    private final int maxSamples = maxDuration * sampleRate;
    private final double raw[] = new double[maxSamples];
    private final double toneFreq = 800;
    private int dotlen = 0;
    private double wpm = 20;
    private static final int PARIS = 50;
    private static final int DASHLEN = 3;
    private static final int INTRACHAR = 1;
    private static final int INTERCHAR = 3;
    private static final int INTERWORD = 7;
    private static final int MAXVOL = 32767;
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
/*
		put("..--..");
		/* 0..9 
		put("-..-.");
		           /* Question Mark 
		put("--..--");
		            /* Slash 
		put(".-.-.-");
		           /* Comma 
		put();
		           /* Period
		put("-...-");
		put(".-.-.");
		            /* BT (double dash)
		put("...-.-");
		            /* AR (end of message)
           /* SK (end of contact)
 * */
    }};


    private final byte dot[] = new byte[2 * maxSamples];
    private final byte dash[] = new byte[2 * maxSamples];
    private final byte silent[] = new byte[2 * maxSamples];

    AudioTrack audioTrack = null;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Log.v("HELLO", String.format("Handler.handleMessage(): msg=%s", bundle.getString("word")));
            soundText(bundle.getString("word").toUpperCase());
        }
    };

    private void makeTone(byte buffer[], int len, int vol, int fadelen) {
        int j = 0;
        for (int i = 0; i < len * 2; i += 2) {
            final short rawval = (short) ((raw[i] * vol));
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
        makeTone(dot, dotlen, MAXVOL, 5);
        makeTone(silent, dotlen * 3, 0, 5);
        makeTone(dash, dotlen * 3, MAXVOL, 5);
    }

    public void run() {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 80000,
                AudioTrack.MODE_STREAM);
        makeTones();
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
            Log.v("CW", String.format("Morse: %c", morseChar.charAt(i)));
        }
        audioTrack.write(silent, 0, dotlen * INTERCHAR);
    }

    void soundText(String text) {
        audioTrack.play();
        writeWord(text);
        audioTrack.stop();
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
