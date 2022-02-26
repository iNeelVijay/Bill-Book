package com.example.mybillingbook.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybillingbook.APIs.JavaMailAPI;
import com.example.mybillingbook.BillActivity;
import com.example.mybillingbook.DailyExpenseActivity;
import com.example.mybillingbook.FullImageActivity;
import com.example.mybillingbook.HomeActivity;
import com.example.mybillingbook.LoginActivity;
import com.example.mybillingbook.Models.DailyExpense;
import com.example.mybillingbook.Models.MyDetails;
import com.example.mybillingbook.R;
import com.example.mybillingbook.UserDetailsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Following are the objects on the settings fragment
    private ImageView logo;
    private TextView buisinessName;
    private TextView contactNumber;
    private TextView addDetails;
    //Now, the menu options
    private TextView myBills;
    private TextView myQuotations;
    private TextView daialyExpenses;
    private TextView aboutUs;
    private TextView contactUs;
    private TextView rateUs;
    //Following is the logout option
    private TextView logout;

    private String url="null";

    //Following is the main view, that the fragment will return
    private View view;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        view= inflater.inflate(R.layout.fragment_settings, container, false);

        intialize(); //Following is the method, that will initialize all the respective objects with  their ids

        ///////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code for the logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog=new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_info);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                //Following are the threee obujects on the dialog
                TextView msg=dialog.findViewById(R.id.msg_dialog);
                TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                msg.setText("Are you sure you want to logout?");

                positiveBtn.setText("YES");
                negativeBtn.setText("NO");

                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();

                        Intent i=new Intent(getContext(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();

                        MDToast md=MDToast.makeText(getContext(),"Logged out",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                        md.show();


                    }
                });

                dialog.show();


            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////


        //////////////////////////////////////////////////////////////////////////////////////////////
        //A bottom sheet to show up the about us dialog//////////////////////////////////////////////
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext());
                View dialog=LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_aboutuus,null);
                bottomSheetDialog.setContentView(dialog);
                bottomSheetDialog.show();
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////////
        //A bottom sheet to show up the contact us dialog//////////////////////////////////////////////
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext());
                View dialog=LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_contactus,null);

                //Following are the objects on the contact us object
                EditText name=dialog.findViewById(R.id.name_contactUs);
                EditText main=dialog.findViewById(R.id.main_contactUs);
                EditText contact=dialog.findViewById(R.id.contact_contactUs);
                TextView send=dialog.findViewById(R.id.send_contactUs);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(name.getText().toString()))
                        {
                            MDToast mdToast=MDToast.makeText(getContext(),"Name is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                        else if (TextUtils.isEmpty(main.getText().toString()))
                        {
                            MDToast mdToast=MDToast.makeText(getContext(),"Query/Problem is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                        else if (TextUtils.isEmpty(contact.getText().toString()))
                        {
                            MDToast mdToast=MDToast.makeText(getContext(),"Contact is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                        else
                        {
                            //Here, we can send the email.........


                            //here put the email on which the mail is to be send
                            String mail = "deveshtiwary19@gmail.com";
                            String subject = "Query/Problem for Ecrrede Billing App";
                            String body = main.getText().toString()+"\n"+name.getText().toString()+"\n"+contact.getText().toString();

                            JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(), mail, subject, body);

                            try {
                                javaMailAPI.execute();

                                MDToast mdToast=MDToast.makeText(getContext(),"Query/Problem registered sucessfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                                mdToast.show();
                                bottomSheetDialog.cancel();
                                //Toast.makeText(this, mail, Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                String msg = e.getMessage().toString();
                                MDToast mdToast=MDToast.makeText(getContext(),msg,MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                                mdToast.show();
                                bottomSheetDialog.cancel();

                            }








                        }
                    }
                });


                bottomSheetDialog.setContentView(dialog);
                bottomSheetDialog.show();
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //Now, we will check for the details of the user avilable or not and hence set the relevant data
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    //Here, this means that the user is already added the detail and has to edit them now
                    addDetails.setText("Edit Details");

                    MyDetails myDetails=snapshot.getValue(MyDetails.class);

                    Picasso.get().load(myDetails.getImage()).into(logo);
                    buisinessName.setText(myDetails.getName());
                    contactNumber.setText(myDetails.getContact());

                    url=myDetails.getImage();


                }
                else
                {
                    //Here, it means that  the user has no details for his buisness record, so he will add the deatils
                    addDetails.setText("Edit Details");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////
        //A click listener on the addDetils that will take the user to the user Details activity
        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getContext(), UserDetailsActivity.class);
                startActivity(i);

            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), FullImageActivity.class);
                i.putExtra("url",url);
                startActivity(i);
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////


        //Setting a on click listener, that fetches all the bills for the current user
        myBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),BillActivity.class);
                i.putExtra("type","bill");
                startActivity(i);
            }
        });

        myQuotations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),BillActivity.class);
                i.putExtra("type","quotation");
                startActivity(i);
            }
        });

        daialyExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), DailyExpenseActivity.class);
                startActivity(i);
            }
        });







        return view;
    }

    private void intialize() {

        logo = view.findViewById(R.id.logo_settings);
        buisinessName = view.findViewById(R.id.businessName_settings);
        contactNumber = view.findViewById(R.id.contactNumber_settings);

        addDetails = view.findViewById(R.id.addDetails_settings);

        //Now the menu options
        myBills = view.findViewById(R.id.myBills_settings);
        myQuotations = view.findViewById(R.id.myQuotations_settings);
        daialyExpenses = view.findViewById(R.id.dailyExpenses_settings);

        contactUs = view.findViewById(R.id.contactUs_settings);
        rateUs = view.findViewById(R.id.rateUs_settings);
        aboutUs = view.findViewById(R.id.aboutUs_settings);

        logout=view.findViewById(R.id.logout_settings);


    }
}