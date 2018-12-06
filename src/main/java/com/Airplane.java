package main.java.com;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.util.Date;


public class Airplane
{
    private SimpleObjectProperty<CheckBox> checkBox;
    private SimpleStringProperty airplaneNumber;
    private SimpleStringProperty type;
    private SimpleDoubleProperty size;
    private SimpleIntegerProperty capacity;
    private SimpleObjectProperty<Date> dateOfPurchase;
    private SimpleStringProperty status;
    private SimpleObjectProperty<Button> addFlightButton;


    public Airplane(String airplaneNumber, String type, double size, int capacity, Date dateOfPurchase,String status)
    {
        this.checkBox = new SimpleObjectProperty<>(new CheckBox());
        this.airplaneNumber = new SimpleStringProperty(airplaneNumber);
        this.type = new SimpleStringProperty(type);
        this.size = new SimpleDoubleProperty(size);
        this.capacity = new SimpleIntegerProperty(capacity);
        this.dateOfPurchase = new SimpleObjectProperty(dateOfPurchase);
        this.status = new SimpleStringProperty(status);
        this.addFlightButton = new SimpleObjectProperty<>(new Button("Add flight"));

    }

    public CheckBox getCheckBox() {
        return checkBox.get();
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox.set(checkBox);
    }

    public String getAirplaneNumber() {
        return airplaneNumber.get();
    }


    public void setAirplaneNumber(String airplaneNumber) {
        this.airplaneNumber.set(airplaneNumber);
    }

    public String getType() {
        return type.get();
    }


    public void setType(String type) {
        this.type.set(type);
    }

    public double getSize() {
        return size.get();
    }


    public void setSize(double size) {
        this.size.set(size);
    }

    public int getCapacity() {
        return capacity.get();
    }

    public void setCapacity(int capacity) {
        this.capacity.set(capacity);
    }

    public Date getDateOfPurchase() {
        return dateOfPurchase.get();
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase.set(dateOfPurchase);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public Button getAddFlightButton() {
        return addFlightButton.get();
    }

    public void setAddFlightButton(Button addFlightButton) {
        this.addFlightButton.set(addFlightButton);
    }

    //getters and seters for Simples Properties
    public SimpleObjectProperty<CheckBox> checkBoxProperty() {
        return checkBox;
    }

    public SimpleStringProperty airplaneNumberProperty() {
        return airplaneNumber;
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public SimpleDoubleProperty sizeProperty() {
        return size;
    }

    public SimpleIntegerProperty capacityProperty() {
        return capacity;
    }

    public SimpleObjectProperty<Date> dateOfPurchaseProperty() {
        return dateOfPurchase;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public SimpleObjectProperty<Button> addFlightButtonProperty() {
        return addFlightButton;
    }
}

