package com.android.ddmlib.input;

import com.android.ddmlib.*;
import com.android.ddmlib.TimeoutException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

class EventHub {


//    private LinkedHashMap<String>

//    private IDevice mAndroidDevice;

    private static final String TAG = "EventHub";
    private static final boolean DEBUG = false;

    private static final int TIMEOUT_SEC = 2;

    private InputManager mContext;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private BlockingQueue<PlainTextRawEvent> rawEvents = new LinkedBlockingQueue<>();

    private HashMap<String, Future> futureHashMap = new HashMap<>();

    private HashMap<String, InputDevice> mDevices = new HashMap<>();

    public EventHub(InputManager inputManager) {
        mContext = inputManager;
        scanDevice(true);
    }

    void scanDevice(boolean force) {
        if (force || mDevices.isEmpty()) {
            try {
                mContext.getAndroidDevice().executeShellCommand(new SingleLineReceiver() {
                    ArrayList<String> sb = new ArrayList<>();

                    @Override
                    public void processNewLines(String line) {
                        if (DEBUG) Log.d(getClass().getName(), line);
                        if (line.startsWith("add device")) {
                            if (sb.size() > 0) {
                                putDevice(sb);
                                sb.clear();
                            }
                        }
                        sb.add(line.trim());
//                    if(DEBUG)Log.d(getClass().getName(), sb.toString());
                    }

                    @Override
                    public void done() {
                        putDevice(sb);
                        sb.clear();
                    }
                }, TIMEOUT_SEC, TimeUnit.SECONDS, Command.GETEVENT_GETDEVICE);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } catch (ShellCommandUnresponsiveException e) {
//            e.printStackTrace();
            } catch (TimeoutException e) {
//            e.printStackTrace();
            } finally {

            }
        }
//        result.add(new InputDevice((List<String>) sb.clone(), dev));
//        return new ArrayList<>(devList.values());
    }

    void putDevice(ArrayList<String> sb) {
        InputDevice tempDev = new InputDevice((List<String>) sb.clone());
        if (!mDevices.containsKey(tempDev.getDevFile())) {
            mDevices.put(tempDev.getDevFile(), tempDev);
            addDevice(tempDev);
        }
    }

    void addDevice(InputDevice tempDev) {
        if (futureHashMap.containsKey(tempDev.getDevFile())) {
            if (!futureHashMap.get(tempDev.getDevFile()).isDone()) {
                return;
            }
        }

        Future<Void> submitFuture = executorService.submit(() -> {
            try {
                mContext.getAndroidDevice().executeShellCommand(new SingleLineReceiver() {
                    @Override
                    public void processNewLines(String line) {
                        PlainTextRawEvent rawEvent = new PlainTextRawEvent(line, tempDev.getDevFile());
                        rawEvents.add(rawEvent);
                    }
                }, -1, Command.GETEVENT_WHATCH_TEXT_EVENT, tempDev.getDevFile());

            } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
                    | IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        futureHashMap.put(tempDev.getDevFile(), submitFuture);
    }


    boolean removeMonitorDevice() {
        return false;
    }

    PlainTextRawEvent getEvent() throws InterruptedException {
        return rawEvents.take();
    }


    public ArrayList<InputDevice> getDevices() {
        return new ArrayList<>(mDevices.values());
    }
}