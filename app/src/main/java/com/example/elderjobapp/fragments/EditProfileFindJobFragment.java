package com.example.elderjobapp.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.helper.FileHelper;
import com.example.elderjobapp.helper.ImageHelper;
import com.example.elderjobapp.models.Address;
import com.example.elderjobapp.models.Education;
import com.example.elderjobapp.models.JobDescription;
import com.example.elderjobapp.models.JobHistory;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.AndroidUtil;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfileFindJobFragment extends Fragment {
    ImageView profileImageOutput;
    Spinner provinceSpinner,districtSpinner,subdistrictSpinner,levelEducationSpinner,typeJobSpinner1,typeJobSpinner2,typeJobSpinner3;
    ArrayList<String> provincelist = new ArrayList<>();
    ArrayList<String> districtlist = new ArrayList<>();
    ArrayList<String> subdistrictlist = new ArrayList<>();
    ArrayList<String> levelEducationlist = new ArrayList<>();
    ArrayAdapter<String> provinceAdapter,districtAdapter,subdistrictAdapter,levelEducationAdapter,typeJobAdapter1,typeJobAdapter2,typeJobAdapter3;
    Button cancelButton,saveButton,selectImageButton;
    EditText firstnameInput,lastnameInput,emailInput,dobInput,addressDetailInput,institutionNameEducationInput,jobNameInput1,jobNameInput2,jobNameInput3,experienceInput1,experienceInput2,experienceInput3;
    TextView errorJobTypeHistOutput,educationNameTextView;
    RadioGroup genderInput;
    RadioButton maleRadioButton,femaleRadioButton;
    String firstname,lastname,gender,email,province,district,subdistrict,addressDetail,levelEducation,institutionName
            ,typeJob1,typeJob2,typeJob3,jobName1,jobName2,jobName3,experience1,experience2,experience3;
    int zipCode;
    Date dob;
    User user;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> imagePickLauncher;

    public EditProfileFindJobFragment() {
        // Required empty public constructor
    }

    public static EditProfileFindJobFragment newInstance(User user) {
        EditProfileFindJobFragment fragment = new EditProfileFindJobFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            ImageHelper.setProfilePic(getContext(),selectedImageUri,profileImageOutput);
                        }
                    }
                }
        );
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_profile_find_job, container, false);
        setupUI(rootView);

        selectImageButton = rootView.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfileFindJobFragment.this).cropSquare().compress(512).maxResultSize(512,512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });


        saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassValidation()){
                    if(selectedImageUri!=null) {
                        FirebaseUtil.getCurrentProfileImageStorageRef().putFile(selectedImageUri)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Log.d(ContentValues.TAG,"Updated profile image successfully");
                                    }else{
                                        Log.d(ContentValues.TAG,"Updated profile image failed");
                                    }
                                });
                    }
                    user.setFirstName(firstname);
                    user.setLastName(lastname);
                    user.setGender(gender);
                    user.setBirthDate(dob);
                    user.setEmail(email);
                    user.setCurrentAddress(new Address(addressDetail,subdistrict,district,province,zipCode));
                    user.setEducation(new Education(levelEducation,institutionName));
                    JobHistory jobHistory = new JobHistory();
                    jobHistory.add(new JobDescription(typeJob1,jobName1,experience1));
                    jobHistory.add(new JobDescription(typeJob2,jobName2,experience2));
                    jobHistory.add(new JobDescription(typeJob3,jobName3,experience3));
                    user.setJobHistory(jobHistory);
                    //user.setJobHistory(n);
                    // job history
                    //----------
                    FirebaseUtil.currentUserDetails().set(user)
                            .addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()){
                                    Log.d(ContentValues.TAG,"Updated user data successfully");
                                    // update user data
                                    ProfileFindJobFragment profileFindJobFragment = ProfileFindJobFragment.newInstance();
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, profileFindJobFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }else{
                                    Log.d(ContentValues.TAG,"Updated user data failed");
                                }
                            });
                }
            }
        });
        cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });

        return rootView;
    }
    private void setupUI(View rootView){
        profileImageOutput = rootView.findViewById(R.id.image);
        firstnameInput = rootView.findViewById(R.id.firstnameInput);
        lastnameInput = rootView.findViewById(R.id.lastnameInput);
        genderInput = rootView.findViewById(R.id.radioGroup);
        maleRadioButton = rootView.findViewById(R.id.maleRadioButton);
        femaleRadioButton = rootView.findViewById(R.id.femaleRadioButton);
        dobInput = rootView.findViewById(R.id.dobInput);
        emailInput = rootView.findViewById(R.id.emailInput);
        provinceSpinner = rootView.findViewById(R.id.provinceSpinner);
        provinceAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        districtSpinner = rootView.findViewById(R.id.districtSpinner);
        districtAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        subdistrictSpinner = rootView.findViewById(R.id.subdistrictSpinner);
        subdistrictAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        addressDetailInput = rootView.findViewById(R.id.addressDetailInput);
        levelEducationSpinner = rootView.findViewById(R.id.levelEducationSpinner);
        educationNameTextView = rootView.findViewById(R.id.educationNameTextView);
        institutionNameEducationInput = rootView.findViewById(R.id.institutionNameEducationInput);
        typeJobSpinner1 = rootView.findViewById(R.id.typeJobSpinner1);
        typeJobAdapter1 = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        typeJobSpinner2 = rootView.findViewById(R.id.typeJobSpinner2);
        typeJobAdapter2 = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        typeJobSpinner3 = rootView.findViewById(R.id.typeJobSpinner3);
        typeJobAdapter3 = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        jobNameInput1 = rootView.findViewById(R.id.jobNameInput1);
        jobNameInput2 = rootView.findViewById(R.id.jobNameInput2);
        jobNameInput3 = rootView.findViewById(R.id.jobNameInput3);
        experienceInput1 = rootView.findViewById(R.id.experienceInput1);
        experienceInput2 = rootView.findViewById(R.id.experienceInput2);
        experienceInput3 = rootView.findViewById(R.id.experienceInput3);
        errorJobTypeHistOutput = rootView.findViewById(R.id.errorJobTypeHistOutput);
        Context context = requireContext();
        FirebaseUtil.getCurrentProfileImageStorageRef().getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(ContentValues.TAG, "Load profile image successfully");
            AndroidUtil.setProfileImage(requireContext(), uri, profileImageOutput);
        }).addOnFailureListener(exception -> {
            Log.w("ProfileImage", "Error getting image.", exception);
            Log.d(ContentValues.TAG, "Failed to load profile image, loading default");
            FirebaseUtil.getDefaultProfileImageStorageRef().getDownloadUrl().addOnSuccessListener(defaultUri -> {
                Log.d(ContentValues.TAG, "Load default profile image successfully");
                AndroidUtil.setProfileImage(requireContext(), defaultUri, profileImageOutput);
            }).addOnFailureListener(defaultException -> {
                Log.d(ContentValues.TAG, "Failed to load default profile image");
            });
        });
        firstnameInput.setText(user.getFirstName());
        lastnameInput.setText(user.getLastName());
        emailInput.setText(user.getEmail());
        addressDetailInput.setText(user.getCurrentAddress().getAddressDetail());
        jobNameInput1.setText(user.getJobHistory().getJobDescriptions().get(0).getJobName());
        experienceInput1.setText(user.getJobHistory().getJobDescriptions().get(0).getExperience());
        jobNameInput2.setText(user.getJobHistory().getJobDescriptions().get(1).getJobName());
        experienceInput2.setText(user.getJobHistory().getJobDescriptions().get(1).getExperience());
        jobNameInput3.setText(user.getJobHistory().getJobDescriptions().get(2).getJobName());
        experienceInput3.setText(user.getJobHistory().getJobDescriptions().get(2).getExperience());

        levelEducationlist.add(user.getEducation().getLevel());
        levelEducationlist.add("ไม่มี");
        levelEducationlist.add("ประถมศึกษา");
        levelEducationlist.add("มัธยมต้น");
        levelEducationlist.add("มัธยมปลาย/ปวช.");
        levelEducationlist.add("อนุปริญญา/ปวส.");
        levelEducationlist.add("ปริญญาตรี");
        levelEducationlist.add("ปริญญาโท");
        levelEducationlist.add("ปริญญาเอก");
        levelEducationAdapter = new ArrayAdapter<>(context,R.layout.list_item,levelEducationlist);
        institutionNameEducationInput.setText(user.getEducation().getInstitutionName());

        //Set spinner (province,district,sub-district)
        try {
            provincelist = FileHelper.readProvince(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        provinceAdapter.add(user.getCurrentAddress().getProvince());
        provinceAdapter.addAll(provincelist);
        provinceSpinner.setAdapter(provinceAdapter);
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = (String) parent.getItemAtPosition(position);
                districtAdapter.clear();
                districtlist.clear();
                subdistrictAdapter.clear();
                subdistrictlist.clear();
                try {
                    districtlist = FileHelper.readDistrict(context,province);
                    districtAdapter.add(user.getCurrentAddress().getDistrict());
                    districtAdapter.addAll(districtlist);
                    districtSpinner.setAdapter(districtAdapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        district = (String) parent.getItemAtPosition(position);
                        subdistrictAdapter.clear();
                        subdistrictlist.clear();
                        try {
                            subdistrictlist = FileHelper.readSubDistrict(context,province,district);
                            subdistrictAdapter.add(user.getCurrentAddress().getSubDistrict());
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
        provinceSpinner.setSelection(0);
        districtSpinner.setSelection(0);
        subdistrictSpinner.setSelection(0);
        //Set radio group (gender)
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
        if(user.getGender().equals("ชาย")){
            maleRadioButton.setChecked(true);
        }else{
            femaleRadioButton.setChecked(true);
        }
        gender = user.getGender();
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
        dobInput.setText(DateHelper.convertDateToStringFormat(user.getBirthDate()));

        //Set education
        levelEducationSpinner.setAdapter(levelEducationAdapter);
        levelEducationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                levelEducation = (String) parent.getItemAtPosition(position);
                if(levelEducation.equals("ไม่มี")){
                    educationNameTextView.setVisibility(View.GONE);
                    institutionNameEducationInput.setVisibility(View.GONE);
                }else{
                    educationNameTextView.setVisibility(View.VISIBLE);
                    institutionNameEducationInput.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        levelEducationSpinner.setSelection(0);

        List<String> jobTypelist = new ArrayList<>();
        jobTypelist.add("งานฝีมือ");
        jobTypelist.add("งานบริหาร/งานเอกสาร");
        jobTypelist.add("งานบริการ");
        jobTypelist.add("งานทั่วไป");
        //Set type job 1
        typeJobAdapter1.addAll(jobTypelist);
        typeJobSpinner1.setAdapter(typeJobAdapter1);
        typeJobSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeJob1 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        for(int i=0;i<jobTypelist.size();i++){
            if(jobTypelist.get(i).equals(user.getJobHistory().getJobDescriptions().get(0).getJobType())){
                typeJobSpinner1.setSelection(i);
                typeJob1 = user.getJobHistory().getJobDescriptions().get(0).getJobType();
            }
        }

        //Set type job 2
        typeJobAdapter2.add(user.getJobHistory().getJobDescriptions().get(1).getJobType());
        typeJobAdapter2.addAll(jobTypelist);
        typeJobSpinner2.setAdapter(typeJobAdapter2);
        typeJobSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeJob2 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        typeJobSpinner2.setSelection(0);
        typeJob2 = user.getJobHistory().getJobDescriptions().get(1).getJobType();

        //Set type job 3
        typeJobAdapter3.add(user.getJobHistory().getJobDescriptions().get(2).getJobType());
        typeJobAdapter3.addAll(jobTypelist);
        typeJobSpinner3.setAdapter(typeJobAdapter3);
        typeJobSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeJob3 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        typeJobSpinner3.setSelection(0);
        typeJob3 = user.getJobHistory().getJobDescriptions().get(2).getJobType();

    }
    private boolean isPassValidation(){
        firstname = firstnameInput.getText().toString().trim();
        lastname = lastnameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        addressDetail = addressDetailInput.getText().toString().trim();
        institutionName = institutionNameEducationInput.getText().toString().trim();
        jobName1 = jobNameInput1.getText().toString().trim();
        jobName2 = jobNameInput2.getText().toString().trim();
        jobName3 = jobNameInput3.getText().toString().trim();
        experience1 = experienceInput1.getText().toString().trim();
        experience2 = experienceInput2.getText().toString().trim();
        experience3 = experienceInput3.getText().toString().trim();
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
        if(addressDetail.isEmpty()){
            addressDetailInput.setError("โปรดระบุรายละเอียดที่อยู่");
            return false;
        }
        if(!levelEducation.equals("ไม่มี") && institutionName.isEmpty()){
            institutionNameEducationInput.setError("โปรดระบุชื่อสถานศึกษา");
            return false;
        }
        if(jobName1.isEmpty()){
            jobNameInput1.setError("โปรดระบุชื่ออาชีพ");
            return false;
        }
        if(experience1.isEmpty()){
            experienceInput1.setError("โปรดระบุรายละเอียดประสบการณ์");
            return false;
        }
        if(typeJob1.isEmpty()){
            errorJobTypeHistOutput.setVisibility(View.VISIBLE);
            return false;
        }else{
            errorJobTypeHistOutput.setVisibility(View.GONE);
        }
        return true;
    }
}