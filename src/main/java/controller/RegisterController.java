package controller;
import fxapp.MainFXApp;
import fxapp.UserType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import fxapp.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Yash on 4/18/2017.
 */
public class RegisterController {
    private MainFXApp main;

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox comboBox;
    @FXML
    private ComboBox selectState;
    @FXML
    private ComboBox selectCity;
    @FXML
    private TextField titleField;
    @FXML
    private Text errorText;

    public void register(MainFXApp main) {
        this.main = main;
    }

    @FXML
    public void onRegisterPressed() throws SQLException {
        if (usernameField.getText().equals("")) {
            errorText.setText("Please enter a username.");
        } else if (checkUNUnique(usernameField.getText()) == false) {
            errorText.setText("An account with that username already exists!");
        } else if (emailField.getText().equals("")) {
            errorText.setText("Please enter an email.");
        } else if (checkEmailUnique(emailField.getText()) == false) {
            errorText.setText("An account with that email address already exists!");
        } else if (passwordField.getText().equals("")) {
            errorText.setText("Please enter a password.");
        } else if (!passwordField.getText().equals(confirmPasswordField.getText())){
            errorText.setText("Passwords do not match!");
        } else {
            String userType = "";
            if (comboBox.getValue() == "City Scientist") {
                userType = "City Scientist";
                createNewUser(usernameField.getText(), emailField.getText(), passwordField.getText(), userType);
                main.setLoginScene();
            } else {
                userType = "City Official";
                Object state = selectState.getSelectionModel().getSelectedItem();
                Object city = selectCity.getSelectionModel().getSelectedItem();
                String title = titleField.getText();
                if (state == null) {
                    errorText.setText("Please select a state!");
                } else if (city == null) {
                    errorText.setText("Please select a city!");
                } else if (title.equals("")) {
                    errorText.setText("Please enter a title!");
                } else {
                    createNewUser(usernameField.getText(), emailField.getText(), passwordField.getText(), userType);
                    createCityOfficial(usernameField.getText(), emailField.getText(), state.toString(), city.toString(), title);
                    main.setLoginScene();
                }
            }
        }
    }

    private void createNewUser(String username, String email, String password, String userType) throws SQLException {
        Statement stmt = null;
        String update = String.format("INSERT INTO USER (EmailAddress, Username, Password, UserType) VALUES ('%s', '%s', '%s', '%s')"
                , email, username, password, userType);
        try {
            DBConnection.connectAndUpdate(stmt, update);
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void createCityOfficial(String username, String email, String state, String city, String title) throws SQLException {
        Statement stmt = null;
        String update = String.format("INSERT INTO CITY_OFFICIAL (EmailAddress, Username, Title, City, State) VALUES ('%s', '%s', '%s', '%s', '%s')"
                , email, username, title, city, state);
        try {
            DBConnection.connectAndUpdate(stmt, update);
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private boolean checkUNUnique(String username) throws SQLException{
        Statement stmt = null;
        String query = "SELECT Username FROM USER WHERE Username = '" + username + "'";
        try {
            ResultSet result = DBConnection.connectAndQuery(stmt, query);
            while (result.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return true;
    }

    private boolean checkEmailUnique(String email) throws SQLException {
        Statement stmt = null;
        String query = "SELECT EmailAddress FROM USER WHERE EmailAddress = '" + email + "'";
        try {
            ResultSet result = DBConnection.connectAndQuery(stmt, query);
            while (result.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return true;
    }

    @FXML
    public void initialize() throws SQLException{
        comboBox.getItems().removeAll(comboBox.getItems());
        comboBox.getItems().addAll("City Scientist", "City Official");
        comboBox.getSelectionModel().select("City Scientist");

        //ADD CODE WHICH QUERIES AND GETS ALL THE STATES
        Statement stmt = null;
        String query = "SELECT DISTINCT State FROM CITY_STATE ORDER BY State";
        try {
            ResultSet result = DBConnection.connectAndQuery(stmt, query);
            while (result.next()) {
                selectState.getItems().add(result.getString("State"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        //selectState.getSelectionModel().select("sdlfsjdf");

    }
   // @FXML
//    public void switchBox() {
//        if (comboBox.getValue() == "City Official") {
//            //main.setMainScene(UserType.CITY_OFFICIAL);
//
//        } else if (comboBox.getValue() == "City Scientist") {
//            //main.setMainScene(UserType.)
//        }
//    }
    @FXML
    public void switchBoxState() throws SQLException{
        //ADD CODE WHICH ADDS ALL CITIES WITHIN A STATE W/ SQL
        //System.out.println(selectState.getValue());
        String state = selectState.getValue().toString();
        Statement stmt = null;
        selectCity.getItems().removeAll(selectCity.getItems());
        String query = "SELECT City FROM CITY_STATE WHERE State = '" +
                state + "' ORDER BY City";
        try {
            ResultSet result = DBConnection.connectAndQuery(stmt, query);
            while (result.next()) {
                selectCity.getItems().add(result.getString("City"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    @FXML
    public void switchBoxCity() {
        System.out.println(selectCity.getValue());
    }

}
