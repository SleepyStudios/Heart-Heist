package net.sleepystudios.bankvault;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.sleepystudios.bankvault.entities.Drone;
import net.sleepystudios.bankvault.proc.Camera;
import net.sleepystudios.bankvault.proc.DecalProcObject;
import net.sleepystudios.bankvault.proc.Heart;
import net.sleepystudios.bankvault.proc.ProcObject;

public class BankVault extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch, guiBatch;
	public static OrthographicCamera camera;
	MapHandler mh;
	ShapeRenderer sr;
	public static boolean showHitBoxes;
	public static Sprite endCircle;
	public static boolean end, win;
	public static ArrayList<ActionMessage> actionMessages = new ArrayList<ActionMessage>();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		guiBatch = new SpriteBatch();
		
		// camera
        float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(w, h);
		camera.zoom = 0.5f;
		
		TmxMapLoader loader = new TmxMapLoader();
		Parameters params = new Parameters();
		params.textureMinFilter = TextureFilter.Nearest;
		params.textureMagFilter = TextureFilter.Nearest;

		int room = rand(1, 1);
		mh = new MapHandler(loader.load("room" + room + ".tmx", params));
		mh.gen();
		sr = new ShapeRenderer();
		
		endCircle = new Sprite(new Texture("endcircle.png"));
		endCircle.setSize(1, 1);
		endCircle.setPosition(Gdx.graphics.getWidth()/2-endCircle.getWidth()/2, Gdx.graphics.getHeight()/2-endCircle.getHeight()/2);
		
		Music music = Gdx.audio.newMusic(Gdx.files.internal("wakeup2.mp3"));
		music.setLooping(true);
		//music.play();
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		mh.render(camera);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for(ProcObject o : mh.procObjs) if(o instanceof DecalProcObject && !(o instanceof Heart) && !(o instanceof Camera)) o.render(batch);
		
		mh.p.render(batch);
		
		for(ProcObject o : mh.procObjs) if(!(o instanceof DecalProcObject) || o instanceof Heart || o instanceof Camera) o.render(batch);
		
		for(int i=0; i<mh.tracers.size(); i++) {
			TracerBit t = mh.tracers.get(i);
			if(t.exists) {
				t.render(batch);
			} else {
				mh.bullets.remove(t);
			}
		}
		
		for(int i=0; i<mh.bullets.size(); i++) {
			Bullet b = mh.bullets.get(i);
			if(b.exists) {
				b.render(batch);
			} else {
				mh.bullets.remove(b);
			}
		}
		
		for(Drone d : mh.drones) d.render(batch);
		
		if(showHitBoxes) renderBoxes();
		
		batch.end();
		
        mh.renderFringe(camera);
        
        batch.begin();
        
        for(Drone d : mh.drones) if(d.e!=null) d.e.render(batch);
        if(mh.p.e!=null) mh.p.e.render(batch);
        
        for(int i=0; i<actionMessages.size(); i++) {
        	actionMessages.get(i).render(batch, mh);
        }
        
        batch.end();
        
        if(end) {
        	batch.begin();
        	renderEndCircle();
        	batch.end();
        	
        	if(win) {
        		guiBatch.begin();
        		for(int i=0; i<actionMessages.size(); i++) {
                	actionMessages.get(i).render(guiBatch, mh);
                }
        		guiBatch.end();
        	}
        }
        
        
	}
	
	float circleSize = 1; boolean circleReverse;
	public void renderEndCircle() {
		float tar;
		
		if(win) {
			tar = Gdx.graphics.getWidth()*1.1f;
			
			circleSize+=(tar-circleSize)*0.04f;
			if((int) circleSize>=tar) {
				circleReverse = true;
			}
			if((int) circleSize+700>=tar) {
				if(!winMsg) {
					mh.addActionMessage("You found your heart! Press Space to play again", 24, Color.WHITE);
					winMsg = true;
				}
			}
		} else {
			if(!circleReverse) {
				tar = Gdx.graphics.getWidth()*1.1f;
				
				circleSize+=(tar-circleSize)*0.06f;
				if((int) circleSize+275>=tar) {
					mh.gen();
					circleReverse = true;
				}
			} else if(circleReverse) {
				tar = 0;
				
				circleSize+=(tar-circleSize)*0.3f;
				if((int) circleSize-50<=tar) {
					end = false;
					circleReverse = false;
					win = false;
				}
			}
		}
		
		endCircle.setSize(circleSize, circleSize);
		endCircle.setPosition(mh.p.box.x+mh.p.box.width/2-endCircle.getWidth()/2, mh.p.box.y+mh.p.box.height/2-endCircle.getHeight()/2);
		endCircle.draw(batch);
	}
	
	private void renderBoxes() {
		batch.end();
		
		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeType.Line);
		sr.setColor(Color.RED);
		
		for(Rectangle r : mh.rects) sr.rect(r.x, r.y, r.width, r.height);
		for(ProcObject o : mh.procObjs) sr.rect(o.rect.x, o.rect.y, o.rect.width, o.rect.height);
		
		for(Drone d : mh.drones) {
			sr.setColor(Color.RED);
			sr.rect(d.box.x, d.box.y, d.box.width, d.box.height);
			
			sr.setColor(Color.PINK);
			sr.polygon(d.vision.getTransformedVertices());
			
			sr.setColor(Color.CYAN);
			
			Vector2 me = new Vector2(d.box.x+d.box.width/2, d.box.y+d.box.height/2);
			Vector2 player = new Vector2(mh.p.box.x+mh.p.box.width/2, mh.p.box.y+mh.p.box.height/2);
			
			for(Rectangle r : mh.rects) {
				if(Intersector.intersectSegmentPolygon(me, player, boxToPoly(r))) {
					sr.setColor(Color.PURPLE);
					break;
				}
			}
			
			for(ProcObject o : mh.procObjs) {
				if(o.hasCollision && Intersector.intersectSegmentPolygon(me, player, boxToPoly(o.rect))) {
					sr.setColor(Color.PURPLE);
					break;
				}
			}
			
			if(mh.p.animIndex==mh.p.SHADOW) sr.setColor(Color.PURPLE);
			
			sr.line(me, player);
			
		}
		sr.setColor(Color.YELLOW);
		
		sr.rect(mh.p.box.x, mh.p.box.y, mh.p.box.width, mh.p.box.height);
		
		sr.end();
		
		batch.begin();
	}
	
	public Polygon boxToPoly(Rectangle box) {
    	if(box==null) return null;
    	
    	Polygon poly = new Polygon(new float[] {
				box.getX(), box.getY(), 
				box.getX(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()+box.getHeight(),
				box.getX()+box.getWidth(), box.getY()});
		
		return poly; 
    }
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	boolean winMsg;
	@Override
	public boolean keyUp(int keycode) {
		//if(keycode==Input.Keys.B) showHitBoxes = !showHitBoxes;
		if(keycode==Input.Keys.R) {
			endCircle.setColor(Color.BLACK);
			end=true;
		}
		if(keycode==Input.Keys.SPACE) {
			if(win) {
				win = false;
				winMsg = false;

			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	// generates a random number
    public static int rand(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
    public static float rand(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
    }

    // random number that cannot be 0
    public static int randNoZero(int min, int max) {
        int r = rand(min, max);
        return r != 0 ? r : randNoZero(min, max);
    }
    public static float randNoZero(float min, float max) {
        float r = rand(min, max);
        return r != 0 ? r : randNoZero(min, max);
    }
    
    public static int snap(float num) {
		int s = 32;
		return Math.round(num/s) * s;
	}
    
    public static void playSound(String s) {
    	Sound sound = Gdx.audio.newSound(Gdx.files.internal(s + ".wav"));
    	sound.play(1f);
    }
}
