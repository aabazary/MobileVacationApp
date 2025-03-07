package com.example.d308abazarytask1.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308abazarytask1.Database.Repository;
import com.example.d308abazarytask1.Entities.Excursion;
import com.example.d308abazarytask1.Entities.Vacation;
import com.example.d308abazarytask1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    String title;
    int excursionID;
    int vacationID;
    EditText editTitle;
    TextView editDate;
    Repository repository;
    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        repository = new Repository(getApplication());
        title = getIntent().getStringExtra("title");
        editTitle = findViewById(R.id.excursionTitle);
        editTitle.setText(title);
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacID", -1);
        editDate = findViewById(R.id.date);
        LinearLayout dateContainer = findViewById(R.id.dateContainer);

        String initialDate = getIntent().getStringExtra("date");

        if (initialDate != null && !initialDate.isEmpty()) {
            try {
                myCalendarStart.setTime(sdf.parse(initialDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        editDate.setText(sdf.format(myCalendarStart.getTime()));

        ArrayList<Vacation> vacationArrayList = new ArrayList<>();
        vacationArrayList.addAll(repository.getmAllVacations());
        ArrayList<Integer> vacationIdList = new ArrayList<>();
        for (Vacation vacation : vacationArrayList) {
            vacationIdList.add(vacation.getVacationID());
        }

        startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                updateLabelStart();
            }

        };

        dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        ExcursionDetails.this,
                        startDate,
                        myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });


    }

    private void updateLabelStart() {
        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.excursionsave) {
            saveExcursion();
            return true;
        }

        if (item.getItemId() == R.id.excursiondelete) {
            deleteExcursion();
            return true;
        }

        if (item.getItemId() == R.id.setexcursionalert) {
            setExcursionAlert();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setExcursionAlert() {
        String dateFromScreen = editDate.getText().toString();
        Date myDate = null;
        try {
            myDate = sdf.parse(dateFromScreen);
            long trigger = myDate.getTime();
            if (trigger <= System.currentTimeMillis()) {
                trigger += 1000 * 60;
            }

            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            intent.putExtra("key", "Reminder: " + editTitle.getText().toString() + " is scheduled for today.");
            PendingIntent sender = PendingIntent.getBroadcast(
                    ExcursionDetails.this,
                    ++MainActivity.numAlert,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, sender);
                    Toast.makeText(this, "Alarm set for: " + sdf.format(myDate), Toast.LENGTH_SHORT).show();
                } else {
                    Intent permissionIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(permissionIntent);
                    Toast.makeText(this, "Please grant exact alarm permission", Toast.LENGTH_LONG).show();
                }
            } else {
                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, sender);
                    Toast.makeText(this, "Alarm set for: " + sdf.format(myDate), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteExcursion() {
        new Thread(() -> {
            repository.deleteExcursionById(excursionID);
            runOnUiThread(() -> {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(this, "Excursion Deleted", Toast.LENGTH_SHORT).show();
                this.finish();
            });
        }).start();
    }

    private void saveExcursion() {

        if (!isDateWithinVacationRange()) {
            Toast.makeText(this, "Excursion date must be within the vacation period.", Toast.LENGTH_LONG).show();
            return;
        }

        Excursion excursion;
        if (excursionID == -1) {
            if (repository.getAllExcursions().isEmpty())
                excursionID = 1;
            else
                excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;

            excursion = new Excursion(excursionID, editTitle.getText().toString(), editDate.getText().toString(), vacationID);
            repository.insert(excursion);
            Toast.makeText(this, "Created new Excursion!", Toast.LENGTH_SHORT).show();
        } else {
            excursion = new Excursion(excursionID, editTitle.getText().toString(), editDate.getText().toString(), vacationID);
            repository.update(excursion);
            Toast.makeText(this, "Updated Excursion information.", Toast.LENGTH_SHORT).show();
        }

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        this.finish();
    }

    private boolean isDateWithinVacationRange() {
        String excursionDateStr = editDate.getText().toString();
        try {
            Date excursionDate = sdf.parse(excursionDateStr);

            Vacation vacation = repository.getVacationById(vacationID);
            if (vacation != null) {
                Date vacationStartDate = sdf.parse(vacation.getStartDate());
                Date vacationEndDate = sdf.parse(vacation.getEndDate());

                return excursionDate != null && excursionDate.compareTo(vacationStartDate) >= 0 && excursionDate.compareTo(vacationEndDate) <= 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}