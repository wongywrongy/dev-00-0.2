package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ShowStudentReportController {
    private Stage stage;

    public RadioButton rbEmployed;
    public RadioButton rbUnemployed;
    public TextField txtJobDetails;
    @FXML
    private TextField txtFullName;
    @FXML
    private ComboBox cbAcademicStatus;

    public void updateStudentReportPage(StudentProfile studentProfile) {
        txtFullName.setText(studentProfile.getFullName());
        cbAcademicStatus.setValue(studentProfile.getAcademicStatus());
        if (studentProfile.isEmployed()) rbEmployed.setSelected(true);
        else rbUnemployed.setSelected(true);
        txtJobDetails.setText(studentProfile.getJobDetails());
    }

    public void goToHome(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
