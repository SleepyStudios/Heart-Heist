package net.sleepystudios.bankvault;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class ActionMessage {
	public static final int DEFAULT_SIZE = 18;
	
	public String text; 
	private int size;
	private Color colour;
	private float y = -1, tmrLife;
	private BitmapFont font;
	
	public ActionMessage(String text) {
		this(text, DEFAULT_SIZE, Color.WHITE);
	}
	
	public ActionMessage(String text, int size, Color colour) {
		this.text = text;
		this.size = size;
		this.colour = colour;
	}
	
	private void initFont() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Freeroad.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		
		parameter.size = size;
		parameter.borderWidth = 1;
		parameter.borderColor = new Color(0.3f, 0.3f, 0.3f, 0.5f);
		parameter.spaceX--;
		parameter.color = colour;

		font = generator.generateFont(parameter);
		generator.dispose();
	}
	
	private void checkFont() {
		if(font==null) initFont();
	}
	
	int actionMsgY = Gdx.graphics.getHeight()/16;
	public void render(SpriteBatch batch, MapHandler mh) {
		checkFont();
		
		boolean winMsg = text.toLowerCase().contains("again");
		
		int index = BankVault.actionMessages.indexOf(this);
		float tar = mh.p.box.y + 50 + (index*20);
		if(winMsg) tar = Gdx.graphics.getHeight()/2 + (index*30);
		
		y+=(tar-y)*0.2f;
		if(!winMsg) tmrLife+=Gdx.graphics.getDeltaTime();
		
		if(Math.abs(y - tar) <= 2 && tmrLife>=2) {
			if(font.getColor().a-0.2f>0) {
				font.getColor().a-=0.2f;
			} else {
				BankVault.actionMessages.remove(this);
				return;
			}
		}
		
		GlyphLayout gl = new GlyphLayout(font, text);
		
		if(winMsg) {
			font.draw(batch, text, Gdx.graphics.getWidth()/2-gl.width/2, y);
		} else {
			font.draw(batch, text, mh.p.box.x+mh.p.box.getWidth()/2-gl.width/2, y);
		}
	}
}
