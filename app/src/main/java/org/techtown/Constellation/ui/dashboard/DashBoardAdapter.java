package org.techtown.Constellation.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.techtown.Constellation.R;
import org.techtown.Constellation.link.UserObj;

import java.util.ArrayList;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.ViewHolder>{

    private int counter = 0,flag = 0;
    private ArrayList<String> Datas = null;
    private UserObj userObj;
    private String temp = "";

    DashBoardAdapter(ArrayList<String> DataList, int counter_n,UserObj userObj1,int Flag) {
        Datas = DataList;
        counter = counter_n;
        userObj = new UserObj();
        userObj = userObj1;
        flag = Flag;
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView4;
        TextView textView7;
        ViewHolder(View itemView){
            super(itemView);
            textView4 = itemView.findViewById(R.id.textView4);
            textView7 = itemView.findViewById(R.id.textView7);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position_info = getAdapterPosition();
                    if(position_info != RecyclerView.NO_POSITION){
                        textView7.setMaxLines(30);
                    }
                }
            });
        }
    }
    @Override
    public DashBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycleritem, parent, false) ;
        DashBoardAdapter.ViewHolder viewHolder = new DashBoardAdapter.ViewHolder(view) ;



        return viewHolder;
    }
    @Override
    public void onBindViewHolder(DashBoardAdapter.ViewHolder holder, int position) {
        if(flag == 0){
            //플래그가 0이면 그룹이면서 본인밖에 없는사람.
            temp = Datas.get(position);
            holder.textView4.setText(userObj.getname());
            holder.textView7.setText(temp);
        }
        else {
            temp = Datas.get(position);
            int count = counter--;
            holder.textView4.append(Integer.toString(count));
            holder.textView7.setText(temp);

        }
    }
    @Override
    public int getItemCount() {
        return Datas.size() ;
    }

}
