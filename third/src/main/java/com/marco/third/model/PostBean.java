package com.marco.third.model;

import java.util.concurrent.atomic.AtomicInteger;

public class PostBean {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int id;

    private String title;

    private String message;

    public PostBean() {
        this.id = COUNTER.getAndIncrement();
    }

    public PostBean(String title, String message) {
        this.id = COUNTER.getAndIncrement();;
        this.title = title;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
