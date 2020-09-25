package com.example.initiativetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    List<RecyclerEntity> recyclerEntities;
    RecyclerAdapter inst;
    public RecyclerAdapter(List<RecyclerEntity> recyclerEntities) {
        this.recyclerEntities = recyclerEntities;
        inst = this;
    }

    public void nextTurn()
    {
        for(int i = 0; i< recyclerEntities.size()-1; i++)
        {
            int fromPosition = i;
            int toPosition = i+1;
            Collections.swap(recyclerEntities, fromPosition, toPosition);
        }
        this.notifyDataSetChanged();
    }

    public void clear()
    {
        if(this.recyclerEntities != null && !this.recyclerEntities.isEmpty()) {
            recyclerEntities.clear();
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.rowCountTextView.setText(recyclerEntities.get(position).roll);
        holder.textView.setText(recyclerEntities.get(position).name);
        //holder.position = position;
        recyclerEntities.get(holder.getAdapterPosition()).viewHolder = holder;
    }

    @Override
    public int getItemCount() {
        return recyclerEntities.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int position;
        ImageView imageView;
        TextView textView, rowCountTextView;
        View v;

        public AlertDialog getAlertDialog(View view)
        {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
            // View mView = view;
            View mView = MainActivity.Instance.getLayoutInflater().inflate(R.layout.dialogcreatureedit, null);
            //View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            final EditText name = (EditText) mView.findViewById(R.id.nameEditText);
            final EditText roll = (EditText) mView.findViewById(R.id.rollEditText);

            name.setText(textView.getText().toString());
            roll.setText(rowCountTextView.getText().toString());

            Button confirm = (Button) mView.findViewById(R.id.btnConfirm);
            Button delete = (Button) mView.findViewById(R.id.btnDelete);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String n = name.getText().toString();
                    String r = roll.getText().toString();
                    textView.setText(n);
                    rowCountTextView.setText(r);
                    recyclerEntities.get(position).name = n;
                    recyclerEntities.get(position).roll = r;
                    dialog.dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerEntities.remove(position);
                    inst.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            return dialog;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           // imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);
            v = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            position = getAdapterPosition();
            View mView = MainActivity.Instance.getLayoutInflater().inflate(R.layout.dialogcreatureedit, null);
            AlertDialog dialog = getAlertDialog(v);
            dialog.show();


        }

    }
}















