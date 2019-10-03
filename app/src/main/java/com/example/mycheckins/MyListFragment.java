package com.example.mycheckins;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

@SuppressLint("ValidFragment")
public class MyListFragment extends Fragment{
    private FragmentManager fManager;
    private ListView list_checkins;
    private Context con;

    @SuppressLint("ValidFragment")
    public MyListFragment(FragmentManager fManager, Context c) {
        this.fManager = fManager;
        con = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_newlist, container, false);
        list_checkins = (ListView) view.findViewById(R.id.list_news);

        DBManager dbMan = new DBManager(getActivity().getApplicationContext());
        //dbMan.DeleteAllRows();
        String[] recs = dbMan.GetAllRecords();
        if (recs == null)
            return view;

        final Vector<String> titles = new Vector<>();
        final Vector<String> dates = new Vector<>();
        final Vector<String> places = new Vector<>();
        for (int i = 0; i < recs.length; i++) {
            String[] oneRec = recs[i].split("!@#");
            titles.add(oneRec[0]);
            dates.add(oneRec[1]);
            places.add(oneRec[2]);
        }

        String cnt = String.valueOf(recs.length);

        ItemAdapter myAdapter = new ItemAdapter(con, titles, dates, places);
        list_checkins.setAdapter(myAdapter);
        list_checkins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentTransaction fTransaction = fManager.beginTransaction();
                RegDetailFragment rdFragment = new RegDetailFragment(fManager, con);
                Bundle bd = new Bundle();
                bd.putString("title", titles.get(i));
                rdFragment.setArguments(bd);
                fTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);
                fTransaction.replace(R.id.your_placeholder, rdFragment);
                fTransaction.addToBackStack(null);
                fTransaction.commit();
            }
        });
        return view;
    }
}
