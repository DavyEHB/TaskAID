package be.ehb.taskaid.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by davy.van.belle on 19/08/2015.
 */
public class TaskAIDDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "TaskAID.db";

    private static final String SQL_CREATE_TASKS =
            "CREATE TABLE " + DBContract.Tasks.TABLE_NAME + " (" +
                    DBContract.Tasks._ID + " INTEGER PRIMARY KEY," +
                    DBContract.Tasks.BEACON_ADDRESS + " TEXT," +
                    DBContract.Tasks.TASK_NAME + " TEXT)";

    private static final String SQL_CREATE_CARDS =
            "CREATE TABLE " + DBContract.Cards.TABLE_NAME + " (" +
                    DBContract.Cards._ID + " INTEGER PRIMARY KEY," +
                    DBContract.Cards.TEXT + " TEXT," +
                    DBContract.Cards.PICTURE + " BLOB," +
                    DBContract.Cards.TASK_ID + " INTEGER," +
                    DBContract.Cards.ORDER + " INTEGER)";

    private static final String SQL_DELETE_TASKS =
            "DROP TABLE IF EXISTS " + DBContract.Tasks.TABLE_NAME;

    private static final String SQL_DELETE_CARDS =
            "DROP TABLE IF EXISTS " + DBContract.Cards.TABLE_NAME;
    private static final String TAG = "TASKAID_DB_HELPER";

    public TaskAIDDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"Creating database");
        db.execSQL(SQL_CREATE_TASKS);
        db.execSQL(SQL_CREATE_CARDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CARDS);
        db.execSQL(SQL_DELETE_TASKS);
        onCreate(db);
    }
}