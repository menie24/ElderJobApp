package com.example.elderjobapp.fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.FileHelper;
import com.example.elderjobapp.models.Address;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.NonNull;

public class JobFormFragment extends Fragment {
    Spinner provinceFormSpinner,districtFormSpinner,subdistrictFormSpinner,jobTypeFormSpinner;
    ArrayList<String> provicelist = new ArrayList<>();
    ArrayList<String> districtlist = new ArrayList<>();
    ArrayList<String> subdistrictlist = new ArrayList<>();
    ArrayList<String> jobTypelist = new ArrayList<>();
    ArrayAdapter<String> provinceAdapter,districtAdapter,subdistrictAdapter,jobTypeAdapter;
    Button saveFormButton;
    EditText institutionNameFormInput,addressDetailFormInput,jobNameFormInput,salaryFormInput,requiementFormInput,benefitFormInput
            ,jobDetailFormInput,contactNameFormInput,phoneFormInput,emailFormInput,expireDateFormInput;
    TextView errorProvinceOutput,errorDistrictOutput,errorSubDistrictOutput,errorJobTypeOutput;
    RadioButton limitedRadioButton,unlimitedRadioButton;
    String institutionName,province,district,subdistrict,addressDetail,jobName,requiement,benefit,jobDetail,contactName,phone,email,type,jobType;
    int zipCode,salary;
    Date expire;
    Context context;

