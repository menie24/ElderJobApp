package com.example.elderjobapp.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elderjobapp.LoginActivity;
import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.AndroidUtil;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import lombok.NonNull;

public class ProfileGiveJobFragment extends Fragment {
    private ImageView profileImageOutput;
    private TextView fullnameOutput,genderOutput,dobOutput,ageOutput,emailOutput,phoneOutput,addressDetailOutput,subdistrictOutput,districtOutput,provinceOutput;
    private User user;
    private Button editProfileButton,logoutButton;
    public ProfileGiveJobFragment() {
        // Required empty public constructor
    }
    public static ProfileGiveJobFragment newInstance() {
        ProfileGiveJobFragment fragment = new ProfileGiveJobFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_profile_give_job, container, false);
        profileImageOutput = rootView.findViewById(R.id.image);
        fullnameOutput = rootView.findViewById(R.id.fullnameOutput);
        genderOutput = rootView.findViewById(R.id.genderOutput);
        dobOutput = rootView.findViewById(R.id.dobOutput);
        ageOutput = rootView.findViewById(R.id.ageOutput);
        emailOutput = rootView.findViewById(R.id.emailOutput);
        phoneOutput = rootView.findViewById(R.id.phoneOutput);
        addressDetailOutput = rootView.findViewById(R.id.addressDetailOutput);
        subdistrictOutput = rootView.findViewById(R.id.subdistrictOutput);
        districtOutput = rootView.findViewById(R.id.districtOutput);
        provinceOutput = rootView.findViewById(R.id.provinceOutput);

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
        FirebaseUtil.currentUserDetails().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("UserData", "Document ID: " + document.getId());
                                user = document.toObject(User.class);
                                String fullname = user.getFirstName()+" "+user.getLastName();
                                fullnameOutput.setText(fullname);
                                genderOutput.setText(user.getGender());
                                dobOutput.setText(DateHelper.convertDateToStringFormat(user.getBirthDate()));
                                ageOutput.setText(String.valueOf(DateHelper.getAgeFromDOB(user.getBirthDate())));
                                emailOutput.setText(user.getEmail());
                                phoneOutput.setText(user.getPhoneNumber());
                                addressDetailOutput.setText(user.getCurrentAddress().getAddressDetail());
                                subdistrictOutput.setText(user.getCurrentAddress().getSubDistrict());
                                districtOutput.setText(user.getCurrentAddress().getDistrict());
                                provinceOutput.setText(user.getCurrentAddress().getProvince());
                            }
                        } else {
                            Log.w("UserData", "Error getting user.", task.getException());
                        }
                    }
                });
        editProfileButton = rootView.findViewById(R.id.saveFormButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileGiveJobFragment editProfileGiveJobFragment = EditProfileGiveJobFragment.newInstance(user);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, editProfileGiveJobFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        logoutButton = rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.logout();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        return rootView;
    }
}