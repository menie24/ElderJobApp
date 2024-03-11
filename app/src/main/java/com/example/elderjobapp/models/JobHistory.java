package com.example.elderjobapp.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class JobHistory implements Serializable {
    private ArrayList<JobDescription> jobDescriptions;

    public JobHistory() {
        this.jobDescriptions = new ArrayList<>();
    }
    public JobHistory(ArrayList<JobDescription> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
    }
    public void add(JobDescription jobDescription){
        this.jobDescriptions.add(jobDescription);
    }
}
