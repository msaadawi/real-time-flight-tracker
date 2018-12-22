package main.java.com.controllers;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import main.java.com.Airplane;
import main.java.com.application.Main;
import main.java.com.database.DButil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ResourceBundle;
@SuppressWarnings("Duplicates")
public class EditAirplaneController implements Initializable
{
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

    private static Airplane airplaneToEdit ;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        airplaneNumber.setText(airplaneToEdit.getAirplaneNumber());
        type.setText(airplaneToEdit.getType());
        size.setText(String.valueOf(airplaneToEdit.getSize()));
        capacity.setText(String.valueOf(airplaneToEdit.getCapacity()));
        if (airplaneToEdit.getDateOfPurchase() == null)
            dateOfPurchase.setValue(null);
        else
            dateOfPurchase.setValue(Instant.ofEpochMilli(airplaneToEdit.getDateOfPurchase().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public void saveChanges(ActionEvent event)
    {
        DButil DBu = DButil.getCurrentInstance();

        if (airplaneNumber.getText().equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have to enter the airplane number!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (!airplaneNumber.getText().equals(airplaneToEdit.getAirplaneNumber()))
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
            Airplane newAirplane  = new Airplane("","",0.0,0,null,"-");

            newAirplane.setAirplaneNumber(airplaneNumber.getText());

            if (type.getText() == null || type.getText().equals(""))
            {
                newAirplane.setType(null);
            }
            else
                newAirplane.setType(type.getText());

            if (size.getText().equals(""))
            {
                newAirplane.setSize(-1.0);
            }
            else
                newAirplane.setSize(Double.parseDouble(size.getText()));

            if (capacity.getText().equals(""))
            {
                newAirplane.setCapacity(-1);
            }
            else
                newAirplane.setCapacity((int)Double.parseDouble(capacity.getText()));

            if (dateOfPurchase.getValue() == null)
            {
                newAirplane.setDateOfPurchase(null);
            }
            else
                newAirplane.setDateOfPurchase(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfPurchase.getValue().toString()));

            //Airplane newAirplane = new Airplane(airplaneNumber.getText(),type.getText(),Double.parseDouble(size.getText()),Integer.parseInt(capacity.getText()),new SimpleDateFormat("yyyy-MM-dd").parse(dateOfPurchase.getValue().toString()),airplaneToEdit.getStatus());

            DBu.editAirplane(airplaneToEdit , newAirplane);
            newAirplane.setStatus(DBu.getAirplaneById(newAirplane.getAirplaneNumber()).getStatus());
            newAirplane.setDateOfPurchase(DBu.getAirplaneById(newAirplane.getAirplaneNumber()).getDateOfPurchase());

            for (int i = 0; i < AirplanesListController.getListItems().size() ; i++)
            {
                if (AirplanesListController.getListItems().get(i).getAirplaneNumber() == airplaneToEdit.getAirplaneNumber())
                {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(true);
                    newAirplane.setCheckBox(checkBox);
                    AirplanesListController.getListItems().set(i, newAirplane);
                    Main.getRoot().lookup("#EditAirplaneButton").setDisable(false);
                    Main.getRoot().lookup("#DeleteAirplaneButton").setDisable(false);
                    AirplanesListController.setItemsCheckBoxesEvents();
                    AirplanesListController.setItemsButtonsEvents();
                }
            }
            cancel(event);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Changes saved successfully!", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel(ActionEvent event)
    {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    public static Airplane getAirplaneToEdit() {
        return airplaneToEdit;
    }

    public static void setAirplaneToEdit(Airplane airplaneToEdit) {
        EditAirplaneController.airplaneToEdit = airplaneToEdit;
    }
}
