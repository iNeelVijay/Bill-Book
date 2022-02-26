package com.example.mybillingbook.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.Items;
import com.example.mybillingbook.Models.Parties;
import com.example.mybillingbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItemAdapter extends RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>
{

    private List<Items> list;
    private Context mContext;

    private Activity activity;

    private String type;

    private String currentBillingID="";
    private boolean PERMISSION_TO_ADD=false;

    //Strings, used for the data storing process
    String price="";
    String taxPercent="";
    String unit="";

    private ProgressDialog progressDialog;

    public AddItemAdapter(List<Items> list, Context mContext, Activity activity, String currentBillingID,String type) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
        this.currentBillingID = currentBillingID;
        this.type=type;
    }

    @NonNull
    @Override
    public AddItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_add_items_items,parent,false);

        return new AddItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddItemViewHolder holder, int position) {

        final Items items=list.get(position);
        holder.name.setText(items.getName().toString());

        //First we need, to fetch the current billing id from current billing nod
        if (type.equals("bill"))
        {
            DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        //Snashot exisits means to set the permission true
                        PERMISSION_TO_ADD=true;

                    }
                    else
                    {
                        //Snashopt do not exists, so we need to set the permission variable as fasle
                        PERMISSION_TO_ADD=false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        //Snashot exisits means to set the permission true
                        PERMISSION_TO_ADD=true;

                    }
                    else
                    {
                        //Snashopt do not exists, so we need to set the permission variable as fasle
                        PERMISSION_TO_ADD=false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("bill"))
                {
                    unit=items.getUnit();
                    price=items.getPrice();
                    taxPercent=items.getTax();


                    progressDialog=new ProgressDialog(activity);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show(); //Starting the progress dialog
                    progressDialog.setContentView(R.layout.dialog_loading);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    if (PERMISSION_TO_ADD)
                    {
                        DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(currentBillingID);
                        String itemID=ref2.push().getKey();

                        final Map<String,Object>map=new HashMap<>();
                        map.put("uid",itemID);
                        map.put("itemid",items.getUid());
                        map.put("qty","1");
                        map.put("unitPrice",price);
                        map.put("taxPercent",taxPercent);
                        map.put("unit",unit);

                        ref2.child(itemID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    activity.finish();




                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                                    mdToast.show();
                                    activity.finish();
                                }
                            }
                        });
                    }
                    else
                    {
                        MDToast mdToast=MDToast.makeText(mContext,"Select a party first",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                        mdToast.show();
                        progressDialog.dismiss();
                    }
                }
                else
                {
                    unit=items.getUnit();
                    price=items.getPrice();
                    taxPercent=items.getTax();


                    progressDialog=new ProgressDialog(activity);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show(); //Starting the progress dialog
                    progressDialog.setContentView(R.layout.dialog_loading);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    if (PERMISSION_TO_ADD)
                    {
                        DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(currentBillingID);
                        String itemID=ref2.push().getKey();

                        final Map<String,Object>map=new HashMap<>();
                        map.put("uid",itemID);
                        map.put("itemid",items.getUid());
                        map.put("qty","1");
                        map.put("unitPrice",price);
                        map.put("taxPercent",taxPercent);
                        map.put("unit",unit);

                        ref2.child(itemID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    activity.finish();




                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                                    mdToast.show();
                                    activity.finish();
                                }
                            }
                        });
                    }
                    else
                    {
                        MDToast mdToast=MDToast.makeText(mContext,"Select a party first",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                        mdToast.show();
                        progressDialog.dismiss();
                    }
                }

            }
        });
























    }

    @Override
    public int getItemCount() {
        return list.size();
    }





    public class AddItemViewHolder extends RecyclerView.ViewHolder
    {

        TextView name;

        public AddItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name_Item);

        }
    }


}
