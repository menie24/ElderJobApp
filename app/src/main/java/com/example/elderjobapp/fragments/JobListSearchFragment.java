package com.example.elderjobapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.elderjobapp.JobBaseAdapter;
import com.example.elderjobapp.JobRecipientsBaseAdapter;
import com.example.elderjobapp.R;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.NonNull;

public class JobListSearchFragment extends Fragment {

    Job[] jobs;
    ListView listView;
    String province,district,subdistrict,jobType,keyword;
    Button backButton;
    public JobListSearchFragment() {
        // Required empty public constructor
    }

    public static JobListSearchFragment newInstance(String province,String district,String subdistrict,String jobType,String keyword) {
        JobListSearchFragment fragment = new JobListSearchFragment();
        Bundle args = new Bundle();
        args.putString("province",province);
        args.putString("district",district);
        args.putString("subdistrict",subdistrict);
        args.putString("jobType",jobType);
        args.putString("keyword",keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            province = getArguments().getString("province");
            district = getArguments().getString("district");
            subdistrict = getArguments().getString("subdistrict");
            jobType = getArguments().getString("jobType");
            keyword = getArguments().getString("keyword");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_list_search, container, false);
        backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
        if(province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            getAllJobs(rootView);
        }else if(!province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByProvince(rootView,province);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByProvinceAndDistrict(rootView,province,district);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && !subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByProvinceDistrictAndSubdistrict(rootView,province,district,subdistrict);
        }else if(province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByJobType(rootView,jobType);
        }else if(!province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByProvinceAndJobType(rootView,province,jobType);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByProvinceDistrictAndJobType(rootView,province,district,jobType);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && !subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && keyword.isEmpty()){
            searchJobByProvinceDistrictSubdistrictAndJobType(rootView,province,district,subdistrict,jobType);
        }else if(province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByKeyword(rootView,keyword);
        }else if(!province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByProvinceAndKeyword(rootView,province,keyword);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByProvinceDistrictAndKeyword(rootView,province,district,keyword);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && !subdistrict.equals("ทุกตำบล") && jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByProvinceDistrictSubdistrictAndKeyword(rootView,province,district,subdistrict,keyword);
        }else if(province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByJobTypeAndKeyword(rootView,jobType,keyword);
        }else if(!province.equals("ทุกจังหวัด") && district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByProvinceJobTypeAndKeyword(rootView,province,jobType,keyword);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByProvinceDistrictJobTypeAndKeyword(rootView,province,district,jobType,keyword);
        }else if(!province.equals("ทุกจังหวัด") && !district.equals("ทุกอำเภอ") && !subdistrict.equals("ทุกตำบล") && !jobType.equals("งานทั้งหมด") && !keyword.isEmpty()){
            searchJobByProvinceDistrictSubdistrictJobTypeAndKeyword(rootView,province,district,subdistrict,jobType,keyword);
        }



        return rootView;
    }
    private void getAllJobs(View rootView) {
        FirebaseUtil.allJobCollectionReference()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                jobList.add(job);
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByProvince(View rootView,String province){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByProvinceAndDistrict(View rootView,String province,String district){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void  searchJobByProvinceDistrictAndSubdistrict(View rootView,String province,String district,String subdistrict){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && subdistrict.equals(job.getInstitutionAddress().getSubDistrict())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByJobType(View rootView,String jobType){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(jobType.equals(job.getJobType())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByProvinceAndJobType(View rootView,String province,String jobType){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && jobType.equals(job.getJobType())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }

    private void searchJobByProvinceDistrictAndJobType(View rootView,String province,String district,String jobType){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && jobType.equals(job.getJobType())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }

    private void  searchJobByProvinceDistrictSubdistrictAndJobType(View rootView,String province,String district,String subdistrict,String jobType){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && subdistrict.equals(job.getInstitutionAddress().getSubDistrict()) && jobType.equals(job.getJobType())){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByKeyword(View rootView,String keyword) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(), jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByProvinceAndKeyword(View rootView,String province,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }

    private void searchJobByProvinceDistrictAndKeyword(View rootView,String province,String district,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }

    private void  searchJobByProvinceDistrictSubdistrictAndKeyword(View rootView,String province,String district,String subdistrict,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && subdistrict.equals(job.getInstitutionAddress().getSubDistrict()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByJobTypeAndKeyword(View rootView,String jobTypem,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(jobType.equals(job.getJobType()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
    private void searchJobByProvinceJobTypeAndKeyword(View rootView,String province,String jobType,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && jobType.equals(job.getJobType()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }

    private void searchJobByProvinceDistrictJobTypeAndKeyword(View rootView,String province,String district,String jobType,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && jobType.equals(job.getJobType()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }

    private void  searchJobByProvinceDistrictSubdistrictJobTypeAndKeyword(View rootView,String province,String district,String subdistrict,String jobType,String keyword){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        FirebaseUtil.allJobCollectionReference()
                .whereGreaterThan("expiredDate", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Job> jobList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                if(province.equals(job.getInstitutionAddress().getProvince()) && district.equals(job.getInstitutionAddress().getDistrict()) && subdistrict.equals(job.getInstitutionAddress().getSubDistrict()) && jobType.equals(job.getJobType()) && job.getJobName().contains(keyword)){
                                    jobList.add(job);
                                }
                            }
                            jobs = jobList.toArray(new Job[0]);
                            listView = rootView.findViewById(R.id.job_list_view);
                            JobBaseAdapter jobBaseAdapter = new JobBaseAdapter(rootView.getContext(),jobs);
                            listView.setAdapter(jobBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobDetailFragment jobDetailFragment = JobDetailFragment.newInstance(jobs[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("JobData", "Error getting jobs.", task.getException());
                        }
                    }
                });
    }
}