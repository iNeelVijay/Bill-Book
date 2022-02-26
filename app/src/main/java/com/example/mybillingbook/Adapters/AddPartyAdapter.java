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

import com.example.mybillingbook.LoginActivity;
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

public class AddPartyAdapter extends RecyclerView.Adapter<AddPartyAdapter.AddPartyViewHolder> {

    private List<Parties> list;
    private Context mContext;

    private Activity activity;

    private String type;

    private ProgressDialog progressDialog;

    public AddPartyAdapter(List<Parties> list, Context mContext, Activity activity,String type) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
        this.type=type;
    }

    @NonNull
    @Override
    public AddPartyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_addpartyitems,parent,false);

        return new AddPartyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddPartyViewHolder holder, int position) {

        final Parties parties=list.get(position);

        holder.name.setText(parties.getName());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("bill"))
                {
                    progressDialog=new ProgressDialog(activity);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show(); //Starting the progress dialog
                    progressDialog.setContentView(R.layout.dialog_loading);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Current Billing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final Map<String,Object>map=new HashMap<>();

                    String billID=ref.push().getKey();
                    map.put("billid",billID);
                    map.put("billto",parties.getUid());
                    map.put("date","null");
                    map.put("time","null");
                    map.put("billFrom",FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                activity.finish();

                            }
                            else
                            {
                                MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                mdToast.show();
                                progressDialog.dismiss();
                                activity.finish();
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

                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Current Quotation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final Map<String,Object>map=new HashMap<>();

                    String billID=ref.push().getKey();
                    map.put("billid",billID);
                    map.put("billto",parties.getUid());
                    map.put("date","null");
                    map.put("time","null");
                    map.put("billFrom",FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                activity.finish();

                            }
                            else
                            {
                                MDToast mdToast=MDToast.makeText(mContext,task.getException().toString(),MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                mdToast.show();
                                progressDialog.dismiss();
                                activity.finish();
                            }
                        }
                    });

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }





    public class AddPartyViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;

        public AddPartyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_item);















        }
    }
}
