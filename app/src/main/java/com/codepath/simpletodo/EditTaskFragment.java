package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
// ...

public class EditTaskFragment extends DialogFragment {

    EditText etTitle;
    EditText etNotes;
    Spinner spPriority;
    DatePicker dpDueDate;
    TodoItem todoItem;

    public EditTaskFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }


    public static EditTaskFragment newInstance(Long editTaskId) {
        EditTaskFragment frag = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putLong("editTaskId", editTaskId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("newItem.getId(): before" + getArguments().getLong("editTaskId"));

        todoItem  = (TodoItem) new Select().from(TodoItem.class)
                .where("id = ?", getArguments().getLong("editTaskId")).executeSingle();


    }

        @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            Button btnSave = (Button) view.findViewById(R.id.btnSave);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveItem(view);
                }
            });

            Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelDialog(view);
                }
            });


        etTitle = (EditText) view.findViewById(R.id.etItemTitle);
        etNotes = (EditText) view.findViewById(R.id.etItemNotes);
        spPriority = (Spinner) view.findViewById(R.id.spPriority);
        dpDueDate = (DatePicker) view.findViewById(R.id.dpDueDate);

        etTitle.setText(todoItem.title);
        etNotes.setText(todoItem.notes);
        spPriority.setSelection(todoItem.priority);

        String dueDateString = todoItem.dueDate;
        DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss z yyyy", Locale.ENGLISH);
        Date dueDate;
        try {
            dueDate = df.parse(dueDateString);

            String intMonth = (String) android.text.format.DateFormat.format("MM", dueDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", dueDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", dueDate); //20

            dpDueDate.updateDate(Integer.parseInt(year), Integer.parseInt(intMonth)-1, Integer.parseInt(day));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveItem(View view) {
        System.out.println("called from df");
        etTitle = (EditText) view.findViewById(R.id.etItemTitle);
        etNotes = (EditText) view.findViewById(R.id.etItemNotes);
        spPriority = (Spinner) view.findViewById(R.id.spPriority);
        dpDueDate = (DatePicker) view.findViewById(R.id.dpDueDate);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(dpDueDate.getYear(), dpDueDate.getMonth(), dpDueDate.getDayOfMonth());


        todoItem.title = etTitle.getText().toString();
        todoItem.notes = etNotes.getText().toString();
        todoItem.priority = spPriority.getSelectedItemPosition();
        todoItem.dueDate = calendar.getTime().toString();
        todoItem.save();

        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        this.dismiss();

    }




    public void cancelDialog(View view) {
        this.dismiss();
    }
}