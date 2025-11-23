package cs151.application;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ShowReportsListController {
    public TableView<StudentProfile> tblProfiles;
    public TableColumn<StudentProfile, String> colName;
    public TableColumn<StudentProfile, String> colAcademic;
    public TableColumn<StudentProfile, String> colEmployed;
    public TableColumn<StudentProfile, String> colJob;
    public TableColumn<StudentProfile, String> colLanguages;
    public TableColumn<StudentProfile, String> colDatabases;
    public TableColumn<StudentProfile, String> colRole;
    public TableColumn<StudentProfile, String> colComments;
    public RadioButton showWhitelistedRadioButton;
    public RadioButton showBlacklistedRadioButton;
    private Stage stage;
    private ObservableList<StudentProfile> master = FXCollections.observableArrayList();

    public void goToHome(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getFullName()));
        colAcademic.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getAcademicStatus()));
        colEmployed.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().isEmployed() ? "Yes" : "No"));
        colJob.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getJobDetails()));
        colLanguages.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getLanguagesCsv()));
        colDatabases.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getDatabasesCsv()));
        colRole.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getPreferredRole()));
        colComments.setCellValueFactory(d -> {
            String text = d.getValue().getLatestComment();
            if (text == null) text = "";
            return new ReadOnlyStringWrapper(text.length() > 80 ? text.substring(0, 77) + "..." : text);
        });

        tblProfiles.setItems(master);
    }

    public void showWhitelisted(ActionEvent actionEvent) {
        master.setAll(StudentDatabase.getWhitelistedStudents());
    }

    public void showBlacklisted(ActionEvent actionEvent) {
        master.setAll(StudentDatabase.getBlacklistedStudents());
    }

    public void clickedTable(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() != 2 || !mouseEvent.getButton().equals(MouseButton.PRIMARY)) return;

//        System.out.println("Student Profile: " + tblProfiles.getSelectionModel().getSelectedItem().getFullName());

        StudentProfile studentToShow = tblProfiles.getSelectionModel().getSelectedItem();

        goToStudentProfilePage(mouseEvent, studentToShow);
    }
    public void goToStudentProfilePage(MouseEvent event, StudentProfile student) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("showStudentReport.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        ShowStudentReportController controller = fxmlLoader.getController();
        controller.updateStudentReportPage(student);
    }
}