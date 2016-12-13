package net.sleepystudios.bankvault;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import net.sleepystudios.bankvault.proc.ProcObject;

public class Bullet {
    float startX, startY, destX, destY, dx, dy, rad, angle, startSpeed, speed;
    Vector2 location;
    TextureRegion bullet;
	public boolean exists = true;
	private ShapeRenderer sr;
	MapHandler mh;
    
    public Bullet(float data[], MapHandler mh) {
    	this.mh = mh;
    	
        startX = data[0];
        startY = data[1];
        destX = data[2];
        destY = data[3];
        
        location = new Vector2(startX, startY);
        angle = (float) (Math.toDegrees(Math.atan2(startY-destY, startX-destX)));
        
        startSpeed = speed = 20f;
        
        recalculateVector();
        
        bullet = new TextureRegion(new Texture("bullet.png"));
    	sr = new ShapeRenderer();
    }
    
    public void render(SpriteBatch batch) {
    	if(!exists) return;
    	
    	batch.draw(bullet, location.x, location.y, bullet.getRegionWidth()/2, 
    			bullet.getRegionHeight()/2, bullet.getRegionWidth(), bullet.getRegionHeight(), 1f, 1f, (float) angle);
    	
    	// hitboxes
        if(BankVault.showHitBoxes && boxToPoly()!=null) {
	        batch.end();
	        
	        sr.begin(ShapeType.Line);
	        sr.setProjectionMatrix(BankVault.camera.combined);
	        sr.setColor(Color.RED);
	        sr.polygon(boxToPoly().getTransformedVertices());
	        sr.end();
	        
	        batch.begin();
        }
        
        move(mh);
    }
    
    private void recalculateVector() {
        rad = (float) (Math.atan2(destX - startX, startY - destY));
        
        dx = (float) MathUtils.sin(rad) * speed;
        dy = (float) MathUtils.cos(rad) * speed;
    }
    
    public void move(MapHandler mh) {
        float x = location.x;
        float y = location.y;
        
        if(speed<=0) {
        	exists = false;
        	return;
        }

        if(!isBlocked(x + dx, y - dy, mh)) {
            x += dx;
            y -= dy;
            location.set(x, y);
            
            mh.tracers.add(new TracerBit(x, y, angle, this));
        } else {
        	speed-=2f;
        	recalculateVector();
        }
    }

    // blocking
    Rectangle box;
    boolean playedSound;
    public boolean isBlocked(float x, float y, MapHandler mh) {
        // create the bullet rectangle
        box = new Rectangle(x+3, y-2, 18, 6);
        
        // get the map properties to find the height/width, etc
    	int mapPixelWidth = mh.getWidth();
    	int mapPixelHeight = mh.getHeight();

        // outside of map?
        if (x < 0 || y < 0 || x > mapPixelWidth || y > mapPixelHeight) {
            return true;
        }
        
        for(Rectangle r : mh.rects) {
			if(Intersector.overlapConvexPolygons(boxToPoly(), makePoly(r))) return true;
		}
		
		for(ProcObject o : mh.procObjs) {
			if(o.rect!=null && o.hasCollision && Intersector.overlapConvexPolygons(boxToPoly(), makePoly(o.rect))) return true;
		}
		
		if(Intersector.overlaps(box, mh.p.box) && !BankVault.win) {
			float delay = 0.1f;
			
			if(!playedSound) {
				BankVault.playSound("hit");
				playedSound = true;
			}
			
			Timer.schedule(new Task(){
			    @Override
			    public void run() {
			    	BankVault.endCircle.setColor(Color.BLACK);
			    	BankVault.end = true;
			    }
			}, delay);
			return true;
		}
        
        return false;
    }
    
    public Polygon boxToPoly() {
    	if(box==null) return null;
    	
    	Polygon poly = new Polygon(new float[] {
				box.getX(), box.getY(), 
				box.getX(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()});
		poly.setOrigin(box.getX()+4-3, box.getY()+2+1);
		poly.rotate(angle);
		
		return poly;
    }
    
    public Polygon makePoly(Rectangle box) {
    	Polygon poly = new Polygon(new float[] {
				box.getX(), box.getY(), 
				box.getX(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()});
		
		return poly;
    }
}
