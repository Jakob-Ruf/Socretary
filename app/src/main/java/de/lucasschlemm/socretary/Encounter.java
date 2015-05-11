package de.lucasschlemm.socretary;

public class Encounter {
    private String encounterId;
    private String personId;
    private String description;
    private String timestamp;
    private int means;
    private int direction;

    public Encounter(){
        this.personId = "1";
        this.encounterId = "1";
        this.description = "Geburtstagsparty Peter";
        this.timestamp = System.currentTimeMillis()+"";
        this.means = 1;
        this.direction = 1;
    }

    public String toString(){
        return "Encounter: { id: " + this.encounterId + ", personId: " + this.personId + ", description: " + this.description + ", timestamp: " + this.timestamp + ", means: " + this.means + ", direction " + this.direction + "}";
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getMeans() {
        return means;
    }

    public void setMeans(int means) {
        this.means = means;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}