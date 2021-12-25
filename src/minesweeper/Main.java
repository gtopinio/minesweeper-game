/***********************************************************
* This is a Java program that simulates a Modified Minesweeper Game using a 9 x 9 grid.
* Exercise on GUI Part 1
*
* @author Mark Genesis C. Topinio
* @created_date 2021-10-29 18:45
*
***********************************************************/
package minesweeper;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage){
		GameStage theGameStage = new GameStage();
		theGameStage.setStage(stage);
	}

}
