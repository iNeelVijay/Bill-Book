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

import com.example.mybillingbook.AddItemActivity;
import com.example.mybillingbook.Adapters.ItemsAdapter;
import com.example.mybillingbook.Models.Items;
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
 * Use the {@link ItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Following i sthe object for  the main view
    private View view;
    //FOllowing are the objet on te design
    private Button createNewBtn;

    //The textView for empty list message
    private TextView empty;

    //The recycler view on the screen
    private RecyclerView recyclerView;
    //Following are the objects for the recycler view items
    private  List<Items> list;
    private ItemsAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //The edit text for search item
    private EditText serachBar;

    public ItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemsFragment newInstance(String param1, String param2) {
        ItemsFragment fragment = new ItemsFragment();
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
        view= inflater.inflate(R.layout.fragment_items, container, false);

        initialize(); //Following is the method to initialize the objects with their respective ids

        createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                            Intent i=new Intent(getContext(), AddItemActivity.class);
                            i.putExtra("type","add");
                            i.putExtra("id","null");
                            startActivity(i);


            }
        });

        empty.setVisibility(View.GONE);
        //Setting up the recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<>();
        fetchAllTheItems(); //Following is the method, that will fetch all the items
        ////////////////////////////////////////////////////////////////////////////////

        serachBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().equals(""))
                {
                    fetchAllTheItems();
                }
                else
                {
                    searchTheItemWithItemId(s.toString());
                }

                
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        return view;
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


                    adapter=new ItemsAdapter(list,getContext());
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

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    empty.setVisibility(View.GONE);
                    //Clearing the list first
                    if (list!=null)
                    {
                        list.clear();
                    }

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Items items=dataSnapshot.getValue(Items.class);
                        list.add(items);
                    }


                        Collections.reverse(list);
                        adapter=new ItemsAdapter(list,getContext());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();







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

    private void initialize() {
        createNewBtn=view.findViewById(R.id.createItem_items);
        recyclerView=view.findViewById(R.id.recycler_items);
        empty=view.findViewById(R.id.emptyMsg_item);
        serachBar=view.findViewById(R.id.search_items);
    }
}