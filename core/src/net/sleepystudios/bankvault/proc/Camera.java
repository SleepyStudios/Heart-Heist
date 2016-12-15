package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.MapHandler;

public class Camera extends DecalProcObject {
	ShapeRenderer sr;
	float laserD;
	
	public Camera(MapHandler mh) {
		super("camera", mh);
		sr = new ShapeRenderer();
	}
	
	@Override
	protected void gen() {
		super.gen();
		checkPosition();
		if(!checkDistance(sprite.getX(), sprite.getY())) gen();
		makeLaser();
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		batch.end();
		sr.setProjectionMatrix(BankVault.camera.combined);
		sr.begin(ShapeType.Filled);
		
		sr.setColor(Color.RED);
		if(sprite.getRotation()==0f) {
			sr.line(new Vector2(rect.x+rect.width/2, rect.y+rect.height), new Vector2(rect.x+rect.width/2, rect.y+rect.height-laserD));
		} else if(sprite.getRotation()==180f) {
			sr.line(new Vector2(rect.x+rect.width/2, rect.y-rect.height), new Vector2(rect.x+rect.width/2, rect.y-rect.height+laserD));
		} else if(sprite.getRotation()==90f) {
			sr.line(new Vector2(rect.x-rect.width, rect.y+rect.height/2), new Vector2(rect.x-rect.width+laserD, rect.y+rect.height/2));
		} else if(sprite.getRotation()==-90f) {
			sr.line(new Vector2(rect.x+rect.width, rect.y+rect.height/2), new Vector2(rect.x+rect.width-laserD, rect.y+rect.height/2));
		}
		
		sr.end();
		batch.begin();
	}
	
	private void makeLaser() {
		boolean found = false;
		float offset = 32;
		Rectangle lRect;
		
		switch((int) sprite.getRotation()) {
		case 0:
			// up
			while(!found) {
				lRect = new Rectangle(rect.x, rect.y-offset, rect.width, rect.height);
				
				for(Rectangle r : mh.rects) {
					if(Intersector.overlaps(r, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			break;
		case 180:
			// down 
			while(!found) {
				lRect = new Rectangle(rect.x, rect.y+offset, rect.width, rect.height);
				
				for(Rectangle r : mh.rects) {
					if(Intersector.overlaps(r, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			break;
		case 90:
			// left 
			while(!found) {
				lRect = new Rectangle(rect.x+offset, rect.y, rect.width, rect.height);
				
				for(Rectangle r : mh.rects) {
					if(Intersector.overlaps(r, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			break;
		case -90:
			// right 
			while(!found) {
				lRect = new Rectangle(rect.x-offset, rect.y, rect.width, rect.height);
				
				for(Rectangle r : mh.rects) {
					if(Intersector.overlaps(r, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
		}
		
		laserD = offset;
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
