package com.example.mycheckins;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MotionEventCompat;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private FragmentManager fManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        mContext = MainActivity.this;
        fManager = getFragmentManager();

        MyListFragment nlFragment = new MyListFragment(fManager, mContext);
        FragmentTransaction ft = fManager.beginTransaction();
        ft.replace(R.id.your_placeholder, nlFragment);
        ft.commit();

        //Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add:


                FragmentTransaction fTransaction = fManager.beginTransaction();
                RegDetailFragment rdFragment = new RegDetailFragment(fManager, mContext);
                Bundle bd = new Bundle();
                bd.putString("title", "showRegAct");
                rdFragment.setArguments(bd);
                fTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);
                fTransaction.replace(R.id.your_placeholder, rdFragment);
                fTransaction.addToBackStack(null);
                fTransaction.commit();
                //Toast.makeText(this, "Add Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_help:
                //Toast.makeText(this, "Help Selected", Toast.LENGTH_SHORT).show();
                Intent showWebViewActivity = new Intent(getApplicationContext(), WebViewActivity.class);
                startActivity(showWebViewActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            getSupportActionBar().show();
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
