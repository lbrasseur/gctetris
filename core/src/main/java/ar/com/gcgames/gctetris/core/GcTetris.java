package ar.com.gcgames.gctetris.core;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.keyboard;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.TypedEvent;

public class GcTetris extends Game.Default {
	private final static int BLOCK_SIZE = 16;
	private final static int SCENARIO_X = 25;
	private final static int SCENARIO_Y = 10;
	private Image blockImage;
	private Image blockImageRed;
	private Image blockImageyellow;
	private Scenario scenario;
	private Token token;
	private int xOffset = 0;
	private boolean rotating;
	private boolean firstTokenUpdate = true;
	private boolean acceleratedFall;
	private int fallMiliseconds = 1000;
	private int fallMilisecondsCounter = 0;

	public GcTetris() {
		super(33); // call update every 33ms (30 times per second)
	}

	@Override
	public void init() {
		// create and add background image layer
		Image bgImage = assets().getImage("images/bg.png");
		ImageLayer bgLayer = graphics().createImageLayer(bgImage);
		graphics().rootLayer().add(bgLayer);

		blockImage = assets().getImage("images/block.jpg");
		blockImageRed = assets().getImage("images/block2.jpg");
		blockImageyellow = assets().getImage("images/block3.jpg");
		scenario = new Scenario(10, 20);

		startNewToken();

		initKeyboard();
	}

	@Override
	public void update(int delta) {
		if (token!=null) {
		if (rotating) {
			token.rotate();
		}
		token.moveX(xOffset);

		if (!firstTokenUpdate) {
			fallMilisecondsCounter += delta;
			if (acceleratedFall || fallMilisecondsCounter > fallMiliseconds) {
				token.moveDown();
				fallMilisecondsCounter -= fallMiliseconds;
			}
		} else {
			firstTokenUpdate = false;
		}
		}
	}

	@Override
	public void paint(float alpha) {
		if (token!=null) {
		token.paint();
		scenario.paint();
		}
	}

	private void initKeyboard() {
		keyboard().setListener(new Keyboard.Listener() {
			@Override
			public void onKeyUp(Event event) {
				xOffset = 0;
				rotating = false;
				acceleratedFall = false;
			}

			@Override
			public void onKeyTyped(TypedEvent event) {
			}

			@Override
			public void onKeyDown(Event event) {
				if (event.key().equals(Key.LEFT)) {
					xOffset = -1;
				} else if (event.key().equals(Key.RIGHT)) {
					xOffset = 1;
				} else if (event.key().equals(Key.UP)) {
					rotating = true;
				} else if (event.key().equals(Key.DOWN)) {
					acceleratedFall = true;
				}
			}
		});
	}

	private void startNewToken() {
		token = new Token();
		firstTokenUpdate = true;
		fallMiliseconds -= 10;
		fallMilisecondsCounter = 0;
	}

	private void fixToken() {
		if (token.getY() == 0) {
			token = null;
scenario.showLooser();
		} else {
			for (int column = 0; column < token.getWidth(); column++) {
				for (int row = 0; row < token.getHeight(); row++) {
					ImageLayer part = token.getParts()[column][row];
					if (part != null) {
						scenario.getParts()[token.getX() + column][token.getY()
								+ row] = part;
					}
				}
			}

			scenario.deleteFullRows();
			startNewToken();
		}
	}

	private class Scenario {
		private ImageLayer[][] parts;
		private int width;
		private int height;

		public Scenario(int width, int height) {
			super();
			this.width = width;
			this.height = height;

			CanvasImage image = graphics().createImage(width * BLOCK_SIZE,
					height * BLOCK_SIZE);
			Canvas canvas = image.canvas();
			canvas.setStrokeWidth(2);
			canvas.setFillColor(0xff000000);
			canvas.fillRect(0, 0, image.width(), image.height());

			ImageLayer layer = graphics().createImageLayer(image);
			layer.setTranslation(SCENARIO_X, SCENARIO_Y);
			graphics().rootLayer().add(layer);

			parts = new ImageLayer[width][height];
		}

		public void showLooser() {
			Image looserImage = assets().getImage("images/looser.jpg");
			ImageLayer looserLayer = graphics().createImageLayer(looserImage);
			looserLayer.setTranslation(
					SCENARIO_X + (width - looserImage.width()) / 2, SCENARIO_Y
							+ (height - looserImage.height()) / 2);
			graphics().rootLayer().add(looserLayer);
		}

		public void paint() {
			for (int column = 0; column < width; column++) {
				for (int row = 0; row < height; row++) {
					ImageLayer part = parts[column][row];
					if (part != null) {
						part.setTranslation(SCENARIO_X + column
								* BLOCK_SIZE, SCENARIO_Y + row
								* BLOCK_SIZE);
					}
				}
			}
		}

		public void deleteFullRows() {
			for (int y = token.getY();y<token.getY()+token.getHeight();y++) {
				boolean full = true;
				for (int x=0;x<width;x++) {
					if (parts[x][y]== null) {
						full = false;
						break;
					}
				}
				if (full) {
					deleteRow(y);
				}
			}
		}

