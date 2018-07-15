package render.draw2;

import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class RenderContext
{
    // these variables are encouraged to be changed (but reading from them is stupid)
    // the only reason that I don't replace them with setters is performance (it's slower to call a function)...
    // and I cannot possibly imagine needing to change the way any of these will be accessed
    public Vector4f color = new Vector4f();
    public Vector2f frameStart = new Vector2f(), frameSize = new Vector2f();
    public Matrix4d matrix = new Matrix4d();
    public int activeTexture = 0; // avoid changing this
    public String text = "";
    public double fontX = 0, fontY = 0;

    public TextureLibrary textures;

    /** key, openGL ID */
    HashMap<Object, Integer> programs = new HashMap<>(), shaders = new HashMap<>(), buffers = new HashMap<>();

    public RenderContext()
    {
        textures = new TextureLibrary();
    }

    public void destroy()
    {
        shaders.forEach((i, j) -> {
            glDeleteShader(j);
        });
        programs.forEach((i, j) -> {
            glDeleteProgram(j);
        });
        buffers.forEach((i, j) -> {
            glDeleteBuffers(j);
        });
    }
}
