package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.MapHandler;
import net.sleepystudios.bankvault.proc.ProcObject;

public class Entity {
	protected MapHandler mh;
	float x, y;
	public Rectangle box;
	float animSpeed = 0.125f, animTmr;
	
	public Entity(MapHandler mh) {
		this.mh = mh;
	}
	
	protected boolean isBlocked(float x, float y) {
		updateHitBox(x, y);
		if(x<0 || x>mh.getWidth()-mh.getTileSize() || y<0 || y>mh.getHeight()-mh.getTileSize()) return true;
		
		for(Rectangle r : mh.rects) {
			if(Intersector.overlaps(box, r)) return true;
		}
		
		for(ProcObject o : mh.procObjs) {
			if(o.rect!=null && o.hasCollision && Intersector.overlaps(box, o.rect)) return true;
		}
		
		return false;
	}
	
	protected void move(float x, float y) {
		updateHitBox(x, y);
		
		this.x = x; 
		this.y = y;
	}
	
	int OX = 0, OY = 0, FW = 32, FH = 32;
	public void updateHitBox(float x, float y) {
		box = new Rectangle(x+OX, y+OY, FW-(OX*2), FH-(OY*2));
	}
}
