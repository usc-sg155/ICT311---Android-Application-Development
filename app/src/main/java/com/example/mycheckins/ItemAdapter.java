package com.example.mycheckins;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    String[] titles;
    String[] dates;
    String[] places;
    Context con;

    public ItemAdapter(Context c, Vector t, Vector d, Vector p){
        titles = new String[t.size()];
        dates = new String[d.size()];
        places = new String[p.size()];
        titles = (String[]) t.toArray(titles);
        dates = (String[]) d.toArray(dates);
        places = (String[]) p.toArray(places);
        con = c;
        //Toast.makeText(c, titles[5]+dates[5]+places[5], Toast.LENGTH_SHORT).show();

        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(con).inflate(R.layout.item_layout, viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.txt_item_title1 = (TextView) view.findViewById(R.id.titleView);
            viewHolder.txt_item_title2 = (TextView) view.findViewById(R.id.dateView);
            viewHolder.txt_item_title3 = (TextView) view.findViewById(R.id.placeView);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        String title = titles[i];
        String date = dates[i];
        String place = places[i];

        viewHolder.txt_item_title1.setText(title);
        viewHolder.txt_item_title2.setText(date);
        viewHolder.txt_item_title3.setText(place);
        return view;

    }

    private class ViewHolder{
        TextView txt_item_title1;
        TextView txt_item_title2;
        TextView txt_item_title3;
    }
}
