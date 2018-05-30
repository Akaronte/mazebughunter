package org.barbasman.mazebughunter;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import org.barbasman.mazebughunter.screens.MainGameScreen;
import com.badlogic.gdx.Game;

public class MyGdxGame extends Game
{
   
    public static final MainGameScreen _mainGameScreen = new MainGameScreen();
    
    @Override
    public void create()
    {
        setScreen(_mainGameScreen);
    }

    @Override
    public void dispose()
    {
        _mainGameScreen.dispose();
    }

   
}
