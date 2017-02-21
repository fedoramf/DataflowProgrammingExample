package ca.cmpt373.ui;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.cmpt373.model.DataManager;
import ca.cmpt373.model.FilterImp;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Class for displaying a pop up to help user edit modules.
 *  
 * @author Fedora Furtado
 *
 */

public class ModuleEditor {

	private static final int PANE_LENGTH = 500;
	private static final int PANE_WIDTH = 1000;
	private static final int PANE_OFFSET = 50;
	private GridPane mainPane = new GridPane();
	private Scene scene = new Scene(mainPane, PANE_WIDTH, PANE_LENGTH);
	private Stage stage;
	private Label instanceNameLabel;
	private Label sourceCodeLabel;
	private Label moduleEditorLabel;
	private Label warningLabel;
	private TextField instanceName;
	private TextArea sourceCode;
	private Button cancel;
	private Button saveChanges;
	private HBox hBox;
	private UIFilter filter;
	private String uiFilterName;
	private boolean instanceNameEdited;
	private boolean sourceCodeEdited;
	private Label noteLabel;
	private String noteText = "Note: Any modifications done to the source code will result in a new filter being created.";
	private final String PACKAGE_COMMENT = " // THIS LINE HAS BEEN FORCED TO THIS PACKAGE BY THE MODULE EDITOR (so that all edited files are saved in the usermade folder)";
	private final String CONSTRUCTOR_COMMENT = " // THIS LINE HAS BEEN FORCED TO HAVE THE FINAL ARGUMENT AS 'FALSE' BY THE MODULE EDITOR (to signifiy that it usermade)";

	
	public ModuleEditor(UIFilter filter){
		setupPane();
		setupStage();
		setButtonEventHandlers();
		populateFields(filter);
		this.filter = filter;
		instanceNameEdited = false;
		sourceCodeEdited = false;
	}

	private void populateFields(UIFilter filter) {
		uiFilterName = filter.getFilterInstanceName();
		instanceName.setText(uiFilterName);
		List<String> srcCode;
		srcCode = filter.getSourceCode();
		setSourceCode(srcCode);
	}

	private void setupStage() {
		stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("Module Editor");
        stage.setScene(scene);
	}

	private void setupPane() {
		initPaneObjects();
		mainPane.setStyle("-fx-background-color:whitesmoke; -fx-padding: 10;");
		mainPane.add(moduleEditorLabel,3,1);
		mainPane.add(instanceNameLabel,3,2);
		mainPane.add(warningLabel,3,2);
		mainPane.add(instanceName,3,5);
		mainPane.add(noteLabel,3,6);
        mainPane.add(sourceCodeLabel,3,7);
        mainPane.add(sourceCode,3,8);
        hBox.getChildren().addAll(saveChanges, cancel);
        mainPane.add(hBox,3,9);
	}

	private void initPaneObjects() {
		stage = new Stage();
		sourceCode = new TextArea();
		sourceCode.setPrefSize(PANE_WIDTH-PANE_OFFSET, PANE_LENGTH-PANE_OFFSET);
		instanceName = new TextField();
		cancel = new Button("Cancel");
		saveChanges = new Button("Save Changes");
		hBox = new HBox();
		hBox.setSpacing(10.0);
		hBox.setStyle("-fx-padding: 10 0 0 0;");
		initLabels();
	}

	private void initLabels() {
		moduleEditorLabel = new Label("Module Editor");
		moduleEditorLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 22; -fx-font-weight: bold;");
		sourceCodeLabel = new Label("Source Code:");
		sourceCodeLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 18;");
		warningLabel = new Label("Warning: Missing instance name and/or source code.");
		warningLabel.setStyle("-fx-padding: 10 0 0 150; -fx-font-size: 14; -fx-font-weight: bold;");
		warningLabel.setTextFill(Color.web("#ff0000"));
		warningLabel.setVisible(false);
		instanceNameLabel = new Label("Instance Name:");
		instanceNameLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 18;");
		noteLabel = new Label(noteText);
		noteLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 14;");
	}

	public void showEditor(){
		stage.show();
	}
	
	private void restoreEditor(){
		instanceName.setText(filter.getFilterInstanceName());
		populateFields(filter);
	}
	
	private boolean textFieldsEmpty(){
		if(instanceName.getText().equals("") || sourceCode.getText().equals("")){
			return true;
		}else{
			return false;
		}
	}
	
	private void setButtonEventHandlers() {
		cancel.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				closeEditor();
			}

			private void closeEditor() {
				warningLabel.setVisible(false);
				stage.close();
				restoreEditor();
			}
		});
		
		saveChanges.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				//save changes events here
				if(textFieldsEmpty()){
					warningLabel.setVisible(true);
				}else{

					if(sourceCodeEdited && instanceNameEdited) {
						loadFilterToProgram();
						filter.drawSelected(true);
						stage.close();
					}


					//update filter CanvasNode parent name
					for(Node child:filter.filterPane.getChildren()){
						if(child instanceof CanvasNode){
							CanvasNode node = (CanvasNode) child;
							node.setCanvasParentName(instanceName.getText());
						}
					}			
				}


			}

			private void loadFilterToProgram() {
				try {
					FilterImp editedFilter = DataManager.loadSingleUsermadeFilter(getSourceCode());

					String filterName = editedFilter.getName() ;
					int filterInputs = DataManager.getNumberOfFilterInputs(filterName);
					int filterOutputs = DataManager.getNumberOfFilterOutputs(filterName);
	        	
					DataManager.addEditedFilterInstance(filterName, instanceName.getText());
					FilterManager.placeItemInWorkspace(filterName, instanceName.getText(), filterInputs, filterOutputs);
					
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		instanceName.textProperty().addListener((observable, oldValue, newValue) -> {
			instanceNameEdited = true;
			
		});
		
		sourceCode.textProperty().addListener((observable, oldValue, newValue) -> {
			sourceCodeEdited = true;
			
		});
		
	}
	
	
	private void setSourceCode(List<String> srcCode){
		Pattern constructorLinePattern = Pattern.compile("(.*true.*)");	
		Pattern packageLinePattern = Pattern.compile("(.*builtin;.*)");	
		boolean constructorLineFound = false;
		boolean packageLineFound = false;
		String txt = "";
		for(String s : srcCode){
			Matcher constructorLineMatcher = constructorLinePattern.matcher(s);
			Matcher packageLineMatcher = packageLinePattern.matcher(s);
			if (constructorLineMatcher.find() && !constructorLineFound) {
				String constructorLine = constructorLineMatcher.group(1);
				String editorReadyConstructorLine = constructorLine.replaceAll("true","false");
				txt += editorReadyConstructorLine + CONSTRUCTOR_COMMENT ;
				constructorLineFound = true;
			} else if (packageLineMatcher.find() && !packageLineFound){
				String packageLine = packageLineMatcher.group(1);
				String editorReadyPackageLine = packageLine.replaceAll("builtin","usermade");
				txt += editorReadyPackageLine + PACKAGE_COMMENT;
				packageLineFound = true;
			} else{
				txt += s;
			}
			txt += "\n";
		}
		sourceCode.setText(txt);
	}
	
	private String getSourceCode(){
		return sourceCode.getText();
	}
	
}
