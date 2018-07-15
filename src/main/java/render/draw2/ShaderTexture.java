package render.draw2;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class ShaderTexture
{
    public static final String VERTEX_SOURCE = "res/shaders/texture.v";
    public static final String FRAGMENT_SOURCE = "res/shaders/texture.f";

    public static RenderFunction initialize(RenderContext context)
    {
        int program = ShaderProgram.create(VERTEX_SOURCE, FRAGMENT_SOURCE, context);

        int uMatrix = glGetUniformLocation(program, "matrix");
        int uColor = glGetUniformLocation(program, "color");
        int uTex = glGetUniformLocation(program, "tex");

        return context1 -> {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                FloatBuffer buffer = stack.mallocFloat(16);
                glUseProgram(program);
                glUniformMatrix4fv(uMatrix, false, context1.matrix.get(buffer));
                glUniform4f(uColor, context1.color.x, context1.color.y, context1.color.z, context1.color.w);
                glUniform1i(uTex, context1.activeTexture);
            }
        };
    }
}
