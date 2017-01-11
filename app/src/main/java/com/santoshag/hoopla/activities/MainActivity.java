package com.santoshag.hoopla.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.santoshag.hoopla.R;
import com.santoshag.hoopla.adapters.CustomTodoItemAdapter;
import com.santoshag.hoopla.decorators.DividerItemDecoration;
import com.santoshag.hoopla.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    CustomTodoItemAdapter adapterTodoItems;
    ArrayList<TodoItem> todoItems;
    RecyclerView rvItems;
    EditText etEditItemText;
    TextView toolbar_title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //set action bar icon

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //prepare list view using custom adapter
        populateTodoItems();

        /*//delete item on long click
        rvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDelete(position);
                return true;
            }
        });

        //start add new todo activity
        rvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem selectedItem = adapterTodoItems.getItem(position);
                Intent i = new Intent(MainActivity.this, ViewTodoItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("dbItemIndex", selectedItem.getId());
                startActivity(i);
            }
        });*/
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
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
            default:
                break;
        }

        return true;
    }

    public void populateTodoItems() {
        // Query ActiveAndroid for list of todo items currenty sorted by priority
        List<TodoItem> queryResults = new Select().from(TodoItem.class)
                .execute();
        // Load the result into the adapter using `addAll`
        todoItems = new ArrayList<TodoItem>(queryResults);
        adapterTodoItems =
                new CustomTodoItemAdapter(this, todoItems);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setAdapter(adapterTodoItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvItems.addItemDecoration(itemDecoration);
    }


    public List<TodoItem> getAllTodoItems() {
        return new Select()
                .from(TodoItem.class)
                .execute();
    }


    //build dialog to confirm delete and execute delete
    private void confirmDelete(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Delete Task");
        alertDialogBuilder.setIcon(R.drawable.ic_delete_black_18dp);
       /* TodoItem itemToBeDeleted = adapterTodoItems.getItem(position);

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
                        adapterTodoItems.remove(itemToBeDeleted);
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
        alertDialog.show();*/

    }

    public void onClickAddNewTask(View view) {
        Intent i = new Intent(MainActivity.this, NewItemActivity.class);
        startActivity(i);
    }
}


