package de.ifdgmbh.mad.mdga.controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyController {
	@FXML
	AnchorPane mypane, gamepane;
	@FXML
	Button minButton, closeButton, rollButton, skipButton, restartButton, rerollButton;
	@FXML
	ImageView myView;
	@FXML
	Label player1Label, player2Label, player3Label, player4Label;

	Player player1, player2, player3, player4;
	ArrayList<Player> players = new ArrayList<>();
	ArrayList<Player> finished = new ArrayList<>();

	int lastNumber = 0;

	Image player1I = new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/player1.png").toString());
	Image player2I = new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/player2.png").toString());
	Image player3I = new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/player3.png").toString());
	Image player4I = new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/player4.png").toString());

	/**
	 * stores the gamefield buttons and the finish buttons
	 */
	Button[] buttons = new Button[57];

	/**
	 * Stores the buttons for the player storages
	 */
	Button[][] pStorage = new Button[5][5];

	int gamefield[][];
	boolean active = false;

	boolean getOut = false;
	boolean atEnd = false;
	int rolls = 3;

	public void initialize() {
		rollButton.setText("GO");
		int k = 0;
		for (Node n : gamepane.getChildren()) {
			if (k > 72)
				break;
			if (n.getClass() == Button.class) {
				if (k >= 0 && k <= 56) {
					buttons[k] = (Button) n;
					buttons[k].setText("");
					buttons[k].setPadding(new Insets(0, 0, 0, 0));
					// buttons[k].setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
					// null, null)));
					if (k > 0) {
						buttons[k].setStyle("-fx-border-width: 1px;\r\n" + "	-fx-border-color: black;\r\n"
								+ "	-fx-background-color: transparent;\r\n" + "	-fx-border-radius: 5em;");
						final int var = k;
						buttons[k].setOnMousePressed(new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								buttons[var].setStyle("-fx-border-width: 1px;\r\n" + "	-fx-border-color: red;\r\n"
										+ "	-fx-background-color: transparent;\r\n" + "	-fx-border-radius: 5em;");
							}
						});
						buttons[k].setOnMouseReleased(new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								buttons[var].setStyle("-fx-border-width: 1px;\r\n" + "	-fx-border-color: black;\r\n"
										+ "	-fx-background-color: transparent;\r\n" + "	-fx-border-radius: 5em;");
								if (!active) {
									popUp(false,false,"Start the game!");
									return;
								}
								if (lastNumber == 0) {
									popUp(false, false, "Roll the dice!");
									return;
								}
								if (getOut) {
									popUp(false, false, "You need to move a storage figure!");
									return;
								}
								int k = giveXY(Integer.valueOf(getActivePlayer().getNumber() - 1 + "" + 1))[0];
								int l = giveXY(Integer.valueOf(getActivePlayer().getNumber() - 1 + "" + 1))[1];

								if (getActivePlayer().getStartNum() > 0
										&& getActivePlayer().getNumber() == gamefield[k][l]) {
									if ((var != giveFeld(k, l)) && (giveFeld(k, l) + lastNumber != var)) {
										popUp(false, false, "You need to move the storage blocking figure!");
										return;
									}
								}
								if (var > 40) {
									// clicks on button in finish sector
									if (moveWithinFinish(var, lastNumber)) {
										nextPlayer();
										updateLabel();
										return;
									}
								} else {
									// wants to move
									// casual next field
									// can only select a figure of him
									if (gamefield[giveXY(var)[0]][giveXY(var)[1]] != getActivePlayer().getNumber()) {
										popUp(false, false, "Pick one of your figures!");
										return;
									}
									// moving to an end?
									if (moveToFinal(var, lastNumber, getActivePlayer().getNumber())) {
										if (getActivePlayer().getFinishNum() == 4) {
											popUp(false, false, "EYO " + getActivePlayer().getName() + " is done!");
											getActivePlayer().setPlaying(false);
											finished.add(getActivePlayer());
											nextPlayer();
											if (finished.size() == 4) {
												popUp(true, false, "Test");
												active = false;
											}
										} else {
											nextPlayer();
										}
										updateLabel();
										return;
									} else if (atEnd) {
										atEnd = false;
										return;
									}
									// is the move position over 40?
									int move = 0;
									if (var + lastNumber > 40) {
										move = var + lastNumber - 40;
									} else {
										move = var + lastNumber;
									}
									// is the move position free to move?
									if (gamefield[giveXY(move)[0]][giveXY(move)[1]] != getActivePlayer().getNumber()) {
										// is there none or an enemy?
										int pos = gamefield[giveXY(move)[0]][giveXY(move)[1]];
										if (pos == 0) {
											// free - nothing
										} else {
											// enemy
											for (Player p : players) {
												if (p.getNumber() == pos) {
													p.setStartNum(p.getStartNum() + 1);
													updateStorage(p);
												}
											}

										}
										// move internally
										gamefield[giveXY(move)[0]][giveXY(move)[1]] = getActivePlayer().getNumber();
										gamefield[giveXY(var)[0]][giveXY(var)[1]] = 0;

										// move visually
										adaptImages();

									} else {
										popUp(false, false, "There is already your figure!");
										return;
									}
								}
								// if player rolls 6 he can re-roll
								if (lastNumber != 6) {
									rolls = 1;
									nextPlayer();
								} else {
									rolls = 1;
								}

								lastNumber = 0;
								updateLabel();

							}
						});
					}
				} else if (k > 56) {
					if (k <= 60) {
						int temp = k - 56;
						pStorage[1][temp] = (Button) n;
					} else if (k <= 64) {
						int temp = k - 60;
						pStorage[2][temp] = (Button) n;
					} else if (k <= 68) {
						int temp = k - 64;
						pStorage[3][temp] = (Button) n;
					} else if (k <= 72) {
						int temp = k - 68;
						pStorage[4][temp] = (Button) n;
					}
				}
				k++;
			}
		}

		for (int p = 1; p <= 4; p++) {
			for (int d = 1; d <= 4; d++) {
				pStorage[p][d].setText("");
				pStorage[p][d].setPadding(new Insets(0, 0, 0, 0));
				pStorage[p][d].setStyle("-fx-border-width: 1px;\r\n" + "	-fx-border-color: black;\r\n"
						+ "	-fx-background-color: transparent;\r\n" + "	-fx-border-radius: 5em;");
				final int var = p;
				final int var2 = d;
				pStorage[p][d].setOnMousePressed(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						pStorage[var][var2].setStyle("-fx-border-width: 1px;\r\n" + "	-fx-border-color: red;\r\n"
								+ "	-fx-background-color: transparent;\r\n" + "	-fx-border-radius: 5em;");
					}
				});
				pStorage[p][d].setOnMouseReleased(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						pStorage[var][var2].setStyle("-fx-border-width: 1px;\r\n" + "	-fx-border-color: black;\r\n"
								+ "	-fx-background-color: transparent;\r\n" + "	-fx-border-radius: 5em;");
						if (!active) {
							popUp(false,false,"Start the game!");
							return;
						}
						// figures to move out?
						if (getActivePlayer().getStartNum() <= 0) {
							popUp(false, false, "No figure left in storage!");
							return;
						}
						// no number rolled
						if (lastNumber == 0) {
							popUp(false, false, "Roll the dice!");
							return;
							// rolled a 6?
						} else if (lastNumber != 6) {
							popUp(false, false, "You gotta roll a 6!");
							return;
						} else {
							String temp = var - 1 + "" + 1;
							int temp2 = Integer.valueOf(temp);
							if (gamefield[giveXY(temp2)[0]][giveXY(temp2)[1]] == var) {
								popUp(false, false, "Move your figures first!");
								return;
							} else if (var != getActivePlayer().getNumber()) {
								popUp(false, false, "Wrong figures!");
								return;
							} else {
								// startField is free
								if (gamefield[giveXY(temp2)[0]][giveXY(temp2)[1]] >= 0) {
									for (Player p : players) {
										if (p.getNumber() == gamefield[giveXY(temp2)[0]][giveXY(temp2)[1]]) {
											p.setStartNum(p.getStartNum() + 1);
											updateStorage(p);
										}
									}
								}
								gamefield[giveXY(temp2)[0]][giveXY(temp2)[1]] = var;
								getOut = false;
								getActivePlayer().setStartNum(getActivePlayer().getStartNum() - 1);
								adaptImages();
								updateStorage(var, var2);
								rolls = 1;
							}
						}
					}
				});
			}

		}

		myView.setImage(new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/gamefield.png").toString()));
		popUp(false, true, "");

		if (players.size() == 0) {
			popUp(false, false, "No player!");
		}
		updateLabel();
	}

	/**
	 * @return the currently active player as player object
	 */
	public Player getActivePlayer() {
		for (Player p : players) {
			if (p.isActive()) {
				return p;
			}
		}
		return null;
	}

	/**
	 * sets the Images according to the int[][] gamefield
	 */
	public void adaptImages() {
		for (int y = 1; y < 12; y++) {
			for (int x = 1; x < 12; x++) {
				if (gamefield[x][y] == 1 && player1 != null) {
					buttons[giveFeld(x, y)].setGraphic(new ImageView(player1.getPic()));
				} else if (gamefield[x][y] == 2 && player2 != null) {
					buttons[giveFeld(x, y)].setGraphic(new ImageView(player2.getPic()));
				} else if (gamefield[x][y] == 3 && player3 != null) {
					buttons[giveFeld(x, y)].setGraphic(new ImageView(player3.getPic()));
				} else if (gamefield[x][y] == 4 && player4 != null) {
					buttons[giveFeld(x, y)].setGraphic(new ImageView(player4.getPic()));
				} else {
					buttons[giveFeld(x, y)].setGraphic(null);
				}
			}
		}
	}

	/**
	 * Checks whether the player can move to his final sector or not and actually
	 * moves the player if he can
	 * 
	 * @param var        - the button number the player is currently standing at
	 * @param lastNumber - last rolled number
	 * @param p          - number of the currently active player
	 * @return
	 */
	public boolean moveToFinal(int var, int lastNumber, int p) {
		boolean move = false;
		int newpos = 0;
		switch (p) {
		case 1: {
			if (var + lastNumber > 40 && (var + lastNumber <= 40 + 4)) {
				atEnd = true;
				if (gamefield[giveXY(var + lastNumber)[0]][giveXY(var + lastNumber)[1]] == p) {
					popUp(false, false, "There already is a figure of yours!");
					return false;
				} else {
					newpos = var + lastNumber;
					move = true;
				}
			}
			break;
		}
		case 2: {
			if (var <= 10) {
				if (var + lastNumber > 10 && (var + lastNumber <= 10 + 4)) {
					atEnd = true;
					if (gamefield[giveXY(var + lastNumber + 34)[0]][giveXY(var + lastNumber + 34)[1]] == p) {
						popUp(false, false, "There already is a figure of yours!");
						return false;
					} else {
						newpos = var + lastNumber + 34;
						move = true;
					}
				}
			}
			break;
		}
		case 3: {
			if (var <= 20) {
				if (var + lastNumber > 20 && (var + lastNumber <= 20 + 4)) {
					atEnd = true;
					if (gamefield[giveXY(var + lastNumber + 28)[0]][giveXY(var + lastNumber + 28)[1]] == p) {
						popUp(false, false, "There already is a figure of yours!");
						return false;
					} else {
						newpos = var + lastNumber + 28;
						move = true;
					}
				}
			}
			break;
		}
		case 4: {
			if (var <= 30) {
				if (var + lastNumber > 30 && (var + lastNumber <= 30 + 4)) {
					atEnd = true;
					if (gamefield[giveXY(var + lastNumber + 22)[0]][giveXY(var + lastNumber + 22)[1]] == p) {
						popUp(false, false, "There already is a figure of yours!");
						return false;
					} else {
						newpos = var + lastNumber + 22;
						move = true;
					}
				}
			}
			break;
		}
		default: {
			popUp(false, false, "Something went wrong!");
			break;
		}
		}

		if (move) {
			// move internally
			gamefield[giveXY(newpos)[0]][giveXY(newpos)[1]] = p;
			gamefield[giveXY(var)[0]][giveXY(var)[1]] = 0;
			getActivePlayer().setFinishNum(getActivePlayer().getFinishNum() + 1);

			// move visually
			adaptImages();

			return true;
		} else {
			return false;
		}

	}

	/**
	 * keeps the player p's figures storage visually updated
	 */
	public void updateStorage(Player p) {

		for (int i = 1; i <= 4; i++) {
			pStorage[p.getNumber()][i].setGraphic(null);
		}
		for (int i = 1; i <= p.getStartNum(); i++) {
			pStorage[p.getNumber()][i].setGraphic(new ImageView(p.getPic()));
		}

	}

	/**
	 * Called whenever a figure wants to move out the storage
	 */
	public void updateStorage(int player_var, int button_var2) {
		pStorage[player_var][button_var2].setGraphic(null);
	}

	/**
	 * Middle button that starts the game and rolls numbers to move
	 */
	public void rollButtonClick() {
		if (active) {
			printField();
			if (rolls > 0) {
				SecureRandom rand = new SecureRandom();
				lastNumber = rand.nextInt(6) + 1;
				buttons[0].setText("" + lastNumber);
				updateLabel();
				int x = giveXY(Integer.valueOf(getActivePlayer().getNumber() - 1 + "" + 1))[0];
				int y = giveXY(Integer.valueOf(getActivePlayer().getNumber() - 1 + "" + 1))[1];
				if ((lastNumber == 6) && (getActivePlayer().getStartNum() > 0)
						&& (gamefield[x][y] != getActivePlayer().getNumber())) {
					getOut = true;
				}
			} else {
				popUp(false, false, "You already rolled!");
				return;
			}
			rolls--;
			updateLabel();
			if (rolls == 0) {
				// if player can not move --> nextPlayer()
				if (!canPlayerMove(lastNumber)) {
					nextPlayer();
					updateLabel();
					lastNumber = 0;
				}
			}
		} else {
			startGame();
		}
	}

	/**
	 * skips the move of the current active player </br>
	 * currently used for manual canPlayerMove()-func
	 */
	public void skipButtonClick() {
		nextPlayer();
		updateLabel();
		printField();
	}

	/**
	 * Should detect whether the player is able to make a move or not
	 * 
	 * @param var
	 * @return
	 */
	public boolean canPlayerMove(int var) {
		// player has all figures in start storage
		if (getActivePlayer().getStartNum() == 4 && lastNumber != 6) {
			return false;
		}
		// player has already finished
		if (getActivePlayer().getFinishNum() == 4) {
			return false;
		}
		// player has all figures in goal or start sector
		if (getActivePlayer().getFinishNum() + getActivePlayer().getStartNum() == 4 && (lastNumber != 6)) {
			return false;
		}
		return true;
	}

	public void startGame() {
		buildField();
		printField();
		adaptImages();
		active = true;
		SecureRandom rand = new SecureRandom();
		int k = rand.nextInt(players.size());
		players.get(k).setActive(true);
		updateLabel();
		for (Player p : players)
			updateStorage(p);
	}

	/**
	 * This function is called whenever a player tries to move within his finish
	 * sector
	 * 
	 * @param var
	 * @param last
	 * @return
	 */
	public boolean moveWithinFinish(int var, int last) {
		int num = 0;

		if (getActivePlayer().getNumber() == 1) {
			num = (var - 44) * -1;
		}
		if (getActivePlayer().getNumber() == 2) {
			num = (var - 48) * -1;
		}
		if (getActivePlayer().getNumber() == 3) {
			num = (var - 52) * -1;
		}
		if (getActivePlayer().getNumber() == 4) {
			num = (var - 56) * -1;
		}
		if (num >= last) {
			// move is legit, but is the field free?
			if (gamefield[giveXY(var + last)[0]][giveXY(var + last)[1]] != getActivePlayer().getNumber()) {
				// move internally
				gamefield[giveXY(var + last)[0]][giveXY(var + last)[1]] = getActivePlayer().getNumber();
				gamefield[giveXY(var)[0]][giveXY(var)[1]] = getActivePlayer().getNumber() + 4;

				// move visually
				adaptImages();

				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	/**
	 * switches the active player to the next player according to the player list
	 */
	public void nextPlayer() {
		int temp = 0;
		for (int k = 0; k < players.size(); k++) {
			if (players.get(k).isActive()) {
				players.get(k).setActive(false);
				for (int i = 1; i <= players.size() - 1; i++) {
					temp = k;
					if (k + i >= players.size()) {
						k = -1;
					}
					if (players.get(k + i).isPlaying() && !(players.get(k + i).isActive())) {
						players.get(k + i).setActive(true);
						break;
					}
					k = temp;
				}

				if (getActivePlayer().getStartNum() == 4) {
					rolls = 3;
				} else if (getActivePlayer().getFinishNum() + getActivePlayer().getStartNum() == 4) {
					rolls = 3;
				} else {
					rolls = 1;
				}
				return;
			}
		}
	}

	/**
	 * [0] = x-Coord [1] = y-Coord
	 * 
	 * @param num
	 * @return
	 */
	public int[] giveXY(int num) {
		int[] arr = new int[2];
		switch (num) {
		case 10: {
			arr[0] = 6;
			arr[1] = 1;
			break;
		}
		case 20: {
			arr[0] = 11;
			arr[1] = 6;
			break;
		}
		case 30: {
			arr[0] = 6;
			arr[1] = 11;
			break;
		}
		case 40: {
			arr[0] = 1;
			arr[1] = 6;
			break;
		}
		default: {
			if (num < 10) {
				for (int j = 5; j >= 1; j--) {
					for (int i = 1; i <= 5; i++) {
						if (gamefield[i][j] >= 0) {
							num--;
						}
						if (num == 0) {
							arr[0] = i;
							arr[1] = j;
							return arr;
						}
					}
				}
			} else if (num < 20) {
				num -= 10;
				for (int j = 1; j <= 5; j++) {
					for (int i = 7; i <= 11; i++) {

						if (gamefield[i][j] >= 0) {
							num--;
						}
						if (num == 0) {
							arr[0] = i;
							arr[1] = j;
							return arr;
						}
					}
				}
			} else if (num < 30) {
				num -= 20;
				for (int j = 7; j <= 11; j++) {
					for (int i = 11; i >= 7; i--) {
						if (gamefield[i][j] >= 0) {
							num--;
						}
						if (num == 0) {
							arr[0] = i;
							arr[1] = j;
							return arr;
						}
					}
				}
			} else if (num < 40) {
				num -= 30;
				for (int j = 11; j >= 7; j--) {
					for (int i = 5; i >= 1; i--) {
						if (gamefield[i][j] >= 0) {
							num--;
						}
						if (num == 0) {
							arr[0] = i;
							arr[1] = j;
							return arr;
						}
					}
				}
			} else if (num < 45) {
				arr[1] = 6;
				num -= 40;
				for (int i = 2; i <= 5; i++) {
					num--;
					if (num == 0) {
						arr[0] = i;
					}
				}
			} else if (num < 49) {
				arr[0] = 6;
				num -= 44;
				for (int i = 2; i <= 5; i++) {
					num--;
					if (num == 0) {
						arr[1] = i;
					}
				}
			} else if (num < 53) {
				arr[1] = 6;
				num -= 48;
				for (int i = 10; i >= 7; i--) {
					num--;
					if (num == 0) {
						arr[0] = i;
					}
				}
			} else if (num <= 56) {
				arr[0] = 6;
				num -= 52;
				for (int i = 10; i >= 7; i--) {
					num--;
					if (num == 0) {
						arr[1] = i;
					}
				}
			}
			break;
		}
		}
		return arr;
	}

	/**
	 * updates the label for each player
	 */
	public void updateLabel() {
		for (Player p : players) {
			String temp = "\nActive: " + p.isActive();
			if (p.isActive()) {
				temp = "\nRolls: " + rolls + temp;
			} else {
				temp = "\nRolls: 0" + temp;
			}

			if (p.getNumber() == 1) {
				player1Label.setText(p.getName() + temp);
			} else if (p.getNumber() == 2) {
				player2Label.setText(p.getName() + temp);
			} else if (p.getNumber() == 3) {
				player3Label.setText(p.getName() + temp);
			} else if (p.getNumber() == 4) {
				player4Label.setText(p.getName() + temp);
			}
		}
	}

	/**
	 * Returns button number with given x,y
	 * </p>
	 * Return 0 = wrong input
	 * 
	 * @return
	 */
	public int giveFeld(int x, int y) {
		for (int k = 1; k <= 56; k++) {
			if (giveXY(k)[0] == x && giveXY(k)[1] == y) {
				return k;
			}
		}
		return 0;
	}

	public void buildField() {
		gamefield = new int[14][14];

		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 13; j++) {
				gamefield[j][i] = 0;
			}
		}

		gamefield[6][6] = -1;
		for (int k = 1; k <= 4; k++) {
			for (int l = 1; l <= 4; l++) {
				gamefield[l][k] = -1;
				gamefield[l + 7][k] = -1;
				gamefield[l][k + 7] = -1;
				gamefield[l + 7][k + 7] = -1;
			}
		}
		for (int y = 0; y <= 12; y++) {
			gamefield[y][0] = -1;
			gamefield[y][12] = -1;
			gamefield[0][y] = -1;
			gamefield[12][y] = -1;
		}
		for (int x = 2; x <= 5; x++) {
			gamefield[x][6] = 5;
			gamefield[x + 5][6] = 7;
			gamefield[6][x] = 6;
			gamefield[6][x + 5] = 8;
		}

	}

	public void rerollButtonClick() {
		SecureRandom rand = new SecureRandom();
		lastNumber = rand.nextInt(6) + 1;
		buttons[0].setText("" + lastNumber);
	}

	public void printField() {
		for (int y = 0; y < 13; y++) {
			for (int x = 0; x < 13; x++) {
				System.out.print(gamefield[x][y] + "\t");
			}
			System.out.println();
		}
	}

	public void restartButtonClick() {
		active = false;
		lastNumber = 0;
		atEnd = false;
		rolls = 3;
		players.clear();
		finished.clear();
		rollButton.setText("");
		popUp(false, true, "");
		updateLabel();
	}

	/**
	 * Pops up a new window with certain game information
	 */
	public void popUp(boolean win, boolean input, String info) {
		Stage popUp = new Stage();
		popUp.initModality(Modality.APPLICATION_MODAL);
		popUp.setMinHeight(300);
		popUp.setMinWidth(300);
		popUp.getIcons().add(new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/gamefield.png").toString()));
		Label label = new Label();
		label.setFont(new Font("Berlin Sans FB", 20));
		label.setTextAlignment(TextAlignment.CENTER);
		label.setStyle("-fx-text-fill: linear-gradient(to top, #ffcc00, #fbff02);");
		VBox vBox = new VBox();
		if (win) {
			popUp.setTitle("Game Over!");
			label.setText("All players reached the goal!\nThank you for playing, have a nice day!");
			vBox.getChildren().add(label);
		} else if (input) {
			popUp.setTitle("Enter Players!");
			popUp.setMinHeight(350);
			label.setText("Enter Name:");
			TextField text1 = new TextField();
			text1.setPromptText("Player 1");
			text1.setFont(label.getFont());

			Label label2 = new Label("Enter Name:");
			label2.setStyle(label.getStyle());
			label2.setFont(label.getFont());
			TextField text2 = new TextField();
			text2.setPromptText("Player 2");
			text2.setFont(label.getFont());

			label.setText("Enter Name:");
			TextField text3 = new TextField();
			text3.setPromptText("Player 3");
			text3.setFont(label.getFont());
			Label label3 = new Label("Enter Name:");
			label3.setStyle(label.getStyle());
			label3.setFont(label.getFont());

			Label label4 = new Label("Enter Name:");
			label4.setStyle(label.getStyle());
			label4.setFont(label.getFont());
			TextField text4 = new TextField();
			text4.setPromptText("Player 4");
			text4.setFont(label.getFont());
			Button button = new Button("Proceed");
			button.setFont(label.getFont());
			// button.setStyle(startButton.getStyle());
			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (!text1.getText().isBlank()) {
						player1 = new Player(text1.getText().trim(), 1, player1I);
						players.add(player1);
					}
					if (!text2.getText().isBlank()) {
						player2 = new Player(text2.getText().trim(), 2, player2I);
						players.add(player2);
					}
					if (!text3.getText().isBlank()) {
						player3 = new Player(text3.getText().trim(), 3, player3I);
						players.add(player3);
					}
					if (!text4.getText().isBlank()) {
						player4 = new Player(text4.getText().trim(), 4, player4I);
						players.add(player4);
					}
					popUp.close();
				}
			});
			vBox.getChildren().addAll(label, text1, label2, text2, label3, text3, label4, text4, button);
		} else {
			vBox.getChildren().add(label);
			popUp.setTitle("Attention");
			label.setText("" + info);
		}

		vBox.setStyle(
				"-fx-background-color: radial-gradient(center 50.0% 50.0%, radius 100.0%, #242424, #434343, #898989);");
		vBox.setAlignment(Pos.CENTER);
		Scene scene = new Scene(vBox);
		popUp.setScene(scene);
		popUp.showAndWait();
	}

	public void minButtonClick() {
		Stage tempStage = (Stage) mypane.getScene().getWindow();
		tempStage.setIconified(true);
	}

	public void closeButtonClick() {
		Stage temp = (Stage) mypane.getScene().getWindow();
		temp.close();
		System.exit(0);
	}
}
