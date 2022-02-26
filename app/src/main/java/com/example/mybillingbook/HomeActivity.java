package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybillingbook.Fragments.HomeFragment;
import com.example.mybillingbook.Fragments.ItemsFragment;
import com.example.mybillingbook.Fragments.PartyFragment;
import com.example.mybillingbook.Fragments.SettingsFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private RelativeLayout mainView;
    int x;
    private boolean networkChecker; //Following i sthe vraible that will be used for the network checing process

    //For back pressing function
    int back=1;

    //Following is the variable to use while on click buttons
    private boolean IS_REGISTERED=false;

    //The object for the bottom nav menu
    private SpaceNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=HomeActivity.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimaryDark));
        }


        /////////////////////////////The animated starting designed for the activity//////////////////////////////////////////
        //////////The functions to beperformed on starting of the activiy/////////////////////////////////////////////////////
        mainView=findViewById(R.id.mainView_home);
        //On craete the mainScroll will be gone, and alert dialog will appear with animationg.
        mainView.setVisibility(View.GONE);
        //Building a dialog
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();

        checkConnectivity();


        checkForRegisteredUser(); //Following is the method, that will check for the registered user


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

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Following will be the intialization forr tthe main fragment on start
       FragmentManager fragmentManager= getSupportFragmentManager();
       FragmentTransaction transaction=fragmentManager.beginTransaction();
       transaction.add(R.id.frameLayout_home, new HomeFragment(),"HOME");
       transaction.addToBackStack(null);
       transaction.commit();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////














        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code for the bottom nav menu set up

        navigationView=findViewById(R.id.space);


        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.home_ic));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.parties_ic));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.items_ic));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.settings_ic));


        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
               // Toast.makeText(HomeActivity.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                navigationView.setCentreButtonSelectable(true);
                back=1;


                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //Here, we will build a dialog that will show up three options as to create new////////////////////////////
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(HomeActivity.this);
                View dialog= LayoutInflater.from(HomeActivity.this).inflate(R.layout.bottomsheet_addnew,null);

                //Following are teh three options on  the bottom sheet view
                TextView newBill=dialog.findViewById(R.id.newBill_home);
                TextView newQuotation=dialog.findViewById(R.id.newQuotation_home);
                TextView dailyExpenses=dialog.findViewById(R.id.dailyExpenses_home);

                //Now, we will have a on click listeners on all
                newBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (IS_REGISTERED)
                        {

                        Intent i=new Intent(getApplicationContext(),AddInvoiceActivity.class);
                        startActivity(i);
                        bottomSheetDialog.cancel();

                        }
                        else
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Please fill your buisiness details before creating a invoice",MDToast.LENGTH_LONG,MDToast.TYPE_WARNING);
                            mdToast.show();
                            bottomSheetDialog.cancel();
                        }
                    }
                });




                bottomSheetDialog.setContentView(dialog);
                bottomSheetDialog.show();
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////////



                newQuotation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (IS_REGISTERED)
                        {
                            Intent i=new Intent(getApplicationContext(),AddQuotationActivity.class);
                            startActivity(i);
                            bottomSheetDialog.cancel();
                        }
                        else
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Please fill your buisiness details before creating a quotation",MDToast.LENGTH_LONG,MDToast.TYPE_WARNING);
                            mdToast.show();
                            bottomSheetDialog.cancel();
                        }

                    }
                });


                dailyExpenses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (IS_REGISTERED)
                        {
                            Intent i=new Intent(getApplicationContext(),DailyExpenseActivity.class);
                            startActivity(i);
                            bottomSheetDialog.cancel();
                        }
                        else
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Please fill your buisiness details before updating daily expenses",MDToast.LENGTH_LONG,MDToast.TYPE_WARNING);
                            mdToast.show();
                            bottomSheetDialog.cancel();
                        }

                    }
                });






            }


            @Override
            public void onItemClick(int itemIndex, String itemName) {

                if (itemIndex==0)
                {
                    FragmentTransaction transaction;
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout_home, new HomeFragment());
                    transaction.commit();
                    back=1;
                }
                if (itemIndex==1)
                {
                    FragmentTransaction transaction;
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout_home, new PartyFragment());
                    transaction.commit();
                    back=1;

                }
                if (itemIndex==2)
                {
                    FragmentTransaction transaction;
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout_home, new ItemsFragment());
                    transaction.commit();
                    back=1;
                }
                if (itemIndex==3)
                {
                    FragmentTransaction transaction;
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout_home, new SettingsFragment());
                    transaction.commit();
                    back=1;
                }



            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

                if (itemIndex==0)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Already on home",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                    mdToast.show();
                }
                if (itemIndex==1)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Already on parties",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                    mdToast.show();
                }
                if (itemIndex==2)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Already on items",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                    mdToast.show();
                }
                if (itemIndex==3)
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Already on settings",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                    mdToast.show();
                }



            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }

    private void checkForRegisteredUser() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    IS_REGISTERED=true;
                }
                else
                {
                    IS_REGISTERED=false;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Following are the methods to detect the internet connectivity
    private void checkConnectivity() {

        networkChecker=haveNetworkConnection();
        Dialog dialog=new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //Following are the threee obujects on the dialog
        TextView msg=dialog.findViewById(R.id.msg_dialog);
        TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
        TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

        msg.setText("No active internet connection!!\nTurn on mobile data and try again.");

        positiveBtn.setText("TRY");
        negativeBtn.setText("EXIT");

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnectivity();
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        if (!networkChecker)
        {

            dialog.show();
        }
        else
        {
            if (x!=0)
            {
                Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
    //Following is the method to check the status of internet connectivity
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onBackPressed() {
        if (back==1)
        {
            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Press again to exit",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
            mdToast.show();
            back++;
        }
        else
        {
            finish();
        }

    }
}