package org.barbasman.mazebughunter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Json;

import android.util.Log;

import org.barbasman.mazebughunter.MyGdxGame;
import org.barbasman.mazebughunter.MapFactory;
import org.barbasman.mazebughunter.MapManager;
import org.barbasman.mazebughunter.Entity;
import org.barbasman.mazebughunter.EntityFactory;
import org.barbasman.mazebughunter.Map;

import org.barbasman.mazebughunter.Component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainGameScreen implements Screen {




    private static final String TAG = MainGameScreen.class.getSimpleName();

    public static class VIEWPORT {
        public static float viewportWidth;
        public static float viewportHeight;
        public static float virtualWidth;
        public static float virtualHeight;
        public static float physicalWidth;
        public static float physicalHeight;
        public static float aspectRatio;
    }

    public static enum GameState {
        SAVING,
        LOADING,
        RUNNING,
        PAUSED,
        GAME_OVER
        }
    private static GameState _gameState;

    protected OrthogonalTiledMapRenderer _mapRenderer = null;
    protected MapManager _mapMgr;
    protected OrthographicCamera _camera = null;
    protected OrthographicCamera _hudCamera = null;

    private Json _json;
    private MyGdxGame _game;
    private Entity _player;

    public MainGameScreen(MyGdxGame game){
        _game = game;
        _mapMgr = new MapManager();
        _json = new Json();

        setGameState(GameState.RUNNING);

  



        //_camera setup
        setupViewport(10, 10);

        //get the current size
        _camera = new OrthographicCamera();
        _camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

        _player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.PLAYER);
        _mapMgr.setPlayer(_player);
        _mapMgr.setCamera(_camera);

        _hudCamera = new OrthographicCamera();
        _hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);

        

        

        //Gdx.app.debug(TAG, "UnitScale value is: " + _mapRenderer.getUnitScale());
    }

    @Override
    public void show() {
        

        if( _mapRenderer == null ){
            _mapRenderer = new OrthogonalTiledMapRenderer(_mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
        }
    }

    @Override
    public void hide() {
        if( _gameState != GameState.GAME_OVER ){
            setGameState(GameState.SAVING);
        }

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
       

    
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _mapRenderer.setView(_camera);

        _mapRenderer.getBatch().enableBlending();
        _mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if( _mapMgr.hasMapChanged() ){
            _mapRenderer.setMap(_mapMgr.getCurrentTiledMap());
            _player.sendMessage(Component.MESSAGE.INIT_START_POSITION, _json.toJson(_mapMgr.getPlayerStartUnitScaled()));

            _camera.position.set(_mapMgr.getPlayerStartUnitScaled().x, _mapMgr.getPlayerStartUnitScaled().y, 0f);
            _camera.update();

            

            _mapMgr.setMapChanged(false);

      
        }

        
        
            if( true) {
            _mapRenderer.getBatch().begin();
            TiledMapTileLayer backgroundMapLayer = (TiledMapTileLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.BACKGROUND_LAYER);
            if( backgroundMapLayer != null ){
                _mapRenderer.renderTileLayer(backgroundMapLayer);
                    Log.d("mapa","mapaaaaaaaa");
            }else{
                    Log.d("mapa","no hay mapaaaaaaaa");
            }

            TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.GROUND_LAYER);
            if( groundMapLayer != null ){
                _mapRenderer.renderTileLayer(groundMapLayer);
            }

            TiledMapTileLayer decorationMapLayer = (TiledMapTileLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.DECORATION_LAYER);
            if( decorationMapLayer != null ){
                _mapRenderer.renderTileLayer(decorationMapLayer);
            }

            _mapRenderer.getBatch().end();

            _mapMgr.updateCurrentMapEntities(_mapMgr, _mapRenderer.getBatch(), delta);
            _player.update(_mapMgr, _mapRenderer.getBatch(), delta);
            //_mapMgr.updateCurrentMapEffects(_mapMgr, _mapRenderer.getBatch(), delta);

            _mapRenderer.getBatch().begin();
            _mapRenderer.getBatch().setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);

          
            _mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            _mapRenderer.getBatch().end();

           
           


          
        }else{
            _mapRenderer.render();
            _mapMgr.updateCurrentMapEntities(_mapMgr, _mapRenderer.getBatch(), delta);
            _player.update(_mapMgr, _mapRenderer.getBatch(), delta);
            //_mapMgr.updateCurrentMapEffects(_mapMgr, _mapRenderer.getBatch(), delta);
        }

        
    }

    @Override
    public void resize(int width, int height) {
        setupViewport(10, 10);
        _camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
       
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        setGameState(GameState.LOADING);
 
    }

    @Override
    public void dispose() {
        if( _player != null ){
            
            _player.dispose();
        }

        if( _mapRenderer != null ){
            _mapRenderer.dispose();
        }

        
    }

    public static void setGameState(GameState gameState){
        switch(gameState){
            case RUNNING:
                _gameState = GameState.RUNNING;
                break;
            case LOADING:
                
                _gameState = GameState.RUNNING;
                break;
            case SAVING:
              
                _gameState = GameState.PAUSED;
                break;
            case PAUSED:
                if( _gameState == GameState.PAUSED ){
                    _gameState = GameState.RUNNING;
                }else if( _gameState == GameState.RUNNING ){
                    _gameState = GameState.PAUSED;
                }
                break;
            case GAME_OVER:
                _gameState = GameState.GAME_OVER;
                break;
            default:
                _gameState = GameState.RUNNING;
                break;
        }

    }

    private void setupViewport(int width, int height){
        //Make the viewport a percentage of the total display area
        VIEWPORT.virtualWidth = width;
        VIEWPORT.virtualHeight = height;

        //Current viewport dimensions
        VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
        VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

        //pixel dimensions of display
        VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
        VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

        //aspect ratio for current viewport
        VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

        //update viewport if there could be skewing
        if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
            //Letterbox left and right
            VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
            VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
        }else{
            //letterbox above and below
            VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
            VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
        }

        Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
        Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
        Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
    }
}
