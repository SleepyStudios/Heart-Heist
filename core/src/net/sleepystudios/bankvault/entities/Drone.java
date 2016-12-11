package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.sleepystudios.bankvault.AnimGenerator;
import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.MapHandler;

public class Drone extends Entity {
	public float angle, shownAngle;
	public int dir;
	Animation anim;
		
	public Drone(MapHandler mh) {
		super(mh);
		
		OX = OY = 6;
		FW = 24;
		FH = 22;
		
		anim = new Animation(animSpeed, AnimGenerator.gen("drone.png", FW, FH));
		
		while(isBlocked(x, y)) {
			x = snap(BankVault.rand(0, mh.getWidth()-FW));
			y = snap(BankVault.rand(0, mh.getHeight()-FH));
		}
		dir = BankVault.rand(0, 3);
		move(x, y);
		
		setDestination();
	}
	
	public void render(SpriteBatch batch) {
		shownAngle += (angle - shownAngle) * 0.15f;
		
		animTmr+=Gdx.graphics.getDeltaTime();
		batch.draw(anim.getKeyFrame(animTmr), x, y, FW/2, FH/2, FW, FH, 1f, 1f, shownAngle-90f);
		
        update();
	}
	
	private float tmrChangeDir, tmrMove; 
	private boolean changeDest;
	int changes = 0;
	public void update() {
		if(changeDest) {
			tmrChangeDir+=Gdx.graphics.getDeltaTime();
			if(tmrChangeDir>=3) {
				setDestination();
				changeDest = false;
				tmrChangeDir = 0;
			} else if((int) tmrChangeDir==0 && changes==0) {
				angle+=180f;//BankVault.rand(-30f, 30f);
				changes++;
			} else if((int) tmrChangeDir==1 && changes==1) {
				angle+=BankVault.rand(-30f, 30f);
				changes++;
			} else if((int) tmrChangeDir==2 && changes==2) {
				angle+=BankVault.rand(-30f, 30f);
				changes = 0;
			}
		}
		
		tmrMove+=Gdx.graphics.getDeltaTime();
		if(tmrMove>=0.01f && !changeDest) {
			float speed = 100 * Gdx.graphics.getDeltaTime();
			
			switch(dir) {
			case 0:
				if(!isBlocked(x, y+speed)) {
					move(x, y+speed);
				} else {
					changeDest = true;
				}
				break;
			case 1:
				if(!isBlocked(x-speed, y)) {
					move(x-speed, y);
				} else {
					changeDest = true;
				}
				break;
			case 2:
				if(!isBlocked(x, y-speed)) {
					move(x, y-speed);
				} else {
					changeDest = true;
				}
				break;
			case 3:
				if(!isBlocked(x+speed, y)) {
					move(x+speed, y);
				} else {
					changeDest = true;
				}
			}

			tmrMove = 0;
		}
	}
	
	private void setDestination() {
		dir = BankVault.rand(0, 3);
		angle = dir*90f;
	}
	
	private int snap(float num) {
		int s = mh.getTileSize();
		return Math.round(num/s) * s;
	}
}
