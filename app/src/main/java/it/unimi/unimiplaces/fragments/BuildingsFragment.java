package it.unimi.unimiplaces.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.List;

import it.unimi.unimiplaces.APIManager;
import it.unimi.unimiplaces.R;
import it.unimi.unimiplaces.activities.BuildingDetailActivity;
import it.unimi.unimiplaces.core.model.BaseEntity;
import it.unimi.unimiplaces.core.model.Building;
import it.unimi.unimiplaces.presenters.BuildingsPresenter;
import it.unimi.unimiplaces.presenters.Presenter;
import it.unimi.unimiplaces.views.BuildingsListView;
import it.unimi.unimiplaces.views.BuildingsMapView;
import it.unimi.unimiplaces.views.BuildingsViewInterface;
import it.unimi.unimiplaces.views.PresenterViewInterface;

public class BuildingsFragment extends Fragment implements
        PresenterViewInterface,
        BuildingsViewInterface {

    private Context context;
    private Presenter presenter;
    private BuildingsListView buildingsListView;
    private BuildingsMapView buildingsMapView;
    private ToggleButton buildingsModeView;
    private Button filterButton;
    private final BuildingsModeView defaultBuildingModeView = BuildingsModeView.BUILDINGS_MODE_VIEW_LIST;
    private View view;
    private AlertDialog filterDialog;

    private final String LOG_TAG = "BUILDINGSMAP";

    private enum BuildingsModeView{
        BUILDINGS_MODE_VIEW_LIST,
        BUILDINGS_MODE_VIEW_MAP
    }

    public BuildingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        presenter = new BuildingsPresenter(
                APIManager.APIManagerFactory.createAPIManager(this.context),
                this
        );
        presenter.init(getResources().getConfiguration().locale.getLanguage());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if( view != null ){
            ViewGroup parent = (ViewGroup) view.getParent();
            if( parent != null ){
                parent.removeView(view);
            }
        }

        try {
            view = inflater.inflate(R.layout.fragment_buildings, container, false);
        }catch (InflateException e){
            Log.e(LOG_TAG,e.getMessage());
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        this.initialize();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initialize(){
        this.buildingsListView = (BuildingsListView) view.findViewById(R.id.buildings_list_view);
        this.buildingsMapView = (BuildingsMapView) view.findViewById(R.id.buildings_map_view);

        filterButton = (Button) view.findViewById(R.id.buildings_filter);

        changeViewMode(defaultBuildingModeView);

        /* initialize toggle button */
        buildingsModeView  = (ToggleButton) view.findViewById(R.id.buildings_view_mode);
        buildingsModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeViewMode(BuildingsModeView.BUILDINGS_MODE_VIEW_MAP);
                } else {
                    changeViewMode(BuildingsModeView.BUILDINGS_MODE_VIEW_LIST);

                }
            }
        });

        /* initialize filter button */
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });

        /* detail listeners */
        buildingsListView.setDetailActionListener(this);
        buildingsMapView.setDetailActionListener(this);
    }


    private void changeViewMode(BuildingsModeView mode){
        switch (mode){
            case BUILDINGS_MODE_VIEW_LIST:
                buildingsListView.setVisibility(View.VISIBLE);
                buildingsMapView.setVisibility(View.INVISIBLE);

                break;
            case BUILDINGS_MODE_VIEW_MAP:
                buildingsMapView.setVisibility(View.VISIBLE);
                buildingsListView.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public void setModel(List<BaseEntity> model) {
        if( this.buildingsListView!=null && this.buildingsMapView!=null ) {
            this.buildingsListView.setModel(model);
            this.buildingsMapView.setModel(model);
        }
    }

    @Override
    public void setAvailableServices(String[] services) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        services[0] = this.context.getString(R.string.available_service_all);
        builder.setTitle(R.string.filter_by);
        builder.setItems(services, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.filterModelWithFilterAtIndex(which);
            }
        });

        filterDialog = builder.create();
    }

    @Override
    public void clearListeners(){
        this.buildingsListView.clearListeners();
        this.buildingsMapView.clearListeners();
    }

    @Override
    public void setDetailActionListener(PresenterViewInterface listener){}

    @Override
    public void onDetailActionListener(int i) {
        Building detailBuilding     = (Building) presenter.payloadForDetailAtIndex(i);
        Intent detailActivityIntent = new Intent(this.context, BuildingDetailActivity.class);
        detailActivityIntent.putExtra(Building.MODEL_KEY, detailBuilding.b_id);
        detailActivityIntent.putExtra(Building.MODEL_NAME_KEY, detailBuilding.building_name);
        startActivity(detailActivityIntent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
