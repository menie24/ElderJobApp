package com.example.elderjobapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.models.Address;
import com.example.elderjobapp.models.Education;
import com.example.elderjobapp.models.User;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class RegisterActivity2 extends AppCompatActivity {
    Spinner proviceSpinner,districtSpinner,subdistrictSpinner,levelEducationSpinner;
    ArrayList<String> provicelist = new ArrayList<>();
    ArrayList<String> districtlist = new ArrayList<>();
    ArrayList<String> subdistrictlist = new ArrayList<>();
    ArrayList<String> levelEducationlist = new ArrayList<>();
    ArrayAdapter<String> proviceAdapter,districtAdapter,subdistrictAdapter,levelEducationAdapter;
    EditText firstnameInput,lastnameInput,emailInput,dobInput,addressDetailInput,institutionNameInput;
    TextView errorProvinceOutput,errorDistrictOutput,errorSubDistrictOutput,schoolNameTextView;
    RadioGroup genderInput;
    RadioButton maleRadioButton,femaleRadioButton;
    Button continueButton,back_button;
    String firstname,lastname,gender,email,province,district,subdistrict,addressDetail,userType,levelEducation,institutionName;
    int zipCode;
    Date dob;
    User user;
    Address address;
    Education education;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initialize();
        setupUI();
        back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity2.this, RegisterActivity1.class);
                startActivity(intent);
                finish();
            }
        });

        continueButton = (Button) findViewById(R.id.loginButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassValidation()){
                    Intent currentIntent = getIntent();
                    userType = currentIntent.getStringExtra("user type");
                    String phone = currentIntent.getStringExtra("phone");
                    String password = currentIntent.getStringExtra("password");
                    address = new Address(addressDetail,subdistrict,district,province,zipCode);
                    education = new Education(levelEducation,institutionName);
                    user = new User(phone,password,firstname,lastname,gender,dob,email,address,userType);
                    user.setEducation(education);
                    Intent intent = new Intent(RegisterActivity2.this, RegisterActivity3.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }else{

                }
            }
        });
    }

    private void initialize(){
        firstname = "";
        lastname = "";
        gender = "ชาย";
        dob = null;
        email = "";
        province = "";
        district = "";
        subdistrict = "";
        zipCode = 0;
        addressDetail = "";
        levelEducation = "ไม่มี";
        institutionName = "";
    }
    private void setupUI(){
        firstnameInput = findViewById(R.id.firstnameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        genderInput = findViewById(R.id.radioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        maleRadioButton.setChecked(true);
        dobInput = findViewById(R.id.dobInput);
        dobInput.setText("DD/MM/YYYY");
        emailInput = findViewById(R.id.emailInput);
        proviceSpinner = findViewById(R.id.provinceSpinner);
        proviceAdapter = new ArrayAdapter<>(this,R.layout.list_item);
        districtSpinner = findViewById(R.id.districtSpinner);
        districtAdapter = new ArrayAdapter<>(this,R.layout.list_item);
        subdistrictSpinner = findViewById(R.id.subdistrictSpinner);
        subdistrictAdapter = new ArrayAdapter<>(this,R.layout.list_item);
        addressDetailInput = findViewById(R.id.addressDetailInput);
        schoolNameTextView = findViewById(R.id.schoolNameTextView);
        institutionNameInput = findViewById(R.id.institutionNameInput);
        errorProvinceOutput = findViewById(R.id.errorJobTypeHistOutput);
        errorDistrictOutput = findViewById(R.id.errorDistrictOutput);
        errorSubDistrictOutput = findViewById(R.id.errorSubDistrictOutput);

        //Set spinner (province,district,sub-district)
        try {
            InputStream inputStream = getAssets().open("Provinces.csv");
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            CSVReader reader = new CSVReader(fileReader);
            String[] line;
            while ((line = reader.readNext()) != null) {
                provicelist.add(line[0]);
            }
            Collections.sort(provicelist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        proviceAdapter.add("");
        proviceAdapter.addAll(provicelist);
        proviceSpinner.setAdapter(proviceAdapter);
        proviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = (String) parent.getItemAtPosition(position);
                districtlist.clear();
                districtAdapter.clear();
                subdistrictlist.clear();
                subdistrictAdapter.clear();
                try {
                    InputStream inputStream = getAssets().open("ProvincesThailand.csv");
                    BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    CSVReader reader = new CSVReader(fileReader);
                    String[] line;
                    while ((line = reader.readNext()) != null) {
                        if(line[0].equals(province) && !districtlist.contains(line[1])){
                            districtlist.add(line[1]);
                        }
                    }
                    Collections.sort(districtlist);
                    districtAdapter.add("");
                    districtAdapter.addAll(districtlist);
                    districtSpinner.setAdapter(districtAdapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        district = (String) parent.getItemAtPosition(position);
                        subdistrictlist.clear();
                        subdistrictAdapter.clear();
                        try {
                            InputStream inputStream = getAssets().open("ProvincesThailand.csv");
                            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                            CSVReader reader = new CSVReader(fileReader);
                            String[] line;
                            while ((line = reader.readNext()) != null) {
                                if(line[0].equals(province) && line[1].equals(district) && !subdistrictlist.contains(line[2])){
                                    subdistrictlist.add(line[2]);
                                }
                            }
                            Collections.sort(subdistrictlist);
                            subdistrictAdapter.add("");
                            subdistrictAdapter.addAll(subdistrictlist);
                            subdistrictSpinner.setAdapter(subdistrictAdapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        subdistrictSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subdistrict = (String) parent.getItemAtPosition(position);
                                try {
                                    InputStream inputStream = getAssets().open("ProvincesThailand.csv");
                                    BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                                    CSVReader reader = new CSVReader(fileReader);
                                    String[] line;
                                    while ((line = reader.readNext()) != null) {
                                        if(line[0].equals(province) && line[1].equals(district) && line[2].equals(subdistrict)){
                                            zipCode = Integer.parseInt(line[3]);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Set radio button (gender)
        maleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleRadioButton.isChecked()) {
                    femaleRadioButton.setChecked(false);
                    gender = maleRadioButton.getText().toString();
                }
            }
        });
        femaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (femaleRadioButton.isChecked()) {
                    maleRadioButton.setChecked(false);
                    gender = femaleRadioButton.getText().toString();
                }
            }
        });

        //Set edit text (date of birth)
        dobInput.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");
                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;
                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int yearBuddhist = Integer.parseInt(clean.substring(4,8));
                        int year = yearBuddhist-543;
                        if(mon > 12) mon = 12;
                        if(mon < 1) mon = 1;
                        int currentYear = LocalDate.now().getYear();
                        year = (year<1857)?1857:(year>currentYear)?currentYear:year;
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, mon-1);
                        if(day > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if(day < 1) day = 1;
                        yearBuddhist = year+543;
                        clean = String.format("%02d%02d%02d",day, mon, yearBuddhist);
                        dob = DateHelper.getDate(day,mon,year);
                    }
                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));
                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dobInput.setText(current);
                    dobInput.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set education
        levelEducationSpinner = findViewById(R.id.levelEducationSpinner);
        levelEducationlist.add("ไม่มี");
        levelEducationlist.add("ประถมศึกษา");
        levelEducationlist.add("มัธยมต้น");
        levelEducationlist.add("มัธยมปลาย/ปวช.");
        levelEducationlist.add("อนุปริญญา/ปวส.");
        levelEducationlist.add("ปริญญาตรี");
        levelEducationlist.add("ปริญญาโท");
        levelEducationlist.add("ปริญญาเอก");
        levelEducationAdapter = new ArrayAdapter<>(this,R.layout.list_item,levelEducationlist);
        levelEducationSpinner.setAdapter(levelEducationAdapter);
        levelEducationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                levelEducation = (String) parent.getItemAtPosition(position);
                if(levelEducation.equals("ไม่มี")){
                    schoolNameTextView.setVisibility(View.GONE);
                    institutionNameInput.setVisibility(View.GONE);
                }else{
                    schoolNameTextView.setVisibility(View.VISIBLE);
                    institutionNameInput.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean isPassValidation(){
        firstname = firstnameInput.getText().toString().trim();
        lastname = lastnameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        addressDetail = addressDetailInput.getText().toString().trim();
        institutionName = institutionNameInput.getText().toString().trim();
        if(firstname.isEmpty()){
            firstnameInput.setError("โปรดระบุชื่อ");
            return false;
        }
        if(lastname.isEmpty()){
            lastnameInput.setError("โปรดระบุนามสกุล");
            return false;
        }
        if(dob==null){
            dobInput.setError("โปรดระบุวันเกิด");
            return false;
        }
        if(email.isEmpty()){
            emailInput.setError("โปรดระบุอีเมล");
            return false;
        }
        if(province.isEmpty()){
            errorProvinceOutput.setVisibility(View.VISIBLE);
            return false;
        }else{
            errorProvinceOutput.setVisibility(View.GONE);
        }
        if(district.isEmpty()){
            errorDistrictOutput.setVisibility(View.VISIBLE);
            return false;
        }else{
            errorDistrictOutput.setVisibility(View.GONE);
        }
        if(subdistrict.isEmpty()){
            errorSubDistrictOutput.setVisibility(View.VISIBLE);
            return false;
        }else{
            errorSubDistrictOutput.setVisibility(View.GONE);
        }
        if(addressDetail.isEmpty()){
            addressDetailInput.setError("โปรดระบุรายละเอียดที่อยู่");
            return false;
        }
        if(!levelEducation.equals("ไม่มี") && institutionName.isEmpty()){
            institutionNameInput.setError("โปรดระบุชื่อสถานศึกษา");
            return false;
        }
        return true;
    }
}