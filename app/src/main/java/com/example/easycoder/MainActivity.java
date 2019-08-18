package com.example.easycoder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Animation from_left, from_right, imgAnim;
    Boolean hidden = true;

    EditText email, password;
    Button login, signUp;
    ImageButton eye;
    ImageView main_img, logo;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //all elements are registered here
        main_img = findViewById(R.id.mainImg);
        logo = findViewById(R.id.logo);
        login = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.btn_signup);
        eye = findViewById(R.id.eye);
        loadAllAnims();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
/////////////////////////////////////////////////////////////////////////////////////////////////////////

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
//          then user is already registered so start new activity
            finish();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hidden){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hidden = false;
                }
                else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hidden = true;
                }
            }
        });


    }

    public void loadAllAnims(){
        //all animations are registered here
        from_left = AnimationUtils.loadAnimation(this, R.anim.from_left);
        from_right = AnimationUtils.loadAnimation(this, R.anim.from_right);
        imgAnim = AnimationUtils.loadAnimation(this, R.anim.img_anim);

        main_img.startAnimation(imgAnim);
        logo.startAnimation(imgAnim);
        login.startAnimation(from_left);
        signUp.startAnimation(from_right);
    }

    public void newUser(){
        String email_txt = email.getText().toString().trim();
        String password_txt = password.getText().toString().trim();

//        check if the email received is empty or not
        if (TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt) ) {
            Toast.makeText(this, "ENTER VALID EMAIL OR PASSWORD", Toast.LENGTH_SHORT).show();
            return;
        }

        //if code reaches till here then email and password is valid, so lets create account
        progressDialog.setMessage("Registering New User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email_txt, password_txt)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    finish();
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed, Please Try Again", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });


    }

    public void userLogin(){
        String email_txt = email.getText().toString().trim();
        String password_txt = password.getText().toString().trim();

//        check if the email received is empty or not
        if (TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt) ) {
            Toast.makeText(this, "ENTER VALID EMAIL OR PASSWORD", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Logging In Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email_txt, password_txt)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()){
                                finish();
                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

    }
}
