package com.android.travel;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
public class TabtestActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent myin = getIntent();
        String src = myin.getStringExtra("dest");
        src = src.replaceAll("%20", " ");
        String dst = myin.getStringExtra("tempdest");
        dst = dst.replaceAll("%20"," ");
        String distance = myin.getStringExtra("sDistance");
        double fare = myin.getDoubleExtra("fare",0);
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        Toast.makeText(TabtestActivity.this,distance,Toast.LENGTH_SHORT).show();
        tabHost.setup();
        
        TabSpec spec1 = tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Tab 1");
        TextView rick = (TextView)findViewById(R.id.txt1);
        rick.setText("Source Address: " + "   "  + src);
        //rick.setText("\n");
        rick.append("\n\nDestination Address: "+ "  " + dst);
        rick.append("\n\nDistance: " + "   " + distance);
        String fr = String.valueOf(fare);
        rick.append("\n\nEstimated Fare:   Rs. " + fr);
        TabSpec spec2 = tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Tab 2");
        Context ctx = this.getApplicationContext();
        Intent i = new Intent(ctx,MapTabView.class);
        spec2.setContent(i);
        
        TabSpec spec3 = tabHost.newTabSpec("Tab 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Tab 3");
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
    }

	
}
