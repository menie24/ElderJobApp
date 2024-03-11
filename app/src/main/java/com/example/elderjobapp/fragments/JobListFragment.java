package com.example.elderjobapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.elderjobapp.JobBaseAdapter;
import com.example.elderjobapp.R;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class JobListFragment extends Fragment {
    Job[] jobs;
    ListView listView;
    public JobListFragment() {
        // Required empty public constructor
    }
    public static JobListFragment newInstance() {
        JobListFragment fragment = new JobListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_job_list, container, false);
        FirebaseUtil.allJobCollectionReference().get()
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

        return rootView;
    }
}