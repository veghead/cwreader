package com.dreadtech.cwreader;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

public class SettingsUI extends Fragment {
    private LEDNumberPicker wpmPicker;
    private LEDNumberPicker freqPicker;
    private LEDNumberPicker lettersPerGroupPicker;
    private LEDNumberPicker numberOfGroupsPicker;
    private ToggleSwitch farnsworthSwitch;
    private CWReaderViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of((FragmentActivity)getActivity()).get(CWReaderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);
        farnsworthSwitch = getActivity().findViewById(R.id.farnsworthSwitch);
        wpmPicker = getActivity().findViewById(R.id.wpm);
        freqPicker = getActivity().findViewById(R.id.freq);
        lettersPerGroupPicker = getActivity().findViewById(R.id.lettersPerGroup);
        numberOfGroupsPicker = getActivity().findViewById(R.id.numberOfGroups);
        numberOfGroupsPicker.setValue(1);
        lettersPerGroupPicker.setValue(5);

        wpmPicker.setOnValueChangedListener(new LEDNumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(LEDNumberPicker picker, int oldVal, int newVal) {
                viewModel.setValueNamed("wpm",(picker.getValue()));
            }
        });

        freqPicker.setOnValueChangedListener(new LEDNumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(LEDNumberPicker picker, int oldVal, int newVal) {
                viewModel.setValueNamed("toneFreq",(picker.getValue()));
            }
        });

        freqPicker.setValue(800);
        wpmPicker.setValue(20);

        farnsworthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setValueNamed("farnsworth", (isChecked));
            }
        });

        farnsworthSwitch.setChecked(true);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
