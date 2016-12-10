package net.sleepystudios.bankvault;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BankVault extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	MapHandler mh;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		// camera
        float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(w, h);
		//mh = new MapHandler(map);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		
		
		batch.end();
	}
	
	public void updateCam(float x, float y) {
    	// get the map properties to find the height/width, etc
    	int mapPixelWidth = 1280; //mapHandler.getWidth();
    	int mapPixelHeight = 720; //mapHandler.getHeight();
    	
    	float minCameraX = camera.zoom * (camera.viewportWidth / 2);
        float maxCameraX = (mapPixelWidth) - minCameraX;
        float minCameraY = camera.zoom * (camera.viewportHeight / 2);
        float maxCameraY = (mapPixelHeight) - minCameraY;
        
        camera.position.set(Math.min(maxCameraX, Math.max(x, minCameraX)), Math.min(maxCameraY, Math.max(y, minCameraY)), 0);
    }
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
