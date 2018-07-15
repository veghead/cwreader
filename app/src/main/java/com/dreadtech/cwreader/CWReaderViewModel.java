package com.dreadtech.cwreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class CWReaderViewModel extends ViewModel {
    public final MutableLiveData<Map<String,String>> settingsData = new MutableLiveData<>();

    public CWReaderViewModel() {}

    public LiveData<Map<String,String>> getLiveSettingsData() {
        return settingsData;
    }

    private Map<String, String> getSettingsData() {
        Map<String, String> data = settingsData.getValue();
        if (data == null) {
            data = new HashMap<>();
            settingsData.postValue(data);
        }
        return data;
    }

    public void setValueNamed(String valueName, int value) {
        setValueNamed(valueName, String.valueOf(value));
    }

    public void setValueNamed(String valueName, boolean value) {
        setValueNamed(valueName, String.valueOf(value));
    }

    public void setValueNamed(String valueName, String value) {
        Map<String, String> data = getSettingsData();
        data.put(valueName, value);
        settingsData.postValue(data);
    }
}
