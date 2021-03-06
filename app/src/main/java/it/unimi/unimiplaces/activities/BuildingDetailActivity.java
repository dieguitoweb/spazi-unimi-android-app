package it.unimi.unimiplaces.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import it.unimi.unimiplaces.APIManager;
import it.unimi.unimiplaces.BookmarksDataSource;
import it.unimi.unimiplaces.BookmarksDb;
import it.unimi.unimiplaces.R;
import it.unimi.unimiplaces.core.model.Building;
import it.unimi.unimiplaces.core.model.Room;
import it.unimi.unimiplaces.presenters.BuildingDetailPresenter;
import it.unimi.unimiplaces.views.BookmarksNotificationBar;
import it.unimi.unimiplaces.views.BuildingDetailViewInterface;

public class BuildingDetailActivity extends AppDetailSectionActivity implements
        BuildingDetailViewInterface,
        ExpandableListView.OnChildClickListener{

    private BuildingDetailPresenter presenter;
    private String buildingId;
    TextView buildingNameTextView;
    TextView buildingAddressTextView;
    TextView buildingNoResults;
    FloorsDetailAdapter floorsDetailAdapter;
    ExpandableListView floorsDetailListView;
    FloatingActionButton bookmarksFab;
    BookmarksNotificationBar bookmarksNotificationBar;
    LinkedHashMap<String, List<String>> floorsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        buildingNameTextView    = (TextView) findViewById(R.id.building_name);
        buildingAddressTextView = (TextView) findViewById(R.id.building_address);
        buildingNoResults       = (TextView) findViewById(R.id.building_no_results);
        floorsDetailListView    = (ExpandableListView) findViewById(R.id.building_detail_rooms);
        bookmarksFab            = (FloatingActionButton) findViewById(R.id.fab_add_remove_bookmarks);

        Intent intent = getIntent();
        this.presenter  = new BuildingDetailPresenter(
                APIManager.APIManagerFactory.createAPIManager(this),
                this,
                new BookmarksDataSource(new BookmarksDb(this)));
        this.buildingId = intent.getStringExtra( Building.MODEL_KEY );
        presenter.init(this.buildingId);
        this.setUpDetailActivity(intent.getStringExtra(Building.MODEL_NAME_KEY));

        this.bookmarksFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveBookmark();
            }
        });
    }


    @Override
    public void setBuildingName(String buildingName) {
        buildingNameTextView.setText(buildingName);
    }

    @Override
    public void setBuildingAddress(String buildingAddress) {
        buildingAddressTextView.setText(buildingAddress);
    }

    @Override
    public void showNoResultsMessage() {
        buildingNameTextView.setVisibility(View.INVISIBLE);
        buildingAddressTextView.setVisibility(View.INVISIBLE);
        buildingNoResults.setVisibility(View.VISIBLE);
    }

    @Override
    public void setFloorsDetailModel(LinkedHashMap<String, List<String>> data) {
        floorsData          = data;
        floorsDetailAdapter = new FloorsDetailAdapter(this,data);
        this.floorsDetailListView.setAdapter(floorsDetailAdapter);
        this.floorsDetailListView.setOnChildClickListener(this);
        this.floorsDetailListView.setGroupIndicator(null);
    }

    @Override
    public void setDisplayAddBookmarksButton(boolean show) {
        if( show ){
            this.bookmarksFab.setVisibility(View.VISIBLE);
            this.bookmarksNotificationBar = new BookmarksNotificationBar(this,findViewById(R.id.building_wrapper));
        }else{
            this.bookmarksFab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccessBookmarkSaved() {
        this.bookmarksNotificationBar.showSuccessMessage();
    }

    @Override
    public void onErrorBookmarkSaved() {
        this.bookmarksNotificationBar.showErrorMessage();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        BuildingDetailPresenter buildingDetailPresenter = this.presenter;
        Room room           = (Room) buildingDetailPresenter.payloadForDetailAtIndex(groupPosition,childPosition);
        String detailTitle  = room.room_name.length()>0 ? room.room_name : room.cat_name;
        Intent roomDetailIntent = new Intent(this,RoomDetailActivity.class);
        roomDetailIntent.putExtra(Room.MODEL_KEY,room.r_id);
        roomDetailIntent.putExtra(Room.MODEL_NAME_KEY,detailTitle);
        roomDetailIntent.putExtra(Building.MODEL_KEY,this.buildingId);
        startActivity(roomDetailIntent);
        this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        return true;
    }

    /**
     * FloorsDetailAdapter
     */
    private class FloorsDetailAdapter extends BaseExpandableListAdapter{

        private Context context;
        private List<String> headers;
        private LinkedHashMap<String,List<String>> data;

        /**
         * Constructor
         * @param data HashMap in which every entry has as key the group name,
         *             and as value the list of entries in that group
         */
        public FloorsDetailAdapter(Context context,LinkedHashMap<String,List<String>> data){
            this.context    = context;
            this.data       = data;
            this.headers    = new ArrayList<>();
            this.headers.addAll(data.keySet());
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.headers.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.data.get(this.headers.get(groupPosition)).get(childPosition);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.data.get(this.headers.get(groupPosition)).size();
        }

        @Override
        public int getGroupCount() {
            return this.data.keySet().size();
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String childText        = (String)this.getChild(groupPosition,childPosition);
            TextView childTextView;

            // inflate layout there's not a recycled view
            if( convertView==null ){
                convertView = getLayoutInflater().inflate(R.layout.building_detail_list_item,parent,false);
            }

            childTextView = (TextView) convertView.findViewById(R.id.building_detail_item);
            childTextView.setText(childText);

            return convertView;

        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String groupText    = (String)this.getGroup(groupPosition);
            TextView groupTextView;

            // inflate layout there's not a recycled view
            if( convertView==null ){
                convertView = getLayoutInflater().inflate(R.layout.building_detail_group_list_item,parent,false);
            }

            groupTextView = (TextView) convertView.findViewById(R.id.building_detail_group);
            groupTextView.setText(groupText);

            // hide or show disclosure icon
            if( this.getChildrenCount(groupPosition)>0 ){
                convertView.findViewById(R.id.building_detail_expand_arrow).setVisibility(View.VISIBLE);
            }else{
                convertView.findViewById(R.id.building_detail_expand_arrow).setVisibility(View.INVISIBLE);
            }

            return convertView;
        }


    }
}
