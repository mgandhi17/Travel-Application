package com.android.travel;


import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;


public class MapTabView extends MapActivity {
	private MyLocationOverlay mylocationoverlay;
	private MapController mapcontroller;
	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.maptabview);
		 MapView mapView = (MapView) findViewById(R.id.mapView);
		 mapView.setBuiltInZoomControls(true);
		 mapcontroller = mapView.getController();
		 mapView.setClickable(true);
		 mapView.setEnabled(true);
		 mapView.displayZoomControls(true);
		
		 mylocationoverlay = new MyLocationOverlay(this,mapView);
		 mapView.getOverlays().add(mylocationoverlay);
		 mylocationoverlay.enableCompass();
		 mylocationoverlay.enableMyLocation();
		 mylocationoverlay.runOnFirstFix(new Runnable(){
			 public void run(){
				 mapcontroller.animateTo(mylocationoverlay.getMyLocation());
			 }
		 });
		
	}
	
	

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
