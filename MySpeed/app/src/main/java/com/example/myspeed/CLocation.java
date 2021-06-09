package com.example.myspeed;

import android.location.Location;

public class CLocation  extends Location {

    public CLocation(Location location){
        super(location);
    }

    // Μέθοδος που μας επιστρέφει στην ταχύτητα.
    @Override
    public float getSpeed() {
        return super.getSpeed();
    }
}

