package com.example.mybillingbook.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.BillViewerActivity;
import com.example.mybillingbook.Models.Bills;
import com.example.mybillingbook.Models.Parties;
import com.example.mybillingbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OnScreenAdapter extends RecyclerView.Adapter<OnScreenAdapter.OnScreenViewHolder>
{

    private List<Bills> list;
    private Context mContext;
    private String type;

    public OnScreenAdapter(List<Bills> list, Context mContext, String type) {
        this.list = list;
        this.mContext = mContext;
        this.type = type;
    }

    @NonNull
    @Override
    public OnScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_itemonscreen,parent,false);

        return new OnScreenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnScreenViewHolder holder, int position) {

        final Bills bills=list.get(position);

        holder.id.setText(bills.getBillid());
        holder.date.setText(bills.getDate());
        holder.time.setText(bills.getTime());

        //Now, fetching the party name
        DatabaseReference ref;

            ref= FirebaseDatabase.getInstance().getReference().child("Parties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bills.getBillto());

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        Parties parties=snapshot.getValue(Parties.class);
                        holder.name.setText(parties.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            holder.viewbtn.setOnClickListener(new View.OnClickListener() {
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





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OnScreenViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the objects on the itemView
        TextView id;
        TextView name;
        TextView time;
        TextView date;
        TextView viewbtn;

        public OnScreenViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assinging the objects with their respective ids
            id=itemView.findViewById(R.id.id_onscreenadapter);
            name=itemView.findViewById(R.id.name_onscreenadapter);
            time=itemView.findViewById(R.id.time_onscreenadapter);
            date=itemView.findViewById(R.id.date_onscreenadapter);
            viewbtn=itemView.findViewById(R.id.viewBtn_onscreenadapter);

        }
    }

}
