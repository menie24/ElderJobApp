package com.example.elderjobapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.elderjobapp.R;
import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;

public class JobDetailFragment extends Fragment {
    private TextView institutionNameOutput,provinceJobOutput,districtJobOutput,subdistrictJobOutput,addressDetailJobOutput,
            jobNameOutput,jobTypeOutput,salaryOutput,requirementOutput,benefitOutput,jobDetailOutput,contactNameOutput,
            phoneJobOutput,emailJobOutput,typeOutput,expiredDateOutput,announcementDate;
    Button backButton;
    private Job job;
    public JobDetailFragment() {
        // Required empty public constructor
    }
    public static JobDetailFragment newInstance(Job job) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("job", job);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            job = (Job) getArguments().getSerializable("job");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_detail, container, false);

        institutionNameOutput = rootView.findViewById(R.id.institutionNameOutput);
        provinceJobOutput = rootView.findViewById(R.id.provinceJobOutput);
        districtJobOutput = rootView.findViewById(R.id.districtJobOutput);
        subdistrictJobOutput = rootView.findViewById(R.id.subdistrictJobOutput);
        addressDetailJobOutput = rootView.findViewById(R.id.addressDetailJobOutput);
        jobNameOutput = rootView.findViewById(R.id.jobNameOutput);
        jobTypeOutput = rootView.findViewById(R.id.jobTypeOutput);
        salaryOutput = rootView.findViewById(R.id.salaryOutput);
        requirementOutput = rootView.findViewById(R.id.requirementOutput);
        benefitOutput = rootView.findViewById(R.id.benefitOutput);
        jobDetailOutput = rootView.findViewById(R.id.jobDetailOutput);
        requirementOutput = rootView.findViewById(R.id.requirementOutput);
        contactNameOutput = rootView.findViewById(R.id.contactNameOutput);
        phoneJobOutput = rootView.findViewById(R.id.phoneJobOutput);
        emailJobOutput = rootView.findViewById(R.id.emailJobOutput);
        typeOutput = rootView.findViewById(R.id.typeOutput);
        expiredDateOutput = rootView.findViewById(R.id.expiredDateOutput);
        announcementDate = rootView.findViewById(R.id.announcementDateOutput);

        institutionNameOutput.setText(job.getInstitutionName());
        provinceJobOutput.setText(job.getInstitutionAddress().getProvince());
        districtJobOutput.setText(job.getInstitutionAddress().getDistrict());
        subdistrictJobOutput.setText(job.getInstitutionAddress().getSubDistrict());
        addressDetailJobOutput.setText(job.getInstitutionAddress().getAddressDetail());
        jobNameOutput.setText(job.getJobName());
        jobTypeOutput.setText(job.getJobType());
        salaryOutput.setText(String.valueOf(job.getSalary()));
        requirementOutput.setText(job.getRequirement());
        benefitOutput.setText(job.getBenefit());
        jobDetailOutput.setText(job.getJobDetail());
        requirementOutput.setText(job.getRequirement());
        contactNameOutput.setText(job.getContactName());
        phoneJobOutput.setText(job.getContactName());
        emailJobOutput.setText(job.getEmail());
        typeOutput.setText(job.getType());
        expiredDateOutput.setText(DateHelper.convertDateToStringFormat(job.getExpiredDate()));
        announcementDate.setText(DateHelper.convertDateToStringFormat(job.getAnnouncementDate()));

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