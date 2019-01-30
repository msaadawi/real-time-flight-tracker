package main.java.com.controllers;


import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.com.Airplane;
import main.java.com.Flight;
import main.java.com.application.Main;
import main.java.com.database.DButil;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class AddFlightController implements Initializable
{
    private static Stage stage = null;

    private static Parent root = null;

    private static Scene scene = null;

    private static WebView webView = null;

    private static WebEngine webEngine = null;

    private static String airplaneNumber;

    @FXML
    private JFXTextField flightNumber;

    @FXML
    private ChoiceBox<String> flightDepartureLocation;

    @FXML
    private ChoiceBox<String> flightDestination;

    @FXML
    private JFXDatePicker flightDepartureDate;

    @FXML
    private JFXTimePicker flightDepartureTime;

    @FXML
    private JFXDatePicker flightArrivalDate;

    @FXML
    private JFXTimePicker flightArrivalTime;

    public AddFlightController() throws IOException
    {}

    @Override
    public void initialize(URL location, ResourceBundle resources){}

    public void addFlight(ActionEvent event)
    {
        DButil DBu = DButil.getCurrentInstance();
        try
        {
            if (flightNumber.getText().equals("") || flightDepartureDate.getValue() == null || flightDepartureTime.getValue() == null || flightArrivalDate.getValue() == null ||flightArrivalTime.getValue() == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "One or more fields are not filled. You have to fill them all!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            for (Flight flight : DBu.getAllFlights())
            {
                if (flight.getFlightNumber().equals(flightNumber.getText()))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Flight number already used. Try another one!", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
            }

            if (flightDepartureLocation.getValue().equals(flightDestination.getValue()) || (flightDepartureDate.getValue().toString()+" "+flightDepartureTime.getValue().toString()).equals(flightArrivalDate.getValue().toString()+" "+flightArrivalTime.getValue().toString()))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You entered invalid flight informations. Please try again!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            LocalDate flightDepDate = flightDepartureDate.getValue();
            LocalTime flightDepTime = flightDepartureTime.getValue();
            LocalDate flightArrDate = flightArrivalDate.getValue();
            LocalTime flightArrTime = flightArrivalTime.getValue();
            String completeDepDate = flightDepDate.toString() + " " + flightDepTime.toString() + ":00";
            String completeArrDate = flightArrDate.toString() + " " + flightArrTime.toString() + ":00";
            Date depDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(completeDepDate);
            Date arrDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(completeArrDate);

            Flight flight = new Flight(flightNumber.getText(), flightDepartureLocation.getValue(), flightDestination.getValue(), depDate, arrDate, airplaneNumber);

            if (elapsedTime(flight.getDepDate(),flight.getArrDate()) > 0)
            {
                if(elapsedTime(flight.getArrDate(), new Date()) > 0)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "You entered invalid flight informations. Please try again!", ButtonType.OK);
                    alert.showAndWait();
                }
                else if (!checkAirplaneAvailability(flight.getAirplaneNumber(), flight))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This Airplane is not available at the specified time! Please try again", ButtonType.OK);
                    alert.showAndWait();
                }
                else if (elapsedTime(flight.getDepDate(), new Date()) >= 0 && elapsedTime(new Date(),flight.getArrDate()) > 0)
                {
                    DBu.addFlight(flight);
                    DBu.updateAirplaneStatus(flight.getAirplaneNumber());
                    for (int i = 0; i < AirplanesListController.getListItems().size() ; i++)
                    {
                        if (AirplanesListController.getListItems().get(i).getAirplaneNumber() == flight.getAirplaneNumber())
                        {
                            Airplane airplane = AirplanesListController.getListItems().get(i);
                            airplane.setStatus("active");
                            AirplanesListController.getListItems().set(i , airplane);
                        }
                    }

                    if (SeeFlightsController.getListItems() != null)
                    {
                        Flight flight1 = DBu.getFlightById(flight.getFlightNumber());
                        flight1.setCancelButton(null);
                        SeeFlightsController.getListItems().add(flight1);
                    }

                    if (stage == null)
                    {
                        String theHtmlFileUrl  = getClass().getResource("/WebBasedTracker/index.html").toExternalForm();

                        root = FXMLLoader.load(getClass().getResource("/fxml/AirplaneTracker.fxml"));

                        webView = (WebView) root.lookup("#map");

                        webEngine = webView.getEngine();

                        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) ->
                        {
                            if (newState == Worker.State.SUCCEEDED)
                            {
                                JSObject window = (JSObject) webEngine.executeScript("window");
                                for (Flight flight1 : DBu.getAllFlights()) {
                                    if (elapsedTime(flight1.getDepDate(), new Date()) > 0 && elapsedTime(new Date(), flight1.getArrDate()) > 0)
                                        window.call("addFlight", Double.parseDouble(Main.getAirportsList().get(flight1.getDepartureLocation()).split(",")[0])
                                                , Double.parseDouble(Main.getAirportsList().get(flight1.getDepartureLocation()).split(",")[1])
                                                , Double.parseDouble(Main.getAirportsList().get(flight1.getDestination()).split(",")[0])
                                                , Double.parseDouble(Main.getAirportsList().get(flight1.getDestination()).split(",")[1])
                                                , 1000 * elapsedTime(new Date(), flight1.getArrDate())
                                                , flight1.getDepartureLocation()
                                                , flight1.getDestination()
                                                , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight1.getDepDate())
                                                , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight1.getArrDate()));
                                }
                            }
                        });

                        webEngine.load(theHtmlFileUrl);

                        scene = new Scene(root);
                        stage = new Stage();
                        stage.setScene(scene);
                        stage.setOnCloseRequest((WindowEvent we) -> {webEngine = null ;webView = null; root = null; scene = null; stage = null;});
                        stage.setResizable(false);
                        stage.show();

                    }
                    else
                    {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.call("addFlight",
                                Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[0])
                                , Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[1])
                                , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[0])
                                , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[1])
                                , 1000 * elapsedTime(new Date() , flight.getArrDate())
                                , flight.getDepartureLocation()
                                , flight.getDestination()
                                , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getDepDate())
                                , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getArrDate()));
                    }
                }
                else
                {
                    DBu.addFlight(flight);
                    Main.getFutureFlights().add(flight);
                    if (SeeFlightsController.getListItems() != null)
                    {
                        flight = DBu.getFlightById(flight.getFlightNumber());
                        Flight finalFlight = flight;
                        flight.getCancelButton().setOnAction(event1 ->
                        {
                            DBu.deleteFlight(finalFlight);
                            SeeFlightsController.getListItems().remove(finalFlight);
                        });
                        SeeFlightsController.getListItems().add(flight);
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Flight added successfully to database. it will begin when ready!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You entered invalid flight informations. Please verify again!", ButtonType.OK);
                alert.showAndWait();
            }
        }
        catch (Exception e)
        { e.printStackTrace(); }
    }

    public void cancel(ActionEvent event)
    {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }


    //date2 - date1 (result could be negative)
    public static long elapsedTime(Date date1, Date date2)
    {
        return (date2.getTime() - date1.getTime())/1000 ;
    }

    public static String getAirplaneNumber() {
        return airplaneNumber;
    }

    public static void setAirplaneNumber(String airplaneNumber) {
        AddFlightController.airplaneNumber = airplaneNumber;
    }

    public static void loadMap()
    {
        DButil DBu = DButil.getCurrentInstance();

        try
        {
            if (stage == null)
            {

                String theHtmlFileUrl = AddFlightController.class.getResource("/WebBasedTracker/index.html").toExternalForm();

                root = FXMLLoader.load(AddFlightController.class.getResource("/fxml/AirplaneTracker.fxml"));

                webView = (WebView) root.lookup("#map");

                webEngine = webView.getEngine();

                webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) ->
                {
                    if (newState == Worker.State.SUCCEEDED)
                    {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        for (Flight flight : DBu.getAllFlights())
                        {
                            if (elapsedTime(flight.getDepDate(), new Date()) > 0 && elapsedTime(new Date(), flight.getArrDate()) > 0)
                                window.call("addFlight", Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[0])
                                        , Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[1])
                                        , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[0])
                                        , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[1])
                                        , 1000 * elapsedTime(new Date(), flight.getArrDate())
                                        , flight.getDepartureLocation()
                                        , flight.getDestination()
                                        , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getDepDate())
                                        , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flight.getArrDate()));
                        }
                    }
                });

                webEngine.load(theHtmlFileUrl);

                scene = new Scene(root);
                stage = new Stage();
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent we) ->
                {
                    webEngine = null;
                    webView = null;
                    root = null;
                    scene = null;
                    stage = null;
                });
                stage.setResizable(false);
                stage.show();
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static WebEngine getWebEngine() {
        return webEngine;
    }


    public Boolean checkAirplaneAvailability(String airplaneNumber, Flight flight) {
        DButil DBu = DButil.getCurrentInstance();
        List<Flight> airplaneFutureFlights = new ArrayList<>();
        Flight currentFlight = DBu.getAirplaneCurrentFlight(airplaneNumber);
        if (currentFlight != null)
        {
            if (elapsedTime(currentFlight.getDepDate(), flight.getDepDate()) == 0 || elapsedTime(currentFlight.getArrDate(), flight.getDepDate()) == 0 || elapsedTime(currentFlight.getDepDate(), flight.getArrDate()) == 0 || elapsedTime(currentFlight.getArrDate(), flight.getArrDate()) == 0)
                return false;
            if (elapsedTime(currentFlight.getDepDate(), flight.getDepDate()) > 0 && elapsedTime(currentFlight.getArrDate(), flight.getDepDate()) < 0)
                return false;
            if (elapsedTime(currentFlight.getDepDate(), flight.getArrDate()) > 0 && elapsedTime(currentFlight.getArrDate(), flight.getArrDate()) < 0)
                return false;
            if (elapsedTime(flight.getDepDate(), currentFlight.getDepDate()) > 0 && elapsedTime(flight.getArrDate(), currentFlight.getArrDate()) < 0)
                return false;
        }

        for (Flight flight1 : Main.getFutureFlights())
        {
            if (flight1.getAirplaneNumber().equals(airplaneNumber))
                airplaneFutureFlights.add(flight1);
        }

        if (airplaneFutureFlights.size() != 0) {
            for (Flight flight2 : airplaneFutureFlights) {
                if (elapsedTime(flight2.getDepDate(), flight.getDepDate()) == 0 || elapsedTime(flight2.getArrDate(), flight.getDepDate()) == 0 || elapsedTime(flight2.getDepDate(), flight.getArrDate()) == 0 || elapsedTime(flight2.getArrDate(), flight.getArrDate()) == 0)
                    return false;
                if (elapsedTime(flight2.getDepDate(), flight.getDepDate()) > 0 && elapsedTime(flight2.getArrDate(), flight.getDepDate()) < 0)
                    return false;
                if (elapsedTime(flight2.getDepDate(), flight.getArrDate()) > 0 && elapsedTime(flight2.getArrDate(), flight.getArrDate()) < 0)
                    return false;
                if (elapsedTime(flight.getDepDate(), flight2.getDepDate()) > 0 && elapsedTime(flight.getArrDate(), flight2.getArrDate()) < 0)
                    return false;
            }
        }

        return true;

    }
}
