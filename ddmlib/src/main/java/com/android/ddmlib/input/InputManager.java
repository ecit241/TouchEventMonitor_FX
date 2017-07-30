package com.android.ddmlib.input;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by majipeng on 2017/6/19.
 */
public class InputManager {
    IDevice mAndroidDevice;
    EventHub eventHub;
    InputReader inputReader;

    InputReaderThread inputReaderThread;
    InputDispatcher inputDispatcher;
    InputDispatcherThread inputDispatcherThread;

    public InputManager(IDevice mAndroidDevice) {
        Log.d("inputmanager", "new ");
        this.mAndroidDevice = mAndroidDevice;
        eventHub = new EventHub(this);
        inputReader = new InputReader(eventHub);
        inputDispatcher = new InputDispatcher(this);
        inputReaderThread = new InputReaderThread(inputReader,inputDispatcher);
        inputDispatcherThread = new InputDispatcherThread(this);
        inputReaderThread.start();
        inputDispatcherThread.start();
    }


    public IDevice getAndroidDevice() {
        return mAndroidDevice;
    }

    EventHub getEventHub() {
        return eventHub;
    }

    InputReader getInputReader() {
        return inputReader;
    }

    public InputDispatcher getInputDispatcher() {
        return inputDispatcher;
    }



    public ArrayList<InputDevice> getDevices() {
        return eventHub.getDevices();
    }


    public boolean addOnTouchEventListener(OnTouchEventListener listener) {
        return inputDispatcherThread.addOnTouchEventListener(listener);
    }


    public boolean unregisterTouchEventListener(OnTouchEventListener listener) {
        return inputDispatcherThread.unregisterTouchEventListener(listener);
    }


    public void onShutDown() {
        eventHub.quit();
        try {
            inputReaderThread.interrupt();
            inputReaderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            inputDispatcherThread.interrupt();
            inputDispatcherThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
