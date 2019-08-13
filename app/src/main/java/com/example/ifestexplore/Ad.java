package com.example.ifestexplore;

public class Ad {
    String comment;
    String serial_no;

    public Ad(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public Ad(String comment, String serial_no) {
        this.comment = comment;
        this.serial_no = serial_no;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "comment='" + comment + '\'' +
                ", serial_no='" + serial_no + '\'' +
                '}';
    }
}
