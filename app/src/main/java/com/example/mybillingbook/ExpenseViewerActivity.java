package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybillingbook.Adapters.AddPartyAdapter;
import com.example.mybillingbook.Adapters.DailyExpenseAdapter;
import com.example.mybillingbook.Models.DailyExpense;
import com.example.mybillingbook.Models.MyDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExpenseViewerActivity extends AppCompatActivity {

    private String SIZE_TO_RETURN="";

    //Folllowing are the header objects on the screen
    private ImageView logo;
    private TextView buisinessName;
    private TextView buisinessEmail;
    private TextView adress;

    private TextView daysNumber;

    //Following are the objects for the recycler view work flow
    private RecyclerView recyclerView;
    private DailyExpenseAdapter adapter;
    private List<DailyExpense> list;

    //Following are the footer objects on the screen
    private TextView buisinessName_footer;
    private TextView adress_footer;
    private TextView gst_footer;
    private TextView contact_footer;

    private  FloatingActionButton saveBtn;
    private ScrollView scrollView;

    public static Bitmap bitScroll;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_viewer);

        Intent i=getIntent();
        SIZE_TO_RETURN=i.getStringExtra("size");

        intialize(); //Following is the method, that intializes all the objects with their respective ids

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=ExpenseViewerActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(ExpenseViewerActivity.this, R.color.colorPrimaryDark));

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ////////////////////////////////////////////////////////////////////////////////////////////
        fetchTheCurrentDetailsOfTheUserandSet();
        //Following is the  method, to fetch and set all the details of the user in the expense header
        //////////////////////////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code to set the number of days on the heading of the activity
        if (SIZE_TO_RETURN.equals("all"))
        {
            daysNumber.setText("Your Expense Record");
        }
        else if (SIZE_TO_RETURN.equals("7"))
        {
            daysNumber.setText("Expense record for last seven days");
        }
        else if (SIZE_TO_RETURN.equals("30"))
        {
            daysNumber.setText("Expense record for last thirty days");
        }
        else if (SIZE_TO_RETURN.equals("183"))
        {
            daysNumber.setText("Expense record for last six months");
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code, to set the recycler view, to fetch the items in daily expense as required by the user
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list=new ArrayList<>();
        fetchAllTheExpensesAndSendWithSize();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////////////////////////////////////////
        //Following is the code, to set the on click listener on the save button///////////////////////////
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ExpenseViewerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {

                    //Here, it means that location permission is not granted yet and we need to get it
                    ActivityCompat.requestPermissions(ExpenseViewerActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },100);

                }
                else
                {

                    bitScroll=getBitmapFromView(scrollView,scrollView.getChildAt(0).getHeight(),scrollView.getChildAt(0).getWidth());
                    saveBitmap(bitScroll);



                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////



    }

    private void fetchAllTheExpensesAndSendWithSize() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Daily Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                        DailyExpense dailyExpense=dataSnapshot.getValue(DailyExpense.class);
                        list.add(dailyExpense);
                    }

                    Collections.reverse(list);
                    adapter=new DailyExpenseAdapter(list,getApplicationContext(),SIZE_TO_RETURN);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();








                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchTheCurrentDetailsOfTheUserandSet() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    MyDetails myDetails=snapshot.getValue(MyDetails.class);
                    //Now, we need to set the data accordingly
                    Picasso.get().load(myDetails.getImage()).placeholder(R.drawable.placeholder).into(logo);
                    buisinessName.setText(myDetails.getName());
                    buisinessEmail.setText(myDetails.getEmail());
                    adress.setText(myDetails.getAdress()+"-"+myDetails.getCity()+" - "+myDetails.getPin()+"\n"+myDetails.getState());

                    //Setting data to the footer
                    buisinessName_footer.setText(myDetails.getName());
                    adress_footer.setText(myDetails.getAdress()+"-"+myDetails.getCity()+" - "+myDetails.getPin()+"\n"+myDetails.getState());
                    contact_footer.setText(myDetails.getContact()+", "+myDetails.getEmail());

                    if (myDetails.getGst().equals("null"))
                    {
                        gst_footer.setVisibility(View.GONE);
                    }
                    else
                    {
                        gst_footer.setText("GSTIN"+myDetails.getGst());
                    }

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

    private Bitmap getBitmapFromView(View view,int height, int width)
    {

        Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);

        Drawable bgDrawable=view.getBackground();

        //Now, setting the consitions for the returning function
        if (bgDrawable!=null)
        {
            bgDrawable.draw(canvas);
        }
        else
        {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);

        return bitmap;


    }

    //Now, followig is the code, that gets the bitmap fom the above code
    public void saveBitmap(Bitmap bitmap)
    {
        Date now=new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss",now);

        //Setting the path, for the external directory saving of the created bitmap(Taken format .JPEG format)
        String mPath= Environment.getExternalStorageDirectory().toString()+"/"+now+".jpeg";
        File imagePath=new File(mPath);

        //The output stream
        FileOutputStream fos;

        try {

            //Trying tosave the screenhot

            fos=new FileOutputStream(imagePath);

            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);

            fos.flush();
            fos.close();


            Toast.makeText(this, imagePath.getAbsolutePath()+" ", Toast.LENGTH_SHORT).show();





        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }



    private void intialize() {
        logo=findViewById(R.id.logo_dailyExpense);
        buisinessName=findViewById(R.id.buisinessName_dailyExpense);
        buisinessEmail=findViewById(R.id.businessEmail_dailyExpense);
        adress=findViewById(R.id.businessAdress_dailyExpense);
        daysNumber=findViewById(R.id.days_dailyExpense);
        recyclerView=findViewById(R.id.recycler_expenseViewer);


        //The footer objects
        buisinessName_footer=findViewById(R.id.buisname_expfooter);
        adress_footer=findViewById(R.id.adress_expfooter);
        gst_footer=findViewById(R.id.gst_expfooter);
        contact_footer=findViewById(R.id.contact_expfooter);

        //The save button
        saveBtn=findViewById(R.id.savebtn_expviewer);
        scrollView=findViewById(R.id.scroll_expViewer);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(ExpenseViewerActivity.this,new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },100);
            }
        }




    }
}