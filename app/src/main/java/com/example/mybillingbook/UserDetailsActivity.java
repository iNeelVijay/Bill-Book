package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.VirtualLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybillingbook.Models.MyDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserDetailsActivity extends AppCompatActivity {

    //The object for the main View
    private RelativeLayout mainView;

    //The obhect for the loading dialog
    private ProgressDialog progressDialog;

    private ImageView backBtn;

    //Following are the objects on the user detail form
    private ImageView logo;
    private EditText buisinessName;
    private EditText contactNumber;
    private EditText gst;
    private EditText email;
    private EditText adress;
    private EditText city;
    private EditText pin;
    private EditText state;

    private TextView updateBtn;

    //Following is teh variable to store the gst as the gst is not a mandatory field
    private String GST="";

    //Objects used in image picking function
    private String downloadImageUrl = "";
    private Uri ImageUri;
    private int RequestCode = 438;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=UserDetailsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(UserDetailsActivity.this, R.color.colorPrimaryDark));


        initialize(); //Following is the method to intialize all the objects with their respective ids


         /////////////////////////////The animated starting designed for the activity//////////////////////////////////////////
        //////////The functions to beperformed on starting of the activiy/////////////////////////////////////////////////////
        mainView=findViewById(R.id.mainView_ud);
        //On craete the mainScroll will be gone, and alert dialog will appear with animationg.
        mainView.setVisibility(View.GONE);
        //Building a dialog
        final Dialog dialog = new Dialog(UserDetailsActivity.this);
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


        fetchTheCurrentUserDetails(); //Following is the method that will fetch the current details of the user if it already exists


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////The function to start the gallery to pick ak logo///////////////////////////////////////////////////////
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////Following is the code for the update button///////////////////////////////////////////////////////////////////
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(buisinessName.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Buisiness name is mandatory",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(contactNumber.getText().toString()) || (contactNumber.getText().toString()).length()<10)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Empty or invalid contact number",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(email.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Empty email",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(adress.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Adress is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(city.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"City is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(pin.getText().toString())  || (pin.getText().toString()).length()<6)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Invialid or empty pin code",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();

                }
                else if (TextUtils.isEmpty(state.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"State is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else
                {
                    if (TextUtils.isEmpty(gst.getText().toString()))
                    {
                        //MAens user left the gst empty
                        GST="null";
                        goForUpdate(); //Following function will now decide, if to upload with/without image and hence call te respective function
                    }
                    else
                    {
                        //Means user gave some input in gst field, so we need to check
                        if ((gst.getText().toString()).length()<15)
                        {
                            //Invalid gst given
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Invalid GST number",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                            mdToast.show();
                        }
                        else
                        {
                            GST=gst.getText().toString();
                            goForUpdate(); //Following function will now decide, if to upload with/without image and hence call te respective function

                        }
                    }

                }










            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    }

    private void fetchTheCurrentUserDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    MyDetails myDetails=snapshot.getValue(MyDetails.class);

                    if (!(myDetails.getImage()).equals("null"))
                    {
                        Picasso.get().load(myDetails.getImage()).into(logo);
                        buisinessName.setText(myDetails.getName());
                        contactNumber.setText(myDetails.getContact());
                        if (!(myDetails.getGst()).equals("null")) {
                            gst.setText(myDetails.getGst());
                        }
                        email.setText(myDetails.getEmail());
                        adress.setText(myDetails.getAdress());
                        city.setText(myDetails.getCity());
                        pin.setText(myDetails.getPin());
                        state.setText(myDetails.getState());


                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void goForUpdate() {
        //Here,we have to decide wwhther the user has selected an image or not
        if (ImageUri==null)
        {
            //Here, the user has not selected the image, so we have to upload the data without the image
            progressDialog=new ProgressDialog(UserDetailsActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

           DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

           final Map<String,Object> map=new HashMap<>();
           map.put("image","null");
           map.put("name",buisinessName.getText().toString());
           map.put("contact",contactNumber.getText().toString());
           map.put("email",email.getText().toString());
           map.put("gst",GST);
           map.put("adress",adress.getText().toString());
           map.put("city",city.getText().toString());
           map.put("pin",pin.getText().toString());
           map.put("state",state.getText().toString());

           ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful())
                   {
                       //Following is the code if the task is sucessfully completed
                       progressDialog.dismiss();
                       MDToast mdToast=MDToast.makeText(getApplicationContext(),"Details updated sucessfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                       mdToast.show();
                       finish();
                   }
                   else
                   {
                       //Following is teh code if some error occurs
                       progressDialog.dismiss();
                       MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                       mdToast.show();

                   }
               }
           });




        }
        else
        {
            //Here, the user has uploaded that image, and we have to upload the image and then upload the data
            progressDialog=new ProgressDialog(UserDetailsActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            //First we need to upload the image to the storage, and then get the link

            final String productRandomKey = Calendar.getInstance().getTime().toString();


            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Logo Images").child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

            final UploadTask uploadTask = filePath.putFile(ImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    String message = e.toString();
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Failed to update",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                        {
                            if (!task.isSuccessful())
                            {
                                throw task.getException();

                            }

                            downloadImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                downloadImageUrl = task.getResult().toString();

                                final Map<String,Object> map=new HashMap<>();
                                map.put("image",downloadImageUrl);
                                map.put("name",buisinessName.getText().toString());
                                map.put("contact",contactNumber.getText().toString());
                                map.put("email",email.getText().toString());
                                map.put("gst",GST);
                                map.put("adress",adress.getText().toString());
                                map.put("city",city.getText().toString());
                                map.put("pin",pin.getText().toString());
                                map.put("state",state.getText().toString());

                                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            progressDialog.dismiss();
                                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Details updated sucessfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                                            mdToast.show();
                                            finish();
                                        }
                                        else
                                        {
                                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Failed to update",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                            mdToast.show();
                                        }
                                    }
                                });



                            }
                            else
                            {
                                progressDialog.dismiss();
                                MDToast mdToast = MDToast.makeText(getApplicationContext(), task.getException().toString(), MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                                mdToast.show();

                            }
                        }
                    });

                }
            });












        }
    }

    private void initialize() {

        logo=findViewById(R.id.logo_ud);
        buisinessName=findViewById(R.id.buisinessname_ud);
        contactNumber=findViewById(R.id.contactNumber_ud);
        gst=findViewById(R.id.gst_ud);
        email=findViewById(R.id.email_ud);
        adress=findViewById(R.id.adress_ud);
        city=findViewById(R.id.city_ud);
        pin=findViewById(R.id.pin_ud);
        state=findViewById(R.id.state_ud);

        updateBtn=findViewById(R.id.updateBtn_ud);

        backBtn=findViewById(R.id.backBtn_ud);
    }
    //Following two are the methods used for picking a image from the gallery
    private void pickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RequestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data.getData() != null) {

            ImageUri = data.getData();

            Picasso.get().load(ImageUri).placeholder(R.drawable.add_ic).into(logo);


        } else {
            MDToast mdToast = MDToast.makeText(getApplicationContext(), "Image not selected", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
            mdToast.show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}