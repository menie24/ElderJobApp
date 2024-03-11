package com.example.elderjobapp.models;

import com.example.elderjobapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User implements Serializable {
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private String email;
    private Address currentAddress;
    private String userType;
    private Education education;
    private JobHistory jobHistory;

    public User(){
        this.phoneNumber = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
        this.gender = "";
        this.birthDate = null;
        this.email = "";
        this.currentAddress = null;
        this.userType = "";
        this.education = null;
        this.jobHistory = null;
    }
    public User(String phoneNumber, String password, String firstName, String lastName, String gender, Date birthDate, String email, Address currentAddress,String userType) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.email = email;
        this.currentAddress = currentAddress;
        this.userType = userType;
        this.education = null;
        this.jobHistory = null;
    }
}
