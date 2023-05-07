package com.example.eventfinder.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.eventfinder.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class VenueFragment extends Fragment implements OnMapReadyCallback {
    private JSONObject venueInfo;
    private TextView VenueName;
    private TextView Address;
    private TextView City;
    private TextView contactInfo;
    private TableRow addressRow;
    private TableRow contactRow;
    private boolean hasOpenHour;
    private TableRow openHourTitle;
    private TableRow openHourContent;
    private TextView openHour;
    private boolean hasGeneralRule;
    private TableRow generalRuleTitle;
    private TableRow generalRuleContent;
    private TextView generalRule;
    private boolean hasChildRule;
    private TableRow childRuleTitle;
    private TableRow childRuleContent;
    private TextView childRule;
    private ConstraintLayout venueBottomCard;
    private boolean isOpenHourExpand;
    private boolean isGeneralRuleExpand;
    private boolean isChildRuleExpand;

    public VenueFragment(JSONObject venueInfo){
        this.venueInfo = venueInfo;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        VenueName = view.findViewById(R.id.VenueName);
        Address = view.findViewById(R.id.Address);
        City = view.findViewById(R.id.City);
        contactInfo = view.findViewById(R.id.contactInfo);
        addressRow = view.findViewById(R.id.addressRow);
        contactRow = view.findViewById(R.id.contactRow);
        openHourTitle = view.findViewById(R.id.openHourTitle);
        openHourContent = view.findViewById(R.id.openHourContent);
        openHour = view.findViewById(R.id.openHour);
        hasOpenHour=true;
        generalRuleTitle = view.findViewById(R.id.generalRuleTitle);
        generalRuleContent = view.findViewById(R.id.generalRuleContent);
        generalRule = view.findViewById(R.id.generalRule);
        hasGeneralRule=true;
        childRuleTitle = view.findViewById(R.id.childRuleTitle);
        childRuleContent = view.findViewById(R.id.childRuleContent);
        childRule = view.findViewById(R.id.childRule);
        hasChildRule=true;
        venueBottomCard = view.findViewById(R.id.venueBottomCard);
        isOpenHourExpand=false;
        isGeneralRuleExpand=false;
        isChildRuleExpand=false;
        try{
            VenueName.setText(venueInfo.getString("name"));
            VenueName.setSelected(true);
            if(venueInfo.getJSONObject("address").has("line1")){
                Address.setText(venueInfo.getJSONObject("address").getString("line1"));
                Address.setSelected(true);
            }else{
                addressRow.setVisibility(View.GONE);
            }
            City.setText(venueInfo.getJSONObject("city").getString("name")+", "+venueInfo.getJSONObject("state").getString("name"));
            City.setSelected(true);
            if(venueInfo.has("boxOfficeInfo") && venueInfo.getJSONObject("boxOfficeInfo").has("phoneNumberDetail")){
                contactInfo.setText(venueInfo.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail"));
                contactInfo.setSelected(true);
            }else{
                contactRow.setVisibility(View.GONE);
            }
            if(venueInfo.has("boxOfficeInfo") && venueInfo.getJSONObject("boxOfficeInfo").has("openHoursDetail")){
                openHour.setText(venueInfo.getJSONObject("boxOfficeInfo").getString("openHoursDetail"));
                openHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isOpenHourExpand) {
                            openHour.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            openHour.setMaxLines(3);
                        }
                        isOpenHourExpand = !isOpenHourExpand;
                    }
                });
            }else{
                openHourTitle.setVisibility(View.GONE);
                openHourContent.setVisibility(View.GONE);
                hasOpenHour=false;
            }
            if(venueInfo.has("generalInfo") && venueInfo.getJSONObject("generalInfo").has("generalRule")){
                generalRule.setText(venueInfo.getJSONObject("generalInfo").getString("generalRule"));
                generalRule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isGeneralRuleExpand) {
                            generalRule.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            generalRule.setMaxLines(3);
                        }
                        isGeneralRuleExpand = !isGeneralRuleExpand;
                    }
                });
            }else{
                generalRuleTitle.setVisibility(View.GONE);
                generalRuleContent.setVisibility(View.GONE);
                hasGeneralRule=false;
            }
            if(venueInfo.has("generalInfo") && venueInfo.getJSONObject("generalInfo").has("childRule")){
                childRule.setText(venueInfo.getJSONObject("generalInfo").getString("childRule"));
                childRule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isChildRuleExpand) {
                            childRule.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            childRule.setMaxLines(3);
                        }
                        isChildRuleExpand = !isChildRuleExpand;
                    }
                });
            }else{
                childRuleTitle.setVisibility(View.GONE);
                childRuleContent.setVisibility(View.GONE);
                hasChildRule=false;
            }
            if(!hasOpenHour && !hasGeneralRule && !hasChildRule){
                venueBottomCard.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        String Latitude=null;
        String Longitude=null;
        try {
            Latitude = venueInfo.getJSONObject("location").getString("latitude");
            Longitude = venueInfo.getJSONObject("location").getString("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert Latitude!=null && Longitude!=null;
        LatLng location = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
        googleMap.addMarker(new MarkerOptions().position(location).title("Venue Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(location,15) );
    }
}
