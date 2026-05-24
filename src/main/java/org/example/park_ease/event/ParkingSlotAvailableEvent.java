package org.example.park_ease.event;

import org.springframework.context.ApplicationEvent;

public class ParkingSlotAvailableEvent extends ApplicationEvent {
    private final Integer slotId;
    private final Integer slotNumber;
    private final Integer parkingLotId;

    public ParkingSlotAvailableEvent(Object source, Integer slotId, Integer slotNumber, Integer parkingLotId) {
        super(source);
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
