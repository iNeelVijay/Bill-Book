package com.example.mybillingbook.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.Models.BillItems;
import com.example.mybillingbook.Models.Items;
import com.example.mybillingbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


import java.util.List;

public class BillViewerAdapter extends RecyclerView.Adapter<BillViewerAdapter.BillViewerViewHolder>
{

    private List<BillItems> list;
    private Context mContext;
    private Activity activity;
    private int SERIAL_NUMBER;


    public BillViewerAdapter(List<BillItems> list, Context mContext, Activity activity, int SERIAL_NUMBER) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
        this.SERIAL_NUMBER = SERIAL_NUMBER;
    }

    @NonNull
    @Override
    public BillViewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_billvieweritems,parent,false);

        return new BillViewerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewerViewHolder holder, int position) {

        SERIAL_NUMBER++;
        final BillItems billItems=list.get(position);

        holder.serialNumber.setText(String.valueOf(SERIAL_NUMBER));

        //Now, we need to fetch the item name, with ITEM Id
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(billItems.getItemid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Items items=snapshot.getValue(Items.class);

                    holder.itemName.setText(items.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemUnitPrice.setText(billItems.getUnitPrice());

        holder.itemQty.setText("("+billItems.getUnit()+")"+" X "+billItems.getQty());

        //Now, we need to get the item total
        double Price=Double.parseDouble(billItems.getUnitPrice());
        int qty=Integer.parseInt(billItems.getQty());

        double taxPercent;
        double netTotal;
        double Total=(Price*qty);
        if ((billItems.getTaxPercent()).equals("inclusive"))
        {
            taxPercent=0;
            netTotal= Total;
        }
        else
        {
            taxPercent=Double.parseDouble(billItems.getTaxPercent());
            netTotal= ((taxPercent/100)*Total+Total);
        }





        holder.itemTotal.setText(netTotal+"/-");







    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BillViewerViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the items on the itemView
        private TextView serialNumber;
        private TextView itemName;
        private TextView itemUnitPrice;
        private TextView itemQty;
        private TextView itemTotal;

        public BillViewerViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assinging the objects on the screen
            serialNumber=itemView.findViewById(R.id.sno_billVitem);
            itemName=itemView.findViewById(R.id.itemName_billVitem);
            itemUnitPrice=itemView.findViewById(R.id.itemunitPrice_billVitem);
            itemQty=itemView.findViewById(R.id.itemQty_billVitem);
            itemTotal=itemView.findViewById(R.id.itemTotal_billVitem);



        }
    }


}
