package com.codepath.simpletodo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.adapters.CustomTodoItemAdapter;
import com.codepath.simpletodo.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CustomTodoItemAdapter adapterTodoItems;
    ArrayList<TodoItem> todoItems;
    ListView lvItems;
    EditText etEditItemText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set action bar icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon);

        //prepare list view using custom adapter
        populateTodoItems();

        //delete item on long click
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDelete(position);
                return true;
            }
        });

        //start add new todo activity
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem selectedItem = adapterTodoItems.getItem(position);
                Intent i = new Intent(MainActivity.this, ViewTodoItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("dbItemIndex", selectedItem.getId());
                startActivity(i);
            }
        });
    }

    //generate tool bar actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    //handle tool bar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_add_new_item:
                Intent i = new Intent(MainActivity.this, NewItemActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }


    public void populateTodoItems() {
        // Query ActiveAndroid for list of todo items currenty sorted by priority
        List<TodoItem> queryResults = new Select().from(TodoItem.class)
                .orderBy("Priority DESC").execute();
        // Load the result into the adapter using `addAll`
        todoItems = new ArrayList<TodoItem>(queryResults);
        adapterTodoItems =
                new CustomTodoItemAdapter(this, todoItems);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(adapterTodoItems);

    }


    public List<TodoItem> getAllTodoItems() {
        return new Select()
                .from(TodoItem.class)
                .orderBy("Priority DESC")
                .execute();
    }


    //build dialog to confirm delete and execute delete
    private void confirmDelete(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Delete Task");
        alertDialogBuilder.setIcon(R.drawable.ic_delete_black_18dp);
        TodoItem itemToBeDeleted = adapterTodoItems.getItem(position);

        // set dialog message
        alertDialogBuilder
                .setMessage("Delete " + itemToBeDeleted.title + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        TodoItem itemToBeDeleted = adapterTodoItems.getItem(position);
                        System.out.println("position: " + position + "  id:" + id + " itemId: " + itemToBeDeleted.getId());
                        todoItems.remove(position);
                        TodoItem.delete(TodoItem.class, itemToBeDeleted.getId());
                        adapterTodoItems.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

}


