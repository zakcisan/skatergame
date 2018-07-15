import game.Game;
import general.GameState;
import menu.Menu;
import render.draw2.RenderContext;
import render.draw2.RenderLibrary;
import render.window.Window;

public class Main
{


    public static void main(String[] args)
    {
        Window window = new Window();
        window.initialize("Game", 800, 600);

        RenderContext context = new RenderContext();
        RenderLibrary library = new RenderLibrary(context);

        Menu menu = new Menu();
        Game game = new Game();

        /*
        Renderer renderer = new Renderer();
        RendererFactory.initialize(renderer);
        TextureLibrary textureLibrary = RendererFactory.getTextureLibrary(renderer);
        textureLibrary.addTexture("res/test.png");
        textureLibrary.addTexture("res/textures/character.png");*/

        GameState state = GameState.MENU;

        while(state != GameState.EXIT)
        {
            System.out.println("State switched to " + state);
            switch(state)
            {
                case MENU:
                    state = menu.run(window, library);
                    break;
                case GAME:
                    state = game.run(window, library);
                    break;
                default:
                    System.out.println("State " + state + " is not recognized.");
                    state = GameState.EXIT;
            }
        }

        System.out.println("Exiting.");

        //renderer.destroy();*/

        context.destroy();
        window.terminate();
    }
}