		private void deleteRow(int rowToDelete) {
			for (int x=0;x<width;x++) {
				ImageLayer part = parts[x][rowToDelete];
				if (part!=null) {
					part.destroy();
				}
			}
			for (int y = rowToDelete; y>0;y--){
				for (int x=0;x<width;x++) {
					parts[x][y] = parts[x][y - 1];
				}
			}
			
			for (int x=0;x<width;x++) {
				parts[x][0] = null;
			}
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public ImageLayer[][] getParts() {
			return parts;
		}
	}

	private class Token {
		private ImageLayer[][] parts;
		private int x;
		private int y;
		private int width;
		private int height;

		public Token() {
			int color = (int) (Math.random() * 3);
			int tokenType = (int) (Math.random() * 6);

			switch (tokenType) {
			case 0:
				createL(color);
				break;
			case 1:
				createI(color);
				break;
			case 2:
				createT(color);
				break;
			case 3:
				createS(color);
				break;
			case 4:
				createZ(color);
				break;
			default:
				createO(color);
			}

			x = (scenario.getWidth() - width) / 2;
			for (ImageLayer[] partRow : parts) {
				for (ImageLayer part : partRow) {
					if (part != null) {
						graphics().rootLayer().add(part);
					}
				}
			}
		}

		private void createL(int color) {
			width = 2;
			height = 3;
			parts = new ImageLayer[width][height];
			createBlock(0, 0, color);
			createBlock(0, 1, color);
			createBlock(0, 2, color);
			createBlock(1, 2, color);
		}

		private void createI(int color) {
			width = 1;
			height = 4;
			parts = new ImageLayer[width][height];
			createBlock(0, 0, color);
			createBlock(0, 1, color);
			createBlock(0, 2, color);
			createBlock(0, 3, color);
		}

		private void createT(int color) {
			width = 3;
			height = 2;
			parts = new ImageLayer[width][height];
			createBlock(1, 0, color);
			createBlock(0, 1, color);
			createBlock(1, 1, color);
			createBlock(2, 1, color);
		}

		private void createS(int color) {
			width = 3;
			height = 2;
			parts = new ImageLayer[width][height];
			createBlock(1, 0, color);
			createBlock(2, 0, color);
			createBlock(0, 1, color);
			createBlock(1, 1, color);
		}

		private void createZ(int color) {
			width = 3;
			height = 2;
			parts = new ImageLayer[width][height];
			createBlock(0, 0, color);
			createBlock(1, 0, color);
			createBlock(1, 1, color);
			createBlock(2, 1, color);
		}

		private void createO(int color) {
			width = 2;
			height = 2;
			parts = new ImageLayer[width][height];
			createBlock(0, 0, color);
			createBlock(1, 0, color);
			createBlock(0, 1, color);
			createBlock(1, 1, color);
		}

		private void paint() {
			for (int column = 0; column < width; column++) {
				for (int row = 0; row < height; row++) {
					ImageLayer part = parts[column][row];
					if (part != null) {
						part.setTranslation(SCENARIO_X + (x + column)
								* BLOCK_SIZE, SCENARIO_Y + (y + row)
								* BLOCK_SIZE);
					}
				}
			}
		}

		private void createBlock(int x, int y, int color) {
			switch (color) {
			case 0:
				parts[x][y] = graphics().createImageLayer(blockImage);
				break;
			case 1:
				parts[x][y] = graphics().createImageLayer(blockImageRed);
				break;
			case 2:
				parts[x][y] = graphics().createImageLayer(blockImageyellow);
				break;
			}
			// parts[x][y] = graphics().createImageLayer(blockImage);
		}

		public void moveDown() {
			if (canMove(0, 1)) {
				y++;
			} else {
				fixToken();
			}
		}

		public void moveX(int offset) {
			if (canMove(offset, 0)) {
				x += offset;
			}
		}

		private boolean canMove(int xOffset, int yOffset) {
			int newX = x + xOffset;
			int newY = y + yOffset;
			if (newY + height > scenario.getHeight() || newX < 0
					|| newX + width > scenario.getWidth()) {
				return false;
			} else {
				for (int column = 0; column < width; column++) {
					for (int row = 0; row < height; row++) {
						ImageLayer part = parts[column][row];
						ImageLayer scenarioPart = scenario.getParts()[newX
								+ column][newY + row];
						if (part != null && scenarioPart != null) {
							return false;
						}
					}
				}

				return true;
			}
		}

		public void rotate() {
			int newWidth = height;
			int newHeight = width;
			ImageLayer[][] newParts = new ImageLayer[newWidth][newHeight];

			for (int column = 0; column < width; column++) {
				for (int row = 0; row < height; row++) {
					ImageLayer part = parts[column][row];
					if (part != null) {
						newParts[row][newHeight - 1 - column] = parts[column][row];
					}
				}
			}

			width = newWidth;
			height = newHeight;
			parts = newParts;

			int widthExcess = scenario.getWidth() - (x + newWidth);
			if (widthExcess < 0) {
				moveX(widthExcess);
			}
		}

		public ImageLayer[][] getParts() {
			return parts;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}
