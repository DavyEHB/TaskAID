package be.ehb.taskaid.UI;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import be.ehb.taskaid.DB.CardDAO;
import be.ehb.taskaid.DB.TaskDAO;
import be.ehb.taskaid.R;
import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.model.Task;

public class TaskListActivity extends ListActivity {

    private static final String TAG = "TASK_LIST_ACTIVITY";
    private static final String IS_REMOVABLE = "is_removable";
    private TaskListAdapter taskListAdapter;
    private TaskDAO taskDAO;
    private CardDAO cardDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskDAO = TaskDAO.getInstance(getApplicationContext());
        cardDAO = CardDAO.getInstance(getApplicationContext());

        // Initializes list view adapter.
        taskListAdapter = new TaskListAdapter();

        if (savedInstanceState != null) {
            taskListAdapter.setRemovable(savedInstanceState.getBoolean(IS_REMOVABLE));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_REMOVABLE,taskListAdapter.isRemovable);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();




        ArrayList<Task> tasks = taskDAO.getAll();
        taskListAdapter.addTask(tasks);

        if (tasks != null) {
            for (Task t : tasks) {
                t.setCards(cardDAO.getByTaskID(t.getID()));
            }
            /*
            for (Task t : tasks) {
                Log.d(TAG, "Task: " + t.toString());
                for (Card c : t.getCards()) {
                    Log.d(TAG, "Card: " + c.toString());
                }
            }
            */
            setListAdapter(taskListAdapter);
        }
        invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        Log.d(TAG, String.valueOf(taskListAdapter.getCount()));
        if (taskListAdapter.getCount() > 0 ){
            if (taskListAdapter.isRemovable){
                menu.findItem(R.id.menu_remove_task).setVisible(false);
                menu.findItem(R.id.menu_cancel_remove).setVisible(true);
            } else {
                menu.findItem(R.id.menu_remove_task).setVisible(true);
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
            case R.id.menu_add_task:
                Log.d(TAG,"Add task click");
                Intent intent = new Intent(this,AddTaskActivity.class);
                intent.addFlags(AddTaskActivity.ADD_FLAG);
                startActivity(intent);
                return true;
            case R.id.menu_remove_task:
                Log.d(TAG,"Remove task click");
                taskListAdapter.setRemovable(true);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_cancel_remove:
                Log.d(TAG,"Stop remove click");
                taskListAdapter.setRemovable(false);
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!taskListAdapter.isRemovable){
            Intent intent = new Intent(this,AddTaskActivity.class);
            intent.putExtra(AddTaskActivity.TASK_ID,taskListAdapter.getTask(position).getID());
            intent.addFlags(AddTaskActivity.EDIT_FLAG);
            startActivity(intent);
        }
        super.onListItemClick(l, v, position, id);
    }

    private class TaskListAdapter extends BaseAdapter {

        private ArrayList<Task> taskArrayList;
        private LayoutInflater layoutInflater;
        private Boolean isRemovable = false;

        public TaskListAdapter() {
            super();
            layoutInflater = TaskListActivity.this.getLayoutInflater();
        }


        public void addTask(Task task) {
            if (!taskArrayList.contains(task)) {
                taskArrayList.add(task);
                notifyDataSetChanged();
            }
        }

        public void setRemovable(boolean removable){
            isRemovable = removable;
            notifyDataSetChanged();
        }


        public void addTask(ArrayList<Task> tasks){
            taskArrayList = tasks;
            notifyDataSetChanged();
        }

        public Task getTask(int position) {
            return taskArrayList.get(position);
        }

        public void clear() {
            taskArrayList.clear();
        }

        @Override
        public int getCount() {
            return taskArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return taskArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return ((Task) getItem(i)).getID();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            final Task task = taskArrayList.get(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.task_list, null);
                viewHolder = new ViewHolder();
                viewHolder.tvDeviceAddress = (TextView) view.findViewById(R.id.txtAddress);
                viewHolder.tvTaskName = (TextView) view.findViewById(R.id.txtName);
                viewHolder.btnDelete = (Button) view.findViewById(R.id.btnDelete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            final String taskName = task.getName();
            viewHolder.tvTaskName.setText(taskName);
            viewHolder.tvDeviceAddress.setText(task.getBeaconAddress());
            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"BtnDel clicked");
                    Log.d(TAG,"Delete " + task.toString());

                    taskArrayList.remove(task);
                    taskDAO.delete(task);
                    notifyDataSetChanged();
                    invalidateOptionsMenu();
                }
            });

            if (isRemovable) viewHolder.btnDelete.setVisibility(View.VISIBLE);
            else viewHolder.btnDelete.setVisibility(View.INVISIBLE);

            return view;
        }
    }

    static class ViewHolder {
        TextView tvTaskName;
        TextView tvDeviceAddress;
        Button btnDelete;
    }
}
