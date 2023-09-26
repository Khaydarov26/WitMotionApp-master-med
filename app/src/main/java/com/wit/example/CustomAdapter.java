package com.wit.example;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHoler> {

    private Context context;
    private ArrayList<String> acc_X, acc_Y, acc_Z,time;

    CustomAdapter(Context context,
                  ArrayList acc_X,
                  ArrayList acc_Y,
                  ArrayList acc_Z,
                  ArrayList time){
        this.context = context;
        this.acc_X = acc_X;
        this.acc_Y = acc_Y;
        this.acc_Z = acc_Z;
        this.time = time;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHoler(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {



        holder.acc_X.setText(String.valueOf(acc_X.get(position)));
        holder.acc_Y.setText(String.valueOf(acc_Y.get(position)));
        holder.acc_Z.setText(String.valueOf(acc_Z.get(position)));

        holder.time.setText(String.valueOf(time.get(position)));
    }

    @Override
    public int getItemCount() {
        return time.size() ;
    }

    public static class MyViewHoler extends RecyclerView.ViewHolder{

        TextView time, acc_X, acc_Y, acc_Z;
        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            acc_X = itemView.findViewById(R.id.acc_X);
            acc_Y = itemView.findViewById(R.id.acc_Y);
            acc_Z = itemView.findViewById(R.id.acc_Z);

            time = itemView.findViewById(R.id.time_db);
        }
    }
}