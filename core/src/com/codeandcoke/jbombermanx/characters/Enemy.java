package com.codeandcoke.jbombermanx.characters;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.codeandcoke.jbombermanx.managers.ResourceManager;
import com.codeandcoke.jbombermanx.managers.SpriteManager;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.codeandcoke.jbombermanx.util.Constants;

/**
 * Clase que representa a los enemigos de diferentes tipos
 * que pueden aparecer
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class Enemy extends Character {

	public enum Direction {
		VERTICAL, HORIZONTAL, RANDOM
	}
	Direction direction;
	boolean exploding;
	boolean dead;
	String name;

	Animation<TextureRegion> animation;
	float stateTime;
	public float speed = 30f;
    float lastJump = 0;

    SpriteManager spriteManager;
	
	public Enemy(float x, float y, String name, Direction direction, SpriteManager spriteManager) {
		super(x, y);
		
		this.name = name;
		animation = new Animation<TextureRegion>(0.15f, ResourceManager.assets.get("enemy/enemies.pack", TextureAtlas.class).findRegions(name));
		
		currentFrame = animation.getKeyFrame(0);
		rect.width = currentFrame.getRegionWidth();
		rect.height = currentFrame.getRegionHeight();
		this.direction = direction;

        this.spriteManager = spriteManager;
	}

	public void move(Vector2 movement) {
		
		movement.scl(speed);
		position.add(movement);
	}
	
	public void explode() {
		exploding = true;
		speed = 0f;
		
		// En 1.5 segundos desaparecerá de la pantalla
		Timer.schedule(new Task() {
			public void run() {
				die();
			}
		}, 1.5f);
	}
	
	public void die() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	@Override
	public void update(float dt) {
		
		super.update(dt);
		
		stateTime += dt;
		if (!exploding)
			currentFrame = animation.getKeyFrame(stateTime, true);
		else
			currentFrame = ResourceManager.assets.get("enemy/enemies.pack", TextureAtlas.class).findRegion(name + "_dead");
		
		switch (direction) {
		case VERTICAL:
			move(new Vector2(0, -dt));
			break;
		case HORIZONTAL:
			move(new Vector2(dt, 0));
			break;
        case RANDOM:
            // Cambia de posición aleatoriamente a algún hueco libre
            if (stateTime - lastJump > 5) {
                int x = MathUtils.random(0, Constants.MAP_WIDTH - 1);
                int y = MathUtils.random(0, Constants.MAP_HEIGHT - 1);
                boolean isABrick = false;

                for (Brick brick : spriteManager.bricks) {
                    if ((brick.position.x == x) && (brick.position.y == y))
                        isABrick = true;
                }

                if (!isABrick) {
                    position.x = x * Constants.BRICK_WIDTH;
                    position.y = y * Constants.BRICK_HEIGHT;
                    lastJump = stateTime;
                }
            }
        }
	}
}
