package org.techtown.Constellation.ui.counter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CounterViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public CounterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is counter fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}