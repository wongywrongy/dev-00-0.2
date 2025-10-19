package cs151.application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentProfilesController {


    @FXML private TextField txtFullName;
    @FXML private ComboBox<String> cbAcademicStatus;
    @FXML private RadioButton rbEmployed;
    @FXML private RadioButton rbUnemployed;
    @FXML private TextField txtJobDetails;
    @FXML private ListView<String> lvLanguages;
    @FXML private ListView<String> lvDatabases;
    @FXML private ComboBox<String> cbPreferredRole;
    @FXML private TextArea txtComments;
    @FXML private CheckBox chkWhitelist;
    @FXML private CheckBox chkBlacklist;

    @FXML
    public void initialize() {

        cbAcademicStatus.setItems(FXCollections.observableArrayList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));


        cbPreferredRole.setItems(FXCollections.observableArrayList(
                "Front-End", "Back-End", "Full-Stack", "Data", "Other"
        ));


        rbUnemployed.setSelected(true);
        txtJobDetails.setDisable(true);
        rbEmployed.selectedProperty().addListener((obs, was, employed) -> {
            txtJobDetails.setDisable(!employed);
            if (!employed) txtJobDetails.clear();
        });


        chkWhitelist.selectedProperty().addListener((obs, oldV, is) -> { if (is) chkBlacklist.setSelected(false); });
        chkBlacklist.selectedProperty().addListener((obs, oldV, is) -> { if (is) chkWhitelist.setSelected(false); });


        lvLanguages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            var langs = LanguageDatabase.getAllLanguages();
            lvLanguages.setItems(FXCollections.observableArrayList(langs));
        } catch (Exception e) {
            lvLanguages.setItems(FXCollections.observableArrayList());
            e.printStackTrace();
        }


        lvDatabases.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvDatabases.setItems(FXCollections.observableArrayList(
                "MySQL", "Postgres", "MongoDB", "SQLite", "Oracle"
        ));
    }

    @FXML
    private void saveProfile() {
        String fullName = safeTrim(txtFullName.getText());
        if (fullName.isEmpty()) { showAlert("Full Name is required."); return; }

        String academic = cbAcademicStatus.getValue();
        if (academic == null) { showAlert("Academic Status is required."); return; }

        boolean employed = rbEmployed.isSelected();
        String job = employed ? safeTrim(txtJobDetails.getText()) : "";
        if (employed && job.isEmpty()) { showAlert("Job Details are required when employed."); return; }

        List<String> langs = new ArrayList<>(lvLanguages.getSelectionModel().getSelectedItems());
        if (langs.isEmpty()) { showAlert("Select at least one Programming Language."); return; }

        List<String> dbs = new ArrayList<>(lvDatabases.getSelectionModel().getSelectedItems());
        if (dbs.isEmpty()) { showAlert("Select at least one Database."); return; }

        String role = cbPreferredRole.getValue();
        if (role == null) { showAlert("Preferred Professional Role is required."); return; }

        boolean whitelist = chkWhitelist.isSelected();
        boolean blacklist = chkBlacklist.isSelected();


        String comment = safeTrim(txtComments.getText());

        if (StudentDatabase.existsByName(fullName)) {
            showAlert("A student with this name already exists.");
            return;
        }

        boolean ok = StudentDatabase.addStudentProfile(
                fullName, academic, employed, job, langs, dbs, role, whitelist, blacklist, comment
        );

        if (ok) {
            clearForm();
            showInfo("Profile saved successfully.");
        } else {
            showAlert("Could not save profile (duplicate or DB error).");
        }
    }


    private Stage stage;

    @FXML
    void homeButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void showAllProfiles(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("studentProfilesList.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }


    private String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        txtFullName.clear();
        cbAcademicStatus.getSelectionModel().clearSelection();
        rbUnemployed.setSelected(true);
        txtJobDetails.clear();

        lvLanguages.getSelectionModel().clearSelection();
        lvDatabases.getSelectionModel().clearSelection();
        cbPreferredRole.getSelectionModel().clearSelection();

        txtComments.clear();
        chkWhitelist.setSelected(false);
        chkBlacklist.setSelected(false);
    }
}
