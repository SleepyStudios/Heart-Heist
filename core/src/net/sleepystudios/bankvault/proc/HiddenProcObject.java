package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.MapHandler;

public class HiddenProcObject extends ProcObject {
	public HiddenProcObject(MapHandler mh) {
		super("hidden", mh);
		
		hasCollision = false;
	}
	
	protected void gen() {
		float x = mh.spawnX-mh.getTileSize()/2;
		float y = mh.spawnY-mh.getTileSize();
		
		rect = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
		sprite.setPosition(x, y);
		sprite.setAlpha(0f);
	}
}
