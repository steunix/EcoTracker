package com.dev.sr.myecotracker;

/**
 * Created by Stefano on 02/04/2015.
 */
public class Entity {
    public Long   id;
    public String description;
    public Long   usage;

    public Entity() {
        id = new Long(0);
        description = "";
        usage = new Long(0);
    }
}
