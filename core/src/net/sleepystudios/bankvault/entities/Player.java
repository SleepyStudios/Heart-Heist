package net.sleepystudios.bankvault.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.sleepystudios.bankvault.AnimGenerator;
import net.sleepystudios.bankvault.MapHandler;

public class Player extends Entity {
	OrthographicCamera camera;
	final int IDLE = 0, UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4, SHADOW = 5;
	Animation anim[] = new Animation[6];
	int animIndex;
	
	public Player(OrthographicCamera camera, MapHandler mh) {
		super(mh);
		this.camera = camera;
		
		OX = OY = 10;
		
		anim[IDLE] = new Animation(animSpeed, AnimGenerator.gen("shadow_idle.png", FW, FH));
		anim[UP] = new Animation(animSpeed, AnimGenerator.gen("shadow_up.png", FW, FH));
		anim[DOWN] = new Animation(animSpeed, AnimGenerator.gen("shadow_down.png", FW, FH));
		anim[LEFT] = new Animation(animSpeed, AnimGenerator.gen("shadow_left.png", FW, FH));
		anim[RIGHT] = new Animation(animSpeed, AnimGenerator.gen("shadow_right.png", FW, FH));
		anim[SHADOW] = new Animation(animSpeed, AnimGenerator.gen("shadow_move.png", FW, FH));
		
		for(int i=0; i<anim.length-1; i++) anim[i].setPlayMode(PlayMode.LOOP_PINGPONG);
		
		shownX = x = mh.spawnX+1;
		y = mh.spawnY;
		shownY = y - mh.getTileSize()*4;
		
		move(x, y);
	}
	
	float shownX, shownY, tmrShadow, tmrCanShadow;
	boolean reversed, canShadow = true;
	public void render(SpriteBatch batch) {
		camera.position.set(shownCamX+=(camX-shownCamX)*0.08f, shownCamY+=(camY-shownCamY)*0.08f, 0);
		
		animTmr += Gdx.graphics.getDeltaTime();
		
        batch.draw(anim[animIndex].getKeyFrame(animTmr, animIndex<=RIGHT), shownX+=(x-shownX)*0.2f, shownY+=(y-shownY)*0.2f);
	
        // shadow form
        if(animIndex==SHADOW) {
        	tmrShadow+=Gdx.graphics.getDeltaTime();
        	if(tmrShadow>=3) {
        		if(!reversed) {
        			animTmr = 0;
        			reversed = true;
        		} else {
        			if(animTmr>=animSpeed) {
        				animTmr = 0;
                    	animIndex = IDLE;
                    	reversed = false;
                    	tmrShadow = 0;
        			}
        		}
            }
        } else {
        	tmrShadow = 0;
        	
        	if(!canShadow) {
            	tmrCanShadow+=Gdx.graphics.getDeltaTime();
            	if(tmrCanShadow>=5) {
            		canShadow = true;
            		tmrCanShadow = 0;
            	}
            }
        }
        
		// movement
        if(!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.S) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
        	if(animIndex!=SHADOW) animIndex = IDLE;
        }
        
    	float speed = 150f * Gdx.graphics.getDeltaTime();
    	
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

	public void goShadow() {
		if(animIndex==SHADOW || !canShadow) return;
		
		animTmr = 0;
		animIndex = SHADOW;
		canShadow = false;
	}
	
	@Override
	protected void move(float x, float y) {
		super.move(x, y);
		updateCam();
	}
	
	float shownCamX, shownCamY, camX, camY;
	boolean firstUpdate;
	private void updateCam() {
    	// get the map properties to find the height/width, etc
    	int mapPixelWidth = mh.getWidth();
    	int mapPixelHeight = mh.getHeight();
    	
    	float minCameraX = camera.zoom * (camera.viewportWidth / 2);
        float maxCameraX = (mapPixelWidth) - minCameraX;
        float minCameraY = camera.zoom * (camera.viewportHeight / 2);
        float maxCameraY = (mapPixelHeight) - minCameraY;
        
        camX = (int) Math.min(maxCameraX, Math.max(x, minCameraX));
        camY = (int) Math.min(maxCameraY, Math.max(y, minCameraY));
        
        if(!firstUpdate) {
            shownCamX = camX;
        	shownCamY = camY;
        	camera.position.set(shownCamX, shownCamY, 0);
        	
        	firstUpdate = true;
        }
    }
}
