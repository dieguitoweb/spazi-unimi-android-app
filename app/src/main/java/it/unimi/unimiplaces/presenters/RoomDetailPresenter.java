package it.unimi.unimiplaces.presenters;

import java.util.List;

import it.unimi.unimiplaces.APIManager;
import it.unimi.unimiplaces.core.api.APIDelegateInterface;
import it.unimi.unimiplaces.core.model.BaseEntity;
import it.unimi.unimiplaces.core.model.Room;
import it.unimi.unimiplaces.views.RoomDetailViewInterface;

/**
 * Room Detail Presenter
 */
public class RoomDetailPresenter implements Presenter, APIDelegateInterface {

    private String roomID;
    private Room room;
    APIManager apiManager;
    RoomDetailViewInterface view;

    public RoomDetailPresenter(APIManager apiManager,RoomDetailViewInterface view){
        this.apiManager = apiManager;
        this.view       = view;
    }

    @Override
    public void apiRequestStart() {

    }

    @Override
    public void apiRequestEnd(List<BaseEntity> results) {

        if( results==null ){
            this.view.showNoResultsMessage();
            return;
        }

        this.room = (Room) results.get(0);
        this.view.setRoomName(room.room_name);
        this.view.setRoomCategory(room.cat_name);
        this.view.setRoomFloor(room.floor);
        this.view.setBuildingName(room.building_name);
        this.view.setBuildingAddress(room.building_address);
    }

    @Override
    public void apiRequestError() {
        this.view.showNoResultsMessage();
    }

    @Override
    public void init(String id) {
        this.roomID = id;
    }

    public void init(String roomID, String buildingID){
        this.apiManager.roomByRIDAndBID(this,roomID,buildingID);
    }


    @Override
    public void filterModelWithFilterAtIndex(int index) {}
    @Override
    public BaseEntity payloadForDetailAtIndex(int index) {return null;}
}
