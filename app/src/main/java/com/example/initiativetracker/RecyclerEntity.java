package com.example.initiativetracker;

import androidx.annotation.NonNull;

public class RecyclerEntity {
    public String name;
    public String roll;
    public RecyclerAdapter.ViewHolder viewHolder;

    public RecyclerEntity(String title, String roll) {
        this.name = title;
        this.roll = roll;
    }

    public String GetTextData() {
        return name + " " + roll + "\n";
    }

    public static RecyclerEntity GetInstance(String text)
    {
        try{
            String split[] = text.split(" ");
            String name = "";
            String roll = split[split.length-1];

            for(int i = 0; i< split.length - 1; i++)
                name += split[i];


            return new RecyclerEntity(name, roll);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}