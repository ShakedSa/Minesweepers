package mines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mines {

	private PlaceStatus[][] mines;
	private boolean showAll = false;
	private Random rand = new Random();

	private enum Status {
		/* Enum to indicate the status of a certain location */
		OPEN, CLOSED, FLAG, MINE;
	}

	private class PlaceStatus {
		/* A private class to represent a certain place with his status. */
		private int adjacentMines;
		private Status state;
		private Status mine;
		private Status flag;
		private List<Place> neighbours;

		public PlaceStatus(Status state, int x, int y) {
			this.state = state;
			mine = null;
			flag = null;
			neighbours = (new Place(x, y)).getNeighbours(mines.length, mines[x].length);
		}

		/*
		 * Calculating the adjacent mines of the place, according to the neightbours of
		 * the place.
		 */
		private void calcAdjacentMines() {
			if (mine == Status.MINE) {
				adjacentMines = 1;
				return;
			}
			adjacentMines = 0;
			for (Place p : neighbours) {
				if (mines[p.x][p.y].mine == Status.MINE) {
					adjacentMines++;
				}
			}
		}
	}

	private class Place {

		/* Representing a certain location. */
		private int x, y;

		public Place(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/* Calculating the neighbours of the location (x,y). */
		public List<Place> getNeighbours(int height, int width) {
			List<Place> neighbours = new ArrayList<>();
			int posStartX = x - 1 < 0 ? x : x - 1;
			int posStartY = y - 1 < 0 ? y : y - 1;
			int posEndX = x + 1 > height - 1 ? x : x + 1;
			int posEndY = y + 1 > width - 1 ? y : y + 1;
			for (int i = posStartX; i <= posEndX; i++) {
				for (int j = posStartY; j <= posEndY; j++) {
					neighbours.add(new Place(i, j));
				}
			}
			return neighbours;
		}
	}

	public Mines(int height, int width, int numMines) {
		mines = new PlaceStatus[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				mines[i][j] = new PlaceStatus(Status.CLOSED, i, j);
			}
		}
		if (numMines > height * width) {
			/* if trying to put more mines than places on the board. */
			numMines = height * width;
		}
		for (int i = 0; i < numMines; i++) {
			/*
			 * Adding mines in a random position on the board, while making sure that there
			 * was no mine in that position before.
			 */
			int x, y;
			do {
				x = rand.nextInt(height);
				y = rand.nextInt(width);
			} while (!addMine(x, y));
		}
	}

	public boolean addMine(int i, int j) {
		/* Adding mine in location (i,j), if its not taken by a mine already. */
		if (mines[i][j].mine == Status.MINE) {
			return false;
		}
		mines[i][j].mine = Status.MINE;
		return true;
	}

	public boolean open(int i, int j) {
		/* If the place (i,j) is already open return. */
		if (mines[i][j].state == Status.OPEN) {
			return true;
		}
		/* If the place (i,j) is a mine unable to open. */
		if (mines[i][j].mine == Status.MINE) {
			return false;
		}
		/*
		 * If it's not a mine, calculate the amount of mines around the place, and open
		 * it. keep openning it's neighbours if the amount of mines are 0.
		 */
		mines[i][j].calcAdjacentMines();
		mines[i][j].state = Status.OPEN;
		if (mines[i][j].adjacentMines == 0) {
			for (Place p : mines[i][j].neighbours) {
				open(p.x, p.y);
			}
		}
		return true;
	}

	public void toggleFlag(int x, int y) {
		/* Toggling flag on the place (x,y). */
		if (mines[x][y].flag == Status.FLAG) {
			mines[x][y].flag = null;
			return;
		}
		mines[x][y].flag = Status.FLAG;
	}

	public String get(int i, int j) {
		/*
		 * According to the state the place (i,j) is in (open/closed/mine/flagged)
		 * return a representing string of the place. If the showAll field is set to
		 * true, return the representing string as if all the board is open.
		 */
		if (showAll) {
			if (mines[i][j].mine == Status.MINE) {
				return "X";
			}
			mines[i][j].calcAdjacentMines();
			if (mines[i][j].adjacentMines == 0) {
				return " ";
			}
			return mines[i][j].adjacentMines + "";
		}
		if (mines[i][j].state == Status.CLOSED) {
			return mines[i][j].flag == Status.FLAG ? "F" : ".";
		}
		if (mines[i][j].mine == Status.MINE) {
			return "X";
		}
		mines[i][j].calcAdjacentMines();
		if (mines[i][j].adjacentMines == 0) {
			return " ";
		}
		return mines[i][j].adjacentMines + "";

	}

	public boolean isDone() {
		/*
		 * Going over the board to check if there are more places to open that are not
		 * mines.
		 */
		for (int i = 0; i < mines.length; i++) {
			for (int j = 0; j < mines[i].length; j++) {
				if (mines[i][j].state == Status.CLOSED && mines[i][j].mine != Status.MINE) {
					return false;
				}
			}
		}
		return true;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	@Override
	public String toString() {
		/* Building a string to represent the entire board. */
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < mines.length; i++) {
			for (int j = 0; j < mines[i].length; j++) {
				b.append(get(i, j));
			}
			b.append("\n");
		}
		return b.toString();
	}
}
