package org.example.park_ease.dto.request;

import java.math.BigDecimal;

public class ParkingSlotRequestDTO {

    private Integer slotNumber;
    private String slotType;
    private Boolean available;
    private BigDecimal pricePerMinute;

    public ParkingSlotRequestDTO() {
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

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public BigDecimal getPricePerMinute() {
        return pricePerMinute;
    }

    public void setPricePerMinute(BigDecimal pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }
}
