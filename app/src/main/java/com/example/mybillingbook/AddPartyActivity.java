package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mybillingbook.Models.Parties;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPartyActivity extends AppCompatActivity {

    //Following is the main View
    private RelativeLayout mainView;

    //Following are the objects on the form
    private EditText name;
    private EditText contact;
    private EditText gst;
    private EditText email;
    private EditText adress;

    private ImageView backBtn;

    private TextView addBtn;

    private ProgressDialog progressDialog;

    private String type="";
    private String id="";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activity
        Window window=AddPartyActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AddPartyActivity.this, R.color.colorPrimaryDark));

        Intent i=getIntent();
        type=i.getStringExtra("type");
        id=i.getStringExtra("id");

        initialize(); //Following is the method, to initialize all the objects on the screen

        checkForRegisteredUser();

        if (type.equals("edit"))
        {
            fetchTheCurrentDetailsForTheRecievedID(); //Following is the method to fetch the details  of the current id
            addBtn.setText("Update");
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



         /////////////////////////////The animated starting designed for the activity//////////////////////////////////////////
        //////////The functions to beperformed on starting of the activiy/////////////////////////////////////////////////////
        mainView=findViewById(R.id.mainView_party);
        //On craete the mainScroll will be gone, and alert dialog will appear with animationg.
        mainView.setVisibility(View.GONE);
        //Building a dialog
        final Dialog dialog = new Dialog(AddPartyActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();

        //Now we will create a timer, after which we can remove the dialog and vivible the main layout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Now the code will run after this timer
                dialog.cancel();
                mainView.setVisibility(View.VISIBLE);

            }
        },3000);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // A on click listener on the add button
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Name is mandatory",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(contact.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Contact is mandatory",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(gst.getText().toString()) || (gst.getText().toString().length())<15)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Invalid/Empty GST",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();

                }
                else if (TextUtils.isEmpty(email.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Email is mandatory",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(adress.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Adress is mandatory",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else
                {
                    if (type.equals("edit"))
                    {
                        updateTheParty(); //Following is the method to update the parrty
                    }
                    else
                    {
                        addTheParty(); //Following is the method, which adds the party under the users nod of parties

                    }
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    }

    private void updateTheParty() {
        progressDialog=new ProgressDialog(AddPartyActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id);


        final Map<String,Object>map=new HashMap<>();
        map.put("name",name.getText().toString());
        map.put("contact",contact.getText().toString());
        map.put("gst",gst.getText().toString());
        map.put("email",email.getText().toString());
        map.put("adress",adress.getText().toString());
        map.put("search",name.getText().toString().toLowerCase());

        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Party Updated Sucessfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                    mdToast.show();
                    progressDialog.dismiss();
                    finish();
                }
                else
                {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), task.getException().toString(), MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                    mdToast.show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void fetchTheCurrentDetailsForTheRecievedID() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Parties parties=snapshot.getValue(Parties.class);
                    name.setText(parties.getName());
                    contact.setText(parties.getContact());
                    gst.setText(parties.getGst());
                    email.setText(parties.getEmail());
                    adress.setText(parties.getAdress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForRegisteredUser() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {

                }
                else
                {
                    Dialog dialog=new Dialog(AddPartyActivity.this);
                    dialog.setContentView(R.layout.dialog_info);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    //Following are the threee obujects on the dialog
                    TextView msg=dialog.findViewById(R.id.msg_dialog);
                    TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                    TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                    msg.setText("Your business details are empty currently. Complete them now to create a new item.");

                    positiveBtn.setText("OK");
                    negativeBtn.setText("Later");

                    negativeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            finish();
                        }
                    });

                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent i=new Intent(getApplicationContext(), UserDetailsActivity.class);
                            startActivity(i);
                            dialog.cancel();
                            finish();



                        }
                    });

                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addTheParty() {
        progressDialog=new ProgressDialog(AddPartyActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String uid=reference.push().getKey();

        final Map<String,Object>map=new HashMap<>();
        map.put("uid",uid);
        map.put("name",name.getText().toString());
        map.put("contact",contact.getText().toString());
        map.put("gst",gst.getText().toString());
        map.put("email",email.getText().toString());
        map.put("adress",adress.getText().toString());
        map.put("search",name.getText().toString().toLowerCase());

        reference.child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Party Added Sucessfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                    mdToast.show();
                    progressDialog.dismiss();
                    finish();
                }
                else
                {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), task.getException().toString(), MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                    mdToast.show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void initialize() {
        name=findViewById(R.id.name_addParty);
        contact=findViewById(R.id.contactNumber_addParty);
        gst=findViewById(R.id.gst_addParty);
        email=findViewById(R.id.email_addParty);
        adress=findViewById(R.id.adress_addParty);

        addBtn=findViewById(R.id.addBtn_addParty);

        backBtn=findViewById(R.id.backBtn_addParty);
    }
}