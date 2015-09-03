package be.ehb.taskaid.UI;

import android.app.Activity;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import be.ehb.taskaid.R;
import be.ehb.taskaid.service.BeaconScanner;

public class SelectorActivity extends Activity {

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
}