    public JobFormFragment() {
        // Required empty public constructor
    }
    public static JobFormFragment newInstance() {
        JobFormFragment fragment = new JobFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_form, container, false);
        initialize();
        setupUI(rootView);
        //save form button
        saveFormButton = rootView.findViewById(R.id.saveFormButton);
        saveFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassValidation()){
                    Calendar calendar = Calendar.getInstance();
                    Date today = calendar.getTime();
                    Address institutionAddress = new Address(addressDetail,subdistrict,district,province,zipCode);
                    Job job = new Job(institutionName,institutionAddress,jobName,jobType,salary,requiement,benefit,jobDetail,FirebaseUtil.getCurrentUserID(),contactName,phone,email,type,expire,today);
                    Log.d(ContentValues.TAG, job.toString());
                    Log.d(ContentValues.TAG, institutionAddress.toString());
                    //create job object
                    FirebaseUtil.allJobCollectionReference()
                            .add(job)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(ContentValues.TAG, "Job added successfully!");
                                    JobListFragment jobListFragment = JobListFragment.newInstance();
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobListFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
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
        return rootView;
    }
    private void initialize(){
        institutionName = "";
        province = "";
        district = "";
        subdistrict = "";
        zipCode = 0;
        addressDetail = "";
        jobName = "";
        jobType = "";
        salary = 0;
        requiement = "";
        benefit = "";
        jobDetail = "";
        contactName = "";
        phone = "";
        email = "";
        expire = null;
        context = requireContext();
    }
    private void setupUI(View rootView){
        institutionNameFormInput = rootView.findViewById(R.id.institutionNameFormInput);
        jobNameFormInput = rootView.findViewById(R.id.jobNameFormInput);
        salaryFormInput = rootView.findViewById(R.id.salaryFormInput);
        requiementFormInput = rootView.findViewById(R.id.requiementFormInput);
        benefitFormInput = rootView.findViewById(R.id.benefitFormInput);
        jobDetailFormInput = rootView.findViewById(R.id.jobDetailFormInput);
        contactNameFormInput = rootView.findViewById(R.id.contactNameFormInput);
        phoneFormInput = rootView.findViewById(R.id.phoneFormInput);
        emailFormInput = rootView.findViewById(R.id.emailFormInput);
        expireDateFormInput = rootView.findViewById(R.id.expireDateFormInput);
        errorProvinceOutput = rootView.findViewById(R.id.errorJobTypeHistOutput);
        errorDistrictOutput = rootView.findViewById(R.id.errorDistrictOutput);
        errorSubDistrictOutput = rootView.findViewById(R.id.errorSubDistrictOutput);
        errorJobTypeOutput = rootView.findViewById(R.id.errorJobTypeOutput);
        limitedRadioButton = rootView.findViewById(R.id.maleRadioButton);
        unlimitedRadioButton = rootView.findViewById(R.id.femaleRadioButton);
        provinceFormSpinner = rootView.findViewById(R.id.provinceFormSpinner);
        provinceAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        districtFormSpinner = rootView.findViewById(R.id.districtFormSpinner);
        districtAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        subdistrictFormSpinner = rootView.findViewById(R.id.subdistrictFormSpinner);
        subdistrictAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        addressDetailFormInput = rootView.findViewById(R.id.addressDetailFormInput);
        jobTypeFormSpinner = rootView.findViewById(R.id.jobTypeFormSpinner);
        jobTypeAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);

        jobTypelist.add("งานบริการ");
        jobTypelist.add("งานฝีมือ");
        jobTypelist.add("งานบริหาร/งานเอกสาร");
        jobTypelist.add("งานทั่วไป");
        jobTypeAdapter.add("");
        jobTypeAdapter.addAll(jobTypelist);
        jobTypeFormSpinner.setAdapter(jobTypeAdapter);
        jobTypeFormSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jobType = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //Set spinner (province,district,sub-district)
        try {
            provicelist = FileHelper.readProvince(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        provinceAdapter.add("");
        provinceAdapter.addAll(provicelist);
        provinceFormSpinner.setAdapter(provinceAdapter);
        provinceFormSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = (String) parent.getItemAtPosition(position);
                districtAdapter.clear();
                districtlist.clear();
                subdistrictAdapter.clear();
                subdistrictlist.clear();
                try {
                    districtlist = FileHelper.readDistrict(context,province);
                    districtAdapter.add("");
                    districtAdapter.addAll(districtlist);
                    districtFormSpinner.setAdapter(districtAdapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                districtFormSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        district = (String) parent.getItemAtPosition(position);
                        subdistrictAdapter.clear();
                        subdistrictlist.clear();
                        try {
                            subdistrictlist = FileHelper.readSubDistrict(context,province,district);
                            subdistrictAdapter.add("");
                            subdistrictAdapter.addAll(subdistrictlist);
                            subdistrictFormSpinner.setAdapter(subdistrictAdapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        subdistrictFormSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subdistrict = (String) parent.getItemAtPosition(position);
                                try {
                                    zipCode = FileHelper.readZipCode(context,province,district,subdistrict);
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

        //Set radio group (gender)
        limitedRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (limitedRadioButton.isChecked()) {
                    unlimitedRadioButton.setChecked(false);
                    type = limitedRadioButton.getText().toString();
                }
            }
        });
        unlimitedRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unlimitedRadioButton.isChecked()) {
                    limitedRadioButton.setChecked(false);
                    type = unlimitedRadioButton.getText().toString();
                }
            }
        });
        limitedRadioButton.setChecked(true);
        type = limitedRadioButton.getText().toString();

        //Set expire date
        expireDateFormInput.setOnClickListener(v -> openDatePicker());
        expireDateFormInput.setFocusable(false);
        expireDateFormInput.setClickable(true);
    }
    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
                    String selectedDate = sdf.format(calendar.getTime());
                    expireDateFormInput.setText(selectedDate);
                    expire = calendar.getTime();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean isPassValidation(){
        institutionName = institutionNameFormInput.getText().toString().trim();
        addressDetail = addressDetailFormInput.getText().toString();
        jobName = jobNameFormInput.getText().toString().trim();
        salary = Integer.parseInt(salaryFormInput.getText().toString().trim());
        requiement = requiementFormInput.getText().toString();
        benefit = benefitFormInput.getText().toString();
        jobDetail = jobDetailFormInput.getText().toString();
        contactName = contactNameFormInput.getText().toString().trim();
        phone = phoneFormInput.getText().toString().trim();
        email = emailFormInput.getText().toString().trim();

        if(institutionName.isEmpty()){
            institutionNameFormInput.setError("โปรดระบุชื่อสถานประกอบการ");
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
            addressDetailFormInput.setError("โปรดระบุรายละเอียดที่อยู่");
            return false;
        }
        if(jobType.isEmpty()){
            errorJobTypeOutput.setVisibility(View.VISIBLE);
            return false;
        }else{
            errorJobTypeOutput.setVisibility(View.GONE);
        }
        if(jobName.isEmpty()){
            jobNameFormInput.setError("โปรดระบุตำแหน่งงาน");
            return false;
        }
        if(salary==0){
            salaryFormInput.setError("โปรดระบุเงินเดือน");
            return false;
        }
        if(requiement.isEmpty()){
            requiementFormInput.setError("โปรดระบุคุณสมบัติผู้สมัคร");
            return false;
        }
        if(benefit.isEmpty()){
            benefitFormInput.setError("โปรดระบุสวัสดิการ");
            return false;
        }
        if(jobDetail.isEmpty()){
            jobDetailFormInput.setError("โปรดระบุรายละเอียดงาน");
            return false;
        }
        if(contactName.isEmpty()){
            contactNameFormInput.setError("โปรดระบุชื่อผู้ติดต่อ");
            return false;
        }
        if(phone.isEmpty()){
            phoneFormInput.setError("โปรดระบุเบอร์โทร");
            return false;
        }
        if(email.isEmpty()){
            emailFormInput.setError("โปรดระบุอีเมล");
            return false;
        }
        if(expire==null){
            expireDateFormInput.setError("โปรดระบุวันที่สิ้นสุดประกาศ");
            return false;
        }

        return true;
    }
}