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

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.helper.FileHelper;
import com.example.elderjobapp.models.Address;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.AndroidUtil;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class EditProfileGiveJobFragment extends Fragment {

    ImageView profileImageOutput;
    Spinner provinceSpinner,districtSpinner,subdistrictSpinner;
    ArrayList<String> provincelist = new ArrayList<>();
    ArrayList<String> districtlist = new ArrayList<>();
    ArrayList<String> subdistrictlist = new ArrayList<>();
    ArrayAdapter<String> provinceAdapter,districtAdapter,subdistrictAdapter;
    Button cancelButton,saveButton,selectImageButton;
    EditText firstnameInput,lastnameInput,emailInput,dobInput,addressDetailInput;
    RadioGroup genderInput;
    RadioButton maleRadioButton,femaleRadioButton;
    String firstname,lastname,gender,email,province,district,subdistrict,addressDetail;
    int zipCode;
    Date dob;
    User user;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> imagePickLauncher;

    public EditProfileGiveJobFragment() {
        // Required empty public constructor
    }

    public static EditProfileGiveJobFragment newInstance(User user) {
        EditProfileGiveJobFragment fragment = new EditProfileGiveJobFragment();
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
                            AndroidUtil.setProfileImage(getContext(),selectedImageUri,profileImageOutput);
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
        View rootView = inflater.inflate(R.layout.fragment_edit_profile_give_job, container, false);
        setupUI(rootView);

        selectImageButton = rootView.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfileGiveJobFragment.this).cropSquare().compress(512).maxResultSize(512,512)
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
                    FirebaseUtil.currentUserDetails().set(user)
                            .addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()){
                                    Log.d(ContentValues.TAG,"Updated user data successfully");
                                    // update user data
                                    ProfileGiveJobFragment profileGiveJobFragment = ProfileGiveJobFragment.newInstance();
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, profileGiveJobFragment);
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
            gender = "ชาย";
        }else{
            femaleRadioButton.setChecked(true);
            gender = "หญิง";
        }
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
    }
    private boolean isPassValidation(){
        firstname = firstnameInput.getText().toString().trim();
        lastname = lastnameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        addressDetail = addressDetailInput.getText().toString().trim();
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
        return true;
    }

}