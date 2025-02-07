package com.example.d308abazarytask1.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308abazarytask1.DAO.ExcursionDAO;
import com.example.d308abazarytask1.DAO.VacationDAO;
import com.example.d308abazarytask1.Entities.Excursion;
import com.example.d308abazarytask1.Entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class},version = 5, exportSchema = false)
public abstract class TravelDatabaseBuilder extends RoomDatabase {
    public abstract ExcursionDAO excursionDAO();
    public abstract VacationDAO vacationDAO();

    private static volatile TravelDatabaseBuilder INSTANCE;

    static TravelDatabaseBuilder getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (TravelDatabaseBuilder.class){
                if (INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),TravelDatabaseBuilder.class,"MyTravelDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
