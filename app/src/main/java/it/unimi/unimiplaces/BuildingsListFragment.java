package it.unimi.unimiplaces;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.unimi.unimiplaces.core.api.APIDelegateInterface;
import it.unimi.unimiplaces.core.model.BaseEntity;
import it.unimi.unimiplaces.core.model.Building;

/**
 * A fragment representing a list of buildings.
 *
 */
public class BuildingsListFragment extends ListFragment implements APIDelegateInterface {

    private List<BaseEntity> model;

    public static BuildingsListFragment newInstance() {
        BuildingsListFragment fragment = new BuildingsListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BuildingsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APIManager apiManager = new APIManager(getActivity());
        apiManager.buildings(this);
    }

    @Override
    public void apiRequestStart() {

    }

    @Override
    public void apiRequestEnd(List<BaseEntity> results) {
        this.model = results;
        setListAdapter(new BuildingsListAdapter(getActivity(),this.model));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


    private class BuildingsListAdapter extends ArrayAdapter<BaseEntity> {
        private final Activity context;
        private final List<BaseEntity> buildings;

        private class BuildingViewHolder{
            public TextView buildingName;
            public TextView buildingAddress;
        }

        public BuildingsListAdapter(Activity context,List<BaseEntity> buildings){
            super( context,R.layout.buildings_list_item , buildings );
            this.context    = context;
            this.buildings  = buildings;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group){
            View buildingRowView = convertView;

            // reuse view
            if( buildingRowView==null ){
                LayoutInflater inflater = context.getLayoutInflater();
                buildingRowView = inflater.inflate(R.layout.buildings_list_item,null);

                // create BuildingViewHolder in order to avoid searching
                // resources within XML and speeds up rendering
                BuildingViewHolder holder = new BuildingViewHolder();
                holder.buildingName     = (TextView) buildingRowView.findViewById(R.id.building_name);
                holder.buildingAddress  = (TextView) buildingRowView.findViewById(R.id.building_address);

                buildingRowView.setTag(holder);
            }

            // fill with building data
            BuildingViewHolder holder = (BuildingViewHolder) buildingRowView.getTag();
            Building building = (Building) this.buildings.get(position);
            holder.buildingName.setText(building.building_name);
            holder.buildingAddress.setText(building.address);


            return buildingRowView;
        }
    }

}