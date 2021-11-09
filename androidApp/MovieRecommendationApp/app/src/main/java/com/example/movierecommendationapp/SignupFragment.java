package com.example.movierecommendationapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {
    boolean isUserValidated = false;
    Button validateBtn, clearBtn, registerBtn, profilePicBtn,loginTriggerFragmentBtn;
    EditText nameEditText,emailEditText,ageEditText,phoneEditText,passwordEditText,reenterpasswordEditText;
    TextView validationStatusTv;
    ImageView dpImageView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginTriggerFragmentBtn = getActivity().findViewById(R.id.loginFragTrigger);
        SharedPreferences prefs = getActivity().getSharedPreferences("LoggedIn",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        validateBtn = view.findViewById(R.id.validateBtn);
        clearBtn = view.findViewById(R.id.clearBtn);
        registerBtn = view.findViewById(R.id.registerBtn);
        profilePicBtn = view.findViewById(R.id.profilePicBtn);
        dpImageView = view.findViewById(R.id.dpImageView);
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        reenterpasswordEditText = view.findViewById(R.id.reenterpasswordEditText);


        validationStatusTv = view.findViewById(R.id.validationStatusTv);

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
                        nameEditText.setText("");
                        emailEditText.setText("");
                        ageEditText.setText("");
                        phoneEditText.setText("");
                        passwordEditText.setText("");
                        reenterpasswordEditText.setText("");
                        isUserValidated = false;
                        validationStatusTv.setText("NOT VALIDATED");
                        validationStatusTv.setTextColor(Color.RED);
                        registerBtn.setEnabled(false);
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


        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password1 = passwordEditText.getText().toString();
                String password2 = reenterpasswordEditText.getText().toString();

                if(name.length()==0 || email.length()==0 || age.length()==0 || phone.length()==0 || password1.length()==0 || password2.length()==0)
                {
                    Toast.makeText(getActivity(), "Not all fields were filled!", Toast.LENGTH_LONG).show();
                    isUserValidated = false;
                }
                else if(phone.length()!=10)
                {
                    Toast.makeText(getActivity(), "Phone no. should have 10 digits", Toast.LENGTH_LONG).show();
                    isUserValidated = false;
                }
                else if(email.indexOf('@')==-1 || email.indexOf('.')==-1 || email.indexOf('@')==email.length()-1 || email.indexOf('.')==email.length()-1)
                {
                    Toast.makeText(getActivity(), "Check Email Address Format", Toast.LENGTH_LONG).show();
                    isUserValidated = false;
                }
                else if(!password1.equals(password2))
                {
                    Toast.makeText(getActivity(), "Enter matching passwords", Toast.LENGTH_LONG).show();
                    isUserValidated = false;
                }
                else
                {
                    for(int i=0;i<name.length();i++)
                    {
                        if(!((name.charAt(i)>='a' && name.charAt(i)<='z')||(name.charAt(i)>='A' && name.charAt(i)<='Z')||(name.charAt(i)==' ')))
                        {
                            Toast.makeText(getActivity(), "Only letters allowed for name", Toast.LENGTH_LONG).show();
                            isUserValidated = false;
                            break;
                        }
                        else
                        {
                            isUserValidated = true;
                        }
                    }
                }
                if(phone.length()==10)
                {
                    try
                    {
                        Long.parseLong(phone);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getActivity(), "Phone no. should have digits only", Toast.LENGTH_LONG).show();
                        isUserValidated = false;
                    }
                }
                if(password1.length()<5)
                {
                    Toast.makeText(getActivity(), "Enter atleast 5 characters for password", Toast.LENGTH_LONG).show();
                    isUserValidated = false;
                }


                if(isUserValidated==true)
                {
                    validationStatusTv.setText("VALIDATED");
                    validationStatusTv.setTextColor(getResources().getColor(R.color.green));
                    registerBtn.setEnabled(true);
                    Toast.makeText(getActivity(),"All data validated, proceed to register",Toast.LENGTH_LONG).show();
                }
                else
                {
                    validationStatusTv.setText("NOT VALIDATED");
                    validationStatusTv.setTextColor(Color.RED);
                    registerBtn.setEnabled(false);
                }
            }
        });


        //Register Button Listener
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUserValidated==true)
                {
                    String name = nameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String age = ageEditText.getText().toString();
                    String phno = phoneEditText.getText().toString();
                    DocumentReference doc = db.collection("users").document(email);
                    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getData()==null)
                            {
                                Toast.makeText(getActivity(),"Registering User...Proceed to Login",Toast.LENGTH_SHORT).show();

                                SharedPreferences preferences = requireContext().getSharedPreferences("Register", Context.MODE_PRIVATE);


                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("Name",name);
                                editor.putString("Email",email);
                                editor.putString("Password",password);
                                editor.putString("Age",age);
                                editor.putString("Phno",phno);
                                editor.commit();

                                //Save the information on Firebase
                                Map<String,Object> newUser = new HashMap<>();
                                newUser.put("name",name);
                                newUser.put("phno",phno);
                                newUser.put("email",email);
                                newUser.put("password",password);
                                newUser.put("age",Integer.parseInt(age));

                                db.collection("users").document(email).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                         Toast.makeText(getActivity(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                         loginTriggerFragmentBtn.performClick();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(getActivity(), "Error Saving to DB", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "You are already a registered user", Toast.LENGTH_SHORT).show();
                                loginTriggerFragmentBtn.performClick();
                                return;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                        }
                    });





                }
                else
                {
                    Toast.makeText(getActivity(),"Validate first before registering",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Profile Pic Button handler
        profilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = { "Take Photo","Cancel" };
                AlertDialog.Builder dpAlertDialog = new AlertDialog.Builder(getActivity());
                dpAlertDialog.setTitle("Choose your profile picture");

                dpAlertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                dpAlertDialog.setCancelable(false);
                dpAlertDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == getActivity().RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        dpImageView.setImageBitmap(selectedImage);
                    }
                    break;
            }
        }
    }
}