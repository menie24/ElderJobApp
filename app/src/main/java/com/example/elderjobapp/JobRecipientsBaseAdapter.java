package com.example.elderjobapp;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elderjobapp.helper.DateHelper;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.AndroidUtil;
import com.example.elderjobapp.utils.FirebaseUtil;

public class JobRecipientsBaseAdapter extends BaseAdapter {
    Context context;
    User[] users;
    LayoutInflater inflater;
    public JobRecipientsBaseAdapter(Context context, User[] users){
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.length;
    }

    @Override
    public Object getItem(int position) {
        return users[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_job_recipients_list_view,null);
        ImageView image = convertView.findViewById(R.id.image);
        TextView fullname = convertView.findViewById(R.id.fullname);
        TextView gender = convertView.findViewById(R.id.gender);
        TextView age = convertView.findViewById(R.id.age);
        TextView jobNameList = convertView.findViewById(R.id.jobNameList);

        FirebaseUtil.getOtherProfileImageStorageRef(users[position].getPhoneNumber()).getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(ContentValues.TAG, "Load profile image successfully");
            AndroidUtil.setProfileImage(context, uri,image);
        }).addOnFailureListener(exception -> {
            Log.w("ProfileImage", "Error getting image.", exception);
            Log.d(ContentValues.TAG, "Failed to load profile image, loading default");
            FirebaseUtil.getDefaultProfileImageStorageRef().getDownloadUrl().addOnSuccessListener(defaultUri -> {
                Log.d(ContentValues.TAG, "Load default profile image successfully");
                AndroidUtil.setProfileImage(context, defaultUri,image);
            }).addOnFailureListener(defaultException -> {
                Log.d(ContentValues.TAG, "Failed to load default profile image");
            });
        });
        String str = users[position].getFirstName()+" "+users[position].getLastName();
        fullname.setText(str);
        gender.setText(users[position].getGender());
        age.setText(String.valueOf(DateHelper.getAgeFromDOB(users[position].getBirthDate())));
        String jobNames = "";
        String jobName1 = users[position].getJobHistory().getJobDescriptions().get(0).getJobName();
        jobNames+=jobName1;
        String jobName2 = users[position].getJobHistory().getJobDescriptions().get(1).getJobName();
        if(!jobName2.isEmpty()){
            jobNames+=","+jobName2;
        }
        String jobName3 = users[position].getJobHistory().getJobDescriptions().get(2).getJobName();
        if(!jobName3.isEmpty()){
            jobNames+=","+jobName3;
        }
        jobNameList.setText(jobNames);

        return convertView;
    }
}
