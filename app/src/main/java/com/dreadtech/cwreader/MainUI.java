package com.dreadtech.cwreader;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainUI extends Fragment {
    private static final String TAG = "CWRMUI";
    private EditText readSource;
    private CWReaderViewModel viewModel;
    private Tony tony;
    private TextToSpeech ttobj;
    boolean ttsReady = false;
    boolean stopped = true;

    private String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String digits = "123456789";
    private Button playButton;
    private Button stopButton;
    private Button groupsButton;
    private String currentWord = "fred";
    private int numLetters = 5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.front, container, false);
        readSource = v.findViewById(R.id.readsource);
        viewModel = ViewModelProviders.of((FragmentActivity)getActivity()).get(CWReaderViewModel.class);
        final Observer<Map<String,String>> updateObserver = new Observer<Map<String, String>>() {
            @Override
            public void onChanged(@Nullable final Map<String, String> settings) {
                for (Map.Entry<String, String> entry : settings.entrySet()) {
                    Log.e(TAG, "Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }
                // Update the UI, in this case, a TextView.
            }
        };

        viewModel.getLiveSettingsData().observe((FragmentActivity)getActivity(), updateObserver);
        tony = new Tony();


        /*v.findViewById(R.id.backgroundView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endEdit();
            }
        });*/

        tony.setFinishedSound(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
                if (!stopped) {
                    ttobj.speak(makeReadable(currentWord), TextToSpeech.QUEUE_FLUSH, null);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopThat();
                            viewModel.setValueNamed("readSource", currentWord);
                        }
                    });
                }
            }
        });
        tony.start();

        ttobj = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    ttobj.setLanguage(Locale.UK);
                    ttsReady = true;
                }
            }
        });

        playButton = v.findViewById(R.id.play_button);
        stopButton =  v.findViewById(R.id.stop_button);
        groupsButton = v.findViewById(R.id.groups_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endEdit();
                if (ttsReady) {
                    v.setEnabled(false);
                    groupsButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
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
                endEdit();
                stopThat();
            }
        });

        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endEdit();
                viewModel.setValueNamed("change", "true");
                //getActivity().toggle();
                /*
                if (ttsReady) {
                    v.setEnabled(false);
                    currentWord = makeWord();
                    playButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    viewModel.setValueNamed("readSource", "");
                    bundle.putString("word", currentWord);
                    msg.setData(bundle);
                    stopped = false;
                    tony.getHandler().sendMessage(msg);
                }*/

            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void stopThat() {
        playButton.setEnabled(true);
        groupsButton.setEnabled(true);
        tony.setStopped();
        stopButton.setEnabled(false);
        stopped = true;
    }

    public String makeWord() {
        Random r = new Random();
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
            sb.append(phonetic(c) + ". ");
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
                return "why";
            case 'Z':
                return "zed";
        }
        return String.valueOf(c);
    }

    private void endEdit() {
        readSource.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }


}
