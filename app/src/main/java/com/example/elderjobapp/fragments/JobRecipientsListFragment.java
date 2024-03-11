package com.example.elderjobapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.elderjobapp.JobRecipientsBaseAdapter;
import com.example.elderjobapp.R;
import com.example.elderjobapp.models.JobDescription;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class JobRecipientsListFragment extends Fragment {

    User[] users;
    ListView listView;
    String jobType,gender,province,keyword;
    Button backButton;

    public JobRecipientsListFragment() {
        // Required empty public constructor
    }

    public static JobRecipientsListFragment newInstance(String jobType,String gender,String province,String keyword) {
        JobRecipientsListFragment fragment = new JobRecipientsListFragment();
        Bundle args = new Bundle();
        args.putString("jobType",jobType);
        args.putString("gender",gender);
        args.putString("province",province);
        args.putString("keyword",keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobType = getArguments().getString("jobType");
            gender = getArguments().getString("gender");
            province = getArguments().getString("province");
            keyword = getArguments().getString("keyword");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_recipients_list, container, false);
        backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });

        if(jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            getAllUsers(rootView);
        } else if(!jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && keyword.isEmpty()) {
            searchUsersByJobType(rootView,jobType);
        }else if(jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            searchUsersByGender(rootView,gender);
        }else if(jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            searchUsersByProvince(rootView,province);
        }else if(jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByKeyword(rootView,keyword);
        }else if(jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            searchUsersByGenderAndProvince(rootView,gender,province);
        }else if(jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && !keyword.isEmpty()) {
            searchUsersByProvinceAndKeyword(rootView,province,keyword);
        }else if(jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByGenderAndKeyword(rootView,gender,keyword);
        }else if(!jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByJobTypeAndKeyword(rootView,jobType,keyword);
        }else if(!jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            searchUsersByJobTypeAndProvince(rootView,jobType,province);
        }else if(!jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            searchUsersByJobTypeAndGender(rootView,jobType,gender);
        }else if(!jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && keyword.isEmpty()){
            searchUsersByJobTypeGenderAndProvince(rootView,jobType,gender,province);
        }else if(!jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByJobTypeGenderAndKeyword(rootView,jobType,gender,keyword);
        }else if(!jobType.equals("งานทั้งหมด") && gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByJobTypeProvinceAndKeyword(rootView,jobType,province,keyword);
        }else if(jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByGenderProvinceAndKeyword(rootView,gender,province,keyword);
        }else if(!jobType.equals("งานทั้งหมด") && !gender.equals("ทุกเพศ") && !province.equals("ทุกจังหวัด") && !keyword.isEmpty()){
            searchUsersByJobTypeGenderProvinceAndKeyword(rootView,jobType,gender,province,keyword);
        }

        return rootView;
    }

    private void getAllUsers(View rootView) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByJobType(View rootView,String jobType) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                for (JobDescription description : user.getJobHistory().getJobDescriptions()) {
                                    if (description.getJobType().equals(jobType)) {
                                        userList.add(user);
                                        break;
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByGender(View rootView,String gender) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });


    }
    private void searchUsersByProvince(View rootView,String province) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (province.equals(user.getCurrentAddress().getProvince())) {
                                    userList.add(user);
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByKeyword(View rootView,String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                    if (jobDescription.getJobName().contains(keyword)) {
                                        Log.d("UserWithKeyword", user.toString());
                                        userList.add(user);
                                        break;
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }


    private void searchUsersByGenderAndProvince(View rootView,String gender, String province) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (province.equals(user.getCurrentAddress().getProvince())) {
                                    Log.d("UserWithGenderAndProvince", user.toString());
                                    userList.add(user);
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }

    private void searchUsersByProvinceAndKeyword(View rootView,String province, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (province.equals(user.getCurrentAddress().getProvince())) {
                                    for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                        if (jobDescription.getJobName().contains(keyword)) {
                                            Log.d("UserWithProvinceAndKeyword", user.toString());
                                            userList.add(user);
                                            break;
                                        }
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByGenderAndKeyword(View rootView,String gender, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                    if (jobDescription.getJobName().contains(keyword)) {
                                        Log.d("UserWithGenderAndKeyword", user.toString());
                                        userList.add(user);
                                        break;
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }

    private void searchUsersByJobTypeAndKeyword(View rootView,String jobType, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        boolean jobTypeFlag;
                        boolean keywordFlag;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                jobTypeFlag = false;
                                keywordFlag = false;
                                for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                    if (jobDescription.getJobName().contains(keyword)) {
                                        keywordFlag = true;
                                    }
                                    if(jobDescription.getJobType().equals(jobType)){
                                        jobTypeFlag = true;
                                    }
                                }
                                if(jobTypeFlag && keywordFlag){
                                    Log.d("UserWithJobTypeAndKeyword", user.toString());
                                    userList.add(user);
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByJobTypeAndProvince(View rootView,String jobType, String province) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (province.equals(user.getCurrentAddress().getProvince())) {
                                    for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                        if (jobDescription.getJobType().equals(jobType)) {
                                            Log.d("UserWithJobTypeAndProvince", user.toString());
                                            userList.add(user);
                                            break;
                                        }
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByJobTypeAndGender(View rootView,String jobType, String gender) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                    if (jobDescription.getJobType().equals(jobType)) {
                                        Log.d("UserWithJobTypeAndGender", user.toString());
                                        userList.add(user);
                                        break;
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }

    private void searchUsersByJobTypeGenderAndProvince(View rootView,String jobType, String gender, String province) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (province.equals(user.getCurrentAddress().getProvince())) {
                                    for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                        if (jobDescription.getJobType().equals(jobType)) {
                                            Log.d("UserWithJobTypeGenderAndProvince", user.toString());
                                            userList.add(user);
                                            break;
                                        }
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }

    private void searchUsersByJobTypeGenderAndKeyword(View rootView,String jobType, String gender, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        boolean jobTypeFlag;
                        boolean keywordFlag;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                jobTypeFlag = false;
                                keywordFlag = false;
                                for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                    if (jobDescription.getJobName().contains(keyword)) {
                                        keywordFlag = true;
                                    }
                                    if(jobDescription.getJobType().equals(jobType)){
                                        jobTypeFlag = true;
                                    }
                                }
                                if(jobTypeFlag && keywordFlag){
                                    Log.d("UserWithJobTypeGenderAndKeyword", user.toString());
                                    userList.add(user);
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByJobTypeProvinceAndKeyword(View rootView,String jobType, String province, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        boolean jobTypeFlag;
                        boolean keywordFlag;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if(province.equals(user.getCurrentAddress().getProvince())){
                                    jobTypeFlag = false;
                                    keywordFlag = false;
                                    for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                        if (jobDescription.getJobName().contains(keyword)) {
                                            keywordFlag = true;
                                        }
                                        if(jobDescription.getJobType().equals(jobType)){
                                            jobTypeFlag = true;
                                        }
                                    }
                                    if(jobTypeFlag && keywordFlag){
                                        Log.d("UserWithJobTypeProvinceAndKeyword", user.toString());
                                        userList.add(user);
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByGenderProvinceAndKeyword(View rootView,String gender, String province, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if(province.equals(user.getCurrentAddress().getProvince())){
                                    for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                        if (jobDescription.getJobName().contains(keyword)) {
                                            Log.d("UserWithJobTypeProvinceAndKeyword", user.toString());
                                            userList.add(user);
                                        }
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void searchUsersByJobTypeGenderProvinceAndKeyword(View rootView,String jobType, String gender, String province, String keyword) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("userType", "find job")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList = new ArrayList<>();
                        boolean jobTypeFlag;
                        boolean keywordFlag;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if(province.equals(user.getCurrentAddress().getProvince())){
                                    jobTypeFlag = false;
                                    keywordFlag = false;
                                    for (JobDescription jobDescription : user.getJobHistory().getJobDescriptions()) {
                                        if (jobDescription.getJobName().contains(keyword)) {
                                            keywordFlag = true;
                                        }
                                        if(jobDescription.getJobType().equals(jobType)){
                                            jobTypeFlag = true;
                                        }
                                    }
                                    if(jobTypeFlag && keywordFlag){
                                        Log.d("UserWithJobTypeGenderProvinceAndKeyword", user.toString());
                                        userList.add(user);
                                    }
                                }
                            }
                            users = userList.toArray(new User[0]);
                            listView = rootView.findViewById(R.id.job_recipients_list_view);
                            JobRecipientsBaseAdapter jobRecipientsBaseAdapter = new JobRecipientsBaseAdapter(rootView.getContext(),users);
                            listView.setAdapter(jobRecipientsBaseAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JobRecipientsDetailFragment jobRecipientsDetailFragment = JobRecipientsDetailFragment.newInstance(users[position]);
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, jobRecipientsDetailFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        } else {
                            Log.w("UserData", "Error getting users.", task.getException());
                        }
                    }
                });
    }
}