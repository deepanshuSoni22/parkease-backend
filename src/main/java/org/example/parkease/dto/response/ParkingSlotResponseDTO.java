package org.example.parkease.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkingSlotResponseDTO {

    private Integer id;
    private Integer slotNumber;
    private String slotType;
    private Boolean available;
    private String bookedByUsername;
    private Integer parkingLotId;
    private String parkingLotName;
    private BigDecimal pricePerMinute;

    private LocalDateTime bookedAt;            // when reservation/booking was made or startTime (fallback)
    private Integer bookedDurationMinutes;     // booking.durationMinutes
    private LocalDateTime bookedUntil;         // optional convenience field: start + duration

    public ParkingSlotResponseDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getBookedByUsername() {
        return bookedByUsername;
    }

    public void setBookedByUsername(String bookedByUsername) {
        this.bookedByUsername = bookedByUsername;
    }

    public Integer getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(Integer parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public BigDecimal getPricePerMinute() {
        return pricePerMinute;
    }

    public void setPricePerMinute(BigDecimal pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public Integer getBookedDurationMinutes() {
        return bookedDurationMinutes;
    }

    public void setBookedDurationMinutes(Integer bookedDurationMinutes) {
        this.bookedDurationMinutes = bookedDurationMinutes;
    }

    public LocalDateTime getBookedUntil() {
        return bookedUntil;
    }

    public void setBookedUntil(LocalDateTime bookedUntil) {
        this.bookedUntil = bookedUntil;
    }
}


