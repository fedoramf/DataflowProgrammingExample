package ca.cmpt373.ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import ca.cmpt373.model.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class for displaying a pop up to help user create their own module (source or filter).
 *  
 * @author Fedora Furtado
 *
 */

public class ModuleCreator {
	
	private static final int PANE_LENGTH = 500;
	private static final int PANE_WIDTH = 1000;
	private static final int PANE_OFFSET = 50;
	private static final String NAME_SAMPLE = "My Module";
	private GridPane mainPane = new GridPane();
	private Scene scene = new Scene(mainPane, PANE_WIDTH, PANE_LENGTH);
	private Stage stage = new Stage();
	private Label sourceCodeLabel;
	private Label classNameLabel;
	private Label filterCreatorLabel;
	private Label classNameWarningLabel;
	private TextArea sourceCode = new TextArea();
	private TextField className;
	private Button cancel;
	private Button createFilter;
	private HBox hBox = new HBox();
	private final String exampleFilter = "exampleFilter.txt";
	private final String exampleSource = "exampleSource.txt";
	private ComboBox<String> comboBox;
	
	public ModuleCreator(){
		setupPane();
		setupStage();
		setButtonEventHandlers();
		readExampleFilterFile(exampleFilter);
	}

	private void setupStage() {
		stage = new Stage();
		stage.setTitle("Module Creator");
	    stage.setScene(scene);
	    stage.initModality(Modality.WINDOW_MODAL);
	}
	
	private void setupPane() {
		initPaneObjects();
		mainPane.setStyle("-fx-background-color:whitesmoke; -fx-padding: 10;");
		mainPane.add(filterCreatorLabel,3,5);
		mainPane.add(classNameLabel,3,6);
		mainPane.add(className,3,7);
		mainPane.add(classNameWarningLabel,3,6);
		mainPane.add(comboBox,3,9);
		mainPane.add(sourceCodeLabel,3,10);
        mainPane.add(sourceCode,3,11);
        hBox.getChildren().addAll(createFilter, cancel);
        mainPane.add(hBox,3,12);
        mainPane.autosize();
	}
	
	private void initPaneObjects() {
		className = new TextField(NAME_SAMPLE);
		className.setPromptText(NAME_SAMPLE);
		sourceCode.setPrefSize(PANE_WIDTH-PANE_OFFSET, PANE_LENGTH-PANE_OFFSET);
		cancel = new Button("Cancel");
		createFilter = new Button("Create Module");
		hBox.setSpacing(10.0);
		hBox.setStyle("-fx-padding: 10 0 0 0;");
		initLabels();
		initComboBox();
	}

	private void initComboBox() {
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "Source",
			        "Filter"
			    );
		comboBox = new ComboBox<String>(options);
		comboBox.setPromptText("Select Type");
		comboBox.setOnAction((event) -> {
		    if(comboBox.getSelectionModel().getSelectedItem() == "Source"){
		    	readExampleFilterFile(exampleSource);
		    }else if(comboBox.getSelectionModel().getSelectedItem() == "Filter"){
		    	readExampleFilterFile(exampleFilter);
		    }
		    
		});
	}

	private void initLabels() {
		filterCreatorLabel = new Label("Module Creator");
		filterCreatorLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 22; -fx-font-weight: bold;");
		sourceCodeLabel = new Label("Source Code:");
		sourceCodeLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 18;");
		classNameLabel = new Label("Class Name:");
		classNameLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 18;");
		classNameWarningLabel = new Label("Warning: Missing class name, source code or type selection.");
		classNameWarningLabel.setStyle("-fx-padding: 10 0 0 100; -fx-font-size: 14; -fx-font-weight: bold;");
		classNameWarningLabel.setTextFill(Color.web("#ff0000"));
		classNameWarningLabel.setVisible(false);
	}
	
	public void showEditor(){
		stage.show();
	}
	
	private void closeEditor(){
		readExampleFilterFile(exampleFilter);
		stage.close();
		classNameWarningLabel.setVisible(false);
	}
	
	
	private void readExampleFilterFile(String exampleType){
		List<String> srcCode = null;
		try {
			srcCode = Files.readAllLines(new File(exampleType).toPath(), Charset.defaultCharset() );

		} catch (IOException exception) {
			exception.printStackTrace();
		}
		setSourceCode(srcCode);
	}
	
	
	private void setSourceCode(List<String> srcCode){
		String code = "";
		for(String string : srcCode){
			code += string;
			code += "\n";
		}
		sourceCode.setText(code);
	}
	
	private void insertClassName(){
		String code = sourceCode.getText();
		code = removeAllWhiteSpace(code);
		code = code.replaceAll("Class Name", className.getText());
		sourceCode.setText(code);
	}
	
	private void insertClassType(){
		String code = sourceCode.getText();
		String selected = comboBox.getValue().toString().toUpperCase();
		code = code.replaceAll("FILTER", selected);
		sourceCode.setText(code);
	}
	

	private String removeAllWhiteSpace(String code) {
		String name = className.getText();
		name = name.replaceAll("\\s","");
		code = code.replaceAll("ClassName", name);
		return code;
	}
	
	private boolean textFieldsEmpty(){
		if(className.getText().equals("") || sourceCode.getText().equals("")){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean typeNotChosen(){
		if(comboBox.getSelectionModel().getSelectedItem() == null){
			return true;
		}else{
			return false;
		}
	}
	
	private String getSourceCode(){
		insertClassName();
		insertClassType();
		return sourceCode.getText();
	}
	
	private void setButtonEventHandlers() {
		cancel.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				closeEditor();
			}
		});
		
		createFilter.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if(textFieldsEmpty() || typeNotChosen()){
					classNameWarningLabel.setVisible(true);
				}else{
					loadFilterToProgram();
					closeEditor();
				}
			}

			private void loadFilterToProgram() {
				try {
					DataManager.loadSingleUsermadeFilter(getSourceCode());
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});
	}
}