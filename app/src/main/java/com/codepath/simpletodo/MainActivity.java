package com.codepath.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> todoItems;
    ArrayAdapter<String> aToDoAdapter;
    ListView lvItems;
    EditText etEditItemText;

    //REQUEST_CODE 1 for edit items
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        populateTodoItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditItemText = (EditText) findViewById(R.id.etAddItem);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoItems.remove(position);
                aToDoAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("itemText", todoItems.get(position));
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    private void readItems(){
        File fileDir = getFilesDir();
        File file = new File(fileDir, "todo.txt");
        try{
            todoItems = new ArrayList<>(FileUtils.readLines(file));

        }catch(IOException e){
            todoItems = new ArrayList<>();
        }

    }

    private void writeItems(){
        File fileDir = getFilesDir();
        File file = new File(fileDir, "todo.txt");
        try{
            FileUtils.writeLines(file, todoItems);

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void populateTodoItems(){
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            int position = data.getExtras().getInt("position", -1);
            String itemText = data.getExtras().getString("itemText");

            todoItems.set(position, itemText);
            aToDoAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    public void onAddItem(View view) {
        aToDoAdapter.add(etEditItemText.getText().toString());
        etEditItemText.setText("");
        writeItems();
    }
}
