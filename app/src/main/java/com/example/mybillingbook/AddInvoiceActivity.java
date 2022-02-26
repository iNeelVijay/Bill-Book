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
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybillingbook.Adapters.AddPartyAdapter;
import com.example.mybillingbook.Adapters.BillItemOnScreenAdapter;
import com.example.mybillingbook.Adapters.ItemsAdapter;
import com.example.mybillingbook.Models.BillItems;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.Parties;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class AddInvoiceActivity extends AppCompatActivity {
    //Following are the objects on the screen
    private ImageView backBtn;
    private TextView addParty;
    private RecyclerView recyclerView;
    private TextView totalTxtView;
    private TextView totalTaxTxtView;
    private FloatingActionButton addItem;
    private Button generateBill;

    //The clear button text view
    private TextView clearCurrentBill;

    //Following is the variable to store, the party id
    private String PARTY_ID="";
    private String BILL_ID="";

    private boolean PERMISSION=false;
    private Bills billsToGenerate;

    //The variables for total price and total TAx
    private Double TOTAL_AMOUNT=0.00;
    private Double TOTAL_TAX=0.00;

    //Following is the object for the progress dialog
    private ProgressDialog progressDialog;



    //FOllowing are the objects for the on screen billing itees adapter
    private List<BillItems> list2;
    private BillItemOnScreenAdapter adapter2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invoice);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=AddInvoiceActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AddInvoiceActivity.this, R.color.colorPrimaryDark));

        intialize(); //Following is the methid that assigns all the ids with their respective ids



        //////////////////////////////////////////////////////////////////////////////////////////////
        //First, we have to set on click loistener on the addd party button to show a bottom sheet dialog, to add a party
        addParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PARTY_ID.equals(""))
                {
                    Intent i=new Intent(getApplicationContext(),PartySelectionActivity.class);
                    i.putExtra("type","bill");
                    startActivity(i);
                }
                else
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Clear the current bill to change the party",MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                    mdToast.show();
                }


            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////

        //Here, we need to fetch the permission first, to generte the bill, along with that the data too
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    billsToGenerate=snapshot.getValue(Bills.class);
                    DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(billsToGenerate.getBillid());
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                PERMISSION=true; //Setting the permission to generate the bill as true
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


        ////////////////////////////////////////////////////////////////////////////////////////////////
        //Following i sthe code to generate a billf from the user
        generateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateUsersBill(); //Following is the method, that generates the user's bill
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////





        /////////////////////////////////////////////////////////////////////////////////////////////////
        //Setting a on click istener on teh back btn/////////////////////////////////////////////////////
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///A on click listener for the clear button that will clear all the data for the current billing o sthe user
        clearCurrentBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(AddInvoiceActivity.this);
                dialog.setContentView(R.layout.dialog_info);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                //Following are the threee obujects on the dialog
                TextView msg=dialog.findViewById(R.id.msg_dialog);
                TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                msg.setText("Are you sure to clear your current draft bill. Items can not be restored after clearing.");

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



        //We need to fetch the selected party, as if present in current billing for teh user
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Bills bills=snapshot.getValue(Bills.class);

                    BILL_ID=bills.getBillid();

                    //Fetching the party name
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(bills.getBillto());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                Parties parties=snapshot.getValue(Parties.class);
                                addParty.setText(parties.getName().toString());

                                PARTY_ID=parties.getUid();

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

        ////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code for the add items button
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ItemSelectionActivity.class);
                i.putExtra("type","bill");
                startActivity(i);
            }
        });
        ///////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code, to fetch all the items for the current billing///////////////////

        //Setting the recycelr view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list2=new ArrayList<>();
        //First we need the current bill id for that items to fetch

        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Bills bills=snapshot.getValue(Bills.class);
                    //NOw, following is the method, to fetch the items for the current billing
                    fetchAllTheCurrentBillingItems(bills.getBillid().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////



    }

    private void generateUsersBill() {
        progressDialog=new ProgressDialog(AddInvoiceActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        if (PERMISSION)
        {
           //The following is the code, togenerate the bill
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Bills").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final Map<String,Object>map=new HashMap<>();
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
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Bill Generated",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                    finish();

                                    Intent i=new Intent(getApplicationContext(),BillViewerActivity.class);
                                    i.putExtra("ID",billsToGenerate.getBillid());
                                    i.putExtra("type","bill");
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

    private void clearCurrentBillIfExist() {

        boolean nodChecker=false;//Following will check the nod if exist,
        // following will check the item nod, if exixt for that id
        boolean itemnodChecker=false;

        progressDialog=new ProgressDialog(AddInvoiceActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(BILL_ID);
                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                MDToast mdToast=MDToast.makeText(getApplicationContext(),"Draft Bill Cleared",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
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

    private void fetchAllTheCurrentBillingItems(String billID) {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(billID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list2!=null)
                {
                    list2.clear();
                    TOTAL_TAX=0.00;
                    TOTAL_AMOUNT=0.00;
                }

                if (snapshot.exists())
                {




                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        BillItems billItems=dataSnapshot.getValue(BillItems.class);
                        list2.add(billItems);

                        double tax=0;


                        if ((billItems.getTaxPercent()).equals("inclusive"))
                        {
                            tax=0;
                        }
                        else
                        {
                            double uP=Double.parseDouble(billItems.getUnitPrice());
                            Integer qty=Integer.parseInt(billItems.getQty());
                            double tP=Double.parseDouble(billItems.getTaxPercent());
                            tax=(((tP/100)*uP)*qty);



                        }


                        TOTAL_TAX=TOTAL_TAX+tax;

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

                    //Now, we will add the tax to  the total Price

                    totalTxtView.setText("Rs "+TOTAL_AMOUNT);



                    totalTaxTxtView.setText("Rs "+TOTAL_TAX);


                    Activity activity=AddInvoiceActivity.this;

                    Collections.reverse(list2);
                    adapter2=new BillItemOnScreenAdapter(list2,getApplicationContext(),activity,BILL_ID,"bill");
                    recyclerView.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    private void intialize() {

        backBtn=findViewById(R.id.backBtn_bill);
        addParty=findViewById(R.id.selectParty_bill);
        recyclerView=findViewById(R.id.recycler_bill);
        totalTxtView=findViewById(R.id.totalAmount_bill);
        totalTaxTxtView=findViewById(R.id.totalTax_bill);
        addItem=findViewById(R.id.addItemBtn_bill);
        generateBill=findViewById(R.id.generateBillBtn);
        clearCurrentBill=findViewById(R.id.clear_currentBill);

    }
}