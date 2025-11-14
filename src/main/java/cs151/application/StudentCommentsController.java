package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class StudentCommentsController {

    @FXML
    private Label lblStudentName;

    @FXML
    private ListView<String> lstComments;

    @FXML
    private TextArea txtNewComment;

    @FXML
    private Label lblStatus;

    private StudentProfile student;
    private Stage stage;

    public void setStudent(StudentProfile student) {
        this.student = student;
        if (lblStudentName != null) {
            lblStudentName.setText(student.getFullName());
        }
        loadComments();
    }

    private void loadComments() {
        if (student == null || lstComments == null) {
            return;
        }

        List<String> comments = StudentDatabase.getAllCommentsForStudent(student.getId());
        lstComments.getItems().setAll(comments);

        if (comments.isEmpty()) {
            lblStatus.setText("No comments yet.");
        } else {
            lblStatus.setText("");
        }
    }

    @FXML
    protected void addComment(ActionEvent event) {
        if (student == null) {
            return;
        }

        String text = txtNewComment.getText();
        if (text == null || text.isBlank()) {
            lblStatus.setText("Please enter a comment first.");
            return;
        }

        boolean ok = StudentDatabase.addNewCommentForStudent(student.getId(), text);
        if (ok) {
            txtNewComment.clear();
            lblStatus.setText("Comment added.");
            loadComments();
        } else {
            lblStatus.setText("Could not save comment.");
        }
    }

    @FXML
    void backToProfilesList(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("studentProfilesList.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }
}