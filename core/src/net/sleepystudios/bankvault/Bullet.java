package net.sleepystudios.bankvault;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    float startX, startY, destX, destY, startSpeed, dx, dy, rad, angle;
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
        
        float speed = 30f;
        dx = (float) MathUtils.sin(rad) * speed;
        dy = (float) MathUtils.cos(rad) * speed;
    }
    
    public void move(MapHandler mh) {
        float x = location.x;
        float y = location.y;

        if(!isBlocked(x + dx, y - dy, mh)) {
            x += dx;
            y -= dy;
            location.set(x, y);
            
            mh.tracers.add(new TracerBit(x, y, angle, this));
        } else {
        	exists = false;
        }
    }

    // blocking
    Rectangle box;
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
}
