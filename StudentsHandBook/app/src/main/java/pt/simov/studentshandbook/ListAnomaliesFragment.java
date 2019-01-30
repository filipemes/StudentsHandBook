package pt.simov.studentshandbook;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.Anomaly;
import pt.simov.helpers.Manager;

public class ListAnomaliesFragment extends Fragment {

    private ListView anomaliesListView;
    private GenericAdapter anomaliesAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<Anomaly> anomalies =null;
        if(getArguments()!=null&&getArguments().getString("filter").compareTo("FilterByUser")==0)
            anomalies=Manager.getInstance().getServiceFacade().getServiceAnomaliesInterface().getAnomaliesByUser();
        else
            anomalies=Manager.getInstance().getServiceFacade().getServiceAnomaliesInterface().getAnomaliesList();

        View view = inflater.inflate(R.layout.fragment_list_anomalies, container, false);
        anomaliesListView = (ListView) view.findViewById(R.id.listViewAnomalies);

        anomaliesAdapter = new GenericAdapter<Anomaly>(view.getContext(), R.layout.anomaly_item, anomalies) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View rowView;
                final Holder holder=new Holder();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(rowLayout, null);
                Anomaly anomaly = (Anomaly)getItem(position);

                holder.status = (TextView) rowView.findViewById(R.id.anomalyStatus);

                holder.status.setText("Status: "+anomaly.getStatus());

                holder.publisher = (TextView) rowView.findViewById(R.id.anomalyPublisher);
                holder.publisher.setText("Publisher: "+anomaly.getPublisher());

                holder.date = (TextView) rowView.findViewById(R.id.anomalyDate);
                holder.date.setText("Date: "+anomaly.getDate());

                holder.room = (TextView) rowView.findViewById(R.id.anomalyRoom);
                holder.room.setText("Room: "+anomaly.getRoom());

                holder.description = (TextView) rowView.findViewById(R.id.anomalyDescription);
                holder.description.setText("Description: "+anomaly.getDescription());

                holder.image = (ImageView) rowView.findViewById(R.id.anomalyImage);
                if(anomaly.getImageURL()!=null && !anomaly.getImageURL().equals("")){
                    Glide.with(rowView).load(anomaly.getImageURL()).into(holder.image);
                }
                return rowView;
            }
            class Holder{
                TextView description;
                ImageView image;
                TextView room;
                TextView publisher;
                TextView date;
                TextView status;
            }
        };
        anomaliesListView.setAdapter(anomaliesAdapter);
        return view;
    }



}
