package com.example.d308abazarytask1.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308abazarytask1.Database.Repository;
import com.example.d308abazarytask1.Entities.Excursion;
import com.example.d308abazarytask1.Entities.Vacation;
import com.example.d308abazarytask1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VacationDetails extends AppCompatActivity {

    int vacationId;
    String title;
    String hotel;
    String startDate;
    String endDate;
    EditText editTitle;
    EditText editHotel;
    EditText editStartDate;
    EditText editEndDate;

    Repository repository;
    private RecyclerView recyclerView;


    private ActivityResultLauncher<Intent> excursionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);


        editTitle= findViewById(R.id.vacationtitle);
        editHotel = findViewById(R.id.hotelname);
        editStartDate=findViewById(R.id.startdate);
        editEndDate=findViewById(R.id.enddate);

        title= getIntent().getStringExtra("title");
        hotel=getIntent().getStringExtra("hotel");
        startDate=getIntent().getStringExtra("startdate");
        endDate=getIntent().getStringExtra("enddate");
        vacationId =getIntent().getIntExtra("id",-1);

        editTitle.setText(title);
        editHotel.setText(hotel);
        editStartDate.setText(startDate);
        editEndDate.setText(endDate);

        excursionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        List<Excursion> filteredExcursion = new ArrayList<>();
                        for (Excursion e : repository.getAllExcursions()) {
                            if (e.getVacationID() == vacationId) filteredExcursion.add(e);
                        }
                        ((ExcursionAdapter) recyclerView.getAdapter()).setExcursions(filteredExcursion);
                    }
                }
        );


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView=findViewById(R.id.excursionRecyclerView);
        repository= new Repository(getApplication());
        final ExcursionAdapter excursionAdapter=new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        excursionAdapter.setExcursions(repository.getAllExcursions());

        List<Excursion> filteredExcursion = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()){
            if(e.getVacationID()== vacationId) filteredExcursion.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursion);
        fab.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacID",vacationId);
//                startActivity(intent);
                excursionLauncher.launch(intent);
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.vacationsave){
            Vacation vacation;
            if (vacationId == -1) {
                vacationId = repository.getmAllVacations().get(repository.getmAllVacations().size()-1).getVacationID()+1;
                vacation = new Vacation(vacationId, editTitle.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.insert(vacation);

            } else {
                vacation = new Vacation(vacationId, editTitle.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.update(vacation);
            }
                this.finish();

        return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadVacationDetails() {
        Vacation vacation = repository.getVacationById(vacationId);
        if (vacation != null) {
            editTitle.setText(vacation.getTitle());
            editHotel.setText(vacation.getHotel());
            editStartDate.setText(vacation.getStartDate());
            editEndDate.setText(vacation.getEndDate());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadVacationDetails(); // Reload vacation details (optional if they may change)
            List<Excursion> filteredExcursion = new ArrayList<>();
            for (Excursion e : repository.getAllExcursions()) {
                if (e.getVacationID() == vacationId) filteredExcursion.add(e);
            }
            ((ExcursionAdapter) recyclerView.getAdapter()).setExcursions(filteredExcursion);
        }
    }


}