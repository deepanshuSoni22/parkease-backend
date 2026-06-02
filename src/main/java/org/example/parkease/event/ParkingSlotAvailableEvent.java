package org.example.parkease.event;

public class ParkingSlotAvailableEvent {
    private final Integer slotId;
    private final Integer slotNumber;
    private final Integer parkingLotId;

    public ParkingSlotAvailableEvent(Integer slotId, Integer slotNumber, Integer parkingLotId) {
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.parkingLotId = parkingLotId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public Integer getParkingLotId() {
        return parkingLotId;
    }
}
