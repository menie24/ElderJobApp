package com.example.elderjobapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.elderjobapp.fragments.JobFormFragment;
import com.example.elderjobapp.fragments.JobListFragment;
import com.example.elderjobapp.fragments.ProfileGiveJobFragment;
import com.example.elderjobapp.fragments.SearchJobRecipientsFragment;
import com.example.elderjobapp.databinding.ActivityGiveJobNavigationBinding;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;

public class GiveJobNavigationActivity extends AppCompatActivity {

    ActivityGiveJobNavigationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGiveJobNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new JobListFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId()==R.id.job_announcement){
                replaceFragment(new JobListFragment());
            }else if(item.getItemId()==R.id.search_job_recipients){
                replaceFragment(new SearchJobRecipientsFragment());
            }else if(item.getItemId()==R.id.create_announcement){
                replaceFragment(new JobFormFragment());
            }else if(item.getItemId()==R.id.profile){
                replaceFragment(new ProfileGiveJobFragment());
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}