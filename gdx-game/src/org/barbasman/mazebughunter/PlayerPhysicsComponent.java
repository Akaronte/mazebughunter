package org.barbasman.mazebughunter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.math.collision.Ray;

public class PlayerPhysicsComponent extends PhysicsComponent {
    private static final String TAG = PlayerPhysicsComponent.class.getSimpleName();
    
    private Entity.State _state;
    private Vector3 _mouseSelectCoordinates;
    private boolean _isMouseSelectEnabled = false;
    private Ray _selectionRay;
    private float _selectRayMaximumDistance = 32.0f;
    
    public PlayerPhysicsComponent(){
        _mouseSelectCoordinates = new Vector3(0,0,0);
        _selectionRay = new Ray(new Vector3(), new Vector3());
    }
    @Override
    public void dispose(){
    }
    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(Component.MESSAGE_TOKEN);
        if( string.length == 0 ) return;
        //Specifically for messages with 1 object payload
        if( string.length == 2 ) {
            if (string[0].equalsIgnoreCase(
                    MESSAGE.INIT_START_POSITION.toString())) {
                _currentEntityPosition = 
                    _json.fromJson(Vector2.class, string[1]);
                _nextEntityPosition.set(_currentEntityPosition.x, 
                                        _currentEntityPosition.y);
            } else if (string[0].equalsIgnoreCase(
                           MESSAGE.CURRENT_STATE.toString())) {
                _state = _json.fromJson(Entity.State.class, string[1]);
            } else if (string[0].equalsIgnoreCase(
                           MESSAGE.CURRENT_DIRECTION.toString())) {
                _currentDirection = 
                    _json.fromJson(Entity.Direction.class, 
                                   string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.INIT_SELECT_ENTITY.toString())) {
                _mouseSelectCoordinates = 
                    _json.fromJson(Vector3.class, string[1]);
                _isMouseSelectEnabled = true;
            }
        }
    }
    
    @Override
    public void update(Entity entity, MapManager mapMgr, float delta) {
        //We want the hitbox to be at the feet for a better feel
        updateBoundingBoxPosition(_nextEntityPosition);
        /*updatePortalLayerActivation(mapMgr);
        updateDiscoverLayerActivation(mapMgr);
        updateEnemySpawnLayerActivation(mapMgr);*/

        if( _isMouseSelectEnabled ){
            selectMapEntityCandidate(mapMgr);
            _isMouseSelectEnabled = false;
        }

        if (    !isCollisionWithMapLayer(entity, mapMgr) &&
            !isCollisionWithMapEntities(entity, mapMgr) &&
            _state == Entity.State.WALKING){
            setNextPositionToCurrent(entity);

            Camera camera = mapMgr.getCamera();
            camera.position.set(_currentEntityPosition.x, _currentEntityPosition.y, 0f);
            camera.update();
        }else{
            updateBoundingBoxPosition(_currentEntityPosition);
        }

        calculateNextPosition(delta);
    }
    
    private void selectMapEntityCandidate(MapManager mapMgr){
        Array<Entity> currentEntities = 
            mapMgr.getCurrentMapEntities();
        //Convert screen coordinates to world coordinates, 
        //then to unit scale coordinates
        mapMgr.getCamera().unproject(_mouseSelectCoordinates);
        _mouseSelectCoordinates.x /= Map.UNIT_SCALE;
        _mouseSelectCoordinates.y /= Map.UNIT_SCALE;
        for( Entity mapEntity : currentEntities ) {
            //Don't break, reset all entities
            mapEntity.sendMessage(MESSAGE.ENTITY_DESELECTED);
            Rectangle mapEntityBoundingBox = 
                mapEntity.getCurrentBoundingBox();
            if (mapEntity.getCurrentBoundingBox().contains(
                    _mouseSelectCoordinates.x, 
                    _mouseSelectCoordinates.y)) {
                //Check distance
                _selectionRay.set(_boundingBox.x, _boundingBox.y, 
                                  0.0f, mapEntityBoundingBox.x, 
                                  mapEntityBoundingBox.y, 0.0f);
                float distance = _selectionRay.origin.dst(
                    _selectionRay.direction);
                if( distance <= _selectRayMaximumDistance ){
                    //We have a valid entity selection
                    //Picked/Selected
                    Gdx.app.debug(TAG, "Selected Entity! " + 
                                  mapEntity.getEntityConfig().getEntityID());
                    mapEntity.sendMessage(MESSAGE.ENTITY_SELECTED);
                }
            }
        }
    }
    
}
