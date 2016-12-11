package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.sleepystudios.bankvault.AnimGenerator;
import net.sleepystudios.bankvault.MapHandler;

public class Player extends Entity {
	OrthographicCamera camera;
	final int IDLE = 0, UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4;
	Animation anim[] = new Animation[5];
	int animIndex;
	float animSpeed = 0.1f, animTmr;
	
	public Player(OrthographicCamera camera, MapHandler mh) {
		super(mh);
		this.camera = camera;
		
		OX = OY = 10;
		
		sprite = new Sprite(new Texture("player.png"));
		
		anim[IDLE] = new Animation(animSpeed, AnimGenerator.gen("shadow_idle.png", FW, FH));
		anim[IDLE].setPlayMode(PlayMode.LOOP_PINGPONG);
		anim[UP] = new Animation(animSpeed, AnimGenerator.gen("shadow_up.png", FW, FH));
		anim[DOWN] = new Animation(animSpeed, AnimGenerator.gen("shadow_down.png", FW, FH));
		anim[LEFT] = new Animation(animSpeed, AnimGenerator.gen("shadow_left.png", FW, FH));
		anim[RIGHT] = new Animation(animSpeed, AnimGenerator.gen("shadow_right.png", FW, FH));
		
		x = 9*32;
		y = mh.getHeight()-8*32;
		
		move(x, y);
	}
	
	public void render(SpriteBatch batch) {
		//sprite.draw(batch);
		
		animTmr += Gdx.graphics.getDeltaTime();
        batch.draw(anim[animIndex].getKeyFrame(animTmr, true), x, y);
		
		// movement
        if(!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.S) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
        	animIndex = IDLE;
        }
        
		float speed = 120f * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
        	animIndex = UP;
            if(!isBlocked(x, y+speed)) move(x, y + speed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
        	animIndex = LEFT;
        	if(!isBlocked(x-speed, y)) move(x - speed, y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
        	animIndex = DOWN;
        	if(!isBlocked(x, y-speed)) move(x, y - speed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
        	animIndex = RIGHT;
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
