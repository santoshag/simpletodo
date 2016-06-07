package com.codepath.simpletodo.activities;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.models.TodoItem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class NewItemActivity extends AppCompatActivity {
    EditText etTitle;
    EditText etNotes;
    Spinner spPriority;
    DatePicker dpDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);
        assert dpDueDate != null;
        dpDueDate.setMinDate(System.currentTimeMillis() - 1000);
    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newtaskmenu, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.new_task);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon);
        return true;
    }

    //handle tool bar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_save_new_item:
                saveItem();
                Intent i = new Intent(NewItemActivity.this, MainActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }

    //save todo item to db
    private void saveItem() {
        etTitle = (EditText) findViewById(R.id.etItemTitle);
        etNotes = (EditText) findViewById(R.id.etItemNotes);
        spPriority = (Spinner) findViewById(R.id.spPriority);
        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(dpDueDate.getYear(), dpDueDate.getMonth(), dpDueDate.getDayOfMonth());

        //store date as string as activeandroid had issues with serializing date
        TodoItem newItem = new TodoItem(etTitle.getText().toString(), etNotes.getText().toString(), spPriority.getSelectedItemPosition(), calendar.getTime().toString());
        newItem.save();


    }
}
