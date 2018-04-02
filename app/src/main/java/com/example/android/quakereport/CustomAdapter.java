package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by antariksh on 26/3/18.
 */

public class CustomAdapter extends ArrayAdapter<EarthquakeData> {

    private ArrayList<EarthquakeData> dataSet;
    private Context mContext;

    private static class ViewHolder{
        TextView txtMag;
        TextView txtOffset;
        TextView txtLoc;
        TextView txtDate;
        TextView txtTime;
    }

    public CustomAdapter(@NonNull Context context, ArrayList<EarthquakeData> data) {
        super(context, R.layout.custom_list, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EarthquakeData dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;
        String locSplitter = " of ";

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_list, parent, false);
            viewHolder.txtMag = convertView.findViewById(R.id.txtMag);
            viewHolder.txtOffset = convertView.findViewById(R.id.txtOffset);
            viewHolder.txtLoc = convertView.findViewById(R.id.txtLoc);
            viewHolder.txtDate = convertView.findViewById(R.id.txtDate);
            viewHolder.txtTime = convertView.findViewById(R.id.txtTime);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        String formattedMagnitude = formatMagnitude(dataModel.getMag());
        viewHolder.txtMag.setText(formattedMagnitude);

        GradientDrawable magnitudeCircle = (GradientDrawable) viewHolder.txtMag.getBackground();
        int magnitudeColor = getMagnitudeColor(dataModel.getMag());
        magnitudeCircle.setColor(magnitudeColor);


        String place = dataModel.getLoc();
        if(place.contains(locSplitter)){
            String[] locSplit = place.split(locSplitter);
            viewHolder.txtOffset.setText(locSplit[0] + locSplitter);
            viewHolder.txtLoc.setText(locSplit[1]);
        } else {
            viewHolder.txtOffset.setText(R.string.near_the);
            viewHolder.txtLoc.setText(dataModel.getLoc());
        }

        Date dateObject = new Date(dataModel.getTime());

        String formattedDate = formatDate(dateObject);
        viewHolder.txtDate.setText(formattedDate);

        String formattedTime = formatTime(dateObject);
        viewHolder.txtTime.setText(formattedTime);

        return convertView;
    }


    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }


    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
