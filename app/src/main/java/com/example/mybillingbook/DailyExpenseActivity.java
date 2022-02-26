package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DailyExpenseActivity extends AppCompatActivity {

    //Following are the objects on the screen
    private ImageView backBtn;
    private TextView date;
    private EditText incomingAmount;
    private EditText outGoingAmount;
    private TextView updateBtn;

    private RadioGroup radGrp;
    private RadioButton sevenDaysBtn;
    private RadioButton thirtyDaysBtn;
    private RadioButton sixMonthsBtn;
    private RadioButton allBtn;

    private TextView getListBtn;

    //Following is the object for the progress dialog
    private ProgressDialog progressDialog;

    //Following are the objects that are set to be made visible and invisible
    private TextView alreadyDoneMsg;
    private LinearLayout layoutToAddExpense;

    //Following is the variable to fetch the today's date
    private String TODAYS_DATE="";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_expense);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=DailyExpenseActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(DailyExpenseActivity.this, R.color.colorPrimaryDark));

        initialize(); //Following is the method, to initialize, all the objects with their respective ids

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Following is the method, to fetch the current date for today
        fetchTodaysDateAndStore();
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////////////////////////////////
        //Now, we will fist check if the record for today exists or not
        checkIfTodaysDateIsInRecordOrNot();
        ///////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////
        //Now, we have to code for the add expense button, that adds the expense for the particular current date
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(incomingAmount.getText()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Empty Amount",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(outGoingAmount.getText()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Empty Amount",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
                else
                {
                    addTodaysExpense(); //Following is the method, that adds expense to the users nod for the daily expenses
                }
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////



        ///////////////////////////////////////////////////////////////////////////////////////////
        //Now, we have to set a on click listener on the get Lsit option///////////////////////////
        getListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTheList();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
















    }

    private void getTheList() {

        int id=radGrp.getCheckedRadioButtonId();

        if (id==-1)
        {
            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Select a option",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
            mdToast.show();
        }
        else if (id==R.id.sevendays_dailyExpense)
        {
            //User wants the last seven days record
            Intent i=new Intent(getApplicationContext(),ExpenseViewerActivity.class);
            i.putExtra("size","7");
            startActivity(i);
            finish();

        }
        else if (id==R.id.thirtydays_dailyExpense)
        {
            //User wants the last thirty days record
            Intent i=new Intent(getApplicationContext(),ExpenseViewerActivity.class);
            i.putExtra("size","30");
            startActivity(i);
            finish();

        }
        else if (id==R.id.sixmonths_dailyExpense)
        {
            //USer wants the last 183 days record
            Intent i=new Intent(getApplicationContext(),ExpenseViewerActivity.class);
            i.putExtra("size","183");
            startActivity(i);
            finish();
        }
        else if (id==R.id.all_dailyExpense)
        {
            //User wants all the record
            Intent i=new Intent(getApplicationContext(),ExpenseViewerActivity.class);
            i.putExtra("size","all");
            startActivity(i);
            finish();

        }


    }

    private void addTodaysExpense() {

        progressDialog=new ProgressDialog(DailyExpenseActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String s1=TODAYS_DATE;
        String s2=s1.replace('/','x');

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Daily Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final Map<String,Object>map=new HashMap<>();
        map.put("date",s2);
        map.put("inc",incomingAmount.getText().toString());
        map.put("out",outGoingAmount.getText().toString());

        ref.child(s2).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    finish();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Daily Expense Updated Sucessfuly",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS);
                    mdToast.show();
                }
                else
                {
                    progressDialog.dismiss();
                    //finish();
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });


    }

    private void checkIfTodaysDateIsInRecordOrNot() {

        String s1=TODAYS_DATE;
        String s2=s1.replace('/','x');

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Daily Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(s2);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    layoutToAddExpense.setVisibility(View.GONE);
                    alreadyDoneMsg.setVisibility(View.VISIBLE);

                }
                else
                {

                    layoutToAddExpense.setVisibility(View.VISIBLE);
                    alreadyDoneMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchTodaysDateAndStore() {

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);

        TODAYS_DATE=thisDate;

        date.setText(TODAYS_DATE);

    }

    private void initialize() {

        backBtn=findViewById(R.id.backBtn_dailyExpense);
        date=findViewById(R.id.date_dailyExpense);
        incomingAmount=findViewById(R.id.incomingAmount_dailyExpense);
        outGoingAmount=findViewById(R.id.outgoingAmount_dailyExpense);
        updateBtn=findViewById(R.id.updateBtnBtn_dailyExpense);

        radGrp=findViewById(R.id.radGrp_dailyExpense);
        sevenDaysBtn=findViewById(R.id.sevendays_dailyExpense);
        thirtyDaysBtn=findViewById(R.id.thirtydays_dailyExpense);
        sixMonthsBtn=findViewById(R.id.sixmonths_dailyExpense);
        allBtn=findViewById(R.id.all_dailyExpense);

        getListBtn=findViewById(R.id.getList_dailyExpense);

        alreadyDoneMsg=findViewById(R.id.alreadyDone_msg);
        layoutToAddExpense=findViewById(R.id.layoutToAddExpense_dailyExpense);



    }
}