package cs151.application;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProgrammingLanguagesController {

    @FXML private TextField txtLanguage;                 
    @FXML private TableView<String> tblLanguages;
    @FXML private TableColumn<String, String> colName;

    // When the programming page start, set up the table and load current data
    @FXML
    public void initialize() {
        colName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue()));
        refreshTable();
    }

    // When enter or save clicked: add new names, skip duplicates, sort A->Z,
    // clear the text field box and refresh tableview
    public void saveLanguages(ActionEvent event) {
        List<String> names = parseNames(txtLanguage.getText());
        if (!names.isEmpty()) {
            for (String n : names) {
                LanguageDatabase.addLanguage(n);
            }
            txtLanguage.clear();
            refreshTable();
        }
    }

    // Rebuild the table from current list
    private void refreshTable() {
        ObservableList<String> data = FXCollections.observableArrayList(LanguageDatabase.getAllLanguages());
        tblLanguages.setItems(data);
    }

    // Turn the text field into correct names by split the comma
    // trim spaces, and ignore empty spaces
    // @param the raw text from TextField
    // @return list of cleaned language names
    private static List<String> parseNames(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null) 
            return out;
        String[] tokens = raw.split(",");
        for (String t : tokens) {
            String s = t.trim().replaceAll("\\s+", " ");
            if (!s.isEmpty()) 
                out.add(s);
        }
        return out;
    }

    private Stage stage;

    /**
     * Switches the page to the home screen.
     * @param event The information of the action that took place.
     * @throws IOException
     */
    @FXML
    void homeButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }

}

