package main.java.com.controllers;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.java.com.Airplane;
import main.java.com.database.DButil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
@SuppressWarnings("Duplicates")
public class AddAirplaneController implements Initializable{

    @FXML
    private JFXTextField airplaneNumber;
    @FXML
    private JFXTextField type;
    @FXML
    private JFXTextField size;
    @FXML
    private JFXTextField capacity;
    @FXML
    private JFXDatePicker dateOfPurchase;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    { }

    public void addAirplane(ActionEvent event)
    {
        DButil DBu = DButil.getCurrentInstance();

        if (airplaneNumber.getText().equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have to enter the airplane number!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        for (Airplane airplane : DBu.getAllAirplanes())
        {
            if (airplane.getAirplaneNumber().equals(airplaneNumber.getText()))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Airplane number already used. Try another one!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }
        if (!size.getText().matches("([0-9]*)(\\.)?([0-9]*)") || !capacity.getText().matches("([0-9]*)(\\.)?(0)*"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You entered invalid airplane informations. Please verify again!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try
        {
            Airplane airplane  = new Airplane("","",0.0,0,null,"inactive");

            airplane.setAirplaneNumber(airplaneNumber.getText());

            if (type.getText().equals(""))
            {
                airplane.setType(null);
            }
            else
                airplane.setType(type.getText());

            if (size.getText().equals(""))
            {
                airplane.setSize(-1.0);
            }
            else
                airplane.setSize(Double.parseDouble(size.getText()));

            if (capacity.getText().equals(""))
            {
                airplane.setCapacity(-1);
            }
            else
                airplane.setCapacity((int)Double.parseDouble(capacity.getText()));

            if (dateOfPurchase.getValue() == null)
            {
                airplane.setDateOfPurchase(null);
            }
            else
                airplane.setDateOfPurchase(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfPurchase.getValue().toString()));

            DBu.addAirplane(airplane);
            AirplanesListController.getListItems().add(DBu.getAirplaneById(airplane.getAirplaneNumber()));
            AirplanesListController.setItemsButtonsEvents();
            AirplanesListController.setItemsCheckBoxesEvents();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Airplane added successfully!", ButtonType.OK);
            alert.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void cancel(ActionEvent event)
    {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
}
