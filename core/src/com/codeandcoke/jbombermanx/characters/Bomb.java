package com.codeandcoke.jbombermanx.characters;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.codeandcoke.jbombermanx.managers.ResourceManager;
import com.codeandcoke.jbombermanx.managers.SpriteManager;
import com.codeandcoke.jbombermanx.util.Constants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.lang.*;

/**
 * Representa las bombas que coloca el jugador
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class Bomb extends Character {
	
	Animation<TextureRegion> animation;
	private final int FRAMES = 3;
	float stateTime;
	boolean hasExploded;
	int length;
	int strength;
	public Array<Explosion> explosions;
	boolean dead;
	
	SpriteManager spriteManager;
	
	/**
	 * 
	 * @param texture La textura inicial de una bomba
	 * @param x La posición x
	 * @param y La posición y
	 * @param spriteManager
	 */
	public Bomb(Texture texture, float x, float y, int length, int strength, SpriteManager spriteManager) {
		super(texture, x, y);
		this.length = length;
		this.strength = strength;
		this.spriteManager = spriteManager;
		
		// Carga la animación de un spritesheet (todos los frames están en un mismo fichero)
		Texture spriteSheet = ResourceManager.assets.get("player/bomb_animation.png", Texture.class);
		TextureRegion[][] frames = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAMES, spriteSheet.getHeight());
		TextureRegion[] rightFrames = new TextureRegion[FRAMES];
		for (int i = 0; i < FRAMES; i++) {
			rightFrames[i] = frames[0][i];
		}
		animation = new Animation<TextureRegion>(0.15f, rightFrames);
		stateTime = 0;
		
		explosions = new Array<Explosion>();
		dead = false;
	}
	
	/**
	 * Hace detonar la bomba
	 */
	public void explode() {
		
		hasExploded = true;
        if (spriteManager.game.configurationManager.isSoundEnabled())
		    ResourceManager.assets.get("sounds/bomb.wav", Sound.class).play();
		
		// Centro de la explosión
		Explosion explosion = new Explosion(position.x, position.y, Explosion.ExplosionType.CENTER);
		explosions.add(explosion);
		
		float offsetX = 0;
		float offsetY = 0;
		
		Explosion vertical = null, horizontal = null;
		for (int i = 1; i < length; i++) {
			
			offsetX += Constants.BRICK_WIDTH;
			offsetY += Constants.BRICK_HEIGHT;
			
			vertical = new Explosion(position.x, position.y + offsetY, Explosion.ExplosionType.VERTICAL);
			explosions.add(vertical);
			vertical = new Explosion(position.x, position.y - offsetY, Explosion.ExplosionType.VERTICAL);
			explosions.add(vertical);
			horizontal = new Explosion(position.x + offsetX, position.y, Explosion.ExplosionType.HORIZONTAL);
			explosions.add(horizontal);
			horizontal = new Explosion(position.x - offsetX, position.y, Explosion.ExplosionType.HORIZONTAL);
			explosions.add(horizontal);
		}
		
		offsetX += Constants.BRICK_WIDTH;
		offsetY += Constants.BRICK_HEIGHT;
		
		Explosion up = new Explosion(position.x, position.y + offsetY, Explosion.ExplosionType.UP);
		explosions.add(up);
		Explosion down = new Explosion(position.x, position.y - offsetY, Explosion.ExplosionType.DOWN);
		explosions.add(down);
		Explosion left = new Explosion(position.x - offsetX, position.y, Explosion.ExplosionType.LEFT);
		explosions.add(left);
		Explosion right = new Explosion(position.x + offsetX, position.y, Explosion.ExplosionType.RIGHT);
		explosions.add(right);
	}
	
	/**
	 * Marca la bomba para desaparecer
	 */
	public void die() {
		dead = true;
	}
	
	/**
	 * Comprueba si la bomba está marcada para desaparecer
	 * @return
	 */
	public boolean isDead() {
		return dead;
	}
	
	@Override
	public void render(Batch batch) {
		
		if (!hasExploded)
			super.render(batch);
		
		for (Explosion explosion : explosions)
			explosion.render(batch);
	}
	
	@Override
	public void update(float dt) {
		
		super.update(dt);
		
		stateTime += dt;
		currentFrame = animation.getKeyFrame(stateTime, true);
		
		// Anima las explosiones de la bomba
		for (Explosion explosion : explosions)
			explosion.update(dt);
	}
}
