package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.BankVault;
import net.sleepystudios.bankvault.MapHandler;

public class ProcObject {
	Sprite sprite;
	MapHandler mh;
	public Rectangle rect = null;
	
	public ProcObject(String name, MapHandler mh) {
		sprite = new Sprite(new Texture(name + ".png"));
		this.mh = mh;
		
		gen();
	}
	
	private void gen() {
		float x=-1, y=-1;
		
		while((x==-1 && y==-1) || overlaps(x, y)) {
			x = snap(BankVault.rand(0, mh.getWidth()-sprite.getWidth()));
			y = snap(BankVault.rand(0, mh.getHeight()-sprite.getHeight()));
		}
		
		rect = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
		sprite.setPosition(x, y);
	}
	
	private boolean overlaps(float x, float y) {
		Rectangle procRect = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
		
		for(Rectangle r : mh.rects) {
			if(Intersector.overlaps(procRect, r)) return true;
		}
		
		for(ProcObject o : mh.procObjs) {
			if(o.rect!=null && Intersector.overlaps(procRect, o.rect)) return true;
		}
		
		return false;
	}
	
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	private int snap(float num) {
		int s = mh.getTileSize();
		return Math.round(num/s) * s;
	}
}
