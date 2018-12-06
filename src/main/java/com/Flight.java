package main.java.com;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

import java.util.Date;

public class Flight
{
    private SimpleStringProperty flightNumber;
    private SimpleStringProperty departureLocation;
    private SimpleStringProperty destination;
    private SimpleObjectProperty<Date> depDate;
    private SimpleObjectProperty<Date> arrDate;
    private SimpleStringProperty airplaneNumber;
    private SimpleObjectProperty<Button> cancelButton;


    public Flight(String flightNumber, String departureLocation, String destination, Date depDate, Date arrDate, String airplaneNumber) {
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.departureLocation = new SimpleStringProperty(departureLocation);
        this.destination = new SimpleStringProperty(destination);
        this.depDate = new SimpleObjectProperty(depDate);
        this.arrDate = new SimpleObjectProperty(arrDate);
        this.airplaneNumber = new SimpleStringProperty(airplaneNumber);
        this.cancelButton = new SimpleObjectProperty<>(new Button("Cancel"));
    }

    public String getFlightNumber() {
        return flightNumber.get();
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber.set(flightNumber);
    }

    public String getDepartureLocation() {
        return departureLocation.get();
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation.set(departureLocation);
    }

    public String getDestination() {
        return destination.get();
    }

    public void setDestination(String destination) {
        this.destination.set(destination);
    }

    public Date getDepDate() {
        return depDate.get();
    }

    public void setDepDate(Date depDate) {
        this.depDate.set(depDate);
    }

    public Date getArrDate() {
        return arrDate.get();
    }

    public void setArrDate(Date arrDate) {
        this.arrDate.set(arrDate);
    }

    public String getAirplaneNumber() {
        return airplaneNumber.get();
    }

    public void setAirplaneNumber(String airplaneNumber) {
        this.airplaneNumber.set(airplaneNumber);
    }

    public Button getCancelButton() {
        return cancelButton.get();
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton.set(cancelButton);
    }

    //getters and setters for Simple Properties
    public SimpleStringProperty flightNumberProperty() {
        return flightNumber;
    }

    public SimpleStringProperty departureLocationProperty() {
        return departureLocation;
    }

    public SimpleStringProperty destinationProperty() {
        return destination;
    }

    public SimpleObjectProperty<Date> depDateProperty() {
        return depDate;
    }

    public SimpleObjectProperty<Date> arrDateProperty() {
        return arrDate;
    }

    public SimpleStringProperty airplaneNumberProperty() {
        return airplaneNumber;
    }

    public SimpleObjectProperty<Button> cancelButtonProperty() {
        return cancelButton;
    }
}
