package com.example.initiativetracker;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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
    boolean canmove = false;

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
        holder.nametextView.setText(recyclerEntities.get(position).name);
        holder.hpTextView.setText(recyclerEntities.get(position).hp);
        holder.maxhpTextView.setText(recyclerEntities.get(position).maxHp);
        holder.acTextView.setText(recyclerEntities.get(position).ac);

        holder.hpbar.setProgress(recyclerEntities.get(position).hpPercentage);

        holder.hppicker.setMinValue(0);
        holder.hppicker.setValue(Integer.valueOf(holder.hpTextView.getText().toString()));
        holder.hppicker.setMaxValue(Integer.valueOf(holder.maxhpTextView.getText().toString()));
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
        TextView nametextView, hpTextView, maxhpTextView,acTextView;
        NumberPicker hppicker;
        ProgressBar hpbar;
        View v;
        ImageView iv;

        public AlertDialog getAlertDialog(View view)
        {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
            // View mView = view;
            View mView = MainActivity.Instance.getLayoutInflater().inflate(R.layout.dialogcreatureedit, null);
            //View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            final EditText name = (EditText) mView.findViewById(R.id.nameEditText);
            final EditText hp = (EditText) mView.findViewById(R.id.hpEditText);
            final EditText maxhp = (EditText) mView.findViewById(R.id.maxhpEditText);
            final EditText ac = (EditText) mView.findViewById(R.id.acEditText);

            name.setText(nametextView.getText().toString());
            hp.setText(hpTextView.getText().toString());
            maxhp.setText(maxhpTextView.getText().toString());
            ac.setText(acTextView.getText().toString());

            Button confirm = (Button) mView.findViewById(R.id.btnConfirm);
            Button delete = (Button) mView.findViewById(R.id.btnDelete);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String n = name.getText().toString();
                    String hpstr = hp.getText().toString();
                    String maxhpstr = maxhp.getText().toString();
                    String acstr = ac.getText().toString();
                    nametextView.setText(n);
                    hpTextView.setText(hpstr);
                    maxhpTextView.setText(maxhpstr);
                    acTextView.setText(acstr);

                    hppicker.setMaxValue(Integer.valueOf(maxhpstr));
                    hppicker.setValue(Integer.valueOf(hpstr));
                    int val = (int)((Integer.parseInt(hpstr)*1.0)/(Integer.parseInt(maxhpstr))*100.0);
                    hpbar.setProgress(val);

                    recyclerEntities.get(position).name = n;
                    recyclerEntities.get(position).hp = hpstr;
                    recyclerEntities.get(position).maxHp = maxhpstr;
                    recyclerEntities.get(position).ac = acstr;

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
            nametextView = itemView.findViewById(R.id.nametextView);
            hpTextView = itemView.findViewById(R.id.hpTextView);
            maxhpTextView = itemView.findViewById(R.id.maxhptextView);
            acTextView = itemView.findViewById(R.id.acTextView);

            hpbar = (ProgressBar) itemView.findViewById(R.id.vertical_progressbar);
            hppicker = itemView.findViewById(R.id.numberPicker);

            hppicker.setMinValue(0);
            hppicker.setValue(Integer.valueOf(hpTextView.getText().toString()));
            hppicker.setMaxValue(Integer.valueOf(maxhpTextView.getText().toString()));

            hppicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    int value = numberPicker.getValue();
                    int maxhp = Integer.valueOf(maxhpTextView.getText().toString());
                    int val = (int)((value*1.0)/(maxhp)*100.0);
                    hpbar.setProgress(val);
                    hpTextView.setText(String.valueOf(value));
                }
            });
            v = itemView;
            itemView.setOnClickListener(this);
            iv = itemView.findViewById(R.id.imageView);
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {
                        canmove = true;
                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                canmove = false;
                            }
                        }.start();
                    }
                    return false;
                }

            });

        }

        boolean open = false;

        @Override
        public void onClick(View view) {
            if(!open) {
                position = getAdapterPosition();
                View mView = MainActivity.Instance.getLayoutInflater().inflate(R.layout.dialogcreatureedit, null);
                AlertDialog dialog = getAlertDialog(v);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        open = true;
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        open = false;
                    }
                });
                dialog.show();
            }
        }

    }
}















