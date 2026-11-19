package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;

/**
 * modifyRoom; undo restores the room's previous values.
 */
public class ModifyRoomCommand implements UndoableCommand {

    private final RoomManager roomManager;
    private final String roomId;
    private final String field;
    private final String newValue;
    private Room room;
    private String oldRoomType;
    private int oldCapacity;
    private String oldFeatures;

    /**
     * @param roomManager room manager
     * @param roomId      room ID
     * @param field       roomType, capacity or features
     * @param newValue    new value
     */
    public ModifyRoomCommand(RoomManager roomManager, String roomId, String field, String newValue) {
        this.roomManager = roomManager;
        this.roomId = roomId;
        this.field = field;
        this.newValue = newValue;
    }

    @Override
    public String execute() {
        Room target = roomManager.requireRoom(roomId);
        oldRoomType = target.getRoomType();
        oldCapacity = target.getCapacity();
        oldFeatures = target.getFeatures();
        room = roomManager.modifyRoom(roomId, field, newValue);
        return "Room " + room.getRoomId() + " modified.";
    }

    @Override
    public void undo() {
        room.setRoomType(oldRoomType);
        room.setCapacity(oldCapacity);
        room.setFeatures(oldFeatures);
    }

    @Override
    public void redo() {
        roomManager.modifyRoom(roomId, field, newValue);
    }
}
