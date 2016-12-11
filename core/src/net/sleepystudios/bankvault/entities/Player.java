package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.sleepystudios.bankvault.MapHandler;

public class Player extends Entity {
	OrthographicCamera camera;
	
	public Player(OrthographicCamera camera, MapHandler mh) {
		super(mh);
		this.camera = camera;
		
		sprite = new Sprite(new Texture("player.png"));
		move(x, y);
	}
	
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
		
		// movement
		float speed = 120f * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if(!isBlocked(x, y+speed)) move(x, y + speed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
        	 if(!isBlocked(x-speed, y)) move(x - speed, y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
        	 if(!isBlocked(x, y-speed)) move(x, y - speed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
        	 if(!isBlocked(x+speed, y)) move(x + speed, y);
        }
	}
	
	@Override
	protected void move(float x, float y) {
		super.move(x, y);
		updateCam();
	}
	
	private void updateCam() {
    	// get the map properties to find the height/width, etc
    	int mapPixelWidth = mh.getWidth();
    	int mapPixelHeight = mh.getHeight();
    	
    	float minCameraX = camera.zoom * (camera.viewportWidth / 2);
        float maxCameraX = (mapPixelWidth) - minCameraX;
        float minCameraY = camera.zoom * (camera.viewportHeight / 2);
        float maxCameraY = (mapPixelHeight) - minCameraY;
        
        camera.position.set(Math.min(maxCameraX, Math.max(x, minCameraX)), Math.min(maxCameraY, Math.max(y, minCameraY)), 0);
    }
}
