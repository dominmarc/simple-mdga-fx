package de.ifdgmbh.mad.mdga.controller;

import javafx.scene.image.Image;

public class Player {

	//player name
	private String name;
	//number x of player
	private int number;
	//is player x currently active
	private boolean active;
	//is player x involved in game
	private boolean playing;
	//figures in storage
	private int startNum;
	//figures in goal
	private int finishNum;
	//image
	private Image pic;

	public Player(String name, int number, Image pic) {
		this.name = name;
		this.number = number;
		this.active = false;
		this.playing = true;
		this.startNum = 4;
		this.finishNum = 0;
		this.pic = pic;
	}

	public Image getPic() {
		return pic;
	}

	public void setPic(Image pic) {
		this.pic = pic;
	}

	public boolean isActive() {
		return active;
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getFinishNum() {
		return finishNum;
	}

	public void setFinishNum(int finishNum) {
		this.finishNum = finishNum;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
