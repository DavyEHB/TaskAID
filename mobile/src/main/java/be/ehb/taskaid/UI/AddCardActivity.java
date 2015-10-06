package be.ehb.taskaid.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import be.ehb.taskaid.R;
import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.service.CardFileContentProvider;

public class AddCardActivity extends Activity {

    public static final int EDIT_FLAG = 1;
    public static final int ADD_FLAG = 0;


    public static final String CARD = "card";

    private static final String TAG = "ADD_CARD_ACTIVITY";
    private static final int SELECT_PHOTO = 2;

    private static final String CARD_NAME = "card_name";
    private static final String CARD_PICTURE = "card_picture";


    private TextView tvCardText;
    private ImageView imCardPicture;
    private Button btnAdd;
    private EditText edCardText;


    private Card card;
    private Bitmap cardPicture = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        tvCardText = (TextView) findViewById(R.id.tvCardText);
        tvCardText.setVisibility(View.INVISIBLE);
        imCardPicture = (ImageView) findViewById(R.id.imCardPicture);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        edCardText = (EditText) findViewById(R.id.edCardText);
        edCardText.setVisibility(View.VISIBLE);


        if (savedInstanceState == null){
            switch (getIntent().getFlags()) {
                case EDIT_FLAG:
                    card = (Card) getIntent().getSerializableExtra(CARD);

                     edCardText.setText(card.getText());
                    if (card.getPicture() != null) {
                        imCardPicture.setImageBitmap(card.getPicture());
                    }

                    getActionBar().setTitle(R.string.edit);
                    btnAdd.setText(R.string.edit);
                    break;
                case ADD_FLAG:
                    card = new Card();
                    break;
            }
        } else {
            switch (getIntent().getFlags()) {
                case EDIT_FLAG:
                    card = (Card) getIntent().getSerializableExtra(CARD);

                    edCardText.setText(savedInstanceState.getString(CARD_NAME));
                    cardPicture = savedInstanceState.getParcelable(CARD_PICTURE);
                    if (cardPicture != null) {
                        imCardPicture.setImageBitmap(cardPicture);
                    }

                    getActionBar().setTitle(R.string.edit);
                    btnAdd.setText(R.string.edit);
                    break;
                case ADD_FLAG:
                    card = new Card();

                    edCardText.setText(savedInstanceState.getString(CARD_NAME));
                    cardPicture = savedInstanceState.getParcelable(CARD_PICTURE);
                    if (cardPicture != null) {
                        imCardPicture.setImageBitmap(cardPicture);
                    }
                    break;
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onBtnCancelClick(View view){
        onBackPressed();
    }

     public void onBtnAddClick(View view){
         Log.d(TAG,"Add clicked");

         if (!edCardText.getText().toString().equals("")){
             Intent output = new Intent();
             card.setText(edCardText.getText().toString());
             card.setPicture(cardPicture);
             output.putExtra(CARD,card);
             setResult(RESULT_OK, output);
             finish();
          } else Toast.makeText(this, R.string.invalid_name, Toast.LENGTH_SHORT).show();
     }

    public void onTvCardTextClick(View view){
        tvCardText.setVisibility(View.INVISIBLE);
        edCardText.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"OnActivityResult");
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(getFilesDir(), CardFileContentProvider.FILE_NAME);
                    Log.d(TAG, "File: " + file.toString());
                    cardPicture = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imCardPicture.setImageBitmap(cardPicture);
                    Log.d(TAG, "Deleted? :" + file.delete());
                }
                break;
        }
    }

    public void onImCardPictureClick(View view) {
        Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, CardFileContentProvider.CONTENT_URI);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CARD_NAME, edCardText.getText().toString());
        outState.putParcelable(CARD_PICTURE, cardPicture);
        super.onSaveInstanceState(outState);
    }
}
