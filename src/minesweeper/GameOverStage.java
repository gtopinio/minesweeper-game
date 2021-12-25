package minesweeper;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameOverStage {
	private StackPane pane;
	private Scene scene;
	private GraphicsContext gc;
	private Canvas canvas;

	public final Image win = new Image("images/win.png",500,500,false,false);
	public final Image lose = new Image("images/lose.png",500,500,false,false);

	GameOverStage(int num){
		this.pane = new StackPane();
		this.scene = new Scene(pane, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.canvas = new Canvas(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
		this.gc = canvas.getGraphicsContext2D();
		this.setProperties(num);

	}

	private void setProperties(int num){

		this.gc.setFill(Color.BISQUE);										//set font color of text
		this.gc.fillRect(0,0,GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		Font theFont = Font.font("Franklin Gothic Demi",FontWeight.BOLD,30);//set font type, style and size
		Font btnFont = Font.font("Franklin Gothic Demi",FontWeight.NORMAL,20);
		this.gc.setFont(theFont);
		ImageView view = new ImageView();
		switch(num){
		case 0: // Case if the player loses the game, this is the losing theme for the game over window
			view.setImage(lose);
			this.gc.drawImage(this.lose, 130, 300);
			this.gc.setFill(Color.CRIMSON);										//set font color of text
			this.gc.fillText("Game Over! You Lose!", GameStage.WINDOW_WIDTH*0.3, GameStage.WINDOW_HEIGHT*0.3);
			break;
		case 1: // Case if the player wins the game, this is the winning theme for the game over window
			view.setImage(win);
			this.gc.drawImage(this.win, 150, 300);
			this.gc.setFill(Color.LIMEGREEN);										//set font color of text
			this.gc.fillText("Congratulations! You Win!", GameStage.WINDOW_WIDTH*0.3, GameStage.WINDOW_HEIGHT*0.3);
			break;
		}
		Button exitbtn = new Button("Exit Game");
		exitbtn.setFont(btnFont);
		this.addEventHandler(exitbtn);
		pane.getChildren().add(view);
		pane.getChildren().add(this.canvas);
		pane.getChildren().add(exitbtn);
	}

	private void addEventHandler(Button btn) {
		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent arg0) {
				System.exit(0);
			}
		});

	}

	Scene getScene(){
		return this.scene;
	}
}
