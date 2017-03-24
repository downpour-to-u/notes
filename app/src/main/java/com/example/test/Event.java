package com.example.test;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Kayla on 2017/3/22.
 */

public class Event extends DataSupport {

    @Column(unique = true, nullable = false)
    private String name;

    public Event(String _name){
        name = _name;
    }
    public Event(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
