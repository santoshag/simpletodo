package com.codepath.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity {
    ArrayList<String> todoItems;
    int position;
    EditText etItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        etItem = (EditText) findViewById(R.id.etItem);
        initItem();
    }

    private void initItem(){
        String itemText = getIntent().getStringExtra("itemText");
        position = getIntent().getIntExtra("position", -1);
        etItem.setText(itemText);
        //set the cursor to end of the text for editing
        etItem.setSelection(itemText.length());

    }

    public void onSaveItem(View v) {
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("itemText", etItem.getText().toString());
        data.putExtra("position", position); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
