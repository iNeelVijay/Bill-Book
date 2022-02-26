package com.example.mybillingbook.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.AddItemActivity;
import com.example.mybillingbook.Models.Items;
import com.example.mybillingbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

    List<Items> list;
    Context mContext;

    ProgressDialog progressDialog;

    public ItemsAdapter(List<Items> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_items,parent,false);

        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {

        final Items items=list.get(position);

        holder.delBtn.setVisibility(View.GONE);

        //Now, we will set the relevant data to the items
        holder.productId.setText(items.getItemid());
        holder.productName.setText(items.getName());
        holder.productPrice.setText("Rs."+items.getPrice());
        holder.productUnit.setText(items.getUnit());

        if ((items.getTax()).equals("inclusive"))
        {
            holder.productTax.setText("Price Inclusive Tax");
        }
        else
        {
            holder.productTax.setText(items.getTax()+" %");
        }

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_info);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                //Following are the threee obujects on the dialog
                TextView msg=dialog.findViewById(R.id.msg_dialog);
                TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                msg.setText("Are you sure you want to delete the item?");

                positiveBtn.setText("Yes");
                negativeBtn.setText("No");

                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();

                    }
                });

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressDialog=new ProgressDialog(mContext);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show(); //Starting the progress dialog
                        progressDialog.setContentView(R.layout.dialog_loading);
                        progressDialog.setCancelable(false);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(items.getUid());
                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    dialog.cancel();
                                    MDToast mdToast=MDToast.makeText(mContext,"Deleted",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                                    mdToast.show();
                                    progressDialog.dismiss();
                                }
                                else
                                {
                                    dialog.cancel();
                                    MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                    mdToast.show();
                                    progressDialog.dismiss();
                                }
                            }
                        });



                    }
                });

                dialog.show();
            }
        });


        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, AddItemActivity.class);
                i.putExtra("type","edit");
                i.putExtra("id",items.getUid());
                mContext.startActivity(i);
            }
        });





    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    public class ItemsViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the items on the item layout
        TextView productId;
        TextView productName;
        TextView productPrice;
        TextView productUnit;
        TextView productTax;

        ImageView delBtn;
        ImageView editBtn;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            productId=itemView.findViewById(R.id.productId_item_itemlayout);
            productName=itemView.findViewById(R.id.productName_item_itemlayout);
            productPrice=itemView.findViewById(R.id.productPrice_item_itemlayout);
            productUnit=itemView.findViewById(R.id.productUnit_item_itemlayout);
            productTax=itemView.findViewById(R.id.productPercent_item_itemlayout);
            delBtn=itemView.findViewById(R.id.delBtn_item_itemlayout);
            editBtn=itemView.findViewById(R.id.editBtn_item_itemlayout);




        }
    }
}
