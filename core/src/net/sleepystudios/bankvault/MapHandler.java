package net.sleepystudios.bankvault;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.entities.Drone;
import net.sleepystudios.bankvault.entities.Player;
import net.sleepystudios.bankvault.proc.Camera;
import net.sleepystudios.bankvault.proc.DecalProcObject;
import net.sleepystudios.bankvault.proc.Heart;
import net.sleepystudios.bankvault.proc.HiddenProcObject;
import net.sleepystudios.bankvault.proc.ProcObject;

public class MapHandler {
	public TiledMap map; 
	private TiledMapRenderer mapRenderer;
	private int[] layers = {0}, fringeLayers = {1,2};
	public ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
	public ArrayList<ProcObject> procObjs = new ArrayList<ProcObject>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public ArrayList<TracerBit> tracers = new ArrayList<TracerBit>();
	
	public int spawnX, spawnY;
	public Player p;
	
	public MapHandler(TiledMap map) {
		this.map = map;
		mapRenderer = new FixedTiledMapRenderer(map);
		
		loadRects();
	}
	
	private void loadRects() {
		int s = getTileSize();
		
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(fringeLayers[0]);
        for(int x = 0; x < layer.getWidth();x++){
            for(int y = 0; y < layer.getHeight();y++){
                TiledMapTileLayer.Cell cell = layer.getCell(x,y);
                
                if(cell!=null) {
                	Object property = cell.getTile().getProperties().get("wall");
                    if(property != null){
                        rects.add(new Rectangle(x*s, y*s, s, s));
                    } else {
                    	property = cell.getTile().getProperties().get("spawn");
                        if(property != null){
                        	spawnX = x*s + getTileSize()/2;
                        	spawnY = (y+2)*s;
                        	rects.add(new Rectangle(x*s, y*s, s, s));
                        }
                    }
                }
            }
        }
	}
	
	public void gen() {
		int size[] = {30, 15, 5, 5};
		
		procObjs.clear();
		
		procObjs.add(new Heart(this));
		
		String decals[] = {"notes1", "notes2", "notes3", "coins1", "coins2", "coins3"};
		procObjs.add(new HiddenProcObject(this));
		for(int i=0; i<size[0]; i++) {
			procObjs.add(new DecalProcObject(decals[BankVault.rand(0, decals.length-1)], this));
		}
		
		String objs[] = {"barstack", "shelf", "vault2"};
		for(int i=0; i<size[1]; i++) {
			procObjs.add(new ProcObject(objs[BankVault.rand(0, objs.length-1)], this));
		}
		
		for(int i=0; i<size[2]; i++) {
			procObjs.add(new Camera(this));
		}
		
		drones.clear();
		for(int i=0; i<size[3]; i++) {
			drones.add(new Drone(this));
		}
		
		p = new Player(this);
		
		bullets.clear();
		tracers.clear();
		BankVault.actionMessages.clear();
		messageNum = 0;
		obDone = false;
		tmrMessages = 0;
	}
	
	int messageNum = 0; float tmrMessages;
	boolean obDone;
	public void render(OrthographicCamera camera) {
		mapRenderer.setView(camera);
        mapRenderer.render(layers);
        
        if(BankVault.end) return;
        
        tmrMessages+=Gdx.graphics.getDeltaTime();
        int time=2;
        if(messageNum==3) time = 5;
        if(tmrMessages>=time) {
        	if(messageNum<5) messageNum++;
        	tmrMessages = 0;
        }
        
        if(messageNum==0) {
        	addActionMessage("Use WASD to move", 12, Color.WHITE);
        }
        if(messageNum==1) {
        	addActionMessage("Use SPACE to hide", 12, Color.WHITE);
        }
        if(messageNum==2) {
        	addActionMessage("Avoid drones and cameras", 12, Color.WHITE);
        }
        if(messageNum==3 && !obDone) {
        	addActionMessage("Find your heart", 12, Color.WHITE);
        	obDone = true;
        }
        if(messageNum==4) {
        	addActionMessage("Press R to restart anytime", 12, Color.WHITE);
        }
	}
	
	public void addActionMessage(String message, int size, Color colour) {
		// make sure its not like any others
		for(ActionMessage am : BankVault.actionMessages) {
			if(message.equals(am.text)) return;
		}
		
		if(BankVault.actionMessages.size()>=1) {
			BankVault.actionMessages.add(new ActionMessage(message, size, colour));
			BankVault.actionMessages.remove(0);
		} else {
			BankVault.actionMessages.add(new ActionMessage(message, size, colour));
		}
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
