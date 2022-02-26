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

import com.example.mybillingbook.AddPartyActivity;
import com.example.mybillingbook.Models.Parties;
import com.example.mybillingbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

public class PartiesAdapter extends RecyclerView.Adapter<PartiesAdapter.PartiesViewHolder> {

    List<Parties> list;
    Context mContext;

    ProgressDialog progressDialog;

    public PartiesAdapter(List<Parties> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PartiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_parties,parent,false);

        return new PartiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartiesViewHolder holder, int position) {

        final Parties parties=list.get(position);

        holder.delBtn.setVisibility(View.GONE);


        //Now setting the data one by one
        holder.name.setText(parties.getName());
        holder.gst.setText(parties.getGst());
        holder.contact.setText(parties.getContact());
        holder.email.setText(parties.getEmail().toString());
        holder.adress.setText(parties.getAdress());

        //Setting a on click listener on the editbtn
        holder.editBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, AddPartyActivity.class);
                i.putExtra("type","edit");
                i.putExtra("id",parties.getUid());
                mContext.startActivity(i);
            }
        });

        //Setting a on click  listener on the delete Btn
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

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(parties.getUid());
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


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //The viewholder class for the adapter
    public class PartiesViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the objects on the itemView
        TextView gst;
        TextView name;
        TextView contact;
        TextView email;
        TextView adress;

        ImageView delBtn;
        ImageView editBTn;


        public PartiesViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assigning ids to the objects
            gst=itemView.findViewById(R.id.gst_partyItem);
            name=itemView.findViewById(R.id.name_partyItem);
            contact=itemView.findViewById(R.id.contact_partyItem);
            email=itemView.findViewById(R.id.email_partyItem);
            adress=itemView.findViewById(R.id.adress_partyItem);

            delBtn=itemView.findViewById(R.id.deleteBtn_partyItem);
            editBTn=itemView.findViewById(R.id.editBtn_partyItem);

        }
    }
}
