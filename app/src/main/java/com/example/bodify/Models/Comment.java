package com.example.bodify.Models;

import java.util.Date;

public class Comment {
    private String comment,reply;
    private Date date;
    private int commentID;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Comment(String comment, String reply, Date date, int commentID) {
        this.comment = comment;
        this.reply = reply;
        this.date = date;
        this.commentID = commentID;
    }

    public Comment() {
    }
}
