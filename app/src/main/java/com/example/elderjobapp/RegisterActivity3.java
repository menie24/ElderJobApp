package com.example.elderjobapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.elderjobapp.models.JobDescription;
import com.example.elderjobapp.models.JobHistory;
import com.example.elderjobapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import lombok.NonNull;

public class RegisterActivity3 extends AppCompatActivity {
    Spinner typeJobSpinner1,typeJobSpinner2,typeJobSpinner3;
    ArrayList<String> jobTypelist;
    ArrayAdapter<String> typeJobAdapter1,typeJobAdapter2,typeJobAdapter3;
    EditText occupationInput1,occupationInput2,occupationInput3,experienceInput1,experienceInput2,experienceInput3;
    TextView errorTypeJobOutput;
    String typeJob1,typeJob2,typeJob3,jobName1,jobName2,jobName3,experience1,experience2,experience3;
    Button confirmButton,back_button;
    JobHistory jobHistory;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        initialize();
        setupUI();

        back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity3.this, RegisterActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        confirmButton = (Button) findViewById(R.id.saveFormButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassValidation()){
                    Intent currentIntent = getIntent();
                    User user = (User) currentIntent.getSerializableExtra("user");
                    jobHistory.add(new JobDescription(typeJob1,jobName1,experience1));
                    jobHistory.add(new JobDescription(typeJob2,jobName2,experience2));
                    jobHistory.add(new JobDescription(typeJob3,jobName3,experience3));
                    user.setJobHistory(jobHistory);
                    db.collection("users")
                            .document(user.getPhoneNumber())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(ContentValues.TAG, "User added successfully!");
                                    startActivity(new Intent(RegisterActivity3.this, WelcomeActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(ContentValues.TAG, "Error adding user", e);
                                }
                            });
                }else{

                }
            }
        });
    }
    private void initialize(){
        typeJob1 = "";
        typeJob2 = "";
        typeJob3 = "";
        jobName1 = "";
        jobName2 = "";
        jobName3 = "";
        experience1 = "";
        experience2 = "";
        experience3 = "";
        jobTypelist = new ArrayList<>();
        jobTypelist.add("");
        jobTypelist.add("งานฝีมือ");
        jobTypelist.add("งานบริหาร/งานเอกสาร");
        jobTypelist.add("งานบริการ");
        jobTypelist.add("งานทั่วไป");
        jobHistory = new JobHistory();
    }
    private void setupUI(){
        occupationInput1 = findViewById(R.id.jobNameInput1);
        occupationInput2 = findViewById(R.id.jobNameInput2);
        occupationInput3 = findViewById(R.id.jobNameInput3);
        experienceInput1 = findViewById(R.id.experienceInput1);
        experienceInput2 = findViewById(R.id.experienceInput2);
        experienceInput3 = findViewById(R.id.experienceInput3);
        errorTypeJobOutput = findViewById(R.id.errorJobTypeHistOutput);
        //Set type job 1
        typeJobSpinner1 = findViewById(R.id.typeJobSpinner1);
        typeJobAdapter1 = new ArrayAdapter<>(this,R.layout.list_item,jobTypelist);
        typeJobSpinner1.setAdapter(typeJobAdapter1);
        typeJobSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeJob1 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Set type job 2
        typeJobSpinner2 = findViewById(R.id.typeJobSpinner2);
        typeJobAdapter2 = new ArrayAdapter<>(this,R.layout.list_item,jobTypelist);
        typeJobSpinner2.setAdapter(typeJobAdapter2);
        typeJobSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeJob2 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Set type job 3
        typeJobSpinner3 = findViewById(R.id.typeJobSpinner3);
        typeJobAdapter3 = new ArrayAdapter<>(this,R.layout.list_item,jobTypelist);
        typeJobSpinner3.setAdapter(typeJobAdapter3);
        typeJobSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeJob3 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private boolean isPassValidation(){
        jobName1 = occupationInput1.getText().toString().trim();
        jobName2 = occupationInput2.getText().toString().trim();
        jobName3 = occupationInput3.getText().toString().trim();
        experience1 = experienceInput1.getText().toString().trim();
        experience2 = experienceInput2.getText().toString().trim();
        experience3 = experienceInput3.getText().toString().trim();

        if(jobName1.isEmpty()){
            occupationInput1.setError("โปรดระบุชื่ออาชีพ");
            return false;
        }
        if(experience1.isEmpty()){
            experienceInput1.setError("โปรดระบุรายละเอียดประสบการณ์");
            return false;
        }
        if(typeJob1.isEmpty()){
            errorTypeJobOutput.setVisibility(View.VISIBLE);
            return false;
        }else{
            errorTypeJobOutput.setVisibility(View.GONE);
        }
        return true;
    }
}