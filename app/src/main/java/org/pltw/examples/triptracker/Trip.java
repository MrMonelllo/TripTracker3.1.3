package org.pltw.examples.triptracker;

import android.support.annotation.NonNull;

import com.backendless.geo.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shunt on 11/18/2017.
 */

public class Trip implements IntentData, Comparable{
    private String objectId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private boolean shared;
    private ArrayList<GeoPoint> locations;

    private String ownerId;


    public Trip(){
        locations = new ArrayList<GeoPoint>();
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public ArrayList<GeoPoint> getLocations() {
        return locations;
    }
    public void setLocations(ArrayList<GeoPoint> locations) {
        this.locations = locations;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Trip temp = (Trip)o;
        return name.compareTo(temp.getName());

    }
}
