package net.sleepystudios.bankvault;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.entities.Drone;
import net.sleepystudios.bankvault.proc.Crate;
import net.sleepystudios.bankvault.proc.Junk;
import net.sleepystudios.bankvault.proc.ProcObject;

public class MapHandler {
	public TiledMap map; 
	private TiledMapRenderer mapRenderer;
	private int[] layers = {0}, fringeLayers = {3};
	public ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
	public ArrayList<ProcObject> procObjs = new ArrayList<ProcObject>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	
	public MapHandler(TiledMap map) {
		this.map = map;
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		
		loadRects();
		gen();
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
	
	public void gen() {
		int size[] = {10, 3};
		
		procObjs.clear();
		for(int i=0; i<size[0]; i++) {
			if(BankVault.rand(0,1)==0) {
				procObjs.add(new Crate(this));
			} else {
				procObjs.add(new Junk(this));
			}
		}
		
		drones.clear();
		for(int i=0; i<size[1]; i++) {
			drones.add(new Drone(this));
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
