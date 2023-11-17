package com.ouslsmartactivitydiary;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private MutableLiveData<Integer> notificationCount = new MutableLiveData<>();

    public MutableLiveData<Integer> getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int value) {
        notificationCount.setValue(value);
    }


}
