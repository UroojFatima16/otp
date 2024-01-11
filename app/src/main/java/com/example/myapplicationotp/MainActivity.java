package com.example.myapplicationotp;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber, editTextOTP;
    private FirebaseAuth firebaseAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextOTP = findViewById(R.id.editTextOTP);

        Button buttonSendOTP = findViewById(R.id.buttonSendOTP);
        Button buttonVerify = findViewById(R.id.buttonVerify);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    sendOTP(phoneNumber);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOTP = editTextOTP.getText().toString().trim();
                if (!enteredOTP.isEmpty()) {
                    verifyOTP(enteredOTP);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Toast.makeText(MainActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyOTP(String enteredOTP) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
