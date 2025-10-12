package cs151.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private Stage stage;

    /**
     * Switches the page to the programming languages screen.
     * @param event The information of the action that took place.
     * @throws IOException
     */
    @FXML
    protected void defineProgrammingLanguagesClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("programmingLanguages.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }


}