package be.ehb.taskaid.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;

import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.model.Task;

/**
 * Created by davy.van.belle on 19/08/2015.
 */
public class TaskDAO {
    private static final String TAG = "TASK_DAO";

    private SQLiteDatabase db;
    private  TaskAIDDBHelper dbHelper;
    private static TaskDAO instance = null;
    private Context context;

    private TaskDAO(Context context){
        this.context = context;
        dbHelper = new TaskAIDDBHelper(context);
    }

    private TaskDAO(){}

    public static TaskDAO getInstance(Context context){
        if (instance == null){
            instance = new TaskDAO(context);
        }
        return instance;
    }

    private void open(){
        db = dbHelper.getWritableDatabase();
    }

    private void close(){
        if (db.isOpen()){
            db.close();
        }
    }

    //CRUD

    public Task insert(Task task){
        ContentValues values = new ContentValues();

        values.put(DBContract.Tasks.TASK_NAME,task.getName());
        values.put(DBContract.Tasks.BEACON_ADDRESS,task.getBeaconAddress());

        open();
        long newID = db.insert(DBContract.Tasks.TABLE_NAME, null, values);
        task.setID((int) newID);
        close();

        return task;
    }

    public Task getByID(int id){
        String selectQuery = "SELECT  * FROM " + DBContract.Tasks.TABLE_NAME + " WHERE " + DBContract.Tasks._ID + " = ?";
        String[] args = {String.valueOf(id)};
        Task task = new Task();

        open();
        Cursor cursor = db.rawQuery(selectQuery, args);

        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            task = cursorToTask(cursor);
        }
        close();
        return task;
    }


    public ArrayList<Task> getAll(){
        String selectQuery = "SELECT  * FROM " + DBContract.Tasks.TABLE_NAME;
        String[] args = null;
        ArrayList<Task> allTasks = new ArrayList<>();

        open();
        Cursor cursor = db.rawQuery(selectQuery,args);

        if(cursor!=null && cursor.getCount()>0) {
            allTasks = new ArrayList<>();
            cursor.moveToFirst();
            do {
                allTasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }
        close();
        return allTasks;
    }


    public int update(Task task){
        ContentValues values = new ContentValues();

        values.put(DBContract.Tasks._ID, task.getID());
        values.put(DBContract.Tasks.TASK_NAME,task.getName());
        values.put(DBContract.Tasks.BEACON_ADDRESS,task.getBeaconAddress());


        String selection = DBContract.Tasks._ID + " = ?";
        String[] selectionArgs = { String.valueOf(task.getID()) };

        open();
        int count = db.update(
                DBContract.Tasks.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        close();

        return count;
    }

    public void delete(Task task){
        String[] args = {String.valueOf(task.getID())};
        CardDAO cardDAO = CardDAO.getInstance(context);
        open();
        for (Card card : task.getCards()){
            cardDAO.delete(card);
        }
        db.delete(DBContract.Tasks.TABLE_NAME, DBContract.Tasks._ID + " = ?",args );
        close();
    }

    public void deleteAll(){
        this.open();
        db.delete(DBContract.Cards.TABLE_NAME,null,null);
        db.delete(DBContract.Tasks.TABLE_NAME,null,null);
        this.close();
    }

    private Task cursorToTask(Cursor cursor) {

        int id  = cursor.getInt(
                cursor.getColumnIndex(DBContract.Tasks._ID));
        String name = cursor.getString(
                cursor.getColumnIndex(DBContract.Tasks.TASK_NAME));
        String beacon = cursor.getString(
                cursor.getColumnIndex(DBContract.Tasks.BEACON_ADDRESS));

        return new Task(id,name,beacon);
    }

}
