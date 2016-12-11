package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.MapHandler;

public class Drone extends Entity {
	public float x, y, rad, shownAngle;
	public float dx, dy;
	
	public Drone(MapHandler mh) {
		super(mh);
		
		FW = 32;
		FH = 32;
		OX = 6;
		OY = 6;
		
		sprite = new Sprite(new Texture("drone.png"));
		
		x = snap(BankVault.rand(0, mh.getWidth()-sprite.getWidth()));
		y = snap(BankVault.rand(0, mh.getHeight()-sprite.getHeight()));
		move(x, y);
		
		setDestination();
	}
	
	public void render(SpriteBatch batch) {
		float angle = (MathUtils.radiansToDegrees*rad) + 180f;
		shownAngle += (angle - shownAngle) * 0.1f;
		
		sprite.setRotation(shownAngle);
		sprite.draw(batch);
		
        update();
	}
	
	private float tmrChangeDir, tmrMove; 
	private boolean changeDest;
	private int boundary = 32, nextX, nextY;
	public void update() {
		if(changeDest) {
			tmrChangeDir+=Gdx.graphics.getDeltaTime();
			if(tmrChangeDir>=1) {
				changeDest = false;
				tmrChangeDir = 0;
			} else if((int) tmrChangeDir==0 || (int) tmrChangeDir==2) {
				rad += BankVault.rand(-0.2f, 0.2f);
			}
			System.out.println("changing");
		}
		
		tmrMove+=Gdx.graphics.getDeltaTime();
		if(tmrMove>=0.01f && !changeDest) {
	        x+=dx;
	        y-=dy;
	        
	        //if(!isBlocked(x+dx, y-dy)) {
	        	move(x+dx, y-dy);
	        //} else {
	        //	changeDest = true;
	        //}
	        
			// need to change?
	        if(!changeDest) {
	        	if(((int) x == nextX && (int) y == nextY)) {
		        	changeDest = true;
		        } else if(x+dx<boundary || x+dx>mh.getWidth()-boundary*2 || y-dy<boundary || y-dy>mh.getHeight()-boundary*2) {
		        	changeDest = true;
		        }
	        }
			
			tmrMove = 0;
		}
	}
	
	private void setDestination() {
		nextX = BankVault.rand(boundary, mh.getWidth()-boundary*2);
		nextY = BankVault.rand(boundary, mh.getHeight()-boundary*2);
		rad = (float) (Math.atan2(nextX - x, y - nextY));
		
		float speed = 1.2f;
		dx = (float) Math.sin(rad) * speed;
        dy = (float) Math.cos(rad) * speed;
	}
	
	private int snap(float num) {
		int s = mh.getTileSize();
		return Math.round(num/s) * s;
	}
}
