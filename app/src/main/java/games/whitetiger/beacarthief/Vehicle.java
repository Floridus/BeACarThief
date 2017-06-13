package games.whitetiger.beacarthief;

import java.sql.Timestamp;

public class Vehicle {

    private int id;
    private String make;
    private String model;
    private double longitude;
    private double latitude;
    private int value;
    private int minLevel;
    private boolean isActive;
    private Timestamp createdAt;

    public Vehicle() {
        super();
    }

    public Vehicle(int id, String make, String model, double longitude, double latitude, int value, int minLevel, boolean isActive, String createdAt) {
        super();
        this.id = id;
        this.make = make;
        this.model = model;
        this.longitude = longitude;
        this.latitude = latitude;
        this.value = value;
        this.minLevel = minLevel;
        this.isActive = isActive;
        this.createdAt = Timestamp.valueOf(createdAt);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vehicle other = (Vehicle) obj;
        if (id != other.id)
            return false;
        return true;
    }

    /**
     * Checks if the creation date has already been more than 10 minutes ago.
     * @return boolean
     */
    public boolean timeIsUp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        if (createdAt.getTime() / 1000 >= tsLong - 10 * 60) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Vehicle [id=" + id + ", make=" + make + ", model="
                + model + ", value=" + value + ", minLevel=" + minLevel + ", longitude="
                + longitude + ", latitude=" + latitude + ", createdAt=" + createdAt + "]";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getValue() {
        return value;
    }

    public boolean isActive() {
        return isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public int getMinLevel() {
        return minLevel;
    }
}
