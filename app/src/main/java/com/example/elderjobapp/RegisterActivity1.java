package com.example.elderjobapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.elderjobapp.helper.PasswordEncoder;
import com.example.elderjobapp.models.Job;
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class RegisterActivity1 extends AppCompatActivity {
    EditText phoneInput,passwordInput,comfirmPasswordInput;
    TextView errorOutput;
    String phone,password,comfirmPassword;
    Button continueButton,back_button;
    String userType;
    User[] users;
    boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        FirebaseUtil.allUserCollectionReference().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> userList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            users = userList.toArray(new User[0]);

                        } else {
                            Log.w("UserData", "Error getting jobs.", task.getException());
                        }
                    }
                });
        isExist = false;
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        comfirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        errorOutput = findViewById(R.id.errorOutput);
        Intent currentIntent = getIntent();
        userType = currentIntent.getStringExtra("user type");

        back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity1.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continueButton = (Button) findViewById(R.id.loginButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassValidation()){
                    Intent intent;
                    if(userType.equals("find job")){
                        intent = new Intent(RegisterActivity1.this, RegisterActivity2.class);
                    }else{
                        intent = new Intent(RegisterActivity1.this, RegisterActivity.class);
                    }
                    intent.putExtra("user type",userType);
                    intent.putExtra("phone",phone);
                    intent.putExtra("password",PasswordEncoder.encodePassword(password));
                    startActivity(intent);
                    finish();
                }else{

                }
            }
        });
    }
    private boolean isPassValidation(){
        phone = phoneInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        comfirmPassword = comfirmPasswordInput.getText().toString().trim();
        if(phone.isEmpty()){
            phoneInput.setError("โปรดระบุเบอร์มือถือ");
            return false;
        }
        if(password.isEmpty()){
            passwordInput.setError("โปรดระบุรหัสผ่าน");
            return false;
        }
        if(password.length()<6) {
            errorOutput.setText("โปรดระบุรหัสผ่านอย่างน้อย 6 ตัว");
            errorOutput.setVisibility(View.VISIBLE);
            return false;
        }
        if(comfirmPassword.isEmpty()){
            comfirmPasswordInput.setError("โปรดระบุยืนยันรหัสผ่าน");
            return false;
        }
        if(!password.equals(comfirmPassword)){
            errorOutput.setText("รหัสผ่านไม่ตรงกัน โปรดลองอีกครั้ง");
            errorOutput.setVisibility(View.VISIBLE);
            return false;
        }
        for(int i=0;i< users.length;i++){
            if(users[i].getPhoneNumber().equals(phone)){
                errorOutput.setText("มีข้อมูลเบอร์มือถือนี้ในระบบแล้ว");
                errorOutput.setVisibility(View.VISIBLE);
                return false;
            }
        }
        errorOutput.setVisibility(View.GONE);
        return true;
    }
}