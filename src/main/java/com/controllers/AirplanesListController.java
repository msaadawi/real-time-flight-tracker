package main.java.com.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.com.Airplane;
import main.java.com.Flight;
import main.java.com.application.Main;
import main.java.com.database.DButil;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;

@SuppressWarnings("Duplicates")

public class AirplanesListController implements Initializable
{
    @FXML
    private TableView<Airplane> tableViewList;
    @FXML
    private TableColumn<Airplane, CheckBox> checkBoxCol;
    @FXML
    private TableColumn<Airplane, String> airplaneNumberCol;
    @FXML
    private TableColumn<Airplane, String> typeCol;
    @FXML
    private TableColumn<Airplane, Double> sizeCol;
    @FXML
    private TableColumn<Airplane, Integer> capacityCol;
    @FXML
    private TableColumn<Airplane, Date> dateOfPurchaseCol;
    @FXML
    private TableColumn<Airplane, String> statusCol;
    @FXML
    private TableColumn<Airplane, Button> addFlightButtonCol;

    @FXML
    private TextField airplaneNumber;
    @FXML
    private TextField typeField;
    @FXML
    private TextField sizeField;
    @FXML
    private TextField capacityField;
    @FXML
    private ChoiceBox<String> dofDayChoiceBox;
    @FXML
    private ChoiceBox<String> dofMonthChoiceBox;
    @FXML
    private ChoiceBox<String> dofYearChoiceBox;
    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private Button EditAirplaneButton;
    @FXML
    private Button DeleteAirplaneButton;

    private static ObservableList<Airplane> listItems;

    private static SortedList<Airplane> sortedList;

    private static Stage flightsStage;

