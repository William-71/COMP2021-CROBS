package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;

/**
 * addRoom; undo removes the room.
 */
public class AddRoomCommand implements UndoableCommand {

    private final RoomManager roomManager;
    private final String roomType;
    private final String capacity;
    private final String features;
    private Room room;

    /**
     * @param roomManager room manager
     * @param roomType    room type
     * @param capacity    capacity text
     * @param features    comma-separated, no spaces
     */
    public AddRoomCommand(RoomManager roomManager, String roomType, String capacity, String features) {
        this.roomManager = roomManager;
        this.roomType = roomType;
        this.capacity = capacity;
        this.features = features;
    }

    @Override
    public String execute() {
        room = roomManager.addRoom(roomType, capacity, features);
        return "Room " + room.getRoomId() + " created.";
    }

    @Override
    public void undo() {
        roomManager.deleteRoom(room.getRoomId());
    }

    @Override
    public void redo() {
        roomManager.restoreRoom(room);
    }
}
