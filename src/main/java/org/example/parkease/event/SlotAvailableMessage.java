package org.example.parkease.event;

public class SlotAvailableMessage {
    private Integer slotId;
    private Integer slotNumber;
    private Integer parkingLotId;

    public SlotAvailableMessage(Integer slotId, Integer slotNumber, Integer parkingLotId) {
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.parkingLotId = parkingLotId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public Integer getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(Integer parkingLotId) {
        this.parkingLotId = parkingLotId;
    }
}
