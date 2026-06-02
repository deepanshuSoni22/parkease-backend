package org.example.parkease.dto.request;

public class ParkingLotRequestDTO {

    private String name;
    private String location;
    private Boolean isActive;

    public ParkingLotRequestDTO() {
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }
}
