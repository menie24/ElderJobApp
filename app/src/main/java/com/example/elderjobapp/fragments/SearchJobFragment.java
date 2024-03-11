package com.example.elderjobapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.FileHelper;

import java.io.IOException;
import java.util.ArrayList;

public class SearchJobFragment extends Fragment {
    Spinner provinceSearchSpinner,districtSearchSpinner,subdistrictSearchSpinner,jobTypeSpinner;
    ArrayList<String> provicelist = new ArrayList<>();
    ArrayList<String> districtlist = new ArrayList<>();
    ArrayList<String> subdistrictlist = new ArrayList<>();
    ArrayList<String> jobTypelist = new ArrayList<>();
    ArrayAdapter<String> provinceAdapter,districtAdapter,subdistrictAdapter,jobTypeAdapter;
    EditText searchTextInput;
    Button searchButton;
    String province,district,subdistrict,jobType,keyword;
    int zipCode;
    Context context;
    public SearchJobFragment() {
        // Required empty public constructor
    }
    public static SearchJobFragment newInstance() {
        SearchJobFragment fragment = new SearchJobFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search_job, container, false);
        context = requireContext();
        setupUI(rootView);

        searchButton = rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchTextInput.getText().toString().trim();
                JobListSearchFragment jobListSearchFragment = JobListSearchFragment.newInstance(province,district,subdistrict,jobType,keyword);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, jobListSearchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }
    private void setupUI(View rootView){
        searchTextInput = rootView.findViewById(R.id.searchTextInput);
        provinceSearchSpinner = rootView.findViewById(R.id.provinceSearchSpinner);
        provinceAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        districtSearchSpinner = rootView.findViewById(R.id.districtSearchSpinner);
        districtAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        subdistrictSearchSpinner = rootView.findViewById(R.id.subdistrictSearchSpinner);
        subdistrictAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);
        jobTypeSpinner = rootView.findViewById(R.id.jobTypeSpinner);
        jobTypeAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_item);

        //initial
        province = "ทุกจังหวัด";
        district = "ทุกอำเภอ";
        subdistrict = "ทุกตำบล";
        jobType = "งานทั้งหมด";
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
                districtAdapter.clear();
                districtlist.clear();
                subdistrictAdapter.clear();
                subdistrictlist.clear();
                try {
                    districtlist = FileHelper.readDistrict(context,province);
                    districtAdapter.add("ทุกอำเภอ");
                    districtAdapter.addAll(districtlist);
                    districtSearchSpinner.setAdapter(districtAdapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                districtSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        district = (String) parent.getItemAtPosition(position);
                        subdistrictAdapter.clear();
                        subdistrictlist.clear();
                        try {
                            subdistrictlist = FileHelper.readSubDistrict(context,province,district);
                            subdistrictAdapter.add("ทุกตำบล");
                            subdistrictAdapter.addAll(subdistrictlist);
                            subdistrictSearchSpinner.setAdapter(subdistrictAdapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        subdistrictSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }
}