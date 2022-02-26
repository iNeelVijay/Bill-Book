package com.example.mybillingbook.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mybillingbook.AddPartyActivity;
import com.example.mybillingbook.Adapters.PartiesAdapter;
import com.example.mybillingbook.Models.Parties;
import com.example.mybillingbook.R;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PartyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //The amin view's object to return for the fragment
    private View view;

    //Following are the objects for the
    private Button addnNewParty;

    //The message to show for empty list
    private TextView emptyMsg;

    //Following are the objects for the items to set in the recycler view
    private RecyclerView recyclerView;
    private List<Parties> list;
    private PartiesAdapter adapter;

    //Following is the search edittext
    private EditText searchEditText;



    public PartyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PartyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartyFragment newInstance(String param1, String param2) {
        PartyFragment fragment = new PartyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_party, container, false);

        initialize(); //Following is the method, which initializes all the objects with their respective ids

        emptyMsg.setVisibility(View.GONE);

        ////////////////////////////////////////////////////////////////////////////////////////
        //Creating a on click listener for the create party button/////////////////////////////
        addnNewParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), AddPartyActivity.class);
                i.putExtra("type","add");
                i.putExtra("id","null");
                startActivity(i);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Setting a text change listener for the search button
        searchEditText.addTextChangedListener(new TextWatcher() {
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
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////////////////
        //Setting up the recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<>();
        fetchAllTheParties(); //Following is the method, that fetches all the parties for the curent user
        /////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////

        return view;
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


                    adapter=new PartiesAdapter(list,getContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchAllTheParties() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if (snapshot.exists())
             {
                 emptyMsg.setVisibility(View.GONE);
                 if (list!=null)
                 {
                     list.clear();
                 }
                 for (DataSnapshot dataSnapshot:snapshot.getChildren())
                 {
                     Parties parties=dataSnapshot.getValue(Parties.class);
                     list.add(parties);
                 }

                 Collections.reverse(list);
                 adapter=new PartiesAdapter(list,getContext());
                 recyclerView.setAdapter(adapter);
                 adapter.notifyDataSetChanged();
             }
             else {
                 emptyMsg.setVisibility(View.VISIBLE);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        addnNewParty=view.findViewById(R.id.createParty_party);
        emptyMsg=view.findViewById(R.id.emptyMsg_party);
        recyclerView=view.findViewById(R.id.recycler_parties);
        searchEditText=view.findViewById(R.id.search_parties);
    }
}