package com.example.mybillingbook.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.HomeActivity;
import com.example.mybillingbook.Models.BillItems;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.Items;
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

public class BillItemOnScreenAdapter extends RecyclerView.Adapter<BillItemOnScreenAdapter.BillItemOnScreenViewHolder>
{
    private List<BillItems> list;
    private Context mContext;

    private Activity activity;

    private String type;

    String BILL_ID;

    int qty;

    int cQ;
    int cQ1;

    private ProgressDialog progressDialog;

    public BillItemOnScreenAdapter(List<BillItems> list, Context mContext, Activity activity,String BILL_ID,String type) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
        this.BILL_ID=BILL_ID;
        this.type=type;
    }

    @NonNull
    @Override
    public BillItemOnScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_add_oncreenitems_items,parent,false);

        return new BillItemOnScreenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillItemOnScreenViewHolder holder, int position) {

        final BillItems billItems=list.get(position);





        //First we need to getch the static item data, with its item id
        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(billItems.getItemid());
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Items items1=snapshot.getValue(Items.class);
                    holder.name.setText(items1.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.unitPrice.setText("Rs "+billItems.getUnitPrice());

        double taxPercent;
        double taxAmount;

        //Now, we need to calculate the tax amount
        double price=Double.parseDouble(billItems.getUnitPrice());
        if ((billItems.getTaxPercent()).equals("inclusive"))
        {
             taxPercent=0.00;
            //Now we can set the tax amount
            holder.taxAmount.setText("Price inclusive tax");
            taxAmount=0.00;
        }
        else
        {
             taxPercent=Double.parseDouble(billItems.getTaxPercent());
            //Now, we have the price and tax percent, so we can get the tax amount
             taxAmount=(taxPercent/100)*price;

            //Now we can set the tax amount
            holder.taxAmount.setText("Rs "+taxAmount);
        }

        //Setting the total price
         qty=Integer.parseInt(billItems.getQty());
        double  netTotal=(price+taxAmount)*qty;
        holder.total.setText("Rs "+netTotal);

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("bill"))
                {
                    progressDialog=new ProgressDialog(activity);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show(); //Starting the progress dialog
                progressDialog.setContentView(R.layout.dialog_loading);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                int x=Integer.parseInt(billItems.getQty());
                x++;
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(BILL_ID).child(billItems.getUid());
                final Map<String,Object>map=new HashMap<>();
                map.put("qty",String.valueOf(x));
                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                }
                else
                {
                    progressDialog=new ProgressDialog(activity);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show(); //Starting the progress dialog
                    progressDialog.setContentView(R.layout.dialog_loading);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                    int x=Integer.parseInt(billItems.getQty());
                    x++;
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(BILL_ID).child(billItems.getUid());
                    final Map<String,Object>map=new HashMap<>();
                    map.put("qty",String.valueOf(x));
                    ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("bill"))
                {
                    progressDialog=new ProgressDialog(activity);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show(); //Starting the progress dialog
                    progressDialog.setContentView(R.layout.dialog_loading);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                    int x=Integer.parseInt(billItems.getQty());

                    if (x==1)
                    {
                        progressDialog.dismiss();
                        MDToast mdToast=MDToast.makeText(mContext,"Reached minimum limit",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                        mdToast.show();
                    }
                    else
                    {
                        x--;
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(BILL_ID).child(billItems.getUid());
                        final Map<String,Object>map=new HashMap<>();
                        map.put("qty",String.valueOf(x));
                        ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                }
                                else {
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
                else
                {
                    progressDialog=new ProgressDialog(activity);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show(); //Starting the progress dialog
                progressDialog.setContentView(R.layout.dialog_loading);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                int x=Integer.parseInt(billItems.getQty());

                if (x==1)
                {
                    progressDialog.dismiss();
                    MDToast mdToast=MDToast.makeText(mContext,"Reached minimum limit",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                    mdToast.show();
                }
                else
                {
                    x--;
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(BILL_ID).child(billItems.getUid());
                    final Map<String,Object>map=new HashMap<>();
                    map.put("qty",String.valueOf(x));
                    ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                }


            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (type.equals("bill"))
               {
                   Dialog dialog=new Dialog(activity);
                   dialog.setContentView(R.layout.dialog_info);
                   dialog.setCancelable(true);
                   dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                   //Following are the threee obujects on the dialog
                   TextView msg=dialog.findViewById(R.id.msg_dialog);
                   TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                   TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                   msg.setText("Are you sure to remove the item from list?");

                   positiveBtn.setText("YES");
                   negativeBtn.setText("NO");

                   positiveBtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           progressDialog=new ProgressDialog(activity);
                           progressDialog.setCanceledOnTouchOutside(false);
                           progressDialog.show(); //Starting the progress dialog
                           progressDialog.setContentView(R.layout.dialog_loading);
                           progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                           DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(BILL_ID).child(billItems.getUid());
                           ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful())
                                   {
                                       MDToast mdToast=MDToast.makeText(mContext,"Removed",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                       mdToast.show();
                                       progressDialog.dismiss();
                                       dialog.cancel();
                                   }
                                   else
                                   {
                                       MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                                       mdToast.show();
                                       progressDialog.dismiss();
                                       dialog.cancel();
                                   }
                               }
                           });

                       }
                   });

                   negativeBtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                       }
                   });


                   dialog.show();
               }
               else
               {
                   Dialog dialog=new Dialog(activity);
                   dialog.setContentView(R.layout.dialog_info);
                   dialog.setCancelable(true);
                   dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                   //Following are the threee obujects on the dialog
                   TextView msg=dialog.findViewById(R.id.msg_dialog);
                   TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                   TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                   msg.setText("Are you sure to remove the item from list?");

                   positiveBtn.setText("YES");
                   negativeBtn.setText("NO");

                   positiveBtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           progressDialog=new ProgressDialog(activity);
                           progressDialog.setCanceledOnTouchOutside(false);
                           progressDialog.show(); //Starting the progress dialog
                           progressDialog.setContentView(R.layout.dialog_loading);
                           progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                           DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(BILL_ID).child(billItems.getUid());
                           ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful())
                                   {
                                       MDToast mdToast=MDToast.makeText(mContext,"Removed",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                       mdToast.show();
                                       progressDialog.dismiss();
                                       dialog.cancel();
                                   }
                                   else
                                   {
                                       MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                                       mdToast.show();
                                       progressDialog.dismiss();
                                       dialog.cancel();
                                   }
                               }
                           });

                       }
                   });

                   negativeBtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                       }
                   });


                   dialog.show();
               }

            }
        });


        holder.qty.setText(billItems.getQty());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BillItemOnScreenViewHolder extends RecyclerView.ViewHolder
    {

        //Following are the objects on the itemView for the adadpter
        TextView name;
        TextView unit;
        TextView unitPrice;
        TextView taxAmount;
        TextView total;
        TextView qty;
        ImageView plus;
        ImageView minus;
        ImageView delete;

        public BillItemOnScreenViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assining the objects wit their respective ids
            name=itemView.findViewById(R.id.itemName_onScreen);
            unit=itemView.findViewById(R.id.itemUnit_onScreen);
            unitPrice=itemView.findViewById(R.id.itemUnitPrice_onScreen);
            taxAmount=itemView.findViewById(R.id.itemTaxAmount_onScreen);
            total=itemView.findViewById(R.id.itemTotal_onScreen);
            qty=itemView.findViewById(R.id.itemQty_onScreen);
            plus=itemView.findViewById(R.id.plusBtn_onScreen);
            minus=itemView.findViewById(R.id.minusBtn_onScreen);
            delete=itemView.findViewById(R.id.delBtn_onScreen);

        }
    }

}
