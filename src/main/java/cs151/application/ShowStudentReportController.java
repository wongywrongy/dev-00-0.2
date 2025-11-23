package cs151.application;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ShowStudentReportController {
    public ListView lvLanguages;
    public ListView lvDatabases;
    public ComboBox cbPreferredRole;
    private Stage stage;
    private StudentProfile currentStudent;

    public RadioButton rbEmployed;
    public RadioButton rbUnemployed;
    public TextField txtJobDetails;
    @FXML
    private TextField txtFullName;
    @FXML
    private ComboBox cbAcademicStatus;
    @FXML
    private TableView<Comment> tblComments;
    @FXML
    private TableColumn<Comment, String> colDate;
    @FXML
    private TableColumn<Comment, String> colComment;

    private ObservableList<Comment> comments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        colDate.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getDate()));
        colComment.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getExcerpt()));
        
        tblComments.setItems(comments);
    }

    public void updateStudentReportPage(StudentProfile studentProfile) {
        this.currentStudent = studentProfile;
        
        txtFullName.setText(studentProfile.getFullName());
        cbAcademicStatus.setValue(studentProfile.getAcademicStatus());
        if (studentProfile.isEmployed()) rbEmployed.setSelected(true);
        else rbUnemployed.setSelected(true);
        txtJobDetails.setText(studentProfile.getJobDetails());
        lvLanguages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvDatabases.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        lvLanguages.setItems(FXCollections.observableArrayList(LanguageDatabase.getAllLanguages()));
        lvDatabases.setItems(FXCollections.observableArrayList(
                "MySQL", "Postgres", "MongoDB", "SQLite", "Oracle"
        ));

        for (String lang : studentProfile.getLanguages()) {
            lvLanguages.getSelectionModel().select(lang);
        }

        for (String database : studentProfile.getDatabases()) {
            lvDatabases.getSelectionModel().select(database);
        }

        cbPreferredRole.setValue(studentProfile.getPreferredRole());
        
        // Load comments
        List<Comment> studentComments = StudentDatabase.getAllCommentsForStudentAsObjects(studentProfile.getId());
        comments.setAll(studentComments);
    }

    @FXML
    public void clickedCommentTable(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() != 1 || !mouseEvent.getButton().equals(MouseButton.PRIMARY)) return;
        
        Comment selectedComment = tblComments.getSelectionModel().getSelectedItem();
        if (selectedComment == null) return;
        
        // Show popup with full comment
        showFullCommentPopup(selectedComment);
    }

    private void showFullCommentPopup(Comment comment) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Full Comment - " + comment.getDate());
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        
        Label lblDate = new Label("Date: " + comment.getDate());
        lblDate.setStyle("-fx-font-weight: bold;");
        
        Label lblCommentHeader = new Label("Comment:");
        lblCommentHeader.setStyle("-fx-font-weight: bold;");
        
        TextArea txtComment = new TextArea(comment.getContent());
        txtComment.setWrapText(true);
        txtComment.setEditable(false);
        txtComment.setPrefRowCount(10);
        txtComment.setPrefWidth(500);
        
        Button btnClose = new Button("Close");
        btnClose.setOnAction(e -> popup.close());
        
        vbox.getChildren().addAll(lblDate, lblCommentHeader, txtComment, btnClose);
        
        Scene scene = new Scene(vbox);
        popup.setScene(scene);
        popup.show();
    }

    public void goToHome(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
