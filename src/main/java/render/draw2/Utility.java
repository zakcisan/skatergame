package render.draw2;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.glGetError;

public class Utility
{
	public static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector4f BLACK = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector4f RED = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector4f GREEN = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector4f BLUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

	public static void checkGLError(String message)
	{
	    int err = glGetError();
	    if(err != 0)
	    {
	        throw new RuntimeException("OpenGL error: " + err + "\n" + message);
	    }
	}
}
