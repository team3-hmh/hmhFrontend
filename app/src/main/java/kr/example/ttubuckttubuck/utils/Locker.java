package kr.example.ttubuckttubuck.utils;

import android.util.Log;

public class Locker {
    private static final String TAG = "Locker_Debug";
    private boolean isLocked = false;
    public synchronized void lock() throws InterruptedException {
        Log.d(TAG, "Success to lock");
        while(isLocked)
            wait();
        isLocked  = true;
    }
    public synchronized void unlock(){
        isLocked = false;
        Log.d(TAG, "Success to unlock");
        notify();
    }
}
