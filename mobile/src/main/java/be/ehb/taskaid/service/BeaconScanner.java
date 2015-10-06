package be.ehb.taskaid.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import be.ehb.taskaid.DB.CardDAO;
import be.ehb.taskaid.DB.TaskDAO;
import be.ehb.taskaid.R;
import be.ehb.taskaid.UI.SelectorActivity;
import be.ehb.taskaid.model.Task;


public class BeaconScanner extends Service {
    private static final String TAG = "BEACON_SCANNER";
    private static final long SCAN_PERIOD = 5000;
    private static final long SCAN_INTERVAL = 10000;
    private static final int RSSI_THRESHOLD = -90;
    private static final long REMOVE_DELAY = 10000;
    private static final int SHOW_TOAST = 99;
    private static final long REMOVE_INTERVAL = 5000;
    private static final int SHOW_TOAST_VIBRATING = 90;
    private static final int POST_NOTIFICATION = 80;
    private static final int CANCEL_NOTIFICATION = 70;
    private static Handler mHandler;
    private static Handler mMainHandler;
   // private Callback mCallback;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;

    private ArrayList<Task> taskArrayList = new ArrayList<>() ;
    private Map<String, Long> detectedDeviceList = new ConcurrentHashMap<>();
    private int postedNotificationCount;


    public class Constants {

        public static final int NOTIFICATION_ID = 101;
        public static final String START_ACTION = "start_action";
        public static final String STOP_ACTION = "stop_action";
        public static final String MAIN_ACTION = "main_action";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case Constants.MAIN_START:
                startScanning();
                break;
            case Constants.MAIN_STOP:
                stopScanning();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        mMainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message message) {
                if (message.what == SHOW_TOAST){
                    Toast.makeText(getApplicationContext(), (CharSequence) message.obj, Toast.LENGTH_SHORT).show();
                } else if (message.what == SHOW_TOAST_VIBRATING){
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);
                    Toast.makeText(getApplicationContext(), (CharSequence) message.obj, Toast.LENGTH_LONG).show();
                } else if (message.what == POST_NOTIFICATION) {
                    Task task = (Task) message.obj;
                    Log.d(TAG,"Post Notif " + task.toString());
                    if (task != null){
                        CardNotification cardNotification = new CardNotification(task);

                        Notification[] notifications = cardNotification.buildNotifications(getApplicationContext());

                        // Post new notifications
                        for (int i = 0; i < notifications.length; i++) {
                            NotificationManagerCompat.from(getApplicationContext()).notify(i, notifications[i]);
                        }

                        // Cancel any that are beyond the current count.
                        for (int i = notifications.length; i < postedNotificationCount; i++) {
                            NotificationManagerCompat.from(getApplicationContext()).cancel(i);
                        }
                        postedNotificationCount = notifications.length;
                    }
                } else if (message.what == CANCEL_NOTIFICATION){
                    NotificationManagerCompat.from(getApplicationContext()).cancelAll();
                }
            }
        };

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        if (intent.getAction().equals(Constants.START_ACTION)) {
            Log.i(TAG, "Received Start Foreground Intent ");

            taskArrayList.addAll(TaskDAO.getInstance(this).getAll());
            if (taskArrayList != null) {
                for (Task t : taskArrayList) {
                    t.setCards(CardDAO.getInstance(this).getByTaskID(t.getID()));
                }
            }

            Intent notificationIntent = new Intent(this, SelectorActivity.class);
            notificationIntent.setAction(Constants.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Intent startIntent = new Intent(this, BeaconScanner.class);
            startIntent.setAction(Constants.STOP_ACTION);
            PendingIntent pStopIntent = PendingIntent.getService(this, 0,
                    startIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_media_play);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("TaskAID")
                    .setTicker("TaskAID")
                    .setContentText("Scanning")
                    .setSmallIcon(R.drawable.ic_media_play)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_pause,
                            "Stop scanning", pStopIntent)
                    .build();

            startLeScan();
            scanDetectedDeviceList();

            startForeground(Constants.NOTIFICATION_ID,
                    notification);
        } else if (intent.getAction().equals(Constants.STOP_ACTION)) {
            Log.i(TAG, "Clicked Stop");
            stopLeScan();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void startLeScan() {
        mScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Scanning " + i++);
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                if (mScanning) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"Waiting");
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            if (mScanning) {
                                startLeScan();
                            }
                        }
                    },SCAN_INTERVAL);
                }
            }
        }, SCAN_PERIOD);
    }

    private void scanDetectedDeviceList(){
        long timeNow = System.currentTimeMillis();
        Log.d(TAG,"Checking detected list");
        for (String device : detectedDeviceList.keySet()){
            Log.d(TAG,"Removable?: " + device + " - TimeStamp: " + detectedDeviceList.get(device) + " Now: " + timeNow + " Difference: " + (timeNow -  detectedDeviceList.get(device)));
            if ((detectedDeviceList.get(device) + REMOVE_DELAY) <= timeNow ){

                Message message = mMainHandler.obtainMessage(SHOW_TOAST,"Removed: " + device);
                message.sendToTarget();
                message = mMainHandler.obtainMessage(CANCEL_NOTIFICATION);
                message.sendToTarget();
                detectedDeviceList.remove(device);
            }
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    scanDetectedDeviceList();
                }
            }
        },REMOVE_INTERVAL);
    }

    private void stopLeScan(){
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    private static int i;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (rssi >= RSSI_THRESHOLD) {
                        if (!detectedDeviceList.containsKey(device.getAddress())) {
                            Task task = checkTaskList(device);
                            if (task != null) {
                                Log.d(TAG, "Task detected: " + task.toString());

                                //Message message = mMainHandler.obtainMessage(SHOW_TOAST_VIBRATING,"Task detected: " + task.toString());
                                Message message = mMainHandler.obtainMessage(POST_NOTIFICATION,task);
                                message.sendToTarget();
                                detectedDeviceList.put(device.getAddress(),System.currentTimeMillis());
                            }
                        }
                        detectedDeviceList.put(device.getAddress(), System.currentTimeMillis());
                    }
                }
            };


    private Task checkTaskList(BluetoothDevice device) {
        for (Task  task : taskArrayList){
            if (device.getAddress().equals(task.getBeaconAddress())){
                return task;
            }
        }
        return null;
    }
}

//TODO Notificatie naar watch verzenden