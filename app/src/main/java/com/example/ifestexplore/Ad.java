package com.example.ifestexplore;

public class Ad {
    String comment;
    int serial_no;

    public Ad(String comment, int serial_no) {
        this.comment = comment;
        this.serial_no = serial_no;
    }

    public String getComment() {
        return comment;
    }

    public int getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(int serial_no) {
        this.serial_no = serial_no;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Ad(String comment) {
        this.comment = comment;
    }
}
