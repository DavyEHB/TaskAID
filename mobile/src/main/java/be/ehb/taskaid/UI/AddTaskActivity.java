package be.ehb.taskaid.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import be.ehb.taskaid.DB.CardDAO;
import be.ehb.taskaid.DB.TaskDAO;
import be.ehb.taskaid.R;
import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.model.Task;


public class AddTaskActivity extends Activity {
    private static final String TAG = "ADD_TASK";
    private static final int SELECT_ADDRESS = 1;
    private static final int ADD_CARD = 2;
    private static final int EDIT_CARD = 3;

    public static final int EDIT_FLAG = 1;
    public static final int ADD_FLAG = 0;
    public static final String TASK_ID = "task_id";
    private static final String IS_REMOVABLE = "is_removable";
    private static final String TASK_NAME = "task_name";
    private static final String BEACON_ADDRESS = "beacon_address";
    private static final String CARD_ARRAY = "card_list";


    private EditText edNewName;
    private TextView tvBeaconAddress;
    private TextView tvEmptyCard;
    private ListView lvAddTaskCards;
    private Button btnAdd;

    private TaskDAO taskDAO;
    private CardDAO cardDAO;

    private CardListAdapter cardListAdapter;

    private int taskID;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case SELECT_ADDRESS:
                    tvBeaconAddress.setText(data.getStringExtra(BeaconSelectorActivity.ADDRESS));
                    break;
                case ADD_CARD:
                    Card newCard = (Card) data.getSerializableExtra(AddCardActivity.CARD);
                    newCard.setTaskID(taskID);
                    newCard.setOrder(cardListAdapter.getCount() + 1);
                    cardListAdapter.addCard(newCard);
                    invalidateOptionsMenu();
                    Log.d(TAG, "New card: " + newCard.toString() );
                    break;
                case EDIT_CARD:
                    Card editCard = (Card) data.getSerializableExtra(AddCardActivity.CARD);
                    cardListAdapter.update(editCard);
                    invalidateOptionsMenu();
                     Log.d(TAG, "Edit card: ");
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        taskDAO = TaskDAO.getInstance(getApplicationContext());
        cardDAO = CardDAO.getInstance(getApplicationContext());

        // Initializes list view adapter.
        cardListAdapter = new CardListAdapter();

        tvBeaconAddress = (TextView) findViewById(R.id.tvBeaconAddress);
        tvEmptyCard = (TextView) findViewById(R.id.tvEmptyCard);
        edNewName = (EditText) findViewById(R.id.etAddTaskName);
        lvAddTaskCards = (ListView) findViewById(R.id.lvAddTaskCards);
        btnAdd = (Button) findViewById(R.id.btnAddTaskAdd);

        taskDAO = TaskDAO.getInstance(getApplicationContext());
        cardDAO = CardDAO.getInstance(getApplicationContext());
        Log.d(TAG, "Flag: " + getIntent().getFlags());

