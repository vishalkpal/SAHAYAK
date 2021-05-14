package com.ashtech.sahayak_updated.Maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashtech.sahayak_updated.Databases.CircleJoin;
import com.ashtech.sahayak_updated.R;

import java.util.ArrayList;

class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewholder>  {
    ArrayList<CircleJoin> groupList;
    Context applicationContext;

    private OnNoteListener mOnNoteListener;
    public GroupAdapter(ArrayList<CircleJoin> groupList, Context applicationContext,OnNoteListener onNoteListener) {
        this.groupList=groupList;
        this.applicationContext=applicationContext;
        this.mOnNoteListener=onNoteListener;
    }

    @NonNull
    @Override
    public GroupViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardgroup,parent,false);

        GroupViewholder groupViewholder=new GroupViewholder(v,applicationContext,groupList,mOnNoteListener);


        return groupViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.GroupViewholder holder, int position) {

        CircleJoin currentgrpobj=groupList.get(position);
        holder.grp_txt.setText(currentgrpobj.getGrpname());

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }




    public class GroupViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView grp_txt;
        Context c;
        ArrayList<CircleJoin> groupList;
        OnNoteListener onNoteListener;

        public GroupViewholder(@NonNull View itemView, Context applicationContext, ArrayList<CircleJoin> groupList,OnNoteListener onNoteListener) {
            super(itemView);
            this.c=c;
            this.groupList= groupList;
            this.onNoteListener=onNoteListener;
            itemView.setOnClickListener(this);
            grp_txt=itemView.findViewById(R.id.item_group);

        }


        @Override
        public void onClick(View v) {
            onNoteListener.onNoteListener(getAdapterPosition());


        }
    }

    public  interface  OnNoteListener{
        void onNoteListener(int position);

    }
}
