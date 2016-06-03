package com.codepath.simpletodo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewTodoItemActivity extends AppCompatActivity {
    EditText etTitle;
    EditText etNotes;
    Spinner spPriority;
    DatePicker dpDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);
        populateItemFields();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewtaskmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_edit_item:
                Intent i = new Intent(ViewTodoItemActivity.this, NewItemActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }


    private void populateItemFields(){
        int position = getIntent().getIntExtra("position", -1);
        Long dbItemIndex = getIntent().getLongExtra("dbItemIndex", -1);
        TodoItem todoItem  = (TodoItem) new Select().from(TodoItem.class)
                .where("id = ?", dbItemIndex).executeSingle();
        System.out.println(todoItem.title);


        etTitle = (EditText) findViewById(R.id.etItemTitle);
        etNotes = (EditText) findViewById(R.id.etItemNotes);
        spPriority = (Spinner) findViewById(R.id.spPriority);

        etTitle.setText(todoItem.title);
        etTitle.setEnabled(false);
        etNotes.setText(todoItem.notes);
        etNotes.setEnabled(false);
        spPriority.setSelection(todoItem.priority);
        spPriority.setEnabled(false);
//        dpDueDate.init();


        String dueDateString = todoItem.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date dueDate;
        try {
            dueDate = df.parse(dueDateString);

            String intMonth = (String) android.text.format.DateFormat.format("MM", dueDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", dueDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", dueDate); //20

            dpDueDate.updateDate(Integer.parseInt(year), Integer.parseInt(intMonth), Integer.parseInt(day));
            dpDueDate.setMinDate(dueDate.getTime());
            dpDueDate.setMaxDate(dueDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
