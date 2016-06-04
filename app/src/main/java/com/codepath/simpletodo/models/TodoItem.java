package com.codepath.simpletodo.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by santoshag on 5/31/16.
 */

@Table(name = "TodoItems")
public class TodoItem extends Model {
    public static int PRIORITY_LOW = 0;
    public static int PRIORITY_MEDIUM = 1;
    public static int PRIORITY_HIGH = 2;

    @Column(name = "Title")
    public String title;
    @Column(name = "Notes")
    public String notes;
    @Column(name = "Priority")
    public int priority;
    @Column(name = "DueDate")
    public String dueDate;

    public TodoItem() {
        super();
    }

    public TodoItem(String title, String notes, int priority, String dueDate) {
        super();
        this.title = title;
        this.notes = notes;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public static String getPriorityInString(int priority) {
        switch (priority) {
            case 0:
                return "LOW";
            case 1:
                return "MEDIUM";
            case 2:
                return "HIGH";
        }
        return "";
    }

}

