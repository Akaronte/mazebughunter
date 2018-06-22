package org.barbasman.mazebughunter;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import android.util.Log;


import org.barbasman.mazebughunter.screens.MainGameScreen;
import com.badlogic.gdx.Game;

public class MyGdxGame extends Game
{
    
    private static MainGameScreen _mainGameScreen;
   
    //public static final MainGameScreen _mainGameScreen = new MainGameScreen(this);
    
    @Override
    public void create()
    {
        
        _mainGameScreen = new MainGameScreen(this);
        setScreen(_mainGameScreen);
    }

    @Override
    public void dispose()
    {
        _mainGameScreen.dispose();
    }

   
}
