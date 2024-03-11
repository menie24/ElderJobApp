package com.example.elderjobapp.fragments;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.AndroidUtil;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import lombok.NonNull;

public class JobRecipientsDetailFragment extends Fragment {
    private ImageView profileImageOutput;
    private TextView fullnameOutput,genderOutput,dobOutput,ageOutput,emailOutput,phoneOutput,addressDetailOutput,subdistrictOutput,districtOutput,provinceOutput,
            levelEducationOutput,institutionNameEducationOutput,typeJob1Output,jobName1Output,experience1Output,
            typeJob2Output,jobName2Output,experience2Output,typeJob3Output,jobName3Output,experience3Output,educationNameTextView;
    Button backButton;
    private User user;

    public JobRecipientsDetailFragment() {
        // Required empty public constructor
    }

    public static JobRecipientsDetailFragment newInstance(User user) {
        JobRecipientsDetailFragment fragment = new JobRecipientsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_recipients_detail, container, false);
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
        levelEducationOutput = rootView.findViewById(R.id.levelEducationOutput);
        educationNameTextView = rootView.findViewById(R.id.educationNameTextView);
        institutionNameEducationOutput = rootView.findViewById(R.id.institutionNameEducationOutput);
        typeJob1Output = rootView.findViewById(R.id.typeJob1Output);
        jobName1Output = rootView.findViewById(R.id.jobName1Output);
        experience1Output = rootView.findViewById(R.id.experience1Output);
        typeJob2Output = rootView.findViewById(R.id.typeJob2Output);
        jobName2Output = rootView.findViewById(R.id.jobName2Output);
        experience2Output = rootView.findViewById(R.id.experience2Output);
        typeJob3Output = rootView.findViewById(R.id.typeJob3Output);
        jobName3Output = rootView.findViewById(R.id.jobName3Output);
        experience3Output = rootView.findViewById(R.id.experience3Output);

        FirebaseUtil.getOtherProfileImageStorageRef(user.getPhoneNumber()).getDownloadUrl().addOnSuccessListener(uri -> {
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
        levelEducationOutput.setText(user.getEducation().getLevel());
        if(user.getEducation().getLevel().equals("ไม่มี")){
            educationNameTextView.setVisibility(View.GONE);
            institutionNameEducationOutput.setVisibility(View.GONE);
        }else{
            institutionNameEducationOutput.setText(user.getEducation().getInstitutionName());
        }
        typeJob1Output.setText(user.getJobHistory().getJobDescriptions().get(0).getJobType());
        jobName1Output.setText(user.getJobHistory().getJobDescriptions().get(0).getJobName());
        experience1Output.setText(user.getJobHistory().getJobDescriptions().get(0).getExperience());
        typeJob2Output.setText(user.getJobHistory().getJobDescriptions().get(1).getJobType());
        jobName2Output.setText(user.getJobHistory().getJobDescriptions().get(1).getJobName());
        experience2Output.setText(user.getJobHistory().getJobDescriptions().get(1).getExperience());
        typeJob3Output.setText(user.getJobHistory().getJobDescriptions().get(2).getJobType());
        jobName3Output.setText(user.getJobHistory().getJobDescriptions().get(2).getJobName());
        experience3Output.setText(user.getJobHistory().getJobDescriptions().get(2).getExperience());

        backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
        return rootView;
    }
}