package com.example.elderjobapp.models;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Education implements Serializable {
    private String level;
    private String institutionName;
    public Education() {
        this.level = "";
        this.institutionName = "";
    }
    public Education(String level, String institutionName) {
        this.level = level;
        this.institutionName = institutionName;
    }

}
