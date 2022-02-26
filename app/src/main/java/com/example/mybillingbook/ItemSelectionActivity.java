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
import android.view.Window;
import android.widget.EditText;

import com.example.mybillingbook.Adapters.AddItemAdapter;
import com.example.mybillingbook.Adapters.AddPartyAdapter;
import com.example.mybillingbook.Adapters.ItemsAdapter;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.Items;
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

public class ItemSelectionActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private AddItemAdapter adapter;
    private List<Items> list;

    //The item for search bar
    private EditText searchBar;

    //The Current Billing iD
    private String CURRENT_BILLING_ID="";

    private String type="";



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);

        Intent i=getIntent();
        type=i.getStringExtra("type");

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=ItemSelectionActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(ItemSelectionActivity.this, R.color.colorPrimaryDark));

        //Assinging the objects to their respective ids
        searchBar=findViewById(R.id.searchBar_selectItem);
        recyclerView=findViewById(R.id.recycler_selectItem);

        //////////////////////////////////////////////////////////////////////////////////
        //Getching the current billing is
        if (type.equals("bill"))
        {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        Bills bills=snapshot.getValue(Bills.class);

                        CURRENT_BILLING_ID=bills.getBillid();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        Bills bills=snapshot.getValue(Bills.class);

                        CURRENT_BILLING_ID=bills.getBillid();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list=new ArrayList<>();
        fetchAllTheItems(); //Following is the method, to fetch all the items


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTheItemWithItemId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    private void searchTheItemWithItemId(String s) {
        Query searchQuery= FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("itemid").startAt(s).endAt(s+"\uf8ff");
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list!=null)
                {
                    list.clear();
                }
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Items items=dataSnapshot.getValue(Items.class);
                        list.add(items);
                    }


                    Activity activity=ItemSelectionActivity.this;

                    Collections.reverse(list);
                    adapter=new AddItemAdapter(list,getApplicationContext(),activity,CURRENT_BILLING_ID,type);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    private void fetchAllTheItems() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list!=null)
                {
                    list.clear();
                }
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Items items=dataSnapshot.getValue(Items.class);
                        list.add(items);
                    }

                    Activity activity=ItemSelectionActivity.this;

                    Collections.reverse(list);
                    adapter=new AddItemAdapter(list,getApplicationContext(),activity,CURRENT_BILLING_ID,type);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}