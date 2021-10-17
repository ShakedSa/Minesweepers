package mines;

import javafx.scene.control.Button;

public class MinesButton extends Button {
	/*A class to represent a Button with his location on the gird.*/
	private final int x, y;

	public MinesButton(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
