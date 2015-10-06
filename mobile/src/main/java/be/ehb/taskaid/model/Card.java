package be.ehb.taskaid.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by davy.van.belle on 20/08/2015.
 */
public class Card implements Serializable {

    private static final int MAX_WIDTH = 400;
    private static final String TAG = "CARD";
    private int ID;
    private String text;
    private Bitmap picture;
    private int order;
    private int taskID;



    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Card() {
    }

    public Card(String text) {
        this.text = text;
    }

    public Card(String text, int order) {
        this.text = text;
        this.order = order;
    }

    public Card(int id, String text, Bitmap picture, int order, int taskID) {
        this.ID = id;
        this.text = text;
        this.picture = picture;
        this.order = order;
        this.taskID = taskID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        if (picture != null) {
            this.picture = getResizedBitmap(picture, MAX_WIDTH);
        }
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "ID: " + this.ID + " - " +
                "Text: " + this.text + " - " +
                "Order " + this.order + " - " +
                "TaskID " + this.taskID;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int maxWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = ((float) maxWidth) / width;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scale, scale);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.ID);
        out.writeObject(this.text);
        out.writeInt(this.order);
        out.writeInt(this.taskID);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (picture != null){
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }

        out.writeObject(stream.toByteArray());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        this.ID = in.readInt();

        this.text = (String) in.readObject();
        this.order = in.readInt();
        this.taskID = in.readInt();

        byte temp[] = (byte[]) in.readObject();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(temp);

        this.picture = BitmapFactory.decodeByteArray(temp, 0, temp.length);

    }

}
