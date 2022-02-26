package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybillingbook.Adapters.BillItemOnScreenAdapter;
import com.example.mybillingbook.Models.BillItems;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.Parties;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddQuotationActivity extends AppCompatActivity {

    //Following are th objects on the screen
    private ImageView backBtn;
    private TextView clearBtn;
    private TextView selectParty;
    private TextView totalAmount;
    private FloatingActionButton addItem;

    //The object to save the current bill
    private Bills billsToGenerate;

    private String PARTY_ID="";
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private BillItemOnScreenAdapter adapter;
    private List<BillItems>list;

    private String CURRENTQUOTATION_ID;

    private TextView clearCurrentBill;

    private Double TOTAL_AMOUNT=0.00;


    private Button generateQuotation;

    //Following is the permission, to generate the Quotation
    private boolean PERMISSION=false;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quotation);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=AddQuotationActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AddQuotationActivity.this, R.color.colorPrimaryDark));

        intialize(); //Following is the method, that intializes all the objects with their respective ids

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //First, we will check, if the user's current quotation is already existing
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    //Here, it means that user has already selected a party, and has a current quotation
                    Bills bills=snapshot.getValue(Bills.class);

                    //Now, we need to fetch the partyID and then get the party name
                    PARTY_ID=bills.getBillto();

                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bills.getBillto());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                Parties parties=snapshot.getValue(Parties.class);
                                selectParty.setText(parties.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else
                {
                    CURRENTQUOTATION_ID="";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //////////////////////////////////////////////////////////////////////////////////////////
        //Getting the permission to generate
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    billsToGenerate=snapshot.getValue(Bills.class);

                    DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(billsToGenerate.getBillid());
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                PERMISSION=true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////
        //Setting a recycler view/////////////////////////////////////////////////////////////////
        recyclerView=findViewById(R.id.recycler_quotations);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list=new ArrayList<>();
        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Bills bills=snapshot.getValue(Bills.class);
                    //NOw, following is the method, to fetch the items for the current billing
                    fetchAllTheCurrentQuotationItems(bills.getBillid().toString()); //Following is the method, to fetch all the items in the current quotation
                    CURRENTQUOTATION_ID=bills.getBillid();





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////
        //Setting a on click listener for the select party button///////////////////////////////////
        selectParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PARTY_ID=="")
                {
                    Intent i=new Intent(getApplicationContext(),PartySelectionActivity.class);
                    i.putExtra("type","quotation");
                    startActivity(i);
                }
                else
                {


                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Clear the current quotation for changing the party",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ItemSelectionActivity.class);
                i.putExtra("type","quotation");
                startActivity(i);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///A on click listener for the clear button that will clear all the data for the current billing o sthe user
        clearCurrentBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(AddQuotationActivity.this);
                dialog.setContentView(R.layout.dialog_info);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                //Following are the threee obujects on the dialog
                TextView msg=dialog.findViewById(R.id.msg_dialog);
                TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                msg.setText("Are you sure to clear your current draft quotation. Items can not be restored after clearing.");

                positiveBtn.setText("OK");
                negativeBtn.setText("NO");

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearCurrentBillIfExist(); //Following is the method, that will clear the cureent bill of the user if the nod for cureent exists
                        dialog.cancel();
                    }
                });

                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////



        //////////////////////////////////////////////////////////////////////////////////////////////
        ////The onclick listener for the generate quotation button
        generateQuotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PERMISSION)
                {
                    generateUsersBill();
                }
                else
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Empty quotation can not be created",MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////


    }

    private void fetchAllTheCurrentQuotationItems(String id) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list!=null)
                {
                    list.clear();
                    TOTAL_AMOUNT=0.00;
                }

                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        BillItems billItems=dataSnapshot.getValue(BillItems.class);
                        list.add(billItems);


                        //Now,, we need to calculate the total along with the price
                        double uP=Double.parseDouble(billItems.getUnitPrice());
                        int qty=Integer.parseInt(billItems.getQty());

                        double total=0.00;
                        if ((billItems.getTaxPercent()).equals("inclusive"))
                        {
                            //The tax is inclusive
                            total=uP*qty;
                        }
                        else
                        {
                            double taxPercent=Double.parseDouble(billItems.getTaxPercent());
                            total=(((taxPercent/100)*uP)*qty+(uP*qty));
                        }
                        TOTAL_AMOUNT=TOTAL_AMOUNT+total;

                    }

                    totalAmount.setText(TOTAL_AMOUNT+"/-");

                    Activity activity=AddQuotationActivity.this;

                    Collections.reverse(list);
                    adapter=new BillItemOnScreenAdapter(list,getApplicationContext(),activity,id,"quotation");
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();






                    {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearCurrentBillIfExist() {

        boolean nodChecker=false;//Following will check the nod if exist,
        // following will check the item nod, if exixt for that id
        boolean itemnodChecker=false;

        progressDialog=new ProgressDialog(AddQuotationActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(billsToGenerate.getBillid());
                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Draft Quotation Cleared",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
                                mdToast.show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                                mdToast.show();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });


    }

    private void generateUsersBill() {
        progressDialog=new ProgressDialog(AddQuotationActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        if (PERMISSION)
        {
            //The following is the code, togenerate the bill
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Quotations").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final Map<String,Object> map=new HashMap<>();
            map.put("billFrom",billsToGenerate.getBillFrom());
            map.put("billid",billsToGenerate.getBillid());
            map.put("billto",billsToGenerate.getBillto());
            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = new Date();
            String thisDate = currentDate.format(todayDate);
            map.put("date",thisDate);
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            map.put("time",currentTime);

            reference.child(billsToGenerate.getBillid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Quotation Generated",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                    finish();

                                    Intent i=new Intent(getApplicationContext(),BillViewerActivity.class);
                                    i.putExtra("ID",billsToGenerate.getBillid());
                                    i.putExtra("type","quotation");
                                    startActivity(i);
                                }
                                else {
                                    progressDialog.dismiss();
                                    MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                    mdToast.show();
                                }
                            }
                        });
                    }
                    else
                    {
                        progressDialog.dismiss();
                        MDToast mdToast=MDToast.makeText(getApplicationContext(),"Unable to create",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                        mdToast.show();
                    }
                }
            });


        }
        else
        {
            progressDialog.dismiss();
            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Empty Bill can not be generated",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
            mdToast.show();
        }




    }


    private void intialize() {
        backBtn=findViewById(R.id.backBtn_quotation);
        selectParty=findViewById(R.id.selectParty_quotation);
        addItem=findViewById(R.id.addItemBtn_quotation);
        clearCurrentBill=findViewById(R.id.clear_currentQuotation);
        totalAmount=findViewById(R.id.totalAmount_quotation);
        generateQuotation=findViewById(R.id.generateBtn_quotation);

    }
}