package be.ehb.taskaid.UI;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import be.ehb.taskaid.DB.CardDAO;
import be.ehb.taskaid.DB.TaskDAO;
import be.ehb.taskaid.R;
import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.model.Task;
import be.ehb.taskaid.service.BeaconScanner;
import be.ehb.taskaid.service.CardNotification;

public class SelectorActivity extends Activity {

    private static final String TAG = "Selector";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
    }

    public void onBtnDBTestClick(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void onBtnTaskListClick(View view){
        Intent intent = new Intent(this,TaskListActivity.class);
        startActivity(intent);
    }

    public void onBtnDevListClick(View view){
        Intent intent = new Intent(this,MainTester.class);
        startActivity(intent);
    }

    public void onBtnAddTask(View view){
        Intent intent = new Intent(this,AddTaskActivity.class);
        startActivity(intent);
    }

    public void onBtnAddCard(View view){
        Intent intent = new Intent(this,AddCardActivity.class);
        startActivity(intent);
    }

    public void onBtnStartService(View view){
        Intent intent = new Intent(this, BeaconScanner.class);
        intent.setAction(BeaconScanner.Constants.START_ACTION);
        startService(intent);
    }

    public void onBtnStopService(View view) {
        Intent stopIntent = new Intent(this, BeaconScanner.class);
        stopIntent.setAction(BeaconScanner.Constants.STOP_ACTION);
        startService(stopIntent);
    }

    public void onBtnNotificationClick(View view){
        Task t1 = new Task("Test Notif");
        Card c1 = new Card("kaart 1");
        Card c2 = new Card("kaart 2");
        Card c3 = new Card("kaart 3");
        Card c4 = new Card("kaart 4");
        t1.setCards(new ArrayList<>(Arrays.asList(c1,c2,c3,c4)));

        TaskDAO taskDAO = TaskDAO.getInstance(this);
        CardDAO cardDAO = CardDAO.getInstance(this);

        ArrayList<Task> tasks = taskDAO.getAll();

        if (tasks != null) {
            for (Task t : tasks) {
                t.setCards(cardDAO.getByTaskID(t.getID()));
            }
        }

        Log.d(TAG,"Notif clicked");
        CardNotification cardNotification = new CardNotification(tasks.get(0));

        Notification[] notifications = cardNotification.buildNotifications(this);

        // Post new notifications
        for (int i = 0; i < notifications.length; i++) {
            NotificationManagerCompat.from(this).notify(i, notifications[i]);
        }

    }

    public void onFileTestClick(View view){

        File file = new File(getApplicationContext().getCacheDir() + "/pic/tmpPic.jpg");
        Uri imageUri = Uri.fromFile(file);
        Log.d(TAG, "File Uri: " + imageUri);
    }
}
