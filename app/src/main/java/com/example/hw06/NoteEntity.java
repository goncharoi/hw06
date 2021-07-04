package com.example.hw06;

import java.util.Date;

public class NoteEntity {
    private String id;
    private String title;
    private String text;
    private Date deadline;
    private Date createdOn;

    public NoteEntity() {

    }

    public NoteEntity(String id, String title, String text, Date deadline) {
        this.id = id;
        this.deadline = deadline;
        createdOn = new Date();//current date\time
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return title + "/" + createdOn.toString();
    }

    public String getId() {
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

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

}
