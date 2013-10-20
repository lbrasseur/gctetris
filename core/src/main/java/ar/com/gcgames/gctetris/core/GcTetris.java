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
	private Scenario scenario;
	private Token token;
	private int xOffset = 0;
	private boolean rotating;

	public GcTetris() {
		super(33); // call update every 33ms (30 times per second)
	}

	@Override
	public void init() {
		// Hola, como te va?
		// create and add background image layer
		Image bgImage = assets().getImage("images/bg.png");
		ImageLayer bgLayer = graphics().createImageLayer(bgImage);
		graphics().rootLayer().add(bgLayer);

		blockImage = assets().getImage("images/block.jpg");

		scenario = new Scenario(10, 20);
		token = new Token();

		initKeyboard();
	}

	@Override
	public void update(int delta) {
		if (rotating) {
			token.rotate();
		}
		token.moveX(xOffset);
	}

	@Override
	public void paint(float alpha) {
		token.paint();
	}

	private void initKeyboard() {
		keyboard().setListener(new Keyboard.Listener() {

			@Override
			public void onKeyUp(Event event) {
				xOffset = 0;
				rotating = false;
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
				}
			}
		});
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

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	private class Token {
		private final static int PARTS_SIZE = 4;
		private ImageLayer[][] parts;
		private int x;
		private int y;
		private int width;
		private int height;

		public Token() {
			createL();

			for (ImageLayer[] partRow : parts) {
				for (ImageLayer part : partRow) {
					if (part != null) {
						graphics().rootLayer().add(part);
					}
				}
			}
		}

		private void createL() {
			width = 2;
			height = 3;
			parts = new ImageLayer[width][height];
			createBlock(0, 0);
			createBlock(0, 1);
			createBlock(0, 2);
			createBlock(1, 2);
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

		private void createBlock(int x, int y) {
			parts[x][y] = graphics().createImageLayer(blockImage);
		}

		public void moveX(int offset) {
			int newX = x + offset;
			if (newX >= 0 && newX + width <= scenario.getWidth()) {
				x = newX;
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
						newParts[row][newHeight - 1 -column] = parts[column][row];
					}
				}
			}

			width = newWidth;
			height = newHeight;
			parts = newParts;
		}
	}
}
