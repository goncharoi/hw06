package com.example.hw06;

import java.util.Date;

public class NoteEntity {
    private final long id;
    private String title;
    private String text;
    private final Date createdOn;

    public NoteEntity(long id, String title, String text) {
        this.id = id;
        createdOn = new Date();//current date\time
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return title + "/" + createdOn.toString();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}
