package application;

import java.util.Random;

import javafx.beans.property.SimpleIntegerProperty;

public class MineHuntModel implements IMineHuntModel {

	private int numberRow;
	private int numberCol;
	private int minesNb;

	private SimpleIntegerProperty nbClick = new SimpleIntegerProperty(0);
	private SimpleIntegerProperty nbError = new SimpleIntegerProperty(0);
	private int error;
	private int count = 0;
	private CellButton[][] mineHunt;

	private Random randomRow = new Random();
	private Random randomCol = new Random();

	private MineHuntController ctrl;

	public MineHuntModel() {
		// Default game
		this.numberRow = CONSTANTE.NBR_ROW;
		this.numberCol = CONSTANTE.NBR_COL;
		this.minesNb = CONSTANTE.NBR_MINES;
		mineHunt = new CellButton[numberRow][numberCol];
		initNewGame(minesNb);
	}

	public MineHuntModel(int minesNb, int numberRow, int numberCol) {
		this.numberRow = numberRow;
		this.numberCol = numberCol;
		this.minesNb = minesNb;
		mineHunt = new CellButton[numberRow][numberCol];
		initNewGame(minesNb);
	}

	public MineHuntModel(CellButton[][] mineHunt) {
		// Default game
		this.numberRow = CONSTANTE.NBR_ROW;
		this.numberCol = CONSTANTE.NBR_COL;
		this.minesNb = CONSTANTE.NBR_MINES;
		this.mineHunt = mineHunt;
		// mineHunt = new Cell[numberRow][numberCol];
		// initNewGame(minesNb);
	}

	@Override
	public void initNewGame(int minesNb) {
		// Init all cell
		for (int a = 0; a < mineHunt.length; a++) {
			for (int b = 0; b < mineHunt[a].length; b++) {
				mineHunt[a][b] = new CellButton(a, b);
			}
		}
		// Add random Mine
		int g = 0, j = 0, i = 0;
		while (i < minesNb) {
			g = randomRow.nextInt(numberRow);
			j = randomCol.nextInt(numberCol);
			if (!mineHunt[g][j].isMine()) {
				mineHunt[g][j].setMine(true);
				i++;
			}
		}
	}

	@Override
	public int gridWidth() {
		return numberCol;
	}

	@Override
	public int gridHeight() {
		return numberRow;
	}

	@Override
	public int mines() {
		return minesNb;
	}

	@Override
	public int errors() {
		return error;
	}

	@Override
	public int neighborMines(int row, int col) {
		int countNeighborMine = 0;
		int i = 0, j = 0;
		if (row != 0)
			i = row - 1;
		while (i <= row + 1) {
			if (col != 0)
				j = col - 1;
			while (j <= col + 1) {
				if (mineHunt[i][j].isMine()) {
					countNeighborMine++;
				}
				if (j == numberRow - 1)
					break;
				j++;
			}
			j = 0;
			if (i == numberCol - 1)
				break;
			i++;
		}

		return countNeighborMine;
	}

	@Override
	public boolean isOpen(int row, int col) {
		return mineHunt[row][col].isOpen();
	}

	@Override
	public boolean isFlagged(int row, int col) {
		return mineHunt[row][col].isFlag();
	}

	@Override
	public boolean isGameOver() {
		boolean checkGameStat = true;
		for (int i = 0; i < mineHunt.length; i++) {
			for (int j = 0; j < mineHunt[i].length; j++) {
				if (!mineHunt[i][j].isMine() && !mineHunt[i][j].isOpen()) {
					checkGameStat = false;
				}
			}
		}
		if (checkGameStat) {
			for (int i = 0; i < mineHunt.length; i++) {
				for (int j = 0; j < mineHunt[i].length; j++) {
					if (mineHunt[i][j].isMine() && !mineHunt[i][j].isOpen()) {
						mineHunt[i][j].setOpen(true);
						ctrl.showLastMine(mineHunt[i][j]);
					}
				}
			}
		}
		return checkGameStat;
	}

	@Override
	public boolean open(int row, int col) {
		boolean checkMine = false;
		CellButton mine = mineHunt[row][col];
		if (!mine.isOpen()) {
			nbClick.setValue(++count);
			if (mine.isMine()) {
				nbError.setValue(++error);
				if(nbError.getValue()==minesNb){
					ctrl.lostGame(nbError.getValue());
				}
				checkMine = true;
			}
			mine.setOpen(true);
		}
		return checkMine;
	}

	@Override
	public void setFlagState(int row, int col, boolean state) {
		CellButton mine = mineHunt[row][col];
		if (mine.isFlag()) {
			mine.setFlag(false);
		} else {
			mine.setFlag(true);
		}
	}

	public String toString() {
		String value = "";
		for (int i = 0; i < mineHunt.length; i++) {
			value += "[ ";
			for (int j = 0; j < mineHunt[i].length; j++) {
				if (mineHunt[i][j].isMine()) {
					value += "M,";
				} else {
					value += "V,";
				}
			}
			value += " ] \n";
		}
		return value;
	}

	public CellButton[][] getMineHunt() {
		return mineHunt;
	}

	public void setMineHunt(CellButton[][] mineHunt) {
		this.mineHunt = mineHunt;
	}

	public SimpleIntegerProperty getNbClick() {
		return nbClick;
	}

	public void setNbClick(SimpleIntegerProperty nbClick) {
		this.nbClick = nbClick;
	}

	public SimpleIntegerProperty getNbError() {
		return nbError;
	}

	public void setNbError(SimpleIntegerProperty nbError) {
		this.nbError = nbError;
	}

	public void setCtrl(MineHuntController ctrl) {
		this.ctrl = ctrl;
	}

}
