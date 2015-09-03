package be.ehb.taskaid.UI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

import be.ehb.taskaid.DB.CardDAO;
import be.ehb.taskaid.DB.TaskDAO;
import be.ehb.taskaid.R;
import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.model.Task;


public class MainActivity extends Activity {

    private static final String TAG = "MAIN_ACTIVITY";

    private CardDAO cardDAO;
    private TaskDAO taskDAO;


    public void onBtnInsertClick(View view){
        Log.d(TAG, "btnInsert Clicked");
        Button button = (Button) view;

        /*
        Card c1 = new Card("Card 1");
        c1.setTaskID(4);
        Log.d(TAG, c1.toString());
        cardDAO.insert(c1);
        Log.d(TAG, c1.toString());
        */

        Task t1 = new Task("Task 1");
        Log.d(TAG, t1.toString());
        taskDAO.insert(t1);
        Log.d(TAG, t1.toString());

    }

    public void onBtnGetByIDClick(View view){
        Log.d(TAG, "btnGetByID Clicked");
        Button button = (Button) view;

        /*
        Card c1 = cardDAO.getByID(2);
        Log.d(TAG, c1.toString());

        Card c2 = cardDAO.getByID(10);
        if (c2 != null){
            Log.d(TAG, c2.toString());
        }


        Card c3 = cardDAO.getByID(0);
        if (c3 != null){
            Log.d(TAG, c3.toString());
        }
        */

        Task t1 = taskDAO.getByID(2);
        Log.d(TAG, t1.toString());

    }

    public void onBtnDeleteClick(View view){
        Log.d(TAG, "btnDelete Clicked");
        Button button = (Button) view;

        /*
        Card c1 = cardDAO.getByID(1);
        if (c1 != null){
            Log.d(TAG, c1.toString());
        }

        cardDAO.delete(c1);

        c1 = cardDAO.getByID(1);
        if (c1 != null){
            Log.d(TAG, c1.toString());
        }
        */

        Task t = taskDAO.getByID(1);
        Log.d(TAG, t.toString());

        taskDAO.delete(t);

        t = taskDAO.getByID(1);

    }

    public void onBtnUpdateClick(View view){
        Log.d(TAG, "btnUpdate Clicked");


        /*
        Card c = cardDAO.getByID(4);
        if (c != null){
            Log.d(TAG, c.toString());
            c.setOrder(10);
            c.setText("New Text");
            cardDAO.update(c);
            c = cardDAO.getByID(4);
            Log.d(TAG, c.toString());
        }
        */

        Task t = taskDAO.getByID(4);
        if (t != null){
            Log.d(TAG, t.toString());
            t.setName("New Name");
            t.setBeaconAddress("0d:45:ef:00:9a");
            taskDAO.update(t);
            t = taskDAO.getByID(4);
            Log.d(TAG, t.toString());
        }
    }

    public void onBtnGetAllClick(View view){
        Log.d(TAG, "btnGetAllClick Clicked");

        /*
        ArrayList<Card> allCards = cardDAO.getAll();
        for (Card card : allCards){
            Log.d(TAG, card.toString());
        }
        */

        ArrayList<Task> allTasks = taskDAO.getAll();
        for (Task t : allTasks){
            Log.d(TAG, t.toString());
        }
    }

    public void onBtnInsertAll(View view){
        Log.d(TAG, "btnGetAllClick Clicked");
        Task t1 = new Task("task 1");
        t1.setBeaconAddress("E5:32:9D:AD:9C");

        Card c1 = new Card("card 1",3);
        Card c2 = new Card("card 2",2);
        Card c3 = new Card("card 3",1);

        taskDAO.insert(t1);

        t1.addCard(c1);
        cardDAO.insert(c1);

        t1.addCard(c2);
        cardDAO.insert(c2);

        t1.addCard(c3);
        cardDAO.insert(c3);


        Task t2 = new Task("task 2");
        t2.setBeaconAddress("E5:32:9D:AD:10");

        Card c4 = new Card("card 4",10);
        Card c5 = new Card("card 5",14);
        Card c6 = new Card("card 6",1);
        Card c7 = new Card("card 7",0);
        Card c8 = new Card("card 8",2);
        Card c9 = new Card("card 9",6);

        Card[] t2c = {c4,c5,c6,c7,c8,c9};

        taskDAO.insert(t2);
        t2.setCards(new ArrayList<>(Arrays.asList(t2c)));
        for (Card c : t2.getCards()){
            cardDAO.insert(c);
        }
    }

    public void onBtnReadAllClick(View view){
        ArrayList<Task> tasks = taskDAO.getAll();
        if (tasks != null) {
            for (Task t : tasks) {
                t.setCards(cardDAO.getByTaskID(t.getID()));
            }
            for (Task t : tasks) {
                Log.d(TAG, "Task: " + t.toString());
                for (Card c : t.getCards()) {
                    Log.d(TAG, "Card: " + c.toString());
                }
            }
        }
    }

    public void onBtnClearDB(View view) {
        taskDAO.deleteAll();
        cardDAO.deleteAll();
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardDAO = CardDAO.getInstance(getApplicationContext());
        taskDAO = TaskDAO.getInstance(getApplicationContext());
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
