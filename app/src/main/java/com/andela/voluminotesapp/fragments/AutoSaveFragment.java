package com.andela.voluminotesapp.fragments;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.andela.voluminotesapp.R;
import com.andela.voluminotesapp.callbacks.AutoSaveCallback;

import java.util.Timer;
import java.util.TimerTask;


public abstract class AutoSaveFragment extends Fragment {
    private long setTime = 0;
    private AutoSaveCallback callback;
    private Timer timer;

    public void setAutoSaveListener(final AutoSaveCallback callback) {
        setTime = getSetTime();
        if (setTime != 0) {
            this.callback = callback;
        }
    }

    public void deactivateAutoSave() {
        if (setTime != 0) {
            timer.cancel();
        }
    }

    public void activateAutoSave() {
        if (setTime != 0) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    callback.onSave();
                }
            }, 0, setTime);
        }
    }

    @Override
    public void onDestroy() {
        deactivateAutoSave();
        super.onDestroy();
    }

    private long getSetTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return Long.parseLong(sharedPreferences.getString(getString(R.string.autosave), "0"));
    }
}
