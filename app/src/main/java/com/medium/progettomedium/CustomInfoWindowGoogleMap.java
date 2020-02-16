package com.medium.progettomedium;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;
    LayoutInflater inflater;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);            // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own
        View view = inflater
                .inflate(R.layout.map_custom_infowindow, null);

        TextView titolo_tv = view.findViewById(R.id.snTitolo);
        ImageView foto = view.findViewById(R.id.snFoto);

        TextView data_tv = view.findViewById(R.id.snData);
        TextView dettagli_tv = view.findViewById(R.id.snDettagli);

        titolo_tv.setText(marker.getTitle());
        data_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        int imageId = context.getResources().getIdentifier(infoWindowData.getFoto().toLowerCase(),
                "drawable", context.getPackageName());
        foto.setImageResource(imageId);

        dettagli_tv.setText(infoWindowData.getDettagli());

        return view;
    }
}