package com.example.d308abazarytask1.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacations")
public class Vacation {
    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    @PrimaryKey(autoGenerate = true)
    private int vacationID;
    private String title;
    private String hotel;
    private String startDate;
    private String endDate;


    public Vacation(int vacationID, String title, String hotel, String startDate, String endDate) {
        this.vacationID = vacationID;
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return vacationID;
    }

    public void setId(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


}
