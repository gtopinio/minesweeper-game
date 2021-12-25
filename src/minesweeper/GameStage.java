package minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameStage {
	private Scene scene;
	private Stage stage;
	/*Group layout/container is a component which applies no special
	layout to its children. All child components (nodes) are positioned at 0,0*/
	private Group root;
	private Canvas canvas;
	private GraphicsContext gc;
	private Element flag; // placeholder for the element of flag, customized during runtime
	private Element flagOff; // placeholder for the element of flagOff, which is used to toggle off the flag mechanic
	private GridPane map;
	private int[][] gameBoard;
	private ArrayList<Element> landCells;


	private boolean flagClicked=false;
	public static int flagCells; // the variable for the number of valid flag cells on bomb cells
	public static ArrayList<Integer> bombCells; // holds the randomized cell areas for the bombs
	public static ArrayList<Integer> safeCells = new ArrayList<Integer>(); // holds the cell areas that have no bombs
	public final static int MAX_CELLS = 81;
	public final static int MAP_NUM_ROWS = 9;
	public final static int MAP_NUM_COLS = 9;
	public final static int MAP_WIDTH = 700;
	public final static int MAP_HEIGHT = 700;
	public final static int CELL_WIDTH = 80;
	public final static int CELL_HEIGHT = 80;
	public final static int FLAG_WIDTH = 90;
	public final static int FLAG_HEIGHT = 90;
	public final static boolean IS_GAME_DONE = false;
	public final static int WINDOW_WIDTH = 777;
	public final static int WINDOW_HEIGHT = 880;
	public final static int MAX_BOMBS = 20;
	public final static int MAX_SAFE_CELLS = 61;

	public final Image bg = new Image("images/background.jpg",800,875,false,false);

	public GameStage() {
		this.root = new Group();
		this.scene = new Scene(root, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT,Color.ANTIQUEWHITE);
		this.canvas = new Canvas(GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.gc = canvas.getGraphicsContext2D();

		Font theFont = Font.font("Bahnschrift",FontWeight.BOLD,25);
		this.gc.setFont(theFont);
		this.gc.setFill(Color.BLACK);
		this.gc.fillText("Click Calm Zhongli to toggle Flag ON!", 300, 35);
		this.gc.fillText("Click Angry Zhongli to toggle Flag OFF!", 300, 75);

		this.flag = new Element(Element.FLAG_TYPE,this);
		this.flagOff = new Element(Element.FLAG_OFF_TYPE,this);
		this.map = new GridPane(); // Main layout for the game
		this.landCells = new ArrayList<Element>(); //List to hold the pattern of the Grid Pane
		this.gameBoard = new int[GameStage.MAP_NUM_ROWS][GameStage.MAP_NUM_COLS];
	}

	//method to add the stage elements
	public void setStage(Stage stage) {
		this.stage = stage;
		//draw the background to the canvas at location x=0, y=90
		this.gc.drawImage( this.bg, 0, 90); // background of the GridPane

		this.initGameBoard();	//initialize game board with 1s and 0s
		this.createMap();

		//set stage elements here
		this.root.getChildren().add(canvas);
		this.root.getChildren().add(map);
		this.root.getChildren().add(this.flagOff.getImageView());
		this.root.getChildren().add(this.flag.getImageView());
		this.stage.setTitle("Modified Minesweeper Game: Genshin Impact Edition");
		this.stage.setScene(this.scene);
		this.stage.show();
	}

	// method to randomize the bomb cell locations on the game board. It returns an array of 20 valid random numbers for the bombs.
	private ArrayList<Integer> randomizeCells(){
		ArrayList<Integer> num = new ArrayList<Integer>();
		Random r = new Random();
		while(num.size() < GameStage.MAX_BOMBS){
			int random = r.nextInt((80-0)+1)+0;
			if(hasNine(random)==true){} // do nothing if random number has the digit "9"
			else if(!num.contains(random)){
				num.add(random);
			}
		} return num;
	}

	// method used in randomizeCells. This is to correct the placement of randomized bomb cells on the 2D table.
	// For example, row # 1 has indices from 1 to 8 only. Row # 2 starts from 10 and ends in 18. Row # 3 starts
	// from index 20 and ends in 28, and so on. If the supposed randomized number location is 9, this method returns
	// true and tells randomizeCells() to randomize another number.
	private boolean hasNine(int num){
	      while(num > 0){
	          if(num % 10 == 9) return true;
	          num=num/10;
	      } return false;
	}

	private void initGameBoard(){ // method to fill up the 9x9 array; 0-no bomb, 1-bomb
		bombCells = randomizeCells();
		for(int i=0;i<GameStage.MAP_NUM_ROWS;i++){
			for(int j=0;j<GameStage.MAP_NUM_COLS;j++){
				int check = i*10+j;
				if(bombCells.contains(check)){ // If the bombCells contain the current row x column index, that index is 1. Otherwise, 0
					gameBoard[i][j] = 1;
				} else{
					gameBoard[i][j] = 0;
				}
			}
		}
		for(int i=0;i<GameStage.MAP_NUM_ROWS;i++){
			System.out.println(Arrays.toString(this.gameBoard[i]));//print final board content
		}
	}

	private void createMap(){ // method to create 9x9 = 81 land cells
		for(int i=0;i<GameStage.MAP_NUM_ROWS;i++){
			for(int j=0;j<GameStage.MAP_NUM_COLS;j++){

				// Instantiate land elements
				Element land = new Element(Element.LAND_TYPE,this); // The GameStage is also passed
				land.initRowCol(i,j); // This sets that land indexing on the grid, which tells if it's 1 or 0

				//add each land to the array list landCells
				this.landCells.add(land);
			}
		}

		this.setGridPaneProperties();
		this.setGridPaneContents();
	}

	// method to set size and location of the grid pane
	private void setGridPaneProperties(){
		this.map.setPrefSize(GameStage.MAP_WIDTH, GameStage.MAP_HEIGHT);
		//set the map to x and y location; add border color to see the size of the gridpane/map
//	    this.map.setStyle("-fx-border-color: red ;");
		this.map.setLayoutX(GameStage.WINDOW_WIDTH*0.010);
	    this.map.setLayoutY(GameStage.WINDOW_WIDTH*0.140);
	    this.map.setVgap(5);
	    this.map.setHgap(5);
	}

	// method to add row and column constraints of the grid pane
	private void setGridPaneContents(){

		 //loop that will set the constraints of the elements in the grid pane
	     int counter=0;
	     for(int row=0;row<GameStage.MAP_NUM_ROWS;row++){
	    	 for(int col=0;col<GameStage.MAP_NUM_COLS;col++){
	    		 // map each land's constraints
	    		 GridPane.setConstraints(landCells.get(counter).getImageView(),col,row);
	    		 counter++;
	    	 }
	     }

	   //loop to add each land element to the gridpane/map
	     for(Element landCell: landCells){
	    	 this.map.getChildren().add(landCell.getImageView());
	     }
	}

	// method to check if a cell is a bomb or not
	boolean isBomb(Element element){
		int i = element.getRow();
		int j = element.getCol();

		//if the row x col cell value is equal to 1, the cell has bomb
		if(this.gameBoard[i][j] == 1){
			System.out.println(">>>>>>>>>Bomb!");
			return true;
		}

		System.out.println(">>>>>>>>>SAFE!");
		return false;
	}

	// checks the number of bombs present on a certain cell's area (8 cells)
	int checkBombs(Element element){
		int i = element.getRow();
		int j = element.getCol();
		int count = 0;
		int cell = (i*10)+j;
		ArrayList<Integer> areaCells = new ArrayList<Integer>();
		int top, bot, left, right, upL, upR, botL, botR;
		top = cell-10; bot = cell+10; left = cell-1; right = cell+1; upL = top-1; upR = top+1; botL = bot-1; botR = bot+1;
		areaCells.add(top); areaCells.add(bot); areaCells.add(left); areaCells.add(right);
		areaCells.add(upL); areaCells.add(upR); areaCells.add(botL); areaCells.add(botR);
		for(Integer num: areaCells){
			if(bombCells.contains(num)) count++;
		}
		return(count);
	}

	// method that increments flagCells if a certain cell that has been placed with a flag is a bomb.
	// If the number of flagCells is equal to the number of bombCells (20), the game ends and the player wins
	void checkFlags(Element element){
		int i = element.getRow();
		int j = element.getCol();
		int cell = (i*10)+j;
		if(bombCells.contains(cell)){
			flagCells++;
		}
		if(flagCells == bombCells.size()){
			setGameOver(1);
		}
	}

	// method to decrement flagCells if a player removes the flag on a certain cell that has a bomb
	void unCheckFlags(Element element){
		int i = element.getRow();
		int j = element.getCol();
		int cell = (i*10)+j;
		if(bombCells.contains(cell)){
			flagCells--;
		}
	}

	// method to add the safe cells to to an array called safeCells. If the cells that has been clicked is safe,
	// the image is cleared and saves that cell's index to the array.
	void setSafeCells(Element element){
		int i = element.getRow();
		int j = element.getCol();
		int cell = (i*10)+j;
		if(!bombCells.contains(cell)){
			if(safeCells.contains(cell)){}
			else safeCells.add(cell);
		}
	}

	public boolean isFlagClicked() {
		return this.flagClicked;
	}

	public void setFlagClicked(boolean value) {
		this.flagClicked = value;
	}

	Stage getStage() {
		return this.stage;
	}

	// method to make the transition to another window that tells the player if he/she wins or not.
	// If num is 1, the player won. If 0, the player lost.
	void setGameOver(int num){
		PauseTransition transition = new PauseTransition(Duration.seconds(1));
		transition.play();

		transition.setOnFinished(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent arg0) {
				GameOverStage gameover = new GameOverStage(num);
				stage.setScene(gameover.getScene());
			}
		});
	}

}

