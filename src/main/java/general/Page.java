package general;

import render.draw2.RenderLibrary;
import render.window.Window;

public abstract class Page
{
    public abstract GameState run(Window window, RenderLibrary library);

    public abstract void destroy();
}
