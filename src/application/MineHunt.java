package application;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MineHunt extends Application {

	private static final String TITLE = "application/resources/minehunt.png";

	private MineHuntModel model;
	private MineHuntController ctrl;

	private ImageView mine;
	private ImageView flag;

	private VBox root = new VBox(20);
	private ImageView title = new ImageView(TITLE);
	private HBox infoGame = new HBox(20);
	private Label clickNb = new Label("Nb clicks:");
	private Label clickCount = new Label("");
	private Label errorNb = new Label("Nb errors:");
	private Label errorCount = new Label("");
	// Button Table List:
	private GridPane grid = new GridPane();
	private CellButton[][] mineGrid;

	private HBox gameButton = new HBox(10);
	private Button showMines = new Button("Show Mines");
	private Button newGame = new Button("New Game");

	private int compteur = 0;

	@Override
	public void init() throws Exception {
		super.init();

		// --- Title
		root.setAlignment(Pos.CENTER);
		root.getChildren().add(title);
		root.setStyle("-fx-background-color: #faf5c8");
		root.setPrefSize(500, 500);

		clickCount.setStyle("-fx-background-color: #33CC33");
		clickCount.setMinSize(60, 20);
		clickCount.setAlignment(Pos.CENTER);

		errorCount.setStyle("-fx-background-color: #FF3333");
		errorCount.setMinSize(60, 20);
		errorCount.setAlignment(Pos.CENTER);
		// --- Add Label Info Game:
		infoGame.getChildren().addAll(clickNb, clickCount, errorNb, errorCount);
		infoGame.setAlignment(Pos.CENTER);
		// --- Info Game
		root.getChildren().add(infoGame);

		// --- Add Buton Game
		gameButton.getChildren().add(showMines);

		gameButton.getChildren().add(newGame);
		gameButton.setAlignment(Pos.CENTER);

		// --- Game Button
		root.getChildren().add(gameButton);

		// --- Button Table List:
		root.getChildren().add(grid);
		grid.setHgap(CONSTANTE.NBR_COL);
		grid.setVgap(CONSTANTE.NBR_ROW);
		grid.setAlignment(Pos.CENTER);

	}

	@Override
	public void start(Stage mainStage) throws Exception {

		model = new MineHuntModel();
		ctrl = new MineHuntController(this, model);
		model.setCtrl(ctrl);

		mainStage.setTitle("MineHunt V1.0");
		Scene scene = new Scene(root);

		initNewGame(CONSTANTE.NBR_ROW, CONSTANTE.NBR_COL, model, false);

		mainStage.setScene(scene);
		mainStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void setImageButton(CellButton cellB, int i, int nb) {
		// Error, mine
		if (i == 0) {
			mine = new ImageView(CONSTANTE.MINE);
			mine.setFitWidth(20);
			mine.setFitHeight(20);
			cellB.setStyle("-fx-background-color: #FF3333");
			cellB.setGraphic(mine);
		}
		// flag
		else if (i == 1) {
			flag = new ImageView(CONSTANTE.FLAG);
			flag.setFitWidth(20);
			flag.setFitHeight(20);
			cellB.setGraphic(flag);
		}
		// remove flag

		else if (i == 2) {
			cellB.setGraphic(null);
		}
		// white
		else if (i == 3) {
			cellB.setGraphic(null);
			cellB.setText("" + nb);
			cellB.setStyle("-fx-background-color: #fff27f");
		}
		// hint
		else if (i == 4) {
			mine = new ImageView(CONSTANTE.MINE);
			mine.setFitWidth(20);
			mine.setFitHeight(20);
			cellB.setGraphic(mine);
		}
		// show last mine
		else if (i == 5) {
			mine = new ImageView(CONSTANTE.MINE);
			mine.setFitWidth(20);
			mine.setFitHeight(20);
			cellB.setGraphic(mine);
			cellB.setStyle("-fx-background-color: #33CC33");
		}

	}

	public void endGame(int errors) {
		Alert dialog = new Alert(AlertType.INFORMATION);
		dialog.setTitle("MineHunt - GameOver");
		dialog.setHeaderText("MineHunt");
		if (errors == 0) {
			dialog.setAlertType(AlertType.INFORMATION);
			dialog.setContentText("Congratulation !\n" + "Current game endes successfully (no error)");
			dialog.showAndWait();
		} else if (errors == CONSTANTE.NBR_MINES) {
			dialog.setAlertType(AlertType.WARNING);
			dialog.setContentText("FAIL !\n" + "Current game ended with ALL mine pressed");
			dialog.showAndWait();
		} else {
			dialog.setAlertType(AlertType.WARNING);
			dialog.setContentText("Current game ended with " + errors + " errors");
			dialog.showAndWait();
		}
	}

	public void initNewGame(int row, int col, MineHuntModel model, boolean restart) {
		// Bind Label info
		clickCount.textProperty().bind(model.getNbClick().asString());
		errorCount.textProperty().bind(model.getNbError().asString());

		if (mineGrid != null) {
			mineGrid = null;
			root.getChildren().remove(grid);
			grid = new GridPane();
			root.getChildren().add(grid);
			grid.setHgap(col);
			grid.setVgap(row);
			grid.setAlignment(Pos.CENTER);
		}
		mineGrid = new CellButton[row][col];
		// Add grid and event to CellButton
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				mineGrid[i][j] = null;
				CellButton cellB = new CellButton(i, j);
				if (mineGrid != null)
					mineGrid[i][j] = cellB;
				cellB.setMinSize(40, 30);
				cellB.setOnMouseClicked(event -> {
					if (event.getButton() == MouseButton.SECONDARY)
						ctrl.boutonClicked(cellB, true);
					else
						ctrl.boutonClicked(cellB, false);
				});
				grid.add(cellB, i, j);
			}
		}
		// Event for button showMine
		showMines.setOnAction(event -> {
			ctrl.showMine(mineGrid, compteur % 2);
			compteur++;
		});

		// Event for button newGame
		newGame.setOnAction(event -> {

			VBox window2 = new VBox();
			Stage stage = new Stage();
			stage.setTitle("Game Setting");
			stage.setScene(new Scene(window2, 450, 450));

			GridPane gameGrid = new GridPane();
			gameGrid.setHgap(20);
			gameGrid.setVgap(20);
			gameGrid.setAlignment(Pos.CENTER);
			// Create composant for the windows Game Setting:
			Label numberRow = new Label("Number row: ");
			Label numbreCol = new Label("Number col: ");
			Label numberMine = new Label("Number mine: ");
			TextField textRow = new TextField();
			TextField textCol = new TextField();
			TextField textMine = new TextField();
			HBox buttonBox = new HBox();
			Button confirmation = new Button("Ok");
			Button cancel = new Button("Cancel");
			// Add element in the grid
			gameGrid.add(numberRow, 0, 0);
			gameGrid.add(numbreCol, 0, 1);
			gameGrid.add(numberMine, 0, 2);
			gameGrid.add(textRow, 1, 0);
			gameGrid.add(textCol, 1, 1);
			gameGrid.add(textMine, 1, 2);

			buttonBox.getChildren().add(confirmation);
			buttonBox.getChildren().add(cancel);
			buttonBox.setAlignment(Pos.CENTER);

			window2.getChildren().add(gameGrid);
			window2.getChildren().add(buttonBox);
			window2.setAlignment(Pos.CENTER);
			stage.show();

			confirmation.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						int row = Integer.valueOf(textRow.getText());
						int col = Integer.valueOf(textCol.getText());
						int mine = Integer.valueOf(textMine.getText());
						if (mine < row * col) {
							ctrl.newGame(row, col, mine);
							stage.close();
						} else
							throw new NumberFormatException();
					} catch (NumberFormatException e) {
						Alert dialog = new Alert(AlertType.WARNING);
						dialog.setTitle("MineHunt - Wrong parameter");
						dialog.setHeaderText("MineHunt");
						dialog.setContentText("Caution !\n" + "Enter a correct number");
						dialog.showAndWait();
					}
				}
			});

			cancel.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					stage.close();
				}
			});

		});
	}

}
