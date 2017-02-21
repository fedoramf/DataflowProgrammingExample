package ca.cmpt373.ui;


import java.net.URL;
import java.util.ResourceBundle;

import ca.cmpt373.model.DataManager;
import ca.cmpt373.ui.directorypanel.DirectoryPanel;
import ca.cmpt373.ui.displaypanel.DisplayPanel;
import ca.cmpt373.ui.workspacepanel.WorkspacePanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainUI implements Initializable {
	@FXML private AnchorPane mainAnchorPane;
	private MenuBar menuBar = new MenuBar();
	private DataManager dataManager = new DataManager();
	private PriorityManager priorityManager = new PriorityManager();
	private WorkspacePanel workspacePanel;		
	private DirectoryPanel directoryPanel;
	private DisplayPanel displayPanel;
	private FilterManager uiFilterManager;
	private BorderPane borderPane;
	private ModuleCreator moduleCreator;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		createPanels();
		initMenu();
	    initBorderPane();
	    initAnchorPane();
	}

	private void initMenu() {
		final Menu file = new Menu("File");
	    final Menu options = new Menu("Options");
		final Menu help = new Menu("Help");
		
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent actionEvent) {
		        System.exit(0);
		    }
		});
		
		MenuItem newFilter = new MenuItem("New Module");
		moduleCreator = new ModuleCreator();
		newFilter.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent actionEvent) {
		        moduleCreator.showEditor();
		    }
		});
		
		file.getItems().addAll(exit);
		options.getItems().addAll(newFilter);
		menuBar.getMenus().addAll(file, options, help);
	}

	private void createPanels() {
		workspacePanel = new WorkspacePanel(dataManager);
		uiFilterManager = new FilterManager(workspacePanel, dataManager, priorityManager);
		directoryPanel = new DirectoryPanel(workspacePanel, dataManager, uiFilterManager);
		displayPanel = new DisplayPanel(dataManager, uiFilterManager);
	}

	private void initAnchorPane() {
		AnchorPane.setTopAnchor(borderPane, 0.0);
	    AnchorPane.setBottomAnchor(borderPane, 0.0);
	    AnchorPane.setLeftAnchor(borderPane, 0.0);
	    AnchorPane.setRightAnchor(borderPane, 0.0);
		
	    mainAnchorPane.getChildren().add(borderPane);
	}

	private void initBorderPane() {
		borderPane = new BorderPane();
	    borderPane.setTop(menuBar);
	    borderPane.setCenter(workspacePanel);
	    borderPane.setLeft(directoryPanel);
	    borderPane.setRight(displayPanel);
	}
}
