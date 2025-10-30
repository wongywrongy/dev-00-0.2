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

public class EditStudentProfileController {

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
    @FXML private Label lblStudentName;

    private StudentProfile studentToEdit;
    private Stage stage;

    @FXML
    public void initialize() {
        // Set up academic status options
        cbAcademicStatus.setItems(FXCollections.observableArrayList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));

        // Set up preferred role options
        cbPreferredRole.setItems(FXCollections.observableArrayList(
                "Front-End", "Back-End", "Full-Stack", "Data", "Other"
        ));

        // Set up employment radio button behavior
        rbUnemployed.setSelected(true);
        txtJobDetails.setDisable(true);
        rbEmployed.selectedProperty().addListener((obs, was, employed) -> {
            txtJobDetails.setDisable(!employed);
            if (!employed) txtJobDetails.clear();
        });

        // Set up mutually exclusive whitelist/blacklist checkboxes
        chkWhitelist.selectedProperty().addListener((obs, oldV, is) -> { 
            if (is) chkBlacklist.setSelected(false); 
        });
        chkBlacklist.selectedProperty().addListener((obs, oldV, is) -> { 
            if (is) chkWhitelist.setSelected(false); 
        });

        // Set up programming languages list
        lvLanguages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            var langs = LanguageDatabase.getAllLanguages();
            lvLanguages.setItems(FXCollections.observableArrayList(langs));
        } catch (Exception e) {
            lvLanguages.setItems(FXCollections.observableArrayList());
            e.printStackTrace();
        }

        // Set up databases list
        lvDatabases.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvDatabases.setItems(FXCollections.observableArrayList(
                "MySQL", "Postgres", "MongoDB", "SQLite", "Oracle"
        ));
    }

    public void setStudentToEdit(StudentProfile student) {
        this.studentToEdit = student;
        populateForm();
    }

    private void populateForm() {
        if (studentToEdit == null) return;

        // Update the header label
        lblStudentName.setText("Student: " + studentToEdit.getFullName());

        // Populate basic information
        txtFullName.setText(studentToEdit.getFullName());
        cbAcademicStatus.setValue(studentToEdit.getAcademicStatus());
        
        if (studentToEdit.isEmployed()) {
            rbEmployed.setSelected(true);
            txtJobDetails.setText(studentToEdit.getJobDetails());
        } else {
            rbUnemployed.setSelected(true);
        }

        // Populate skills and interests
        lvLanguages.getSelectionModel().clearSelection();
        for (String lang : studentToEdit.getLanguages()) {
            int index = lvLanguages.getItems().indexOf(lang);
            if (index >= 0) {
                lvLanguages.getSelectionModel().select(index);
            }
        }

        lvDatabases.getSelectionModel().clearSelection();
        for (String db : studentToEdit.getDatabases()) {
            int index = lvDatabases.getItems().indexOf(db);
            if (index >= 0) {
                lvDatabases.getSelectionModel().select(index);
            }
        }

        cbPreferredRole.setValue(studentToEdit.getPreferredRole());

        // Populate faculty evaluation
        txtComments.setText(studentToEdit.getLatestComment());

        // Populate flags
        chkWhitelist.setSelected(studentToEdit.isWhitelist());
        chkBlacklist.setSelected(studentToEdit.isBlacklist());
    }

    @FXML
    private void saveChanges() {
        String fullName = safeTrim(txtFullName.getText());
        String academic = cbAcademicStatus.getValue();
        boolean employed = rbEmployed.isSelected();
        String job = employed ? safeTrim(txtJobDetails.getText()) : "";
        List<String> langs = new ArrayList<>(lvLanguages.getSelectionModel().getSelectedItems());
        List<String> dbs = new ArrayList<>(lvDatabases.getSelectionModel().getSelectedItems());
        String role = cbPreferredRole.getValue();
        boolean whitelist = chkWhitelist.isSelected();
        boolean blacklist = chkBlacklist.isSelected();
        String comment = safeTrim(txtComments.getText());

        boolean success = StudentDatabase.updateStudentProfile(
                studentToEdit.getId(),
                fullName, academic, employed, job, langs, dbs, role, whitelist, blacklist, comment
        );

        if (success) {
            backToList(null);
        }
    }

    @FXML
    private void cancelEdit() {
        backToList(null);
    }

    @FXML
    void backToList(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("studentProfilesList.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage = (Stage) (((Node) (event != null ? event.getSource() : txtFullName)).getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String safeTrim(String s) { 
        return s == null ? "" : s.trim(); 
    }
}
