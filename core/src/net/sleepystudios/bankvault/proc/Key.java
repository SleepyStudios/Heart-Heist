package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.MapHandler;

public class Key extends DecalProcObject {
	public Key(MapHandler mh) {
		super("key", mh);
	}
	
	protected void gen() {
		super.gen();
		if(!checkDistance(sprite.getX(), sprite.getY())) gen();
		rect = new Rectangle(rect.x+8, rect.y+8, sprite.getWidth()-16, sprite.getHeight()-16);
	}
	
	private boolean checkDistance(float x, float y) {
		Vector2 pos = new Vector2(x, y);
		Vector2 spawn = new Vector2(mh.spawnX, mh.spawnY);
		
		if(pos.dst(spawn)>mh.getHeight()/3) return true; 
		
		return false;
	}
}
