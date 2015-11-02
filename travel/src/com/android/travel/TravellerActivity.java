package com.android.travel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.GeoPoint;

public class TravellerActivity extends Activity {
    
	/** Called when the activity is first created. */
   Location mlocation,nlocation;
   	TextView tctview;
   TextView txtview;
   public String dest,src;
   public InputStream is,in;
   public String result,rest,sDistance;
   private String dst,np;
   public int flag=0;
   public double jlat,jlong,ge,fare;
   private ProgressBar mSpinner;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        tctview = (TextView)findViewById(R.id.widget48);
        setcurrentlocation();
        seemap();
  
        
    }
	private void loc(){
		txtview = (TextView)findViewById(R.id.widget50);
        List<Address>addresses;
        String myAddress = "London";
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+dest+"&sensor=true";
		try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            
    }catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
    }
    
  //convert response to string
    try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
            }
            is.close();
             result = sb.toString();
             //Toast.makeText(TravellerActivity.this,result,Toast.LENGTH_LONG).show();
             System.out.print(result);
    }catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
    }
ArrayList<InfoPoint> informationGeocoding= parsePoints(result);
   
        
	}
    private void setcurrentlocation(){
    	
    	
		Button locbutton = (Button)findViewById(R.id.widget47);
    	locbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
		        String locationprovider = lm.getBestProvider(criteria,false);
		        mlocation = lm.getLastKnownLocation(locationprovider);
		        EditText edittext = (EditText)findViewById(R.id.widget44);
		        
		        if(mlocation!=null){
		        String tt = mlocation.getLatitude() + "		" + mlocation.getLongitude();
				edittext.setText(tt, TextView.BufferType.EDITABLE);
		        }
		        else{
		        	edittext.setText("Provider not enabled");
		        }
		        edittext.setEnabled(false);
		        edittext.setInputType(InputType.TYPE_NULL);
		        flag = 1;
		        
		        List<Address>addresses;
		        try{
		        	Geocoder mgc = new Geocoder(getApplicationContext());
			        addresses = mgc.getFromLocation(mlocation.getLatitude(),mlocation.getLongitude(),1);
			        if(addresses!=null){
			        	Address currentaddr = addresses.get(0);
			        	StringBuilder mss = new StringBuilder("\n");
			        	for(int i=0;i<currentaddr.getMaxAddressLineIndex();i++){
			        		mss.append(currentaddr.getAddressLine(i)).append("\n");
			        	}
			        	src = mss.toString();
			        	//Toast.makeText(TravellerActivity.this,src,Toast.LENGTH_LONG);
			        } 
			        
		        }catch(IOException e){
		        	src = e.getMessage();
		        }
		        //Toast.makeText(getApplicationContext(), "test",10);	
		        
			}
		});
    	
    }
    private ArrayList<InfoPoint> parsePoints(String strResponse) {
        ArrayList<InfoPoint> result=new ArrayList<InfoPoint>();
        try {
            JSONObject obj=new JSONObject(strResponse);
            JSONArray array=obj.getJSONArray("results");
            for(int i=0;i<array.length();i++)
            {
                            InfoPoint point=new InfoPoint();

                JSONObject item=array.getJSONObject(i);
                ArrayList<HashMap<String, Object>> tblPoints=new ArrayList<HashMap<String,Object>>();
                JSONArray jsonTblPoints=item.getJSONArray("address_components");
                for(int j=0;j<jsonTblPoints.length();j++)
                {
                    JSONObject jsonTblPoint=jsonTblPoints.getJSONObject(j);
                    HashMap<String, Object> tblPoint=new HashMap<String, Object>();
                    Iterator<String> keys=jsonTblPoint.keys();
                    while(keys.hasNext())
                    {
                        String key=(String) keys.next();
                        if(tblPoint.get(key) instanceof JSONArray)
                        {
                            tblPoint.put(key, jsonTblPoint.getJSONArray(key));
                        }
                        tblPoint.put(key, jsonTblPoint.getString(key));
                    }
                    tblPoints.add(tblPoint);
                }
                point.setAddressFields(tblPoints);
                point.setStrFormattedAddress(item.getString("formatted_address"));
                JSONObject geoJson=item.getJSONObject("geometry");
                JSONObject locJson=geoJson.getJSONObject("location");
                point.setDblLatitude(Double.parseDouble(locJson.getString("lat")));
                jlat = point.getDblLatitude();
                //Toast.makeText(TravellerActivity.this,locJson.getString("lat"),Toast.LENGTH_LONG).show();
                point.setDblLongitude(Double.parseDouble(locJson.getString("lng")));
                jlong = point.getDblLongitude();
                result.add(point);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
    private void GetDistance(GeoPoint src, GeoPoint dest) {

        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/distancematrix/json?");
        urlString.append("origins=");//from
        urlString.append( Double.toString((double)src.getLatitudeE6()/ 1E6));
        //String lat1 = Double.toString((double)src.getLatitudeE6());
        //Toast.makeText(TravellerActivity.this,lat1, Toast.LENGTH_SHORT).show();
        urlString.append(",");
        urlString.append( Double.toString((double)src.getLongitudeE6() / 1E6));
        String longt1 = Double.toString((double)src.getLongitudeE6() / 1E6);
        urlString.append("&destinations=");//to
        urlString.append( Double.toString((double)dest.getLatitudeE6() / 1E6));
        String lat2 = Double.toString((double)dest.getLatitudeE6() / 1E6);
        urlString.append(",");
        urlString.append( Double.toString((double)dest.getLongitudeE6() / 1E6));
        String longt2 = Double.toString((double)dest.getLongitudeE6() / 1E6);

        urlString.append("&mode=driving&sensor=true");
        Log.d("xxx","URL="+urlString.toString());
        //Toast.makeText(TravellerActivity.this, urlString, Toast.LENGTH_LONG).show();
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlString.toString());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            
    }catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
    }
    try{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
        }
        is.close();
         rest = sb.toString();
         //Toast.makeText(TravellerActivity.this,rest,Toast.LENGTH_LONG).show();
         System.out.print(rest);
    }catch(Exception e){
        Log.e("log_tag", "Error converting result "+e.toString());
    }
		        //Sortout JSONresponse 
        //JSONObject object;
		try {
			JSONObject object = new JSONObject(rest);	
		    JSONArray rows = object.getJSONArray("rows");
		        //Log.d("JSON","array: "+array.toString());

		    //Routes is a combination of objects and arrays
		    JSONObject rowsobject = rows.getJSONObject(0);
		        //Log.d("JSON","routes: "+routes.toString());

		    //String summary = routes.getString("summary");
		        //Log.d("JSON","summary: "+summary);

		    JSONArray elements = rowsobject.getJSONArray("elements");
		        //Log.d("JSON","legs: "+legs.toString());

		    JSONObject elementsobject = elements.getJSONObject(0);
		            //Log.d("JSON","steps: "+steps.toString());

		    JSONObject distance = elementsobject.getJSONObject("distance");
		        //Log.d("JSON","distance: "+distance.toString());

		            sDistance = distance.getString("text");
		            int iDistance = distance.getInt("value");
		            ge = (double)(iDistance);
		            ge = ge/1000;
		            ge = Math.round(ge*1e1)/1e1;
		            if(ge<=1.0){
		            	fare = 11.0;
		            }
		            else{
		            fare = 11.0 + ((ge - 1.0)*10.0) ;
		            }
		            np = String.valueOf(fare);
		            
		            Toast.makeText(TravellerActivity.this,np, Toast.LENGTH_LONG).show();
		            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
                
    }
    private void seemap(){
    	mSpinner = new ProgressBar(getApplicationContext());
		mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addContentView(mSpinner,mSpinner.getLayoutParams());
		mSpinner.setVisibility(View.INVISIBLE);
    	Button getfare = (Button)findViewById(R.id.widget46);
    	getfare.setOnClickListener(new View.OnClickListener(){
    		@Override
    		public void onClick(View mp){
    			mSpinner.setVisibility(View.VISIBLE);
				mSpinner.setIndeterminate(true);
    			Double lat,longt,lat1,longt1;
    			EditText ed = (EditText)findViewById(R.id.widget45);
    			dest = ed.getText().toString();
    			dest = dest.replaceAll(" ","%20");
    			Toast.makeText(TravellerActivity.this,dest,Toast.LENGTH_SHORT).show();
    			String tempdest = dest;
    			
    			loc();
    			lat = jlat;
    			longt = jlong;
    			if(flag==0){
    				EditText sr = (EditText)findViewById(R.id.widget44);
    				dest = sr.getText().toString();
    				dest = dest.replaceAll(" ", "%20");
    				Toast.makeText(TravellerActivity.this,dest,Toast.LENGTH_SHORT).show();
    				loc();
    				lat1 = jlat;
        			longt1 = jlong;
        		}
    			else{
    				lat1 = mlocation.getLatitude();
    				longt1 = mlocation.getLongitude();
    				dest=src;
    			}
    			//double fare;
    			
    			//fare = 11;
    			//fare = 11.0 + ((ge-1.0)*10.0);
    			
    			//String tp = String.valueOf(ge);
    			//Toast.makeText(TravellerActivity.this,tp ,Toast.LENGTH_LONG).show();
    			GeoPoint src = new GeoPoint((int)(lat*1E6),(int)(longt*1E6));
    			GeoPoint dts = new GeoPoint((int)(lat1*1E6),(int)(longt1*1E6));
    			GetDistance(src,dts);
    			Intent myIntent = new Intent(mp.getContext(),TabtestActivity.class);
    			//dst = myIntent.getStringExtra(dest);
    			myIntent.putExtra("dest", dest);
    			myIntent.putExtra("tempdest",tempdest);
    			myIntent.putExtra("sDistance",sDistance);
    			myIntent.putExtra("fare",fare);
    			mSpinner.setIndeterminate(false);
		        mSpinner.setVisibility(View.GONE);
    			startActivityForResult(myIntent,0);
    		}
    	});
    }
    private class mylocationlistener implements LocationListener{
    	@Override
    	public void onLocationChanged(Location location){
    			if(location!=null){
    			mlocation = location;
    			Log.d("LOCATION CHANGED",mlocation.getLatitude()+"");
    			Log.d("LOCATION CHANGED",mlocation.getLongitude()+"");
    			showUpdate();
       			//Toast.makeText(TravellerActivity.this,tt,Toast.LENGTH_SHORT).show();
    			}
    			else{
    				EditText edittext = (EditText)findViewById(R.id.widget44);
    				edittext.setText("Provider not available", TextView.BufferType.EDITABLE);
    			}
    	}
    	@Override
    	public void onProviderDisabled(String provider){
    		
    	}
    	@Override
    	public void onProviderEnabled(String provider){
    		
    	}
    	@Override
    	public void onStatusChanged(String provider,int status,Bundle extras){
   		
    	}
    }
    @Override
    protected void onResume(){
    	super.onResume();
    	setcurrentlocation();
    }
    public void showUpdate(){
    	String tt = mlocation.getLatitude() + "" + mlocation.getLongitude();
		EditText edittext = (EditText)findViewById(R.id.widget44);
		edittext.setText(tt, TextView.BufferType.EDITABLE);

    }
}