package render.draw2;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram
{
    public static int create(String vsource, String fsource, RenderContext context)
    {
        int vertex = Shader.create(vsource, GL_VERTEX_SHADER, context);
        int fragment = Shader.create(fsource, GL_FRAGMENT_SHADER, context);
        int program = glCreateProgram();
        glAttachShader(program, vertex);
        glAttachShader(program, fragment);
        glLinkProgram(program);
        glUseProgram(program);
        context.programs.put(new Object(), program);
        Utility.checkGLError("Linking " + vertex + " and " + fragment);
        return program;
    }
}
