package com.codepath.simpletodo;

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
import android.widget.Toast;
import android.widget.Toolbar;

import com.activeandroid.query.Select;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CustomTodoItemAdapter adapterTodoItems;
    ArrayList<TodoItem> todoItems;
    ListView lvItems;
    EditText etEditItemText;

    //REQUEST_CODE 1 for edit items
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        populateTodoItems();
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TodoItem itemToBeDeleted = adapterTodoItems.getItem(position);
                System.out.println("position: " + position + "  id:" + id + " itemId: " + itemToBeDeleted.getId());
                todoItems.remove(position);
                TodoItem.delete(TodoItem.class, itemToBeDeleted.getId());
                adapterTodoItems.notifyDataSetChanged();

                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem itemToBeDeleted = adapterTodoItems.getItem(position);
                Intent i = new Intent(MainActivity.this, ViewTodoItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("dbItemIndex", itemToBeDeleted.getId());
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

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


    public void populateTodoItems(){


    // Query ActiveAndroid for list of data
            List<TodoItem> queryResults = new Select().from(TodoItem.class)
                    .orderBy("Priority DESC").execute();
    // Load the result into the adapter using `addAll`
        todoItems = new ArrayList<TodoItem>(queryResults);
        adapterTodoItems =
                new CustomTodoItemAdapter(this, todoItems);
//            adapterTodoItems.addAll(queryResults);
    // Attach the adapter to a ListView
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(adapterTodoItems);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            int position = data.getExtras().getInt("position", -1);
            String itemText = data.getExtras().getString("itemText");


        }
    }

//    public void onAddItem(View view) {
//        Intent i = new Intent(MainActivity.this, NewItemActivity.class);
//        startActivity(i);
//    }

    public static List<TodoItem> getAllTodoItems() {
        return new Select()
                .from(TodoItem.class)
                .orderBy("Priority DESC")
                .execute();
    }
}
