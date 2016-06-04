package com.codepath.simpletodo.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.fragments.EditTaskFragment;
import com.codepath.simpletodo.models.TodoItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewTodoItemActivity extends AppCompatActivity {
    TextView tvItemTitle;
    TextView tvItemNotes;
    TextView tvPriority;
    DatePicker dpDueDate;
    Long dbItemIndex;
    TodoItem todoItem;
    EditTaskFragment editItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        //set tool bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.view_task);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon);

        dbItemIndex = getIntent().getLongExtra("dbItemIndex", -1);
        todoItem = (TodoItem) new Select().from(TodoItem.class)
                .where("id = ?", dbItemIndex).executeSingle();

        populateItemFields();
    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewtaskmenu, menu);
        return true;
    }

    //handle tool bar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_edit_item:
                showEditDialog();
                break;
            default:
                break;
        }

        return true;
    }

    //dialogfragment for edit todo item
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();

        editItemFragment = EditTaskFragment.newInstance(dbItemIndex);
        editItemFragment.show(fm, "fragment_edit_name");
    }


    private void populateItemFields() {
        int position = getIntent().getIntExtra("position", -1);

        tvItemTitle = (TextView) findViewById(R.id.tvItemTitle);
        tvItemNotes = (TextView) findViewById(R.id.tvItemNotes);
        tvPriority = (TextView) findViewById(R.id.tvPriority);
        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);

        tvItemTitle.setText(todoItem.title);
        tvItemNotes.setText(todoItem.notes);
        tvPriority.setText(TodoItem.getPriorityInString(todoItem.priority));
        tvPriority.setTextColor(getPriorityColor(todoItem.priority));

        String dueDateString = todoItem.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date dueDate;
        try {
            dueDate = df.parse(dueDateString);

            String intMonth = (String) android.text.format.DateFormat.format("MM", dueDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", dueDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", dueDate); //20

            dpDueDate.updateDate(Integer.parseInt(year), Integer.parseInt(intMonth), Integer.parseInt(day));
            //set min and max date as the due date to make it uneditable
            dpDueDate.setMinDate(dueDate.getTime());
            dpDueDate.setMaxDate(dueDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    int getPriorityColor(int priority) {
        switch (priority) {
            case 0:
                return getApplicationContext().getResources().getColor(R.color.priority_low);
            case 1:
                return getApplicationContext().getResources().getColor(R.color.priority_medium);
            case 2:
                return getApplicationContext().getResources().getColor(R.color.priority_high);
        }
        return getApplicationContext().getResources().getColor(R.color.priority_medium);
    }

}