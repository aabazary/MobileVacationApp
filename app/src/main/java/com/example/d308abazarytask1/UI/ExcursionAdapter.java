package com.example.d308abazarytask1.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308abazarytask1.Entities.Excursion;
import com.example.d308abazarytask1.R;

import java.util.ArrayList;
import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {


    class ExcursionViewHolder extends RecyclerView.ViewHolder{
        private final TextView excursionItemView;
        private final TextView excursionItemView2;
        private ExcursionViewHolder(View itemView){
            super(itemView);
            excursionItemView=itemView.findViewById(R.id.textView2);
            excursionItemView2=itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    final Excursion current=mExcursions.get(position);
                    Intent intent=new Intent(context,ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionID());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("date", current.getDate());
                    intent.putExtra("vacID", current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }
    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }
    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.excursion_list_item,parent,false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if(mExcursions!=null){
            Excursion current=mExcursions.get(position);
            String name=current.getTitle();
            int prodID= current.getVacationID();
            holder.excursionItemView.setText(name);
            holder.excursionItemView2.setText(Integer.toString(prodID));
        }
        else{
            holder.excursionItemView.setText("No excursion name");
            holder.excursionItemView.setText("No product id");
        }
    }

    public void setExcursions(List<Excursion> excursions){
        mExcursions=excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExcursions.size();
    }
    public void updateExcursion(int position, Excursion updatedExcursion) {
        mExcursions.set(position, updatedExcursion);
        notifyItemChanged(position);
    }


}


