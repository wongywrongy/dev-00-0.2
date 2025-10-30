package cs151.application;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class StudentProfilesListController {

    @FXML private TableView<StudentProfile> tblProfiles;
    @FXML private TableColumn<StudentProfile, String> colName;
    @FXML private TableColumn<StudentProfile, String> colAcademic;
    @FXML private TableColumn<StudentProfile, String> colEmployed;
    @FXML private TableColumn<StudentProfile, String> colJob;
    @FXML private TableColumn<StudentProfile, String> colLanguages;
    @FXML private TableColumn<StudentProfile, String> colDatabases;
    @FXML private TableColumn<StudentProfile, String> colRole;
    @FXML private TableColumn<StudentProfile, String> colFlags;
    @FXML private TableColumn<StudentProfile, String> colComments;

    // Search control
    @FXML private TextField txtSearch;
    @FXML private Label lblResultCount;

    private final ObservableList<StudentProfile> master = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colName.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getFullName()));
        colAcademic.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getAcademicStatus()));
        colEmployed.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().isEmployed() ? "Yes" : "No"));
        colJob.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getJobDetails()));
        colLanguages.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getLanguagesCsv()));
        colDatabases.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getDatabasesCsv()));
        colRole.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getPreferredRole()));
        colFlags.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getFlagsText()));
        colComments.setCellValueFactory(d -> {
            String text = d.getValue().getLatestComment();
            if (text == null) text = "";
            return new ReadOnlyStringWrapper(text.length() > 80 ? text.substring(0, 77) + "..." : text);
        });

        tblProfiles.setItems(master);
        refresh();
    }

    @FXML
    private void refresh() {
        master.setAll(StudentDatabase.getAllProfilesSorted());
        lblResultCount.setText("Total: " + master.size());
    }

    @FXML
    private void applyFilters() {
        String searchTerm = txtSearch.getText();
        
        if (searchTerm == null || searchTerm.isEmpty()) {
            refresh();
            return;
        }
        
        String searchLower = searchTerm.toLowerCase();
        List<StudentProfile> allProfiles = StudentDatabase.getAllProfilesSorted();
        List<StudentProfile> filteredList = new ArrayList<>();

        for (StudentProfile profile : allProfiles) {
            boolean matches = false;

            // Search in name
            if (profile.getFullName().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            // Search in academic status
            if (profile.getAcademicStatus().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            // Search in job details
            if (profile.getJobDetails() != null && profile.getJobDetails().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            // Search in programming languages
            for (String lang : profile.getLanguages()) {
                if (lang.toLowerCase().contains(searchLower)) {
                    matches = true;
                    break;
                }
            }

            // Search in databases
            for (String db : profile.getDatabases()) {
                if (db.toLowerCase().contains(searchLower)) {
                    matches = true;
                    break;
                }
            }

            // Search in preferred role
            if (profile.getPreferredRole().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            // Search in flags
            if (profile.getFlagsText().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            if (matches) {
                filteredList.add(profile);
            }
        }

        master.setAll(filteredList);
        lblResultCount.setText("Found: " + filteredList.size());
    }

    @FXML
    private void clearFilters() {
        txtSearch.clear();
        refresh();
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
    void goDefineProfiles(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("studentProfiles.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    protected void editSelectedStudent(ActionEvent event) {
        StudentProfile studentToEdit = tblProfiles.getSelectionModel().getSelectedItem();

        if (studentToEdit == null) {
            System.out.println("select a student to edit please");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("editStudentProfile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
            stage.setScene(scene);
            stage.show();
            
            // Pass the selected student to the edit controller
            EditStudentProfileController editController = fxmlLoader.getController();
            editController.setStudentToEdit(studentToEdit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void deleteSelectedStudent(ActionEvent event) {
        StudentProfile studentToDelete = tblProfiles.getSelectionModel().getSelectedItem();

        if (studentToDelete == null) {
            System.out.println("select a student to delete please");
            return;
        }

        System.out.println("Student Name: " + studentToDelete.getFullName());
        System.out.println("Student ID: " + studentToDelete.getId());

        tblProfiles.getItems().remove(studentToDelete);

        StudentDatabase.deleteStudentInDatabase(studentToDelete.getId());
    }
}
