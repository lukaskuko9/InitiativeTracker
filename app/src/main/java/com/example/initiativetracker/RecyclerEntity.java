package com.example.initiativetracker;

import android.widget.ProgressBar;

public class RecyclerEntity {
    public String name;
    public String hp;
    public String maxHp;
    public String ac;
    public int hpPercentage;

    public RecyclerAdapter.ViewHolder viewHolder;

    public RecyclerEntity(String title, String hp, String maxHp, String ac) {
        this.name = title;
        this.hp = hp;
        this.maxHp = maxHp;
        this.hpPercentage = (int)((Integer.parseInt(hp)*1.0)/(Integer.parseInt(maxHp))*100.0);
        this.ac = ac;
    }

    private static final String divider = " ";
    public String GetTextData() {
        return name + divider + hp + divider + maxHp + divider + ac + "\n";
    }

    public static RecyclerEntity GetInstance(String text)
    {
        try{
            final int otherCols = 3;

            String split[] = text.split(divider);
            String name = "";
            String hp = split[split.length-3];
            String maxhp = split[split.length-2];
            String ac = split[split.length-1];

            for(int i = 0; i< split.length - otherCols; i++)
                name += split[i] + divider;


            return new RecyclerEntity(name, hp, maxhp,ac);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}