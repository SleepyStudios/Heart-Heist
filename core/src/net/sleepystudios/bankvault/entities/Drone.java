package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.AnimGenerator;
import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.MapHandler;

public class Drone extends Entity {
	public float angle, shownAngle;
	public int dir;
	Animation anim; Texture shadow;
		
	public Drone(MapHandler mh) {
		super(mh);
		
		OX = OY = 6;
		FW = 24;
		FH = 22;
		
		anim = new Animation(animSpeed, AnimGenerator.gen("drone.png", FW, FH));
		shadow = new Texture("drone_shadow.png");
		
		while(isBlocked(x, y) || !checkDistance(x, y)) {
			x = BankVault.snap(BankVault.rand(0, mh.getWidth()-FW));
			y = BankVault.snap(BankVault.rand(0, mh.getHeight()-FH));
		}
		dir = BankVault.rand(0, 3);
		move(x, y);
		
		setDestination();
	}
	
	
	private boolean checkDistance(float x, float y) {
		Vector2 pos = new Vector2(x, y);
		Vector2 spawn = new Vector2(mh.spawnX, mh.spawnY);
		
		if(pos.dst(spawn)>150) return true; 
		
		return false;
	}
	
	public void render(SpriteBatch batch) {
		shownAngle += (angle - shownAngle) * 0.15f;
		
		animTmr+=Gdx.graphics.getDeltaTime();
		
		batch.draw(shadow, x+FW/2-shadow.getWidth()/2, y+FH/2-shadow.getHeight()/2);
		batch.draw(anim.getKeyFrame(animTmr), x, y, FW/2, FH/2, FW, FH, 1f, 1f, shownAngle-90f);
		
        update();
	}
	
	private float tmrChangeDir, tmrMove; 
	private boolean changeDest;
	int changes = 0;
	public void update() {
		if(changeDest) {
			float maxA = 60f;
			
			tmrChangeDir+=Gdx.graphics.getDeltaTime();
			if(tmrChangeDir>=3) {
				setDestination();
				changeDest = false;
				tmrChangeDir = 0;
			} else if((int) tmrChangeDir==0 && changes==0) {
				angle+=180f;
				changes++;
			} else if((int) tmrChangeDir==1 && changes==1) {
				angle+=BankVault.rand(-maxA, maxA);
				changes++;
			} else if((int) tmrChangeDir==2 && changes==2) {
				angle+=BankVault.rand(-maxA, maxA);
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
	
	public boolean castRay(Player p) {
		Vector2 me = new Vector2(box.getX()+box.getWidth()/2, box.getY()+box.getHeight()/2);
		Vector2 player = new Vector2(p.box.getX()+p.box.getWidth()/2, p.box.getX()+p.box.getWidth()/2);
		
//		for(int i=0; i<Builder.mainGame.blocks.size(); i++) {
//			Block b = Builder.mainGame.blocks.get(i);
//			
//			// check for collision
//			if(b.poly!=null && b.solid && Intersector.intersectSegmentPolygon(player, block, b.poly)) {
//				return false;
//			}
//		}
		return true;
	}
}
