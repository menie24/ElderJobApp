package com.example.elderjobapp.helper;

import android.content.Context;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class FileHelper {
    public static ArrayList<String> readProvince(Context context) throws IOException {
        ArrayList<String> provicelist = new ArrayList<>();
        InputStream inputStream = context.getAssets().open("Provinces.csv");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        CSVReader reader = new CSVReader(fileReader);
        String[] line;
        while ((line = reader.readNext()) != null) {
            provicelist.add(line[0]);
        }
        Collections.sort(provicelist);
        fileReader.close();
        reader.close();
        return provicelist;
    }
    public static ArrayList<String> readDistrict(Context context, String province) throws IOException {
        ArrayList<String> districtlist = new ArrayList<>();
        InputStream inputStream = context.getAssets().open("ProvincesThailand.csv");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        CSVReader reader = new CSVReader(fileReader);
        String[] line;
        while ((line = reader.readNext()) != null) {
            if(line[0].equals(province) && !districtlist.contains(line[1])){
                districtlist.add(line[1]);
            }
        }
        Collections.sort(districtlist);
        fileReader.close();
        reader.close();
        return districtlist;
    }
    public static ArrayList<String> readSubDistrict(Context context,String province,String district) throws IOException {
        ArrayList<String> subdistrictlist = new ArrayList<>();
        InputStream inputStream = context.getAssets().open("ProvincesThailand.csv");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        CSVReader reader = new CSVReader(fileReader);
        String[] line;
        while ((line = reader.readNext()) != null) {
            if(line[0].equals(province) && line[1].equals(district) && !subdistrictlist.contains(line[2])){
                subdistrictlist.add(line[2]);
            }
        }
        Collections.sort(subdistrictlist);
        fileReader.close();
        reader.close();
        return subdistrictlist;
    }
    public static int readZipCode(Context context,String province,String district,String subdistrict) throws IOException {
        int zipcode = 0;
        InputStream inputStream = context.getAssets().open("ProvincesThailand.csv");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        CSVReader reader = new CSVReader(fileReader);
        String[] line;
        while ((line = reader.readNext()) != null) {
            if(line[0].equals(province) && line[1].equals(district) && line[2].equals(subdistrict)){
                zipcode = Integer.parseInt(line[3]);
            }
        }
        fileReader.close();
        reader.close();
        return zipcode;
    }
}
