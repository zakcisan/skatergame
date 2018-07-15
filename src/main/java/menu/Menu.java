package menu;

import general.GameState;
import general.Page;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector4d;
import org.joml.Vector4f;
import render.draw2.RenderLibrary;
import render.window.IClose;
import render.window.IKeyboard;
import render.window.IMouse;
import render.window.Window;

public class Menu extends Page implements IKeyboard, IClose, IMouse
{
    private GameState page = GameState.MENU;
    private Button button;

    public Menu()
    {

    }

    public GameState run(Window window, RenderLibrary library)
    {
        window.setWindowFunctions(this, this, this);
        page = GameState.MENU;


        Matrix4d ortho = new Matrix4d().ortho(0, 800, 600, 0, 0, 100);

        button = new Button(800 / 2, 600 / 2 + 150, 200, 100, new Vector4f(1, 0, 0, 1), "Play");

        while(window.open() && page == GameState.MENU)
        {
            /*

            renderer.function(RenderFunction.SHADER_SIMPLE, new RenderParametersShaderSimple(new Matrix4d(ortho).mul(MatrixMath.getMatrix(100, 100, 100, 100))));
            renderer.function(RenderFunction.VBO_SIMPLE, null);
            renderer.execute(RenderFunction.FUNC_DRAWARRAYS, null);
            renderer.defunction(RenderFunction.VBO_SIMPLE, null);

            renderer.function(RenderFunction.SHADER_TEXTURE, new RenderParametersShaderSimple(new Matrix4d(ortho).mul(MatrixMath.getMatrix(250, 100, 100, 100))));
            renderer.function(RenderFunction.TEXTURE_LIBRARY, new RenderParametersTexture("res/test.png"));
            renderer.function(RenderFunction.VBO_TEXTURE, null);
            renderer.execute(RenderFunction.FUNC_DRAWARRAYS, null);
            renderer.defunction(RenderFunction.VBO_TEXTURE, null);

            renderer.function(RenderFunction.SHADER_TEXTURE_FRAMED, new RenderParametersShaderTextureFramed(new Matrix4d(ortho).mul(MatrixMath.getMatrix(400, 100, 100, 100)),
                    new Vector4d(1.0), new Vector2d(0.45, 0.35), new Vector2d(0.75, 0.75)));
            renderer.function(RenderFunction.TEXTURE_LIBRARY, new RenderParametersTexture("res/test.png"));
            renderer.function(RenderFunction.VBO_TEXTURE, null);
            renderer.execute(RenderFunction.FUNC_DRAWARRAYS, null);
            renderer.defunction(RenderFunction.VBO_TEXTURE, null);*/
            //renderer.function(RenderFunction.OBJ_FONT_ARIAL_64, new RenderParametersFont("Game",
            //        new Matrix4d(ortho), 800 / 2 - 1 / 2.0 * Font.getFontWidth(renderer, RenderFunction.OBJ_FONT_ARIAL_64, "Game"), 600 / 2, new Vector4d(1, 1, 1, 1)));

            library.context.matrix = new Matrix4d(ortho);
            library.context.color = new Vector4f(1, 1, 1, 1);
            library.context.fontX = 800 / 2 - 1 / 2.0 * library.arial64.getWidth("Game");
            library.context.fontY = 600 * 2 / 5;
            library.context.text = "Game";
            library.drawFontArial64.execute(library.context);

            button.update(library, ortho);

            window.update();
        }

        return page;
    }

    public void destroy()
    {

    }

    public void mouseClick(Window window, double x, double y, int button)
    {
        if(button == 0)
        {
            if(this.button.within(x, y))
            {
                page = GameState.GAME;
            }
        }
    }

    public void mouseDrag(Window window, double x, double y, int button)
    {

    }

    public void mouseRelease(Window window, double x, double y, int button)
    {

    }

    public void windowClose()
    {
        page = GameState.EXIT;
    }

    public void keyAction(Window window, int key, int scancode, int action, int mods)
    {

    }
}
