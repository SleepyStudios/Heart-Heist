package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.MapHandler;

public class Camera extends DecalProcObject {
	ShapeRenderer sr;
	Sprite laser;
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
	
	float tmrOff, nextOff = BankVault.rand(3f, 10f), holdTime = BankVault.rand(2f, 3f), tmrBlink, numBlinks;
	boolean off, blinking, blink;
	@Override
	public void render(SpriteBatch batch) {
		if(off) {
			tmrOff+=Gdx.graphics.getDeltaTime();
			if(tmrOff>=holdTime) {
				blinking = true;
				tmrOff = 0;
			}
		} else {
			tmrOff+=Gdx.graphics.getDeltaTime();
			if(tmrOff>=nextOff) {
				blinking = true;
				tmrOff = 0;
			}
		}
		
		if(blinking) {
			tmrBlink+=Gdx.graphics.getDeltaTime();
			if(tmrBlink>=0.025f) {
				blink = !blink;
				numBlinks++;
				
				if(numBlinks>=10) {
					blinking = false;
					off = !off;
					numBlinks = 0;
				}
				
				tmrBlink = 0;
			}
		}
		
		if(blinking) {
			if(!blink) laser.draw(batch);
		} else {
			if(!off) laser.draw(batch);
		}
		
		super.render(batch);
	}
	
	public boolean visible() {
		if(blinking) {
			if(!blink) return true;
		} else {
			if(!off) return true;
		}
		return false;
	}
	
	private void makeLaser() {
		boolean found = false;
		float offset = 32;
		Rectangle lRect;
		
		laser = new Sprite(new Texture("laser.png"));
		laser.setOrigin(sprite.getOriginX(), sprite.getOriginY());
		laser.setRotation(sprite.getRotation());
		laser.setPosition(sprite.getX(), sprite.getY());
		
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
				
				for(ProcObject o : mh.procObjs) {
					if(o.hasCollision && Intersector.overlaps(o.rect, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			laser.setPosition(sprite.getX(), sprite.getY()+sprite.getHeight()+32-offset);
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
				
				for(ProcObject o : mh.procObjs) {
					if(o.hasCollision && Intersector.overlaps(o.rect, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			laser.setPosition(sprite.getX(), sprite.getY()-sprite.getHeight()-32+offset);
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
				
				for(ProcObject o : mh.procObjs) {
					if(o.hasCollision && Intersector.overlaps(o.rect, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			laser.setPosition(sprite.getX()-sprite.getWidth()-32+offset, sprite.getY());
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
				
				for(ProcObject o : mh.procObjs) {
					if(o.hasCollision && Intersector.overlaps(o.rect, lRect)) {
						found = true;
						break;
					}
				}
				
				offset+=32;
			}
			laser.setPosition(sprite.getX()+sprite.getWidth()+32-offset, sprite.getY());
		}
		
		laserD = offset;
		laser.setSize(laser.getWidth(), laserD);
		makeLaserPoly();
	}
	
	public Polygon laserPoly;
	private void makeLaserPoly() {
		laserPoly = new Polygon(new float[] {
				laser.getX()+15, laser.getY(), 
				laser.getX()+15, laser.getY()+laser.getHeight(),
				laser.getX()+15+2, laser.getY()+laser.getHeight(),
				laser.getX()+15+2, laser.getY()});
		
		if(laser.getRotation()==0f || laser.getRotation()==180f) {
			laserPoly.setOrigin(laser.getX()+laser.getWidth()/2, laser.getY()+32);
		} else if(laser.getRotation()==90f) {
			laserPoly.setOrigin(laser.getX()+32, laser.getY()+32);
		} else if(laser.getRotation()==-90f) {
			laserPoly.setOrigin(laser.getX(), laser.getY()+32);
		}
		laserPoly.rotate(laser.getRotation());
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
		
		// make sure they're away from other cameras
		for(ProcObject o : mh.procObjs) {
			if(!o.equals(this)) {
				Vector2 cPos = new Vector2(o.rect.x, o.rect.y);
				
				if(o instanceof Camera) {
					if(pos.dst(cPos)<=96) return false;
				} else {
					if(o.hasCollision) if(pos.dst(cPos)<=32) return false;
				}
			}
		}
		
		return false;
	}
}