        lvAddTaskCards.setEmptyView(tvEmptyCard);
        lvAddTaskCards.setAdapter(cardListAdapter);
        lvAddTaskCards.setClickable(true);
        lvAddTaskCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG,"List Clicked");
                if (!cardListAdapter.isRemovable){
                    Intent intent = new Intent(getApplicationContext(),AddCardActivity.class);
                    Card card = cardListAdapter.getCard(position);
                    intent.putExtra(AddCardActivity.CARD,card);
                    intent.addFlags(AddTaskActivity.EDIT_FLAG);
                    startActivityForResult(intent, EDIT_CARD);
                }
            }
        });

        if (savedInstanceState == null) {
            if (getIntent().getFlags() == EDIT_FLAG){
                taskID = getIntent().getIntExtra(TASK_ID, 0);
                Task task = taskDAO.getByID(taskID);

                Log.d(TAG, task.toString());
                tvBeaconAddress.setText(task.getBeaconAddress());
                edNewName.setText(task.getName());

                cardListAdapter.addCards(cardDAO.getByTaskID(task.getID()));

                getActionBar().setTitle(R.string.edit);
                btnAdd.setText(R.string.edit);
            }
        } else {

            if (getIntent().getFlags() == EDIT_FLAG){
                getActionBar().setTitle(R.string.edit);
                btnAdd.setText(R.string.edit);
            }

            cardListAdapter.setRemovable(savedInstanceState.getBoolean(IS_REMOVABLE));
            edNewName.setText(savedInstanceState.getString(TASK_NAME));
            tvBeaconAddress.setText(savedInstanceState.getString(BEACON_ADDRESS));
            cardListAdapter.addCards((ArrayList<Card>) savedInstanceState.getSerializable(CARD_ARRAY));

        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_REMOVABLE,cardListAdapter.isRemovable);
        outState.putString(TASK_NAME, edNewName.getText().toString());
        outState.putString(BEACON_ADDRESS,tvBeaconAddress.getText().toString());
        outState.putSerializable(CARD_ARRAY,cardListAdapter.cardArrayList);
        super.onSaveInstanceState(outState);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_list, menu);
        Log.d(TAG, String.valueOf(cardListAdapter.getCount()));
        if (cardListAdapter.getCount() > 0 ){
            if (cardListAdapter.isRemovable){
                menu.findItem(R.id.menu_remove_card).setVisible(false);
                menu.findItem(R.id.menu_cancel_remove).setVisible(true);
            } else {
                menu.findItem(R.id.menu_remove_card).setVisible(true);
                menu.findItem(R.id.menu_cancel_remove).setVisible(false);
            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_add_card:
                Log.d(TAG,"Add card click");
                Intent intent = new Intent(this,AddCardActivity.class);
                intent.addFlags(AddCardActivity.ADD_FLAG);
                startActivityForResult(intent, ADD_CARD);
                return true;
            case R.id.menu_remove_card:
                Log.d(TAG,"Remove card click");
                cardListAdapter.setRemovable(true);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_cancel_remove:
                Log.d(TAG,"Stop remove click");
                cardListAdapter.setRemovable(false);
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBtnCancelClick(View view){
        onBackPressed();
    }

    public void onBtnAddClick(View view){
        String name = edNewName.getText().toString();
        String address = tvBeaconAddress.getText().toString();
        String dummy = getResources().getString(R.string.dummy_beacon_address);
        if (!name.equals("")){
            if (!address.equals(dummy)){
                if (getIntent().getFlags() == ADD_FLAG) {
                    Task newTask = new Task(name);
                    newTask.setBeaconAddress(address);
                    taskDAO.insert(newTask);
                    newTask.setCards(cardListAdapter.cardArrayList);
                    cardDAO.insert(cardListAdapter.cardArrayList);

                    Log.d(TAG, "Created task: " + newTask.toString());
                } else if (getIntent().getFlags() == EDIT_FLAG) {
                    Task task = new Task(taskID,name,address);
                    taskDAO.update(task);

                    ArrayList<Card> oldCards = cardDAO.getByTaskID(task.getID());
                    ArrayList<Card> newCards = cardListAdapter.cardArrayList;
                    ArrayList<Integer> cardIDs = new ArrayList<>();

                    task.setCards(newCards);

                    int i = 0;
                    for (Card card : newCards){
                        cardIDs.add(card.getID());
                        card.setOrder(++i);
                        if (card.getID() == 0){
                            //insert card
                            Log.d(TAG,"Insert: " + card.toString());
                            cardDAO.insert(card);
                        } else {
                            //update card
                            Log.d(TAG,"Update: " + card.toString());
                            cardDAO.update(card);
                        }
                    }

                    for (Card card : oldCards){
                        if (!cardIDs.contains(card.getID())){
                            //delete card
                            Log.d(TAG,"Delete: " + card.toString());
                            cardDAO.delete(card);
                        }
                    }
                    Log.d(TAG, "Updated task: " + task.toString());
                }

                this.finish();
            } else Toast.makeText(this, R.string.invalid_beacon, Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, R.string.invalid_name, Toast.LENGTH_SHORT).show();
    }

    public void onTvBeaconAddressClick(View view){
        Intent intent = new Intent(this,BeaconSelectorActivity.class);
        startActivityForResult(intent, SELECT_ADDRESS);
    }

    private class CardListAdapter extends BaseAdapter{

        private ArrayList<Card> cardArrayList = new ArrayList<>();
        private LayoutInflater inflator;
        private Boolean isRemovable = false;

        public CardListAdapter(){
            super();
            inflator = AddTaskActivity.this.getLayoutInflater();
        }

        public void addCard(Card card){
            if (!cardArrayList.contains(card)){
                cardArrayList.add(card);
                notifyDataSetChanged();
            }
        }

        public void setRemovable(Boolean removable){
            isRemovable = removable;
            notifyDataSetChanged();
        }

        public void addCards(ArrayList<Card> cards){
            cardArrayList = cards;
            notifyDataSetChanged();
        }

        public Card getCard(int position){
            return cardArrayList.get(position);
        }

        @Override
        public int getCount() {
            return cardArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return cardArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return ((Card) getItem(i)).getID();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            final int pos = i;
            final Card card = cardArrayList.get(i);
            final String cardText = card.getText();
            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflator.inflate(R.layout.card_list, null);
                viewHolder.tvCardText = (TextView) view.findViewById(R.id.tvCardText);
                viewHolder.tvOrder = (TextView) view.findViewById(R.id.tvOrder);
                viewHolder.btnDelete = (Button) view.findViewById(R.id.btnDelete);
                viewHolder.llUpDown = (LinearLayout) view.findViewById(R.id.llUpDown);
                viewHolder.btnUp = (Button) view.findViewById(R.id.btnUp);
                viewHolder.btnDown = (Button) view.findViewById(R.id.btnDown);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tvCardText.setText(cardText);
            viewHolder.tvOrder.setText(pos+1 + ":");
            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"Delete " + card.toString());
                    cardArrayList.remove(card);
                    notifyDataSetChanged();
                    invalidateOptionsMenu();
                }
            });

            viewHolder.btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Collections.swap(cardArrayList,pos,pos-1);
                    notifyDataSetChanged();
                }
            });

            viewHolder.btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Collections.swap(cardArrayList,pos,pos+1);
                    notifyDataSetChanged();
                }
            });

            if (isRemovable){
                viewHolder.btnDelete.setVisibility(View.VISIBLE);
                viewHolder.llUpDown.setVisibility(View.GONE);
            }
            else {
                viewHolder.btnDelete.setVisibility(View.GONE);
                viewHolder.llUpDown.setVisibility(View.VISIBLE);
                viewHolder.btnUp.setVisibility(View.VISIBLE);
                viewHolder.btnDown.setVisibility(View.VISIBLE);

                if (pos == 0){
                    viewHolder.btnUp.setVisibility(View.INVISIBLE);
                }
                if (pos == getCount()-1){
                    viewHolder.btnDown.setVisibility(View.INVISIBLE);
                }
            }
            return view;
        }

        public Card getByID(int id) {
            for (Card card : cardArrayList){
                if (card.getID() == id){
                    return card;
                }
            }
            return null;
        }

        public void update(Card card) {
            Card oldCard = getByID(card.getID());
            int pos = cardArrayList.indexOf(oldCard);
            cardArrayList.set(pos,card);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder{
        TextView tvCardText;
        TextView tvOrder;
        Button btnDelete;
        LinearLayout llUpDown;
        Button btnUp;
        Button btnDown;
    }
}
