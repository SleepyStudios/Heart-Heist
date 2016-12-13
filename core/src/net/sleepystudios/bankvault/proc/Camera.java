package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.MapHandler;

public class Camera extends DecalProcObject {
	ShapeRenderer sr;
	
	public Camera(MapHandler mh) {
		super("camera", mh);
		sr = new ShapeRenderer();
	}
	
	@Override
	protected void gen() {
		super.gen();
		checkPosition();
		if(!checkDistance(sprite.getX(), sprite.getY())) gen();
	}
	
	private void checkPosition() {
		boolean found = false;
		int s = mh.getTileSize();
		
		// up
		Rectangle nr = new Rectangle(rect.x, rect.y+s, rect.width, rect.height);
		for(Rectangle r : mh.rects) {
			if(r.x == nr.x && r.y == nr.y) {
				sprite.setRotation(0);
				found = true;
				break;
			}
		}
		
		// down
		nr = new Rectangle(rect.x, rect.y-s, rect.width, rect.height);
		for(Rectangle r : mh.rects) {
			if(r.x == nr.x && r.y == nr.y) {
				sprite.setRotation(180f);
				found = true;
				break;
			}
		}
		
		// left
		nr = new Rectangle(rect.x-s, rect.y, rect.width, rect.height);
		for(Rectangle r : mh.rects) {
			if(r.x == nr.x && r.y == nr.y) {
				sprite.setRotation(90f);
				found = true;
				break;
			}
		}
		
		// right
		nr = new Rectangle(rect.x+s, rect.y, rect.width, rect.height);
		for(Rectangle r : mh.rects) {
			if(r.x == nr.x && r.y == nr.y) {
				sprite.setRotation(-90f);
				found = true;
				break;
			}
		}
		
		if(!found) gen();
	}
	
	private boolean checkDistance(float x, float y) {
		Vector2 pos = new Vector2(x, y);
		Vector2 spawn = new Vector2(mh.spawnX, mh.spawnY);
		
		if(pos.dst(spawn)>100) return true; 
		
		return false;
	}
}
