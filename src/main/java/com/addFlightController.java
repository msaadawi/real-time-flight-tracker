package main.java.com;


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
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class addFlightController implements Initializable
{
    private static Stage stage = null;

    private static Parent root = null;

    private static Scene scene = null;

    private static WebView webView = null;

    private static WebEngine webEngine = null;

    private static JSObject window = null;

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

    public addFlightController() throws IOException
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
                Alert alert = new Alert(Alert.AlertType.ERROR, "You entered invalid flight informations. Please verify again!", ButtonType.OK);
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

            if (DBu.getAirplaneById(flight.getAirplaneNumber()).getStatus().equals("active") && (elapsedTime(flight.getDepDate(), new Date()) > 0 && elapsedTime(new Date(),flight.getArrDate()) > 0))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "This Airplane is on flight right now!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            if (elapsedTime(flight.getDepDate(),flight.getArrDate()) > 0)
            {
                if(elapsedTime(flight.getArrDate(), new Date()) > 0)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "You entered invalid flight informations. Please verify again!", ButtonType.OK);
                    alert.showAndWait();
                }
                else if (elapsedTime(flight.getDepDate(), new Date()) >= 0 && elapsedTime(new Date(),flight.getArrDate()) > 0)
                {
                    DBu.addFlight(flight);
                    DBu.updateAirplaneStatus(flight.getAirplaneNumber());
                    for (int i =0; i < AirplanesListController.getListItems().size() ;i++)
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
                        String theHtmlFileUrl  = getClass().getClassLoader().getResource("\\WebBasedTracker\\index.html").toExternalForm();

                        root = FXMLLoader.load(getClass().getResource("AirplaneTracker.fxml"));

                        webView = (WebView) root.lookup("#map");

                        webEngine = webView.getEngine();

                        webEngine.load(theHtmlFileUrl);

                        window = (JSObject) webEngine.executeScript("window");

                        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) ->
                        {
                            for (Flight flight1 : DBu.getAllFlights())
                            {
                                if (elapsedTime(flight1.getDepDate(), new Date()) > 0 && elapsedTime(new Date(), flight1.getArrDate()) > 0)
                                    window.call("addFlight", Double.parseDouble(Main.getAirportsList().get(flight1.getDepartureLocation()).split(",")[0])
                                            , Double.parseDouble(Main.getAirportsList().get(flight1.getDepartureLocation()).split(",")[1])
                                            , Double.parseDouble(Main.getAirportsList().get(flight1.getDestination()).split(",")[0])
                                            , Double.parseDouble(Main.getAirportsList().get(flight1.getDestination()).split(",")[1])
                                            , 1000 * elapsedTime(new Date(), flight1.getArrDate()));
                            }
                        });

                        scene = new Scene(root);
                        stage = new Stage();
                        stage.setScene(scene);
                        stage.setOnCloseRequest((WindowEvent we) -> {window = null; webEngine = null ;webView = null; root = null; scene = null; stage = null;});
                        stage.setResizable(false);
                        stage.show();

                    }
                    else
                    {
                        window.call("add_flight",
                                Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[0])
                                , Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[1])
                                , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[0])
                                , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[1])
                                , 1000 * elapsedTime(new Date() , flight.getArrDate()));
                    }
                }
                else
                {
                    DBu.addFlight(flight);
                    Main.getFutureFlights().add(flight);
                    if (SeeFlightsController.getListItems() != null)
                    {
                        flight.getCancelButton().setOnAction(event1 ->
                        {
                            DBu.deleteFlight(flight);
                            SeeFlightsController.getListItems().remove(flight);
                        });
                        SeeFlightsController.getListItems().add(DBu.getFlightById(flight.getFlightNumber()));
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
        addFlightController.airplaneNumber = airplaneNumber;
    }

    public static void loadMap()
    {
        DButil DBu = DButil.getCurrentInstance();

        try
        {
            if (stage == null)
            {

                String theHtmlFileUrl = addFlightController.class.getClassLoader().getResource("\\WebBasedTracker\\index.html").toExternalForm();

                root = FXMLLoader.load(addFlightController.class.getResource("AirplaneTracker.fxml"));

                webView = (WebView) root.lookup("#map");

                webEngine = webView.getEngine();

                webEngine.load(theHtmlFileUrl);

                window = (JSObject) webEngine.executeScript("window");

                webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) ->
                {
                    for (Flight flight : DBu.getAllFlights()) {
                        if (elapsedTime(flight.getDepDate(), new Date()) > 0 && elapsedTime(new Date(), flight.getArrDate()) > 0)
                            window.call("addFlight", Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[0])
                                    , Double.parseDouble(Main.getAirportsList().get(flight.getDepartureLocation()).split(",")[1])
                                    , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[0])
                                    , Double.parseDouble(Main.getAirportsList().get(flight.getDestination()).split(",")[1])
                                    , 1000 * elapsedTime(new Date(), flight.getArrDate()));
                    }
                });

                scene = new Scene(root);
                stage = new Stage();
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent we) ->
                {
                    window = null;
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

    public static JSObject getWindow() {
        return window;
    }

}
