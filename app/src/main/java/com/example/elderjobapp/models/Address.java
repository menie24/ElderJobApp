package com.example.elderjobapp.models;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Address implements Serializable {
    private String addressDetail;
    private String subDistrict;
    private String district;
    private String province;
    private int zipCode;

    public Address(){
        setEmptyState();
    }
    public Address(String addressDetail, String subDistrict, String district, String province,int zipCode) {
        this.addressDetail = addressDetail;
        this.subDistrict = subDistrict;
        this.district = district;
        this.province = province;
        this.zipCode = zipCode;
    }
    public void setEmptyState(){
        this.addressDetail = "";
        this.subDistrict = "";
        this.district = "";
        this.province = "";
        this.zipCode = 0;
    }
}
