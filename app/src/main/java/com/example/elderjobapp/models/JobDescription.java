package com.example.elderjobapp.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class JobDescription implements Serializable {
    private String jobType;
    private String jobName;
    private String experience;
    public JobDescription() {
        this.jobType = "";
        this.jobName = "";
        this.experience = "";
    }
    public JobDescription(String jobType, String jobName, String experience) {
        this.jobType = jobType;
        this.jobName = jobName;
        this.experience = experience;
    }

}

