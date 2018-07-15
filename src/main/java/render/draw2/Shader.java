package render.draw2;

import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.nio.IntBuffer;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader
{
    public static int create(String path, int type, RenderContext context)
    {
        String source = getSource(path);
        int identity = glCreateShader(type);
        glShaderSource(identity, source);
        glCompileShader(identity);
        checkShaderCompilation(identity);
        Utility.checkGLError("Compilation of " + path);
        context.shaders.put(new Object(), identity);
        return identity;
    }

    public static void checkShaderCompilation(int shader)
    {
        IntBuffer success = BufferUtils.createIntBuffer(1);
        glGetShaderiv(shader, GL_COMPILE_STATUS, success);
        if(success.get(0) == GL_FALSE)
        {
            IntBuffer logSize = BufferUtils.createIntBuffer(1);
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, logSize);
            String errorLog = glGetShaderInfoLog(shader);
            throw new RuntimeException("Shader compilation error!\n" + errorLog);
        }
    }

    public static String getSource(String path)
    {
        try
        {
            Scanner s = new Scanner(new FileInputStream(path));
            String content = s.useDelimiter("\\Z").next();
            s.close();
            return content;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return "";
    }
}
