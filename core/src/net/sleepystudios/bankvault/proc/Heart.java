package net.sleepystudios.bankvault.proc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.AnimGenerator;
import net.sleepystudios.bankvault.MapHandler;

public class Heart extends DecalProcObject {
	Animation anim; float animTmr;
	
	public Heart(MapHandler mh) {
		super("hearts", mh);
		
		anim = new Animation(0.15f, AnimGenerator.gen("hearts.png", 32, 32));
		anim.setPlayMode(PlayMode.LOOP_PINGPONG);
	}
	
	protected void gen() {
		super.gen();
		rect.setWidth(32);
		if(!checkDistance(sprite.getX(), sprite.getY())) gen();
	}
	
	private boolean checkDistance(float x, float y) {
		Vector2 pos = new Vector2(x, y);
		Vector2 spawn = new Vector2(mh.spawnX, mh.spawnY);
		
		if(pos.dst(spawn)>mh.getHeight()/2) return true; 
		
		return false;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		animTmr+=Gdx.graphics.getDeltaTime();
		batch.draw(anim.getKeyFrame(animTmr, true), sprite.getX(), sprite.getY());
	}
}
