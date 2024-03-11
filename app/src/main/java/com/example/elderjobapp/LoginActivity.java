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
import com.example.elderjobapp.models.User;
import com.example.elderjobapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lombok.NonNull;

public class LoginActivity extends AppCompatActivity {
    EditText phoneInput,passwordInput;
    TextView errorOutput;
    String phone,password,userType;
    Button loginButton,registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        errorOutput = findViewById(R.id.errorOutput);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassValidation()) {
                    FirebaseFirestore.getInstance().collection("users").document(phone).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("UserData", "Document ID: " + document.getId());
                                            User user = document.toObject(User.class);
                                            if (PasswordEncoder.verifyPassword(password, user.getPassword())) {
                                                FirebaseUtil.setCurrentUserID(phone);
                                                userType = user.getUserType();
                                                Intent intent;
                                                if (userType.equals("find job")) {
                                                    intent = new Intent(LoginActivity.this, FindJobNavigationActivity.class);
                                                } else {
                                                    intent = new Intent(LoginActivity.this, GiveJobNavigationActivity.class);
                                                }
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                errorOutput.setText("รหัสผ่านไม่ถูดต้อง");
                                                errorOutput.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            errorOutput.setText("ไม่มีเบอร์มือถือนี้ในระบบ กรุณาลงทะเบียน");
                                            errorOutput.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        Log.w("UserData", "Error getting user.", task.getException());
                                    }
                                }
                            });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isPassValidation(){
        phone = phoneInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        if(phone.isEmpty()){
            phoneInput.setError("โปรดระบุเบอร์มือถือ");
            return false;
        }
        if(password.isEmpty()){
            phoneInput.setError("โปรดระบุรหัสผ่าน");
            return false;
        }
        return true;
    }
}