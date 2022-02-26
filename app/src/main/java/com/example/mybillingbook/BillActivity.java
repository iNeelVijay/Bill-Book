package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybillingbook.Adapters.BillViewerAdapter;
import com.example.mybillingbook.Adapters.BillsAdapter;
import com.example.mybillingbook.Models.Bills;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BillActivity extends AppCompatActivity {

    //Following is the object will detect the type of obtained list
    private String TO_DISPLAY="";

    //Following is the object to show, the empty message
    private TextView empty;

    //Following are the objects on the screen
    private ImageView backBtn;
    private TextView title;
    private EditText searchEditTxt;
    private RecyclerView recyclerView;

    //Following are the objects to display the bill in the recycelr view
    private BillsAdapter adapterBill;
    private List<Bills> listBills;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        intialize(); //Following is the method, taht initializes all the objets with their respective ids

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=BillActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(BillActivity.this, R.color.colorPrimaryDark));

        Intent i=getIntent();
        TO_DISPLAY=i.getStringExtra("type");

        //Setting the recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listBills=new ArrayList<>();


        if (TO_DISPLAY.equals("bill"))
        {
            title.setText("My Bills    ");

            fetchAllTheBillsToDisplay();//Following is the method, that will fetch all the bills of the current user and send to the adapter
        }
        else
        {
            title.setText("My Quotations    ");
            searchEditTxt.setHint("Enter quotation id to search");

            fetchAllTheQuotationsToDisplay(); //Following is the method, that will fetch all the quotations of the current user
        }

        ///////////////////////////////////////////////
        //Setting a on click listener o the back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ////////////////////////////////////////////////
        //////////////////////////////////////////////


        //////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code to set the search function///////////////////////////////////////
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TO_DISPLAY.equals("bill"))
                {
                    searchTheBill(s.toString()); //Following is the method to serach for the bills
                }
                else
                {
                    searchTheQuotation(s.toString()); //Following is the method to serach for the bills
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////




    }

    private void searchTheQuotation(String toString) {
        Query searchQuery= FirebaseDatabase.getInstance().getReference().child("Quotations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("billid").startAt(toString).endAt(toString+"\uf8ff");
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listBills!=null)
                {
                    listBills.clear();
                }
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Bills bills=dataSnapshot.getValue(Bills.class);
                        listBills.add(bills);
                    }

                    Activity activity=BillActivity.this;

                    Collections.reverse(listBills);
                    adapterBill=new BillsAdapter(listBills,getApplicationContext(),activity,"quotation");
                    recyclerView.setAdapter(adapterBill);
                    adapterBill.notifyDataSetChanged();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void fetchAllTheQuotationsToDisplay() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Quotations").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listBills!=null)
                {
                    listBills.clear();
                }

                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Bills bills=dataSnapshot.getValue(Bills.class);
                        listBills.add(bills);
                    }

                    Activity activity=BillActivity.this;

                    Collections.reverse(listBills);
                    adapterBill=new BillsAdapter(listBills,getApplicationContext(),activity,"quotation");
                    recyclerView.setAdapter(adapterBill);
                    adapterBill.notifyDataSetChanged();





                }
                else
                {
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchTheBill(String toString) {

        Query searchQuery= FirebaseDatabase.getInstance().getReference().child("Bills").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("billid").startAt(toString).endAt(toString+"\uf8ff");
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listBills!=null)
                {
                    listBills.clear();
                }
               if (snapshot.exists())
               {
                   for (DataSnapshot dataSnapshot:snapshot.getChildren())
                   {
                       Bills bills=dataSnapshot.getValue(Bills.class);
                       listBills.add(bills);
                   }

                   Activity activity=BillActivity.this;

                   Collections.reverse(listBills);
                   adapterBill=new BillsAdapter(listBills,getApplicationContext(),activity,"bill");
                   recyclerView.setAdapter(adapterBill);
                   adapterBill.notifyDataSetChanged();
               }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void fetchAllTheBillsToDisplay() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Bills").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listBills!=null)
                {
                    listBills.clear();
                }

                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Bills bills=dataSnapshot.getValue(Bills.class);
                        listBills.add(bills);
                    }

                    Activity activity=BillActivity.this;

                    Collections.reverse(listBills);
                    adapterBill=new BillsAdapter(listBills,getApplicationContext(),activity,"bill");
                    recyclerView.setAdapter(adapterBill);
                    adapterBill.notifyDataSetChanged();





                }
                else
                {
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void intialize() {
        backBtn=findViewById(R.id.backBtn_bills);
        title=findViewById(R.id.title_bills);
        searchEditTxt=findViewById(R.id.searchEditTxt_bills);
        recyclerView=findViewById(R.id.recyclerView_bills);
        empty=findViewById(R.id.emptyMsg_bills);

    }
}