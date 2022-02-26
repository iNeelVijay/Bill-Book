package com.example.mybillingbook.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.BillViewerActivity;
import com.example.mybillingbook.HomeActivity;
import com.example.mybillingbook.Models.Bills;
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

import java.util.List;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder>
{
    private List<Bills> list;
    private Context mContext;
    private Activity activity;
    private String type;

    //The object for the progress dialog
    private ProgressDialog progressDialog;

    public BillsAdapter(List<Bills> list, Context mContext, Activity activity,String type) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
        this.type=type;
    }

    @NonNull
    @Override
    public BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_bills,parent,false);

        return new BillsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillsViewHolder holder, int position) {

        final Bills bills=list.get(position);

        holder.billID.setText(bills.getBillid());
        holder.date.setText(bills.getDate());
        holder.time.setText(bills.getTime());

        //Now, we need to fetch the party name
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bills.getBillto());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Parties parties=snapshot.getValue(Parties.class);
                    holder.partyName.setText("To,\n"+parties.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (type.equals("bill"))
               {
                   Intent i=new Intent(mContext, BillViewerActivity.class);
                   i.putExtra("ID",bills.getBillid());
                   i.putExtra("type","bill");
                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   mContext.startActivity(i);
               }
               else
               {
                   Intent i=new Intent(mContext, BillViewerActivity.class);
                   i.putExtra("ID",bills.getBillid());
                   i.putExtra("type","quotation");
                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   mContext.startActivity(i);
               }
            }
        });

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //First, we will show, a confirmation dialouge
                Dialog dialog=new Dialog(activity);
                dialog.setContentView(R.layout.dialog_info);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                //Following are the threee obujects on the dialog
                TextView msg=dialog.findViewById(R.id.msg_dialog);
                TextView positiveBtn=dialog.findViewById(R.id.yes_dialog);
                TextView negativeBtn=dialog.findViewById(R.id.no_dialog);

                if (type.equals("bill"))
                {
                    msg.setText("Are you sure you want to remove the bill? Bill removed can not be restored.");
                }
                else
                {
                    msg.setText("Are you sure you want to remove the quotation? Quotation removed can not be restored.");
                }


                positiveBtn.setText("YES");
                negativeBtn.setText("NO");

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       if (type.equals("bill"))
                       {
                           progressDialog=new ProgressDialog(activity);
                           progressDialog.setCanceledOnTouchOutside(false);
                           progressDialog.show(); //Starting the progress dialog
                           progressDialog.setContentView(R.layout.dialog_loading);
                           progressDialog.setCancelable(false);
                           progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                           dialog.cancel();

                           DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Bills").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bills.getBillid());
                           reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if (task.isSuccessful())
                                   {
                                       DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Bill Items").child(bills.getBillid());
                                       ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful())
                                               {
                                                   progressDialog.dismiss();
                                               }
                                               else{
                                                   progressDialog.dismiss();
                                                   MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                                   mdToast.show();
                                               }
                                           }
                                       });

                                   }
                                   else
                                   {
                                       progressDialog.dismiss();
                                       MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                       mdToast.show();


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
                           progressDialog.setCancelable(false);
                           progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                           dialog.cancel();

                           DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Quotations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bills.getBillid());
                           reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if (task.isSuccessful())
                                   {
                                       DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Quotation Items").child(bills.getBillid());
                                       ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful())
                                               {
                                                   progressDialog.dismiss();
                                               }
                                               else{
                                                   progressDialog.dismiss();
                                                   MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                                   mdToast.show();
                                               }
                                           }
                                       });

                                   }
                                   else
                                   {
                                       progressDialog.dismiss();
                                       MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                       mdToast.show();


                                   }

                               }
                           });
                       }


                    }
                });

                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       dialog.cancel();
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

    public class BillsViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the objects on the item view
        TextView billID;
        TextView partyName;
        TextView totalAmount;
        TextView date;
        TextView time;

        TextView removeBtn;
        TextView viewBtn;

        public BillsViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assiging ids to the respective objecrts
            billID=itemView.findViewById(R.id.billID_billItem);
            partyName=itemView.findViewById(R.id.partyName_billItem);
            totalAmount=itemView.findViewById(R.id.totalAmount_billItem);
            date=itemView.findViewById(R.id.date_billItem);
            time=itemView.findViewById(R.id.time_billItem);

            //The two action buttons on the item view
            removeBtn=itemView.findViewById(R.id.removeBtn_billItem);
            viewBtn=itemView.findViewById(R.id.viewBtn_billItem);


        }
    }



}
