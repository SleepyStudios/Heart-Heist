package net.sleepystudios.bankvault;

import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.proc.ProcObject;

public class Camera extends ProcObject {
	public Camera(MapHandler mh) {
		super("camera", mh);
		hasCollision = false;
	}
	
	@Override
	protected void gen() {
		super.gen();
		checkPosition();
	}
	
	private void checkPosition() {
		boolean found = false;
		int s = mh.getTileSize();
		
		// up
		Rectangle nr = new Rectangle(rect.x, rect.y+s, rect.width, rect.height);
		for(Rectangle r : mh.rects) {
			if(r.x == nr.x && r.y == nr.y) {
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
}
