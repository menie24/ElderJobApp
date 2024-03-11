package com.example.elderjobapp.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Job implements Serializable {
    private String institutionName;
    private Address institutionAddress;
    private String jobName;
    private String jobType;
    private int salary;
    private String requirement;
    private String benefit;
    private String jobDetail;
    private String ownerID;
    private String contactName;
    private String phone;
    private String email;
    private String type;
    private Date expiredDate;
    private Date announcementDate;
    public Job() {
        this.institutionName = "";
        this.institutionAddress = null;
        this.jobName = "";
        this.jobType = "";
        this.salary = 0;
        this.requirement = "";
        this.benefit = "";
        this.jobDetail = "";
        this.ownerID = "";
        this.contactName = "";
        this.phone = "";
        this.email = "";
        this.type = "";
        this.expiredDate = null;
        this.announcementDate = null;
    }
    public Job(String institutionName, Address institutionAddress, String jobName, String jobType,int salary, String requirement, String benefit, String jobDetail, String ownerID,String contactName,String phone,String email, String type, Date expiredDate,Date announcementDate) {
        this.institutionName = institutionName;
        this.institutionAddress = institutionAddress;
        this.jobName = jobName;
        this.jobType = jobType;
        this.salary = salary;
        this.requirement = requirement;
        this.benefit = benefit;
        this.jobDetail = jobDetail;
        this.ownerID = ownerID;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
        this.type = type;
        this.expiredDate = expiredDate;
        this.announcementDate = announcementDate;
    }

}
