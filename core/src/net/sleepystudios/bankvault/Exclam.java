package net.sleepystudios.bankvault;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Exclam {
	float x, y, tar;
	Sprite e;
	
	public Exclam(float x, float y) {
		this.x = x;
		this.y = y;
		tar = y+10;
		e = new Sprite(new Texture("exclam.png"));
		e.setPosition(x, y);
	}
	
	public void render(SpriteBatch batch) {
		e.setY(e.getY()+(tar-e.getY())*0.1f);
		e.setAlpha(e.getColor().a+(0-e.getColor().a)*0.1f);
		e.draw(batch);
	}
}
