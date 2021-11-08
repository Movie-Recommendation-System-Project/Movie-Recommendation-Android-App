package com.example.movierecommendationapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movierecommendationapp.cardview.CardMovieDetails;
import com.example.movierecommendationapp.cardview.CardViewActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    Button clearBtn, login;
    EditText emailEditText,passwordEditText;
    FirebaseFirestore db;

    SharedPreferences sp;

/*    public static final String filename="login";
    public static final String emailid="email";
    public static final String Password="password";*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        db = FirebaseFirestore.getInstance();

        clearBtn = view.findViewById(R.id.clearBtn);
        login = view.findViewById(R.id.loginBtn);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);



        sp = getActivity().getSharedPreferences("LoggedIn",Context.MODE_PRIVATE);
        if(!sp.getString("loggedin","null").equals("null")){
            Intent intent = new Intent(getContext(), CardViewActivity.class);
            startActivity(intent);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if(email.length()==0 || password.length()==0)
                {
                    Toast.makeText(getActivity(), "Fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                DocumentReference doc = db.collection("users").document(email);
                doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getData()==null)
                        {
                            Toast.makeText(getActivity(), "You are not registered", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            if(documentSnapshot.getString("password").equals(password))
                            {
                                Toast.makeText(getActivity(), "Login Success...", Toast.LENGTH_SHORT).show();
                                SharedPreferences prefs = getActivity().getSharedPreferences("LoggedIn",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("loggedin",email);
                                editor.commit();
                                /*sp.edit().putBoolean("loggedin",true).apply();*/
                                Intent intent = new Intent(getContext(), CardViewActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "Wrong Password", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(), "Error Saving to DB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });




//                if(emailEditText.getText().toString().equals(registeredEmail)){
//                    if(passwordEditText.getText().toString().equals(registeredPassword)){
//                        Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getContext(),ProfileActivity.class);
//                        startActivity(intent);
//
//                    }
//                    else {
//                        Toast.makeText(getActivity(),"Incorrect Password",Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//                else{
//                    Toast.makeText(getActivity(),"Email not registered",Toast.LENGTH_SHORT).show();
//
//                }




            }
        });




        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Clear Fields");
                alertDialog.setMessage("Are you sure to clear?");
                alertDialog.setIcon(R.drawable.ic_baseline_warning_24);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emailEditText.setText("");
                        passwordEditText.setText("");
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });



        return view;
    }


}