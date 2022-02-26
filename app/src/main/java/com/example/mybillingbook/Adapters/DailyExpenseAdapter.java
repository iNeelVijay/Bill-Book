package com.example.mybillingbook.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybillingbook.Models.DailyExpense;
import com.example.mybillingbook.R;

import java.util.List;

public class DailyExpenseAdapter extends RecyclerView.Adapter<DailyExpenseAdapter.DailyExpenseViewHolder> {

    private List<DailyExpense>list;
    private Context mContext;
    private String size;

    public DailyExpenseAdapter(List<DailyExpense> list, Context mContext, String size) {
        this.list = list;
        this.mContext = mContext;
        this.size = size;
    }

    @NonNull
    @Override
    public DailyExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.z_itemlayout_dailyexpenses,parent,false);

        return new DailyExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyExpenseViewHolder holder, int position) {

        final DailyExpense dailyExpense=list.get(position);
        if (size.equals("all"))
        {
            //Normally, show all the existing data
            String s1=dailyExpense.getDate();
            String s2=s1.replace('x','/');
            holder.date.setText(s2);

            holder.inc.setText(dailyExpense.getInc());
            holder.out.setText(dailyExpense.getOut());

        }
        else
        {
            int s= Integer.parseInt(size);
            //Normally, show all the existing data
            String s1=dailyExpense.getDate();
            String s2=s1.replace('x','/');
            holder.date.setText(s2);

            holder.inc.setText(dailyExpense.getInc());
            holder.out.setText(dailyExpense.getOut());

            if (position>s-1)
            {
                holder.itemView.setVisibility(View.GONE);
            }


        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DailyExpenseViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the objects on the screen
        TextView date;
        TextView inc;
        TextView out;

        public DailyExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assinging the objects to their respective ids
            date=itemView.findViewById(R.id.date_itemExp);
            inc=itemView.findViewById(R.id.inc_itemExp);
            out=itemView.findViewById(R.id.out_itemExp);


        }
    }
}
