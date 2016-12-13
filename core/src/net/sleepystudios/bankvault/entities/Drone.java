package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.sleepystudios.bankvault.AnimGenerator;
import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.Bullet;
import net.sleepystudios.bankvault.Exclam;
import net.sleepystudios.bankvault.MapHandler;
import net.sleepystudios.bankvault.proc.ProcObject;

public class Drone extends Entity {
	public float angle, shownAngle;
	public int dir;
	Animation anim; Texture shadow;
	public Polygon vision;
	public Exclam e;
		
	public Drone(MapHandler mh) {
		super(mh);
		
		OX = OY = 2;
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
		float temp = angle - 90f;
		temp = temp % 360;
		if (temp < 0)  temp += 360;
		
		shownAngle += (temp - shownAngle) * 0.1f;
		
		animTmr+=Gdx.graphics.getDeltaTime();
		
		batch.draw(shadow, x+FW/2-shadow.getWidth()/2, y+FH/2-shadow.getHeight()/2);
		batch.draw(anim.getKeyFrame(animTmr, true), x, y, FW/2, FH/2, FW, FH, 1f, 1f, shownAngle);
		
		updateHitBox(x, y);
        update();
	}
	
	private float tmrChangeDir, tmrMove, tmrSpot, tmrShoot; 
	private boolean changeDest;
	int changes = 0;
	boolean seesPlayer;
	public void update() {
		Vector2 me = new Vector2(box.getX()+FW/2, box.getY()+FH/2);
		Vector2 player = new Vector2(mh.p.box.getX()+FW/2, mh.p.box.getY()+mh.p.FH/2);
		
		if(castRay(me, player, mh.p)) {
			if(!seesPlayer) {
				e = new Exclam(me.x-10, me.y);
				mh.p.e = new Exclam(player.x-10, player.y);
				seesPlayer = true;
			}
			
			tmrSpot+=Gdx.graphics.getDeltaTime();
			if(tmrSpot>=0.1) {
				if(me.dst(player)<maxRange) {
					float yd = y - mh.p.y;
					float xd = x - mh.p.x;
					float rad = (float) Math.atan2(yd, xd);
					angle = (float) Math.toDegrees(rad)+90f;
				}
			}
			
			if(castRay(me, player, mh.p) && Intersector.overlapConvexPolygons(vision, boxToPoly(mh.p.box, false)) && mh.p.animIndex!=mh.p.SHADOW && !BankVault.win) {
				tmrShoot+=Gdx.graphics.getDeltaTime();
				if(tmrShoot>=0.25) {
					mh.bullets.add(new Bullet(new float[]{me.x-12, me.y-12, player.x-12, player.y-12}, mh));
					tmrShoot = 0;
				}
			}
		} else {
			seesPlayer = false;
			tmrSpot = 0;
		}
		
		if(changeDest && !seesPlayer) {
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
		if(tmrMove>=0.01f && !changeDest && !seesPlayer) {
			float speed = 100 * Gdx.graphics.getDeltaTime();
			
			angle = dir*90f;
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
	
	int maxRange = 240;
	public boolean castRay(Vector2 me, Vector2 player, Player p) {
		if(!BankVault.camera.frustum.pointInFrustum(new Vector3(me.x, me.y, 0))) return false;
		
		if(p.animIndex==p.SHADOW) return false;
		
		if(me.dst(player)>maxRange && !seesPlayer) return false;
		
		for(Rectangle r : mh.rects) {
			if(Intersector.intersectSegmentPolygon(me, player, boxToPoly(r, false))) return false;
		}
		
		for(ProcObject o : mh.procObjs) {
			if(o.hasCollision && Intersector.intersectSegmentPolygon(me, player, boxToPoly(o.rect, false))) return false;
		}
		
		return true;
	}
	
	public Polygon boxToPoly(Rectangle box, boolean vision) {
    	if(box==null) return null;
    	
    	Polygon poly = new Polygon(new float[] {
				box.getX(), box.getY(), 
				box.getX(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()});
    	
    	
    	if(vision) {
    		poly.setOrigin(this.box.getX()+this.box.getWidth()/2, this.box.getY()+this.box.getHeight()/2);
    		poly.rotate(shownAngle+180f);
    	}
    	
		return poly; 
    }
	
	@Override
	public void updateHitBox(float x, float y) {
		super.updateHitBox(x, y);
		vision = boxToPoly(new Rectangle(x+box.width, box.y, maxRange, box.height), true);
	}
}
