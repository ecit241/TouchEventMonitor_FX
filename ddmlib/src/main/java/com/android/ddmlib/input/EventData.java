package com.android.ddmlib.input;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class EventData {
    private final SimpleStringProperty eventType = new SimpleStringProperty();
    private final SimpleStringProperty eventDesc = new SimpleStringProperty();
    private final SimpleStringProperty eventDur = new SimpleStringProperty();
    private final SimpleDoubleProperty progress = new SimpleDoubleProperty();

    /**
     * @return the progress
     */
    public double getProgress() {
        return progress.get();
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    public String getEventType() {
        return eventType.get();
    }

    public String getEventDesc() {
        return eventDesc.get();
    }

    public String getEventDur() {
        return eventDur.get();
    }

    public void setEventType(String value) {
        eventType.set(value);
    }

    public void setEventDesc(String value) {
        eventDesc.set(value);
    }

    public void setEventDur(String value) {
        eventDur.set(value);
    }

    public SimpleStringProperty eventTypeProperty() {
        return eventType;
    }

    public SimpleStringProperty eventDescProperty() {
        return eventDesc;
    }

    public SimpleStringProperty eventDurProperty() {
        return eventDur;
    }

}