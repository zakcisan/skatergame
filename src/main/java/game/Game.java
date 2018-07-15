package game;

import general.GameState;
import general.Page;
import org.joml.Matrix4d;
import render.draw2.RenderLibrary;
import render.draw2.Utility;
import render.window.IClose;
import render.window.IKeyboard;
import render.window.IMouse;
import render.window.Window;

public class Game extends Page implements IKeyboard, IClose, IMouse
{
    private GameState page = GameState.GAME;
    private double delta;

    public GameState run(Window window, RenderLibrary library)
    {
        // sets interfaces for standard window input functions
        window.setWindowFunctions(this, this, this);
        page = GameState.GAME;

        Matrix4d ortho = new Matrix4d().ortho(0, 800, 600, 0, 0, 100);

        // declare everything

        // keeping track of time for FPS and time passed since last frame (delta)
        long currentTime = System.nanoTime(), lastTime = currentTime;
        int fps = 0, fpsCounter = 0;
        long lastFPSTick = currentTime;

        // this is the main gameloop
        while(window.open() && page == GameState.GAME)
        {
            // time update
            currentTime = System.nanoTime();
            delta = (currentTime - lastTime) / 1000000000.0d;
            lastTime = currentTime;
            fpsCounter++;
            if(currentTime - lastFPSTick > 1000000000)
            {
                lastFPSTick = currentTime;
                fps = fpsCounter;
                fpsCounter = 0;
            }

            // display FPS (last so that it overlays everything else)
            library.context.matrix = new Matrix4d(ortho);
            library.context.color = Utility.WHITE;
            library.context.fontX = 1;
            library.context.fontY = 11;
            library.context.text = "FPS: " + fps;
            library.drawFontArial16.execute(library.context);

            // draws and clears the draw buffer, as well as updating input callbacks
            window.update();
        }

        return page;
    }

    public void destroy()
    {

    }

    public void mouseClick(Window window, double x, double y, int button)
    {
        // left click
        if(button == 0)
        {

        }
        // right click
        if(button == 1)
        {

        }
    }

    public void mouseDrag(Window window, double x, double y, int button)
    {

    }

    public void mouseRelease(Window window, double mouseX, double mouseY, int button)
    {

    }

    public void windowClose()
    {
        page = GameState.EXIT;
    }

    public void keyAction(Window window, int key, int scancode, int action, int mods)
    {

    }

    public double getDelta()
    {
        return delta;
    }
}
