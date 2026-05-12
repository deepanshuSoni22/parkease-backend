package org.example.park_ease.dto.response;

public class ParkingLotResponseDTO {

    private String name;
    private String location;
    private Double hourlyRate;
    private Integer dailyMax;
    private Boolean active;
    private String ownerName;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getDailyMax() {
        return dailyMax;
    }

    public void setDailyMax(Integer dailyMax) {
        this.dailyMax = dailyMax;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
