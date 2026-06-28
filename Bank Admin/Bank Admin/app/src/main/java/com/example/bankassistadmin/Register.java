package com.example.bankassistadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.aniketjain.weatherapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.List;

public class Register extends AppCompatActivity {

    EditText name, email, pass, repass, phone;
    Button register;
    TextView login, CreateAccount_Text;
    View CreateAccount_Button;
    ProgressBar buttonProgress;
    boolean passwordVisible;
    Spinner spinnerDealWith;
    String selectedDealWith;
    Model userData;

    // Firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        pass = findViewById(R.id.password);
        repass = findViewById(R.id.repassword);
        login = findViewById(R.id.login);
        CreateAccount_Text = findViewById(R.id.buttonText);
        CreateAccount_Button = findViewById(R.id.login_button);
        spinnerDealWith = findViewById(R.id.spinner_deal_with);

        // Setup Spinner with predefined intent categories
        List<String> intentList = Arrays.asList(
                "Select Option",
                "New Loan Request",
                "Loan Assistance",
                "Loan Repayment",
                "Fraud or Dispute"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, intentList);
        spinnerDealWith.setAdapter(adapter);

        spinnerDealWith.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDealWith = intentList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        firebaseAuth = FirebaseAuth.getInstance();
        CreateAccount_Button.setOnClickListener(view -> SetValidation());

        login.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    public void SetValidation() {
        userData = new Model();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("REGISTERING ...");
        progressDialog.show();

        String password = pass.getText().toString().trim();
        String repassword = repass.getText().toString().trim();

        userData.setName(name.getText().toString());
        userData.setEmail(email.getText().toString());
        userData.setMno(phone.getText().toString());
        userData.setImageurl("https://firebasestorage.googleapis.com/v0/b/womensafety-fa171.appspot.com/o/profile_nav_logo.png?alt=media&token=8dc14aa7-7075-499f-bf47-cb5821271c98");
        userData.setDealWith(selectedDealWith);

        // Firebase Database Reference
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // Validation Checks
        if (TextUtils.isEmpty(userData.name)) {
            Toast.makeText(this, "Please Enter Full Name", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (TextUtils.isEmpty(userData.email)) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, "Please Enter RePassword", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password Must be at least 6 characters", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (TextUtils.isEmpty(userData.mno) || userData.mno.length() < 10) {
            Toast.makeText(this, "Please Enter a Valid 10-Digit Mobile Number", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (!password.equals(repassword)) {
            Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (selectedDealWith.equals("Select Option")) {
            Toast.makeText(this, "Please Select a Category in 'Deal With'", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(userData.email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            myRef.child("Admin").child(firebaseAuth.getCurrentUser().getUid()).setValue(userData);
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, LoginActivity.class));
                            finish();
                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(Register.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
