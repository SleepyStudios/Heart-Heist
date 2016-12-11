package net.sleepystudios.bankvault;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MapHandler {
	public TiledMap map; 
	private TiledMapRenderer mapRenderer;
	private int[] layers = {0}, fringeLayers = {3};
	public ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
	
	public MapHandler(TiledMap map) {
		this.map = map;
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		
		loadRects();
	}
	
	private void loadRects() {
		int s = getTileSize();
		
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        for(int x = 0; x < layer.getWidth();x++){
            for(int y = 0; y < layer.getHeight();y++){
                TiledMapTileLayer.Cell cell = layer.getCell(x,y);
                Object property = cell.getTile().getProperties().get("wall");
                if(property != null){
                    rects.add(new Rectangle(x*s, y*s, s, s));
                }
            }
        }
	}
	
	public void render(OrthographicCamera camera) {
		mapRenderer.setView(camera);
        mapRenderer.render(layers);
	}
	
	public void renderFringe(OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render(fringeLayers);
	}
	
	public int getTileSize() {
		return map.getProperties().get("tilewidth", Integer.class);
	}
	
	public int getWidth() {
		return map.getProperties().get("width", Integer.class) * getTileSize();
	}
	
	public int getHeight() {
		return map.getProperties().get("height", Integer.class) * getTileSize();
	}
}
