package com.example.test;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Kayla on 2017/3/17.
 */

public class Note extends DataSupport{

    private String event;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, unique = true)
    private String time;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
