package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybillingbook.Adapters.BillViewerAdapter;
import com.example.mybillingbook.Adapters.PartiesAdapter;
import com.example.mybillingbook.Models.BillItems;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.MyDetails;
import com.example.mybillingbook.Models.Parties;
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

public class
BillViewerActivity extends AppCompatActivity {

    //The object for the main Layout
    private RelativeLayout mainView;


    //Following is the button to save the doc as JPEG
    private FloatingActionButton saveBtn;

    private ScrollView scrollView;

    public static Bitmap bitScroll;

    //Following are the bill header objects on the bill preview
    private ImageView logo;
    private TextView  businessName;
    private TextView businessEmail;
    private TextView businessAddress;

    private TextView date;
    private TextView time;
    private TextView billid;

    //Following are the two object for  the changes accordingto the quotation
    private TextView header;
    private TextView billingTo;
    private TextView msgQuo;

    //Teh objesct to set the details for the party
    private TextView partyName;
    private TextView partyAdress;
    private TextView partyGST;

    //Following is the object for the the recycler view uses
    private RecyclerView recyclerView;
    private BillViewerAdapter adapter;
    private List<BillItems>list;

    //The variables for total price and total TAx
    private Double TOTAL_AMOUNT=0.00;
    private Double TOTAL_TAX=0.00;

    //Following are the footer objects
    private TextView getBusinessName_footer;
    private TextView adress_footer;
    private TextView gst_footer;
    private TextView contact_footer;

    private String type="";

    //Following is the ID to fetch the bill details, with tHE BILLID
    private String BILL_ID="";

    //Following are the textviews for the total tax and total amount
    private TextView totalAmount;
    private TextView totalTax;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bill_viewer);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=BillViewerActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(BillViewerActivity.this, R.color.colorPrimaryDark));

         /////////////////////////////The animated starting designed for the activity//////////////////////////////////////////
        //////////The functions to beperformed on starting of the activiy/////////////////////////////////////////////////////
        mainView=findViewById(R.id.mainView_billView);
        //On craete the mainScroll will be gone, and alert dialog will appear with animationg.
        mainView.setVisibility(View.GONE);
        //Building a dialog
        final Dialog dialog = new Dialog(BillViewerActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();

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

        intialize(); //Following are the objects to initialize the objects with theor respective ids

        //set your status bar gone  Here

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Getting the bill ID, to show the bill details
        Intent i=getIntent();
        BILL_ID=i.getStringExtra("ID");
        type=i.getStringExtra("type");

        if (type.equals("quotation"))
        {
            header.setText("Quotation");
            billingTo.setText("Quoting To,");
            msgQuo.setVisibility(View.VISIBLE);

        }





        /////////////////////////////////////////////////////////////////////////////////////////
        //Setting a on click listener on the save Btn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//Asking the storage permission
                //////////////////////////////////////////////////////////////////////////////////////////////
                //Runtime permission for getting the current locationa dn updating the users current location
                if (ContextCompat.checkSelfPermission(BillViewerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {

                    //Here, it means that location permission is not granted yet and we need to get it
                    ActivityCompat.requestPermissions(BillViewerActivity.this,new String[]{
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
        /////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        //Now, we will fetch and set the data on the header
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("My Details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    MyDetails myDetails=snapshot.getValue(MyDetails.class);
                    //Now, setting the users data one by one
                    Picasso.get().load(myDetails.getImage()).placeholder(R.drawable.addimage_ic).into(logo);
                    businessName.setText(myDetails.getName());
                    businessEmail.setText(myDetails.getEmail());
                    String add=myDetails.getAdress()+","+myDetails.getCity()+"-"+myDetails.getPin()+"--"+myDetails.getState();
                    businessAddress.setText(add);


                    getBusinessName_footer.setText(myDetails.getName());
                    adress_footer.setText(add);

                    if ((myDetails.getGst()).equals("null"))
                    {
                        gst_footer.setVisibility(View.GONE);
                    }
                    else
                    {
                        gst_footer.setText(myDetails.getGst());
                    }


                    contact_footer.setText(myDetails.getEmail()+", +91 "+myDetails.getContact());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        billid.setText(BILL_ID);

        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        //Now, we need to fetch the date, tiem and party details according to the bill id details
        DatabaseReference reference;
        if (type.equals("bill"))
        {
             reference=FirebaseDatabase.getInstance().getReference().child("Bills").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(BILL_ID);

        }
        else {
            reference=FirebaseDatabase.getInstance().getReference().child("Quotations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(BILL_ID);

        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Bills bills=snapshot.getValue(Bills.class);
                    date.setText(bills.getDate());
                    time.setText(bills.getTime());
                    //Now, we will fetch the party details with the party ID
                    DatabaseReference partyRef=FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bills.getBillto());
                    partyRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                Parties parties=snapshot.getValue(Parties.class);

                                //Noow, settng the party details
                                partyName.setText(parties.getName());
                                partyAdress.setText(parties.getAdress());

                                if ((parties.getGst()).equals("null"))
                                {
                                    partyGST.setVisibility(View.GONE);
                                }
                                else
                                {
                                    partyGST.setText("GSTIN"+parties.getGst());
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Following is the method, to fetch the bill  items for the following billa nd set them on the screen
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list=new ArrayList<>();
        fetchAllTheBillItems();




















    }


    //Following is the method, to get the bitmap from the scroll view
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





    private void fetchAllTheBillItems() {
        DatabaseReference reference;
        if (type.equals("bill"))
        {
             reference=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(BILL_ID);

        }
        else
        {
            reference=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(BILL_ID);
        }

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
                        BillItems billItems=dataSnapshot.getValue(BillItems.class);
                        list.add(billItems);

                        double tax=0;
                        if ((billItems.getTaxPercent()).equals("inclusive"))
                        {
                            tax=0;
                        }
                        else
                        {
                            double uP=Double.parseDouble(billItems.getUnitPrice());
                            Integer qty=Integer.parseInt(billItems.getQty());
                            double tP=Double.parseDouble(billItems.getTaxPercent());
                            tax=(((tP/100)*uP)*qty);



                        }


                        TOTAL_TAX=TOTAL_TAX+tax;

                        //Now,, we need to calculate the total along with the price
                        double uP=Double.parseDouble(billItems.getUnitPrice());
                        int qty=Integer.parseInt(billItems.getQty());

                        double total=0.00;
                        if ((billItems.getTaxPercent()).equals("inclusive"))
                        {
                            //The tax is inclusive
                            total=uP*qty;
                        }
                        else
                        {
                            double taxPercent=Double.parseDouble(billItems.getTaxPercent());
                            total=(((taxPercent/100)*uP)*qty+(uP*qty));
                        }
                        TOTAL_AMOUNT=TOTAL_AMOUNT+total;




                    }

                    totalAmount.setText(String.valueOf(TOTAL_AMOUNT)+"/-");
                    totalTax.setText(String.valueOf(TOTAL_TAX)+"/-");

                    Collections.reverse(list);
                    adapter=new BillViewerAdapter(list,getApplicationContext(),BillViewerActivity.this,0);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void intialize() {

        logo=findViewById(R.id.logo_billViewer);
        businessName=findViewById(R.id.buisinessName_billViewer);
        businessEmail=findViewById(R.id.businessEmail_billViewer);
        businessAddress=findViewById(R.id.businessAdress_billViewer);


        date=findViewById(R.id.date_billViewer);
        time=findViewById(R.id.time_billViewer);
        billid=findViewById(R.id.billID_billViewer);

        partyName=findViewById(R.id.partyName_billViewer);
        partyAdress=findViewById(R.id.partyAdress_billViewer);
        partyGST=findViewById(R.id.partyGST_billViewer);

        recyclerView=findViewById(R.id.recycler_billViewer);

        totalAmount=findViewById(R.id.totalAmount_billViewer);
        totalTax=findViewById(R.id.totalTax_billViewer);

        getBusinessName_footer=findViewById(R.id.buisname_billFooter);
        adress_footer=findViewById(R.id.adress_billFooter);
        gst_footer=findViewById(R.id.gst_billFooter);
        contact_footer=findViewById(R.id.contact_billFooter);


        header=findViewById(R.id.header_billViewer);
        billingTo=findViewById(R.id.billingTo_billViewer);
        msgQuo=findViewById(R.id.msgQuo_billViewer);

        saveBtn=findViewById(R.id.save_billViewer);

        scrollView=findViewById(R.id.scrollView_billViewer);




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
                ActivityCompat.requestPermissions(BillViewerActivity.this,new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },100);
            }
        }




    }


}