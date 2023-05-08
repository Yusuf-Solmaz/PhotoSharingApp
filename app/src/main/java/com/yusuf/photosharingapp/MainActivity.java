package com.yusuf.photosharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yusuf.photosharingapp.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    String nameRegex = "^[a-zA-ZğüşıöçĞÜŞİÖÇ]+$";

    ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    String email;
    String password;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        binding.registerButton.setVisibility(View.GONE);
        binding.textView3.setVisibility(View.GONE);
        binding.nameText.setVisibility(View.GONE);

        FirebaseUser user = auth.getCurrentUser();
        if (user !=null){
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }

    }


    public void signIn(View view) {

        email = binding.emailText.getText().toString();
        password = binding.passwordText.getText().toString();

       if (emailAndPasswordControl(email,password)){
           auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
               @Override
               public void onSuccess(AuthResult authResult) {
                   Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                   startActivity(intent);
                   finish();
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
               }
           });
       }

    }

    public void signUp(View view) {

        name = binding.nameText.getText().toString();
        email = binding.emailText.getText().toString();
        password = binding.passwordText.getText().toString();

        if (emailAndPasswordControl(email,password) && nameControl(name)){
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Map<String, Object> user = new HashMap<>();

                    user.put("email",email);
                    user.put("username",name);
                    user.put("password",password);

                    firestore.collection("users")
                            .document(auth.getCurrentUser().getUid())
                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMessage = "There is an error.";
                    if (e instanceof FirebaseAuthException) {
                        FirebaseAuthException authException = (FirebaseAuthException) e;
                        switch (authException.getErrorCode()) {
                            case "ERROR_INVALID_EMAIL":
                                errorMessage = "Invalid e-mail.";
                                break;
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                errorMessage = "This e-mail already in use.";
                                break;
                            case "ERROR_WEAK_PASSWORD":
                                errorMessage = "Password is weak";
                                break;

                        }
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void visibleSignUp(View view) {
        binding.nameText.setVisibility(View.VISIBLE);
        binding.textView3.setVisibility(View.VISIBLE);
        binding.textView2.setVisibility(View.GONE);
        binding.singInButton.setVisibility(View.GONE);
        binding.registerButton.setVisibility(View.VISIBLE);
        binding.textView.setText("Have an account?");

    }

    public void visibleSignIn(View view) {
        binding.nameText.setVisibility(View.GONE);
        binding.textView2.setVisibility(View.VISIBLE);
        binding.textView3.setVisibility(View.GONE);
        binding.singInButton.setVisibility(View.VISIBLE);
        binding.registerButton.setVisibility(View.GONE);
        binding.textView.setText("Don't have an account?");
    }

    public boolean emailAndPasswordControl(String email, String password) {
        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "E-mail or Password can not be empty.", Toast.LENGTH_LONG).show();
            return false;
        } else if (!email.matches(emailRegex)) {
            Toast.makeText(MainActivity.this, "Please write your email in the correct format.", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            return true;
        }
    }

    public boolean nameControl(String name){
        if (name.matches(nameRegex)){
            return true;
        }
        else {
            Toast.makeText(MainActivity.this,"Name can not be empty!",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}