package org.barbasman.mazebughunter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import org.barbasman.mazebughunter.Entity;

public class TownMap extends Map {
    private static final String TAG = PlayerPhysicsComponent.class.getSimpleName();
    private static String _mapPath = "maps/town.tmx";
    private static String _townGuardWalking = "scripts/town_guard_walking.json";
    private static String _townBlacksmith = "scripts/town_blacksmith.json";
    private static String _townMage = "scripts/town_mage.json";
    private static String _townInnKeeper = "scripts/town_innkeeper.json";
    private static String _townFolk = "scripts/town_folk.json";

    TownMap(){
        super(MapFactory.MapType.TOWN, _mapPath);
        for( Vector2 position: _npcStartPositions){
            _mapEntities.add(Entity.initEntity(Entity.getEntityConfig(
                                            _townGuardWalking), position));
        }
        /*//Special cases
        _mapEntities.add(initSpecialEntity(Entity.getEntityConfig(
                                               _townBlacksmith)));
        _mapEntities.add(initSpecialEntity(Entity.getEntityConfig(
                                               _townMage)));
        _mapEntities.add(initSpecialEntity(Entity.getEntityConfig(
                                               _townInnKeeper)));
        //When we have multiple configs in one file
        Array<EntityConfig> configs = 
            Entity.getEntityConfigs(_townFolk);
        for(EntityConfig config: configs){
            _mapEntities.add(initSpecialEntity(config));
        }*/
    }
}


