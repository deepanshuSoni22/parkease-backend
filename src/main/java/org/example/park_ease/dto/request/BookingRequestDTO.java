package org.example.park_ease.dto.request;

public class BookingRequestDTO {

    private Integer slotId;
    private Integer durationMinutes;

    public BookingRequestDTO() {
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
