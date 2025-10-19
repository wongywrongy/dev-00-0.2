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
}
