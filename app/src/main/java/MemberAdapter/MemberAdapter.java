package MemberAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashtech.sahayak_updated.Databases.UserHelperClass;
import com.ashtech.sahayak_updated.R;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewholder> {
    ArrayList<UserHelperClass> nameList;
    Context applicationContext;
    private OnSelectListener monSelectListener;

    public MemberAdapter(ArrayList<UserHelperClass> nameList, Context applicationContext, OnSelectListener onSelectListener) {
        this.nameList = nameList;
        this.applicationContext = applicationContext;
        this.monSelectListener = onSelectListener;
    }

    @NonNull
    @Override
    public MemberViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        MemberViewholder memberViewholder = new MemberViewholder(v, applicationContext, nameList, monSelectListener);

        return memberViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewholder holder, int position) {
        UserHelperClass currentUserobj = nameList.get(position);
        holder.name_txt.setText(currentUserobj.getFullname());

        if(currentUserobj.getActive())
        holder.active_img.setImageResource(R.drawable.green_circle_icon);
        else
            holder.active_img.setImageResource(R.drawable.red_circle);



    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


    public static class MemberViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name_txt;
        ImageView  active_img;
        Context c;
        ArrayList<UserHelperClass> nameArraylist;
        OnSelectListener onSelectListener;

        public MemberViewholder(@NonNull View itemView, Context c, ArrayList<UserHelperClass> nameArraylist, OnSelectListener monSelectListener) {
            super(itemView);
            this.c = c;
            this.nameArraylist = nameArraylist;
            this.onSelectListener=monSelectListener;
            itemView.setOnClickListener(this);
            name_txt = itemView.findViewById(R.id.item_title);
            active_img=itemView.findViewById(R.id.ActivecardCircleBtn);



        }

        @Override
        public void onClick(View v) {
            onSelectListener.onSelectListener(getAdapterPosition());
        }
    }

    public interface OnSelectListener {
        void onSelectListener(int position);

    }

}


