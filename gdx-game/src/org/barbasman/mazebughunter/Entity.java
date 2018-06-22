package org.barbasman.mazebughunter;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.ArrayList;

import android.util.Log;


public class Entity {
    private static final String TAG = Entity.class.getSimpleName();
    private Json _json;
    private EntityConfig _entityConfig;
    public static enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT;
        static public Direction getRandomNext() {
            return Direction.values()[
                MathUtils.random(Direction.values().length - 1)];
        }
        public Direction getOpposite() {
            if( this == LEFT){
                return RIGHT;
            }else if( this == RIGHT){
                return LEFT;
            }else if( this == UP){
                return DOWN;
            }else{
                return UP;
            }
        }
    }
    public static enum State {
        IDLE,
        WALKING,
        IMMOBILE;//This should always be last
        static public State getRandomNext() {
//Ignore IMMOBILE which should be last state
            return State.values()[
                MathUtils.random(State.values().length - 2)];
        }
    }
    public static enum AnimationType {
        WALK_LEFT,
        WALK_RIGHT,
        WALK_UP,
        WALK_DOWN,
        IDLE,
        IMMOBILE
        }
    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;
    private static final int MAX_COMPONENTS = 5;
    private Array<Component> _components;
    private InputComponent _inputComponent;
    private GraphicsComponent _graphicsComponent;
    private PhysicsComponent _physicsComponent;
    
    public Entity(InputComponent inputComponent, PhysicsComponent physicsComponent, GraphicsComponent graphicsComponent){
        _entityConfig = new EntityConfig();
        _json = new Json();
        _components = new Array<Component>(MAX_COMPONENTS);
        _inputComponent = inputComponent;
        _physicsComponent = physicsComponent;
        _graphicsComponent = graphicsComponent;
        _components.add(_inputComponent);
        _components.add(_physicsComponent);
        _components.add(_graphicsComponent);
    }
    public EntityConfig getEntityConfig() {
        return _entityConfig;
    }
    public void sendMessage(Component.MESSAGE messageType, String ... 
                            args){
        String fullMessage = messageType.toString();
        for (String string : args) {
            fullMessage += Component.MESSAGE_TOKEN + string;
        }
        for(Component component: _components){
            component.receiveMessage(fullMessage);
        }
    }
    
    public static Entity initEntity(EntityConfig entityConfig, Vector2 position){
        Json json = new Json();
        Entity entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC);
        entity.setEntityConfig(entityConfig);

        entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));
        entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.getEntityConfig().getState()));
        entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.getEntityConfig().getDirection()));

        return entity;
	}
    
    public void update(MapManager mapMgr, Batch batch, float delta){
        _inputComponent.update(this, delta);
        _physicsComponent.update(this, mapMgr, delta);
        _graphicsComponent.update(this, mapMgr, batch, delta);
    }
    
    public void dispose(){
        for(Component component: _components){component.dispose();
        }
    }
    public Rectangle getCurrentBoundingBox(){
        return _physicsComponent._boundingBox;
    }
    public void setEntityConfig(EntityConfig entityConfig){
        this._entityConfig = entityConfig;
    }
    static public EntityConfig getEntityConfig(String configFilePath){
        Json json = new Json();
        Log.d("mazebughunter","json" + configFilePath);
        return json.fromJson(EntityConfig.class, Gdx.files.internal(configFilePath));
    }
    static public Array<EntityConfig> getEntityConfigs(String 
                                                       configFilePath){
        Json json = new Json();
        Array<EntityConfig> configs = new Array<EntityConfig>();
        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));
        for (JsonValue jsonVal : list) {
            configs.add(json.readValue(EntityConfig.class, 
                                       jsonVal));
        }
        return configs;
    }
    

    
}
