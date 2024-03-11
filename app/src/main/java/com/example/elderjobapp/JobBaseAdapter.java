package com.example.elderjobapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.models.Job;

public class JobBaseAdapter extends BaseAdapter {
    Context context;
    Job[] jobs;
    LayoutInflater inflater;
    public JobBaseAdapter(Context context, Job[] jobs){
        this.context = context;
        this.jobs = jobs;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return jobs.length;
    }

    @Override
    public Object getItem(int position) {
        return jobs[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_job_list_view,null);
        TextView jobName = (TextView) convertView.findViewById(R.id.fullname);
        TextView institutionName = (TextView) convertView.findViewById(R.id.jobType1);
        TextView districtAndProvince = (TextView) convertView.findViewById(R.id.districtAndProvince);
        TextView salary = (TextView) convertView.findViewById(R.id.salary);
        TextView announcementDate = (TextView) convertView.findViewById(R.id.announcementDate);
        jobName.setText(jobs[position].getJobName());
        institutionName.setText(jobs[position].getInstitutionName());
        String location = jobs[position].getInstitutionAddress().getDistrict()+" จ."+jobs[position].getInstitutionAddress().getProvince();
        districtAndProvince.setText(location);
        String s = jobs[position].getSalary()+" บาท";
        salary.setText(s);
        announcementDate.setText(DateHelper.convertDateToStringFormat(jobs[position].getAnnouncementDate()));
        return convertView;
    }
}
