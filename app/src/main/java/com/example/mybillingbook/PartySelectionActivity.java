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

import com.example.mybillingbook.Adapters.AddPartyAdapter;
import com.example.mybillingbook.Adapters.ItemsAdapter;
import com.example.mybillingbook.Adapters.PartiesAdapter;
import com.example.mybillingbook.Models.Parties;
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

public class PartySelectionActivity extends AppCompatActivity {

    private AddPartyAdapter adapter;
    private List<Parties> list;

    private RecyclerView recyclerView;

    //Following is the object for the search bar
    private EditText serachBar;

    private String type="";



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_selection);

        Intent i=getIntent();
        type=i.getStringExtra("type");

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=PartySelectionActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(PartySelectionActivity.this, R.color.colorPrimaryDark));

        //Assigning the id of the serach bar with its object
        serachBar=findViewById(R.id.searchBar_selectParty);


        recyclerView=findViewById(R.id.recycler_selectParty);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                        Parties parties=dataSnapshot.getValue(Parties.class);
                        list.add(parties);
                    }

                    Activity  activity=PartySelectionActivity.this;

                    Collections.reverse(list);
                    adapter=new AddPartyAdapter(list,getApplicationContext(),activity,type);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /////////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code for the search bar
        serachBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchForParties(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });






    }

    private void searchForParties(String s) {
        Query searchQuery= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("search").startAt(s).endAt(s+"\uf8ff");
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
                        Parties parties=dataSnapshot.getValue(Parties.class);
                        list.add(parties);
                    }

                    Activity activity=PartySelectionActivity.this;

                    Collections.reverse(list);
                    adapter=new AddPartyAdapter(list,getApplicationContext(),activity,type);
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