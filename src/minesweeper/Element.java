package minesweeper;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Element {
	private String type;
	protected Image img;
	protected ImageView imgView;
	protected GameStage gameStage;
	protected int row, col;

	// The images are stored in constant values
	public final static Image FLAG_IMAGE = new Image("images/flag.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,true,true);
	public final static Image FLAG_OFF_IMAGE = new Image("images/flagOff.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,true,true);
	public final static Image BOMB_IMAGE = new Image("images/bomb.gif",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image LAND_IMAGE = new Image("images/land.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image ONE_IMAGE = new Image("images/one.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image TWO_IMAGE = new Image("images/two.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image THREE_IMAGE = new Image("images/three.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image FOUR_IMAGE = new Image("images/four.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image FIVE_IMAGE = new Image("images/five.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image SIX_IMAGE = new Image("images/six.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image SEVEN_IMAGE = new Image("images/seven.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static Image EIGHT_IMAGE = new Image("images/eight.png",GameStage.CELL_WIDTH,GameStage.CELL_WIDTH,false,false);
	public final static int IMAGE_SIZE = 70;

	public final static String FLAG_TYPE = "flag"; // Separate flag type from the land's
	public final static String FLAG_OFF_TYPE = "flagOff"; // This is to employ toggle ON and OFF for the placement of flags
	public final static String BOMB_TYPE = "bomb";
	public final static String LAND_TYPE = "land";
	public final static String LAND_FLAG_TYPE = "landToFlag";
	public final static String CLEARED_TYPE = "clear";

	public Element(String type, GameStage gameStage) { // Note: an ELEMENT has an imgView (to set up an Image)
		this.type = type;
		this.gameStage = gameStage;

		// load image depending on the type
		switch(this.type) { // This determines which picture to use, depending on the element to be added on the layout
			case Element.FLAG_TYPE: this.img = Element.FLAG_IMAGE; break;
			case Element.LAND_TYPE: this.img = Element.LAND_IMAGE; break;
			case Element.BOMB_TYPE: this.img = Element.BOMB_IMAGE; break;
			case Element.FLAG_OFF_TYPE: this.img = Element.FLAG_OFF_IMAGE; break;
		}

		this.setImageView();
		this.setMouseHandler();
	}

	protected void loadImage(String filename,int width, int height){
		try{
			this.img = new Image(filename,width,height,false,false);
		} catch(Exception e){}
	}


	String getType(){
		return this.type;
	}

	int getRow() {
		return this.row;
	}

	int getCol() {
		return this.col;
	}


	protected ImageView getImageView(){
		return this.imgView;
	}

	void setType(String type){
		this.type = type;
	}

	void initRowCol(int i, int j) {
		this.row = i;
		this.col = j;
	}

	private void setImageView() { //Adjusting the properties of an image
		// initialize the image view of this element
		this.imgView = new ImageView();
		this.imgView.setImage(this.img);
		this.imgView.setLayoutX(0); // JavaFX method of setLayout for Image. Sets the X/Y positioning of the node
		this.imgView.setLayoutY(0);
		this.imgView.setPreserveRatio(true);

		if(this.type.equals(Element.FLAG_TYPE)) {
			this.imgView.setLayoutY(8);
			this.imgView.setFitWidth(GameStage.FLAG_WIDTH);
			this.imgView.setFitHeight(GameStage.FLAG_HEIGHT);
		}
		else if(this.type.equals(Element.FLAG_OFF_TYPE)) {
			this.imgView.setLayoutX(95);
			this.imgView.setFitWidth(GameStage.FLAG_WIDTH);
			this.imgView.setFitHeight(GameStage.FLAG_HEIGHT);
		}
		else {
			this.imgView.setFitWidth(GameStage.CELL_WIDTH);
			this.imgView.setFitHeight(GameStage.CELL_HEIGHT);
		}
	}

	private void setMouseHandler(){
		Element element = this; // Notice that no parameters, but we still got the element
		this.imgView.setOnMouseClicked(new EventHandler<MouseEvent>(){ //Notice that the ImageView element is the one that interacts
			public void handle(MouseEvent e) {
                switch(element.getType()) {
	                case Element.FLAG_TYPE: 		// if flag, set flagClicked to true
								                	System.out.println("FLAG clicked!");
								    	            gameStage.setFlagClicked(true);
								    	            break;
	                case Element.FLAG_OFF_TYPE: 	// if flagOff, set flagClicked back to false
	                								System.out.println("FLAG OFF clicked!");
	                								gameStage.setFlagClicked(false);
	                								break;
	    			case Element.LAND_TYPE:	// Check if the Flag is toggled for that land
			    									System.out.println("LAND clicked!");
								    				if(!gameStage.isFlagClicked()) {
								    					if(!gameStage.isBomb(element)){	// if not a bomb, clear image
								    						int bombCount = gameStage.checkBombs(element);
								    						setImage(element, bombCount);
								    						element.setType(CLEARED_TYPE);
								    						gameStage.setSafeCells(element); // save the cleared cell's index to an array
								    						// also check if the number of safe cells has been reached already
									    					if(GameStage.safeCells.size() == GameStage.MAX_SAFE_CELLS) gameStage.setGameOver(1);
								    					}
								    					else {
								    						changeImage(element,Element.BOMB_IMAGE); // if bomb, change image to bomb
								    						gameStage.setGameOver(0);
								    					}
								    	            } else {
								    	            	changeImage(element,Element.FLAG_IMAGE);	// if flag was clicked before hand, change image to flag
								    	            	element.setType(LAND_FLAG_TYPE);			// change type to landToFlag
								    	            	//gameStage.setFlagClicked(false);	    	// reset flagClicked to false
								    	            	gameStage.checkFlags(element);
								    	            }
								    				break;
	    			case Element.LAND_FLAG_TYPE:
								    				changeImage(element,Element.LAND_IMAGE);		// if flag is clicked, change image back to land
							    	            	element.setType(LAND_TYPE);
							    	            	gameStage.unCheckFlags(element);				// decrement flagCells if the cell returns back to land
							    	            	break;
	    			case Element.CLEARED_TYPE:
	    											// do nothing if cell was cleared
	    											break;
                }
			}	//end of handle()
		});
	}

//	private void clearImage(Element element) {
//		imgView.setImage(null);
//	}

	private void changeImage(Element element, Image image) {
		this.imgView.setImage(image);

	}

	// method to set the clicked image's appropriate image that pertains to the number of bomb cells present in the cell's area
	private void setImage(Element element, int count){
		switch(count){
		case 0:
			changeImage(element, null); // If there's no bombs within the area, set image to null
			break;
		case 1:
				changeImage(element, Element.ONE_IMAGE);
				break;
		case 2:
			changeImage(element, Element.TWO_IMAGE);
				break;
		case 3:
			changeImage(element, Element.THREE_IMAGE);
				break;
		case 4:
			changeImage(element, Element.FOUR_IMAGE);
				break;
		case 5:
			changeImage(element, Element.FIVE_IMAGE);
				break;
		case 6:
			changeImage(element, Element.SIX_IMAGE);
				break;
		case 7:
			changeImage(element, Element.SEVEN_IMAGE);
				break;
		case 8:
			changeImage(element, Element.EIGHT_IMAGE);
				break;
		}
	}
}
