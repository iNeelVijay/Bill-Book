package com.example.mybillingbook.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mybillingbook.Adapters.BillsAdapter;
import com.example.mybillingbook.Adapters.OnScreenAdapter;
import com.example.mybillingbook.Adapters.RunningImagesAdapter;
import com.example.mybillingbook.AddItemActivity;
import com.example.mybillingbook.BillActivity;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.MyDetails;
import com.example.mybillingbook.R;
import com.example.mybillingbook.UserDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView heading;


    //The main view to return for the fragment
    private View view;

    int currentPage=0;

    //The view Pager for the running Images
    private ViewPager viewPager;

    private RunningImagesAdapter adapter;

    //Following  are the items on the card view for the user details
    private ImageView logo;
    private TextView name;
    private TextView conntact;



    private RecyclerView recyclerView;
    private List<Bills> list;
    private OnScreenAdapter adapter2;

    //Following are the objects for the radio buttoon on the screen
    private RadioGroup radGrp;
    //Following are the two radiobuttons on the home screen
    private RadioButton radBills;
    private RadioButton radQuos;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        view= inflater.inflate(R.layout.fragment_home, container, false);

        //////////////////////////////////////////////////////////////////////////////////////////
        //Following is teh method, to initialize all the objects with theor respective ids
        initialize();


        //Setting  the recycler view first
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        list=new ArrayList<>();

        //Setting, a initial selection and method calling from system side, for the radio buttons at bills
        radBills.setChecked(true);
        fetchAllTheBillsForTheUser(); //Following is the method, that will ftech all the bils for the current user


        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=radGrp.getCheckedRadioButtonId();
                if (id==R.id.quotation_RadBtn)
                {
                    MDToast mdToast=MDToast.makeText(getContext(),"Showing quotations",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                    mdToast.show();
                    fetchAllTheQuotationsForTheUser();
                }
                else if (id==R.id.bills_RadBtn)
                {
                    MDToast mdToast=MDToast.makeText(getContext(),"Showing bills",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                    mdToast.show();
                    fetchAllTheBillsForTheUser();
                }
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code for the runnong image sviewpager o the home fragment
        ////////////////////////////////////////////////////////////////////////////////////////////
        //Following will be the code for the the running Images view pager
        List<Fragment> list=new ArrayList<>();
        list.add(new R1());  //First page
        list.add(new R2());  //Second page
        list.add(new R3());  //Third page
        list.add(new R4());  //Fourth page

        //Now setting and passing the data to adapter
        adapter=new RunningImagesAdapter(getActivity().getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);

        //Following is the code to make the viewpager swipe automaticallly
        final Handler handler=new Handler();
        final Runnable update=new Runnable() {
            @Override
            public void run() {
                if (currentPage==4)
                {
                    currentPage=0;
                }

                viewPager.setCurrentItem(currentPage++,true);
            }
        };

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);

            }
        },700,3000);
        ///////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////


        fetchTheCurrentDetailStatus(); //Following is the method, that will fetch the current status of the user(either details registered or not)


        return view;
    }

    private void fetchAllTheQuotationsForTheUser() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Quotations").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
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
                        Bills bills=dataSnapshot.getValue(Bills.class);
                        list.add(bills);
                    }

                    // Activity activity= BillActivity.this;

                    Collections.reverse(list);
                    adapter2=new OnScreenAdapter(list,getContext(),"quotation");
                    recyclerView.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();





                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchAllTheBillsForTheUser() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Bills").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
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
                        Bills bills=dataSnapshot.getValue(Bills.class);
                        list.add(bills);
                    }

                   // Activity activity= BillActivity.this;

                    Collections.reverse(list);
                    adapter2=new OnScreenAdapter(list,getContext(),"bill");
                    recyclerView.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();





                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void fetchTheCurrentDetailStatus() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    MyDetails myDetails=snapshot.getValue(MyDetails.class);

                    Picasso.get().load(myDetails.getImage()).placeholder(R.drawable.addimage_ic).into(logo);
                    name.setText(myDetails.getName());
                    conntact.setText(myDetails.getContact());


                }
                else
                {



                    Dialog dialog=new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_info);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    //Following are the threee obujects on the dialog
                    TextView msg=dialog.findViewById(R.id.msg_dialog);
                    TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                    TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                    msg.setText("Add your buisiness details and start working hastle free now.");

                    positiveBtn.setText("OK");
                    negativeBtn.setText("Later");

                    negativeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();

                        }
                    });

                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent i=new Intent(getContext(), UserDetailsActivity.class);
                            startActivity(i);
                            dialog.cancel();



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

    private void initialize() {
        viewPager=view.findViewById(R.id.viewPager_home);
        logo=view.findViewById(R.id.logo_home);
        name=view.findViewById(R.id.name_home);
        conntact=view.findViewById(R.id.contact_home);

        recyclerView=view.findViewById(R.id.recycler_home);

        radGrp=view.findViewById(R.id.radGrp_home);

        radBills=view.findViewById(R.id.bills_RadBtn);
        radQuos=view.findViewById(R.id.quotation_RadBtn);

        heading=view.findViewById(R.id.heading_home);
    }
}