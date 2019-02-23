package main.java.com.controllers;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.com.Flight;
import main.java.com.application.Main;
import main.java.com.database.DButil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;

@SuppressWarnings("Duplicates")
public class SeeFlightsController implements Initializable
{

    @FXML
    private TableView<Flight> tableViewList;
    @FXML
    private TableColumn<Flight, String> flightNumberCol;
    @FXML
    private TableColumn<Flight, String> airplaneNumberCol;
    @FXML
    private TableColumn<Flight, String> departureLocationCol;
    @FXML
    private TableColumn<Flight, String> DestinationCol;
    @FXML
    private TableColumn<Flight, Date> departureDateCol;
    @FXML
    private TableColumn<Flight, Date> arrivalDateCol;
    @FXML
    private TableColumn<Flight, Date> cancelButtonCol;

    @FXML
    private TextField flightNumber;
    @FXML
    private TextField airplaneNumber; //default value is ""
    @FXML
    private ChoiceBox<String> status; //default value is "-"
    @FXML
    private ChoiceBox<String> depLocation;  //default value is "-"
    @FXML
    private ChoiceBox<String> destination; //default value is "-"
    @FXML
    private JFXDatePicker depDate; //default value is null
    @FXML
    private JFXTimePicker depTime;  //default value is null
    @FXML
    private JFXDatePicker arrDate;  //default value is null
    @FXML
    private JFXTimePicker arrTime;  //default value is null

    private static ObservableList<Flight> listItems;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        flightNumberCol.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        airplaneNumberCol.setCellValueFactory(new PropertyValueFactory<>("airplaneNumber"));
        departureLocationCol.setCellValueFactory(new PropertyValueFactory<>("departureLocation"));
        DestinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureDateCol.setCellValueFactory(new PropertyValueFactory<>("depDate"));
        arrivalDateCol.setCellValueFactory(new PropertyValueFactory<>("arrDate"));
        cancelButtonCol.setCellValueFactory(new PropertyValueFactory<>("cancelButton"));

        DButil DBu = DButil.getCurrentInstance();
        listItems = DBu.getAllFlights();
        FilteredList<Flight> filteredList = new FilteredList<>(listItems, p -> true);

        ObjectProperty<Predicate<Flight>> flightNumberFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> airplaneNumberFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> statusFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> depLocFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> destFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> depDateFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> depTimeFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> arrDateFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Flight>> arrTimeFilter = new SimpleObjectProperty<>();

        flightNumberFilter.bind(Bindings.createObjectBinding(()-> flight ->
        {
            if (flightNumber.getText().equals(""))
                return true;
            if (flight.getFlightNumber().startsWith(flightNumber.getText()))
                return true;

            return false;
        },flightNumber.textProperty()));

        airplaneNumberFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (airplaneNumber.getText().equals(""))
                return true;

            if (flight.getAirplaneNumber().contains(airplaneNumber.getText()))
                return true;

            return false;
        }, airplaneNumber.textProperty()));

        statusFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (status.getValue().equals("-"))
                return true;

            if (status.getValue().equals("Terminated")) {
                if (AddFlightController.elapsedTime(flight.getArrDate(), new Date()) > 0)
                    return true;
            }
            if (status.getValue().equals("Current")) {
                if (AddFlightController.elapsedTime(flight.getDepDate(), new Date()) > 0 && AddFlightController.elapsedTime(new Date(), flight.getArrDate()) > 0)
                    return true;
            }
            if (status.getValue().equals("Future")) {
                if (AddFlightController.elapsedTime(new Date(), flight.getDepDate()) > 0)
                    return true;
            }
            return false;
        }, status.valueProperty()));

        depLocFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (depLocation.getValue().equals("-"))
                return true;

            if (flight.getDepartureLocation().equals(depLocation.getValue()))
                return true;

            return false;

        }, depLocation.valueProperty()));

        destFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (destination.getValue().equals("-"))
                return true;

            if (flight.getDestination().equals(destination.getValue()))
                return true;

            return false;

        }, destination.valueProperty()));

        depDateFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (depDate.getValue() == null)
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getDepDate()).split(" ")[0].equals(java.sql.Date.valueOf(depDate.getValue()).toString()))
                return true;

            return false;
        }, depDate.valueProperty()));

        depTimeFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (depTime.getValue() == null)
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getDepDate()).split(" ")[1].startsWith(depTime.getValue().toString()))
                return true;

            return false;
        }, depTime.valueProperty()));

        arrDateFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (arrDate.getValue() == null)
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getArrDate()).split(" ")[0].equals(java.sql.Date.valueOf(arrDate.getValue()).toString()))
                return true;

            return false;

        }, arrDate.valueProperty()));

        arrTimeFilter.bind(Bindings.createObjectBinding(() -> flight ->
        {
            if (arrTime.getValue() == null)
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getArrDate()).split(" ")[1].startsWith(arrTime.getValue().toString()))
                return true;

            return false;

        }, arrTime.valueProperty()));

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
                flightNumberFilter.get().and(airplaneNumberFilter.get()).and(statusFilter.get()).and(depLocFilter.get()).and(destFilter.get()).and(depDateFilter.get()).and(depTimeFilter.get()).and(arrDateFilter.get()).and(arrTimeFilter.get()), flightNumberFilter, airplaneNumberFilter, statusFilter, depLocFilter, destFilter, depDateFilter, depTimeFilter, arrDateFilter, arrTimeFilter));

        //set Cancel buttons events
        for (Flight flight : listItems)
        {
            if (AddFlightController.elapsedTime(new Date(),flight.getDepDate()) > 0)
            {
                flight.getCancelButton().setOnAction(event ->
                {
                    DBu.deleteFlight(flight);
                    Main.setFutureFlights(DBu.getFutureFlights());
                    SeeFlightsController.getListItems().remove(flight);
                });
            }
            else
                flight.setCancelButton(null);
        }

        SortedList<Flight> sortedList = new SortedList<>(filteredList);

        sortedList.comparatorProperty().bind(tableViewList.comparatorProperty());

        tableViewList.setItems(sortedList);

    }

    public void close(ActionEvent event)
    {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        AirplanesListController.setFlightsStage(null);
    }

    public static ObservableList<Flight> getListItems() {
        return listItems;
    }

    public static void setListItems(ObservableList<Flight> listItems) {
        SeeFlightsController.listItems = listItems;
    }

}