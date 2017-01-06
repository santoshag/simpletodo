package com.santoshag.hoopla.models;

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
    @Column(name = "IsLocationSet")
    public Boolean isLocationSet;
    @Column(name = "PlaceName")
    public String placeName;
    @Column(name = "PlaceAddress")
    public String placeAddress;
    @Column(name = "Latitude")
    public Double latitude;
    @Column(name = "Longitude")
    public Double longitude;



    public TodoItem() {
        super();
    }

    public TodoItem(String title, String notes, int priority, String dueDate, Boolean isLocationSet,String placeName, String placeAddress, Double latitude, Double longitude) {
        super();
        this.title = title;
        this.notes = notes;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isLocationSet = isLocationSet;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return "TodoItem{" +
                "title='" + title + '\'' +
                ", notes='" + notes + '\'' +
                ", priority=" + priority +
                ", dueDate='" + dueDate + '\'' +
                ", isLocationSet=" + isLocationSet +
                ", placeName='" + placeName + '\'' +
                ", placeAddress='" + placeAddress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

