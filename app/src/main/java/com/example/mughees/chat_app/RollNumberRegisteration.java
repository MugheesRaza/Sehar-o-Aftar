package com.example.mughees.chat_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RollNumberRegisteration extends AppCompatActivity {
    NiceSpinner year, programe;
    EditText rollno;
    EditText password ,confrimpassword ,usermail,user_name,phonenumber;
    FloatingActionButton floatingActionButton;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    Button btnlogin;
    connectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_number_registeration);
        cd = new connectionDetector(RollNumberRegisteration.this);
        if (cd.isConnected()){

        }
        else{
            android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(RollNumberRegisteration.this);
            a_builder.setMessage("You need to have Mobile Data or wifi to access this.Press Ok to Exit  !!!")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RollNumberRegisteration.this.finish();

                        }
                    });
            android.app.AlertDialog alert = a_builder.create();
            alert.setTitle("Alert !!!");
            alert.show();
        }
        mAuth = FirebaseAuth.getInstance();
        btnlogin = findViewById(R.id.btn_login);
        password = findViewById(R.id.password);
        confrimpassword =findViewById(R.id.confirmpassword);
        usermail = findViewById(R.id.email);
        user_name = findViewById(R.id.username);
        phonenumber = findViewById(R.id.userphone);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Showdata();
            }
        });
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Showdata();
//            }
//        });

    }

    private void Showdata() {
        if (password.getText().toString().equals("") || password.getText().toString().length() <= 5) {
            Toast.makeText(getApplicationContext(), "Please enter the greater than 6 digit password", Toast.LENGTH_SHORT).show();
        }
        else if (phonenumber.getText().toString().length()>11||phonenumber.getText().toString().length()<11){
            Toast.makeText(getApplicationContext(),"Please enter the valid phone number",Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValid(usermail.getText().toString())){
            usermail.setError("Invalid Email");
            usermail.requestFocus();
            return;
        }
        else if (!password.getText().toString().equals(confrimpassword.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please confirm your password",Toast.LENGTH_SHORT).show();
        }
        else {
            go_to_Main();
        }
    }
    boolean isEmailValid(CharSequence email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private void go_to_Main() {
        if (password.getText().toString().equals("")||confrimpassword.getText().toString().equals("")||user_name.getText().toString().equals("")||usermail.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Please enter all fields",Toast.LENGTH_SHORT).show();
        }
        else {
            final ProgressDialog pd = new ProgressDialog(RollNumberRegisteration.this);
            pd.setCancelable(false);
            pd.setMessage("Loading...");
            pd.show();

            mAuth.fetchProvidersForEmail(usermail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                    boolean check = !task.getResult().getProviders().isEmpty();
                    if(!check){
                        String passwrod  = password.getText().toString();
                        mAuth.createUserWithEmailAndPassword(usermail.getText().toString(),passwrod).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(),"Please go to this email and verify ",Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(),"Unable to send the email verification code ",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
//                                else {
//                                    pd.dismiss();
//
//                                    FirebaseAuth.getInstance().getCurrentUser()
//                                            .reload()
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                                    if (user.isEmailVerified()){
//
//                                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                                                        assert firebaseUser != null;
//                                                        String userID = firebaseUser.getUid();
//                                                        reference = FirebaseDatabase.getInstance().getReference("User").child(userID);
//                                                        HashMap<String ,String> hashMap = new HashMap<>();
//                                                        hashMap.put("id",userID);
//                                                        hashMap.put("User_name",name);
//                                                        hashMap.put("imageUrl","default");
//                                                        hashMap.put("status","Offline");
//                                                        hashMap.put("Phone Number",phonenumber);
//                                                        hashMap.put("Phone status","private");
//                                                        hashMap.put("student status",stusent_status);
//                                                        hashMap.put("search",name.toLowerCase());
//                                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()){
//                                                                    Intent i  = new Intent(getApplicationContext(),MainActivity.class);
//                                                                    i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK|i.FLAG_ACTIVITY_NEW_TASK);
//                                                                    startActivity(i);
//                                                                    finish();
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                    else
//                                                        Toast.makeText(getApplicationContext(),"Please go to the email and verify this email ",Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
                            }
                        });
                    }
                    else {
                        pd.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null){
                            Toast.makeText(getApplicationContext(),"You account is already approved please go to the login page",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            FirebaseAuth.getInstance().getCurrentUser()
                                    .reload()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            if (user.isEmailVerified()){
                                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                assert firebaseUser != null;
                                                String userID = firebaseUser.getUid();
                                                reference = FirebaseDatabase.getInstance().getReference("User").child(userID);
                                                HashMap<String ,String> hashMap = new HashMap<>();
                                                hashMap.put("id",userID);
                                                hashMap.put("User_name",user_name.getText().toString());
                                                hashMap.put("imageUrl","default");
                                                hashMap.put("status","Offline");
                                                hashMap.put("Phone Number",phonenumber.getText().toString());
                                                hashMap.put("Phone status","private");
                                                hashMap.put("search",user_name.getText().toString().toLowerCase());
                                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Intent i  = new Intent(getApplicationContext(),MainActivity.class);
                                                            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK|i.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                            else
                                                Toast.makeText(getApplicationContext(),"Please go to this email and verify. ",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    }
                }
            });


        }
    }


}
