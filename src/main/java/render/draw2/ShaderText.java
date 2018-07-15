package render.draw2;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderText
{
    public static final String VERTEX_SOURCE = "res/shaders/text.v";
    public static final String FRAGMENT_SOURCE = "res/shaders/text.f";

    public static RenderFunction initialize(RenderContext context)
    {
        int program = ShaderProgram.create(VERTEX_SOURCE, FRAGMENT_SOURCE, context);
        int uMatrix = glGetUniformLocation(program, "matrix");
        int uColor = glGetUniformLocation(program, "color");

        return context1 -> {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                FloatBuffer buffer = stack.mallocFloat(16);
                glUseProgram(program);
                glUniformMatrix4fv(uMatrix, false, context1.matrix.get(buffer));
                glUniform4f(uColor, context1.color.x, context1.color.y, context1.color.z, context1.color.w);
            }
        };
    }
}