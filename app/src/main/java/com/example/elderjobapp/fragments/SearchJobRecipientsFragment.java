package com.example.elderjobapp.fragments;

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
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.FileHelper;
import com.example.elderjobapp.models.JobDescription;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class SearchJobRecipientsFragment extends Fragment {
    Spinner provinceSearchSpinner,jobTypeSpinner;
    ArrayList<String> provicelist = new ArrayList<>();
    ArrayList<String> jobTypelist = new ArrayList<>();
    ArrayAdapter<String> provinceAdapter,jobTypeAdapter;
    EditText searchTextInput;
    Button searchButton;
    RadioGroup genderInput;
    RadioButton maleRadioButton,femaleRadioButton,allGenderRadioButton;
    String province,jobType,gender,keyword;
    Context context;
    User[] users;
    public SearchJobRecipientsFragment() {
        // Required empty public constructor
    }

    public static SearchJobRecipientsFragment newInstance() {
        SearchJobRecipientsFragment fragment = new SearchJobRecipientsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search_job_recipients, container, false);
        context = requireContext();
        setupUI(rootView);

        searchButton = rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchTextInput.getText().toString().trim();
                JobRecipientsListFragment jobRecipientsListFragment = JobRecipientsListFragment.newInstance(jobType,gender,province,keyword);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, jobRecipientsListFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });

        return rootView;
    }
    private void setupUI(View rootView){
        searchTextInput = rootView.findViewById(R.id.searchTextInput);
        genderInput = rootView.findViewById(R.id.radioGroup);
        allGenderRadioButton = rootView.findViewById(R.id.allGenderRadioButton);
        maleRadioButton = rootView.findViewById(R.id.maleRadioButton);
        femaleRadioButton = rootView.findViewById(R.id.femaleRadioButton);
        allGenderRadioButton.setChecked(true);
        gender = allGenderRadioButton.getText().toString();
        provinceSearchSpinner = rootView.findViewById(R.id.provinceSearchSpinner);
        provinceAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        jobTypeSpinner = rootView.findViewById(R.id.jobTypeSpinner);
        jobTypeAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);

        //initial
        jobType = "งานทั้งหมด";
        gender = "ทุกเพศ";
        province = "ทุกจังหวัด";
        keyword = "";

        // job type spinner
        jobTypelist.add("งานบริการ");
        jobTypelist.add("งานฝีมือ");
        jobTypelist.add("งานบริหาร/งานเอกสาร");
        jobTypelist.add("งานทั่วไป");
        jobTypeAdapter.add("งานทั้งหมด");
        jobTypeAdapter.addAll(jobTypelist);
        jobTypeSpinner.setAdapter(jobTypeAdapter);
        jobTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        provinceAdapter.add("ทุกจังหวัด");
        provinceAdapter.addAll(provicelist);
        provinceSearchSpinner.setAdapter(provinceAdapter);
        provinceSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = (String) parent.getItemAtPosition(position);
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
                    allGenderRadioButton.setChecked(false);
                    gender = maleRadioButton.getText().toString();
                }
            }
        });
        femaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (femaleRadioButton.isChecked()) {
                    maleRadioButton.setChecked(false);
                    allGenderRadioButton.setChecked(false);
                    gender = femaleRadioButton.getText().toString();
                }
            }
        });
        allGenderRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allGenderRadioButton.isChecked()) {
                    maleRadioButton.setChecked(false);
                    femaleRadioButton.setChecked(false);
                    gender = allGenderRadioButton.getText().toString();
                }
            }
        });
    }















}