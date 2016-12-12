package net.sleepystudios.bankvault;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TracerBit {
	float alpha = 1f;
	float x, y;
	public boolean exists = true;
	Sprite bit;
	Bullet bullet;
	
	public TracerBit(float x, float y, float angle, Bullet bullet) {
		this.x = x;
		this.y = y;
		
		bit = new Sprite(new Texture("tracer.png"));
		bit.setOrigin(3, 1);
		bit.setRotation(angle);
		bit.setPosition(x, y);
		
		this.bullet = bullet;
	}
	
	public void render(SpriteBatch batch) {
		alpha+=(0-alpha)*0.6f;
		bit.setColor(1f, 1f, 1f, alpha);
		bit.draw(batch);
		
		if(alpha<(5/255f) || !bullet.exists) exists = false;
	}
}