    public AirplanesListController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkBoxCol.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        airplaneNumberCol.setCellValueFactory(new PropertyValueFactory<>("airplaneNumber"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        dateOfPurchaseCol.setCellValueFactory(new PropertyValueFactory<>("dateOfPurchase"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        addFlightButtonCol.setCellValueFactory(new PropertyValueFactory<>("addFlightButton"));

        DButil DBu = DButil.getCurrentInstance();
        DBu.cleanFlights();

        listItems = DBu.getAllAirplanes();

        EditAirplaneButton.setDisable(true);
        DeleteAirplaneButton.setDisable(true);

        setItemsButtonsEvents();
        setItemsCheckBoxesEvents();

        FilteredList<Airplane> filteredList = new FilteredList<>(listItems, p -> true);

        ObjectProperty<Predicate<Airplane>> airplaneNumberFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> typeFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> sizeFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> capacityFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> dopDayFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> dopMonthFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> dopYearFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Airplane>> statusFilter = new SimpleObjectProperty<>();

        airplaneNumberFilter.bind(Bindings.createObjectBinding(()-> airplane ->
        {
           if (airplaneNumber.getText().equals(""))
               return true;
           if (airplane.getAirplaneNumber().startsWith(airplaneNumber.getText()))
               return true;

           return false;
        },airplaneNumber.textProperty()));

        typeFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (typeField.getText().equals(""))
                return true;
            if (airplane.getType().toLowerCase().startsWith(typeField.getText().toLowerCase()))
                return true;

            return false;
        }, typeField.textProperty()));

        sizeFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (sizeField.getText().equals(""))
                return true;

            if (!sizeField.getText().matches("([0-9]*)(\\.)?([0-9]*)"))
                return false;

            if (airplane.getSize() == Double.parseDouble(sizeField.getText()))
                return true;

            return false;
        }, sizeField.textProperty()));

        capacityFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (capacityField.getText().equals(""))
                return true;
            if (!capacityField.getText().matches("([0-9]*)(\\.)?(0)*"))
                return false;

            if (airplane.getCapacity() == Double.parseDouble(capacityField.getText()))
                return true;

            return false;
        }, capacityField.textProperty()));

        dopDayFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (dofDayChoiceBox.getValue().equals("-"))
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd").format(airplane.getDateOfPurchase()).split("-")[2].equals(dofDayChoiceBox.getValue()))
                return true;

            return false;
        }, dofDayChoiceBox.valueProperty()));

        dopMonthFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (dofMonthChoiceBox.getValue().equals("-"))
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd").format(airplane.getDateOfPurchase()).split("-")[1].equals(dofMonthChoiceBox.getValue()))
                return true;

            return false;
        }, dofMonthChoiceBox.valueProperty()));

        dopYearFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (dofYearChoiceBox.getValue().equals("-"))
                return true;

            if (new SimpleDateFormat("yyyy-MM-dd").format(airplane.getDateOfPurchase()).split("-")[0].equals(dofYearChoiceBox.getValue()))
                return true;

            return false;
        }, dofYearChoiceBox.valueProperty()));

        statusFilter.bind(Bindings.createObjectBinding(() -> airplane ->
        {
            if (statusChoiceBox.getValue().equals("-"))
                return true;

            if (airplane.getStatus().equals(statusChoiceBox.getValue()))
                return true;

            return false;
        }, statusChoiceBox.valueProperty()));

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
                airplaneNumberFilter.get().and(typeFilter.get()).and(sizeFilter.get()).and(capacityFilter.get()).and(dopDayFilter.get()).and(dopMonthFilter.get()).and(dopYearFilter.get()).and(statusFilter.get()),airplaneNumberFilter, typeFilter, sizeFilter, capacityFilter, dopDayFilter, dopMonthFilter, dopYearFilter, statusFilter));

        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableViewList.comparatorProperty());

        tableViewList.setItems(sortedList);


    }

    public static void setItemsButtonsEvents()
    {
        for (Airplane airplane : listItems)
        {
            airplane.getAddFlightButton().setOnAction(event ->
            {
                try
                {
                    AddFlightController.setAirplaneNumber(airplane.getAirplaneNumber());
                    Parent root = FXMLLoader.load(AirplanesListController.class.getResource("/fxml/addFlight.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.setTitle("Airplane " + airplane.getAirplaneNumber());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void setItemsCheckBoxesEvents()
    {

        for (Airplane airplane : listItems)
        {
            airplane.getCheckBox().selectedProperty().addListener((observable, oldValue, newValue) ->
            {
                int j = 0;
                for (int i = 0; i < listItems.size(); i++)
                {
                    if (listItems.get(i).getCheckBox().isSelected())
                        j++;
                }
                if (j == 1)
                    Main.getRoot().lookup("#EditAirplaneButton").setDisable(false);
                else
                    Main.getRoot().lookup("#EditAirplaneButton").setDisable(true);
                if (j >= 1)
                    Main.getRoot().lookup("#DeleteAirplaneButton").setDisable(false);
                else
                    Main.getRoot().lookup("#DeleteAirplaneButton").setDisable(true);
            });
        }

    }

    public void close(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public void addAirplane(ActionEvent event) {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/addAirplane.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(" Add Airplane");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openGoogleMap(ActionEvent event) {
        AddFlightController.loadMap();
    }

    public void editAirplane(ActionEvent event)
    {

        for (int k = 0; k < listItems.size(); k++)
        {
            if (listItems.get(k).getCheckBox().isSelected())
            {
                EditAirplaneController.setAirplaneToEdit(listItems.get(k));
                try
                {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/editAirplane.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.setTitle("Edit Airplane " + listItems.get(k).getAirplaneNumber());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void deleteAirplane(ActionEvent event)
    {
        ArrayList<Airplane> airplanes = new ArrayList<>();
        ArrayList<Flight> flights = new ArrayList<>();
        DButil DBu = DButil.getCurrentInstance();

        for (Airplane airplane : listItems)
        {
            if (airplane.getCheckBox().isSelected())
            {
                airplanes.add(airplane);
                DBu.deleteAirplane(airplane);
            }
        }
        listItems.removeAll(airplanes);
        EditAirplaneButton.setDisable(true);
        DeleteAirplaneButton.setDisable(true);

        if (flightsStage != null)
        {
            for (Airplane airplane : airplanes)
            {
                for (Flight flight : SeeFlightsController.getListItems())
                {
                    if(flight.getAirplaneNumber().equals(airplane.getAirplaneNumber()))
                        flights.add(flight);
                }
            }
        }

        SeeFlightsController.getListItems().removeAll(flights);
    }

    public void seeFlights(ActionEvent event)
    {
        if (flightsStage == null)
        {
            try
            {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/seeFlights.fxml"));
                Scene scene = new Scene(root);
                flightsStage = new Stage();
                flightsStage.setScene(scene);
                flightsStage.setResizable(false);
                flightsStage.setTitle("Flights");
                flightsStage.show();
                flightsStage.setOnCloseRequest((WindowEvent we) -> flightsStage = null);

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public static ObservableList<Airplane> getListItems() {
        return listItems;
    }

    public static void setListItems(ObservableList<Airplane> listItems) {
        AirplanesListController.listItems = listItems;
    }

    public static Stage getFlightsStage() {
        return flightsStage;
    }

    public static void setFlightsStage(Stage flightsStage) {
        AirplanesListController.flightsStage = flightsStage;
    }

}
