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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mybillingbook.Models.Items;
import com.example.mybillingbook.Models.MyDetails;
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
import java.util.Random;

public class AddItemActivity extends AppCompatActivity {

    //Following are the objects on the screen
    private ImageView backBtn;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemunit;
    private RadioGroup taxRadGrp;
    private EditText taxPercentage;
    private TextView saveBtn;

    //Following are the objects for the radioBtn
    private RadioButton inclusiveTax;
    private RadioButton exculsivetax;

    //The mainView
    private RelativeLayout mainView;

    //For intent catching
    private String type="";
    private String id="";

    //The object for proress dialog
    private ProgressDialog progressDialog;

    private String ITEMID_TAG=""; //Following is the variable to store the tag before item id

    //Following is the variable to store the respective radio b uttto checkedid
    int radID=-1;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activity
        Window window=AddItemActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AddItemActivity.this, R.color.colorPrimaryDark));

        initialize(); //Following is the method, that initializes the objects with  the respective ids

        Intent i=getIntent();
        id=i.getStringExtra("id");
        type=i.getStringExtra("type");

        /////////////////////////////The animated starting designed for the activity//////////////////////////////////////////
        //////////The functions to beperformed on starting of the activiy/////////////////////////////////////////////////////
        mainView=findViewById(R.id.mainview_addItem);
        //On craete the mainScroll will be gone, and alert dialog will appear with animationg.
        mainView.setVisibility(View.GONE);
        //Building a dialog
        final Dialog dialog = new Dialog(AddItemActivity.this);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        taxRadGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radID=taxRadGrp.getCheckedRadioButtonId();

                if (radID==R.id.inclusiveradBtn_addItem)
                {
                    taxPercentage.setEnabled(false);
                    taxPercentage.setText("0.00");
                }
                if (radID==R.id.exclusiveradBtn_addItem)
                {
                    taxPercentage.setEnabled(true);
                    taxPercentage.setText("");
                }


            }
        });

        checkForRegisteredUser();


        //Following is the method, that fetches the tag for item ID
        fetchATag();

        //Now, the code for the item
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Now we have to code here, for all the restrictions
                if (TextUtils.isEmpty(itemName.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Item name is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(itemPrice.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Item price is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(itemunit.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Item unit is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(taxPercentage.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Tax percentage is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (radID==-1)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Select a tax percent option",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else
                {
                    if (type.equals("edit"))
                    {
                        updateTheItemDetails();//Following is teh method that updates the current item detail
                    }
                    else
                    {
                        addAItem(); //Followng is the method, that will add the item in the database
                    }

                }
            }
        });

        if (type.equals("edit"))
        {
            saveBtn.setText("UPDATE");
            fetchTheCurrentDetails(); //following is the method that will fetch the current details of the item taken to edit
        }
        else
        {

        }




    }

    private void updateTheItemDetails() {

        progressDialog=new ProgressDialog(AddItemActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getUid()).child(id);

        //Generating a uid for the item, to use as key nod
        String uid=ref.push().getKey();

        final Map<String,Object>map=new HashMap<>();

        map.put("price",itemPrice.getText().toString());
        map.put("unit",itemunit.getText().toString());
        map.put("name",itemName.getText().toString());

        if (radID==R.id.inclusiveradBtn_addItem)
        {
            map.put("tax","inclusive");
        }
        if (radID==R.id.exclusiveradBtn_addItem)
        {
            map.put("tax",taxPercentage.getText().toString());
        }

        ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Item Updated Sucessfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                    mdToast.show();
                    finish();
                }
                else
                {
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Failed to update item",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });





    }

    private void fetchTheCurrentDetails() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    Items items=snapshot.getValue(Items.class);

                    //Now, setting the data
                    itemName.setText(items.getName());
                    itemPrice.setText(items.getPrice());
                    itemunit.setText(items.getUnit());

                    if ((items.getTax()).equals("inclusive"))
                    {
                        radID=R.id.inclusiveradBtn_addItem;
                        inclusiveTax.setChecked(true);
                        taxPercentage.setEnabled(false);
                        taxPercentage.setText("0.00");
                    }
                    else
                    {
                        exculsivetax.setChecked(true);
                        taxPercentage.setText(items.getTax());
                        radID=R.id.exclusiveradBtn_addItem;
                    }
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
                    Dialog dialog=new Dialog(AddItemActivity.this);
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

    private void addAItem() {

        progressDialog=new ProgressDialog(AddItemActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getUid());

        //Generating a uid for the item, to use as key nod
        String uid=ref.push().getKey();

        final Map<String,Object>map=new HashMap<>();
        map.put("uid",uid);
        String name=itemName.getText().toString();
        String part=(name.substring(0,2)).toUpperCase();
        //Generating a random number
        Random random=new Random();
        String rand=String.format("%04d", random.nextInt(10000));
        map.put("itemid",ITEMID_TAG+"-"+part+"-"+rand);
        map.put("price",itemPrice.getText().toString());
        map.put("unit",itemunit.getText().toString());
        map.put("name",itemName.getText().toString());

        if (radID==R.id.inclusiveradBtn_addItem)
        {
            map.put("tax","inclusive");
        }
        if (radID==R.id.exclusiveradBtn_addItem)
        {
            map.put("tax",taxPercentage.getText().toString());
        }

        ref.child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Item Added Sucessfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                    mdToast.show();
                    finish();
                }
                else
                {
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Failed to create a item",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });




    }

    private void fetchATag() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    MyDetails myDetails=snapshot.getValue(MyDetails.class);
                    String name=myDetails.getName();

                    ITEMID_TAG=(name.substring(0,2)).toUpperCase().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initialize() {
        backBtn=findViewById(R.id.backBtn_addItem);
       itemName=findViewById(R.id.itemName_addItem);
       itemPrice=findViewById(R.id.itemPrice_addItem);
       itemunit=findViewById(R.id.itemUnit_addItem);
       taxRadGrp=findViewById(R.id.radGrp_addItem);
       taxPercentage=findViewById(R.id.taxPercent_addItem);
       saveBtn=findViewById(R.id.saveBtn_addItem);

       inclusiveTax=findViewById(R.id.inclusiveradBtn_addItem);
       exculsivetax=findViewById(R.id.exclusiveradBtn_addItem);


    }
}