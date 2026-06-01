package org.example.parkease.dto.request;

import java.math.BigDecimal;

public class BookingRequestDTO {

    private Integer slotId;
    private Integer durationMinutes;
    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
