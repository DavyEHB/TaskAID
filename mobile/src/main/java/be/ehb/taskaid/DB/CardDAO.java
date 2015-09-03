package be.ehb.taskaid.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import be.ehb.taskaid.model.Card;

/**
 * Created by davy.van.belle on 19/08/2015.
 */
public class CardDAO {

    private static final String TAG = "CARD_DAO";

    private  SQLiteDatabase db;
    private  TaskAIDDBHelper dbHelper;

    private static CardDAO instance = null;

    private CardDAO(Context context){
        dbHelper = new TaskAIDDBHelper(context);
    }

    private CardDAO(){}

    public static CardDAO getInstance(Context context){
        if (instance == null) {
            instance = new CardDAO(context);
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

    public Card insert(Card card){
        ContentValues values = new ContentValues();

        values.put(DBContract.Cards.TEXT,card.getText());
        values.put(DBContract.Cards.PICTURE,getBytes(card.getPicture()));
        values.put(DBContract.Cards.ORDER,card.getOrder());
        values.put(DBContract.Cards.TASK_ID,card.getTaskID());

        open();
        long newID = db.insert(DBContract.Cards.TABLE_NAME, null, values);
        card.setID((int) newID);
        close();

        return card;
    }

    public void insert(ArrayList<Card> cards) {
        for (Card card : cards){
            insert(card);
        }
    }

    public Card getByID(int id){
        String selectQuery = "SELECT  * FROM " + DBContract.Cards.TABLE_NAME + " WHERE " + DBContract.Cards._ID + " = ?";
        String[] args = {String.valueOf(id)};
        Card card = new Card();

        open();
        Cursor cursor = db.rawQuery(selectQuery, args);


        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            card = cursorToCard(cursor);
        }
        close();
        return card;
    }

    public ArrayList<Card> getByTaskID(int taskID){
        String selectQuery = "SELECT  * FROM " + DBContract.Cards.TABLE_NAME + " WHERE " + DBContract.Cards.TASK_ID + " = ? ORDER BY " + DBContract.Cards.ORDER + " ASC";
        String[] args = {String.valueOf(taskID)};
        ArrayList<Card> allCards = new ArrayList<>();

        open();
        Cursor cursor = db.rawQuery(selectQuery,args);

        if(cursor!=null && cursor.getCount()>0) {
            allCards = new ArrayList<>();
            cursor.moveToFirst();
            do {
                allCards.add(cursorToCard(cursor));
            } while (cursor.moveToNext());
        }
        close();
        return allCards;
    }

    public ArrayList<Card> getAll(){
        String selectQuery = "SELECT  * FROM " + DBContract.Cards.TABLE_NAME;
        String[] args = null;
        ArrayList<Card> allCards = new ArrayList<>();

        open();
        Cursor cursor = db.rawQuery(selectQuery,args);

        if(cursor!=null && cursor.getCount()>0) {
            allCards = new ArrayList<>();
            cursor.moveToFirst();
            do {
                allCards.add(cursorToCard(cursor));
            } while (cursor.moveToNext());
        }
        close();
        return allCards;
    }


    public int update(Card card){
        ContentValues values = new ContentValues();

        values.put(DBContract.Cards._ID,card.getID());
        values.put(DBContract.Cards.TEXT,card.getText());
        values.put(DBContract.Cards.PICTURE,getBytes(card.getPicture()));
        values.put(DBContract.Cards.ORDER,card.getOrder());

        String selection = DBContract.Cards._ID + " = ?";
        String[] selectionArgs = { String.valueOf(card.getID()) };

        open();
        int count = db.update(
                DBContract.Cards.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        close();

        return count;
    }

    public void update(ArrayList<Card> cards) {
        for (Card card : cards){
            update(card);
        }
    }

    public void delete(Card card){
        open();
        db.delete(DBContract.Cards.TABLE_NAME, DBContract.Cards._ID + " = ?", new String[]{String.valueOf(card.getID())});
        close();
    }

    public void deleteAll(){
        this.open();
        db.delete(DBContract.Cards.TABLE_NAME,null,null);
        this.close();
    }


    // convert from bitmap to byte array
    private static byte[] getBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        } else return null;
    }

    // convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } else return null;
    }

    private Card cursorToCard(Cursor cursor) {

        int id  = cursor.getInt(
                cursor.getColumnIndex(DBContract.Cards._ID));
        String text = cursor.getString(
                cursor.getColumnIndex(DBContract.Cards.TEXT));
        Bitmap picture = getImage(
                cursor.getBlob(
                        cursor.getColumnIndex(DBContract.Cards.PICTURE)));
        int order = cursor.getInt(
                cursor.getColumnIndex(DBContract.Cards.ORDER));
        int taskID = cursor.getInt(
                cursor.getColumnIndex(DBContract.Cards.TASK_ID));

           return new Card(id,text,picture,order,taskID);
    }



}
