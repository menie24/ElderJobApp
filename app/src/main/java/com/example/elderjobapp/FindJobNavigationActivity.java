package com.example.elderjobapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.elderjobapp.databinding.ActivityFindJobNavigationBinding;
import com.example.elderjobapp.databinding.ActivityGiveJobNavigationBinding;
import com.example.elderjobapp.fragments.JobListFragment;
import com.example.elderjobapp.fragments.ProfileFindJobFragment;
import com.example.elderjobapp.fragments.ProfileGiveJobFragment;
import com.example.elderjobapp.fragments.SearchJobFragment;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;

public class FindJobNavigationActivity extends AppCompatActivity {

    ActivityFindJobNavigationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindJobNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new JobListFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.job_announcement){
                replaceFragment(new JobListFragment());
            }else if(item.getItemId()==R.id.search_job){
                replaceFragment(new SearchJobFragment());
            }else if(item.getItemId()==R.id.profile){
                replaceFragment(new ProfileFindJobFragment());
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