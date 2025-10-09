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
import java.util.Comparator;
import java.util.List;

public class ProgrammingLanguagesController {

    @FXML private TextField txtLanguage;                 
    @FXML private TableView<String> tblLanguages;
    @FXML private TableColumn<String, String> colName;

    private static final List<String> languages = new ArrayList<>();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue()));
        refreshTable();
    }

    public void saveLanguages(ActionEvent event) {
        List<String> names = parseNames(txtLanguage.getText());
        if (!names.isEmpty()) {
            for (String n : names) {
                boolean exists = languages.stream().anyMatch(s -> s.equalsIgnoreCase(n));
                if (!exists) languages.add(n);
            }
            languages.sort(Comparator.comparing(String::toLowerCase));
            txtLanguage.clear();
            refreshTable();
        }
    }

    private void refreshTable() {
        ObservableList<String> data = FXCollections.observableArrayList(languages);
        tblLanguages.setItems(data);
    }

    
    private static List<String> parseNames(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null) return out;
        String[] tokens = raw.split(",");
        for (String t : tokens) {
            String s = t.trim().replaceAll("\\s+", " ");
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private Stage stage;

    @FXML
    void homeButtonClicked(ActionEvent event) throws IOException {
        //System.out.println("");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("homepage.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }

}

