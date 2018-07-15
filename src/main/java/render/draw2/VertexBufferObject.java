package render.draw2;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import javax.rmi.CORBA.Util;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;

public class VertexBufferObject
{
    public static final String VERTEX_SOURCE = "res/shaders/text.v";
    public static final String FRAGMENT_SOURCE = "res/shaders/text.f";

    public static RenderFunction[] initializeDefault(RenderContext context)
    {
        return initialize(context, new float[] {
                -0.5f, -0.5f,
                +0.5f, -0.5f,
                +0.5f, +0.5f,
                +0.5f, +0.5f,
                -0.5f, +0.5f,
                -0.5f, -0.5f
        }, 6, GL_TRIANGLES, new ArrayList<VertexBufferObject.Attribute>(Arrays.asList(
                new VertexBufferObject.Attribute(0, 2, 2, 0))));
    }

    public static RenderFunction[] initializeDefaultTextured(RenderContext context)
    {
        return initialize(context, new float[] {
                -0.5f, -0.5f, 0.0f, 0.0f,
                +0.5f, -0.5f, 1.0f, 0.0f,
                +0.5f, +0.5f, 1.0f, 1.0f,
                +0.5f, +0.5f, 1.0f, 1.0f,
                -0.5f, +0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.0f, 0.0f
        }, 6, GL_TRIANGLES, new ArrayList<VertexBufferObject.Attribute>(Arrays.asList(
                new VertexBufferObject.Attribute(0, 2, 4, 0),
                new VertexBufferObject.Attribute(1, 2, 4, 2))));
    }

    /** four functions, 0 is bind, 1 is draw, 2 is unbind, 3 is delete */
    public static RenderFunction[] initialize(RenderContext context,
                                              float[] raw,
                                              int vertcount,
                                              int type,
                                              ArrayList<Attribute> attributes)
    {
        int buffer;
        /* WARNING!!!!!!!!!!!!!!!!!!!!! IDK IF IT IS ACTUALLY 65536 FOR MAX MEMORY STACK!!!!!!!!! */
        boolean smallcheck = raw.length < 65536;
        if(smallcheck)
        {
            try(MemoryStack stack = MemoryStack.stackPush())
            {
                FloatBuffer vertices = stack.mallocFloat(raw.length);
                vertices.put(raw);
                vertices.flip();

                buffer = glGenBuffers();

                glBindBuffer(GL_ARRAY_BUFFER, buffer);
                glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            }
        }
        else
        {
            FloatBuffer vertices = BufferUtils.createFloatBuffer(raw.length);
            vertices.put(raw);
            vertices.flip();

            buffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, buffer);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }

        return getFunction(context, buffer, attributes, vertcount, type);
    }
    /** four functions, 0 is bind, 1 is draw, 2 is unbind, 3 is delete */
    public static RenderFunction[] initialize(RenderContext context,
                                              FloatBuffer raw,
                                              int vertcount,
                                              int type,
                                              ArrayList<Attribute> attributes)
    {
        int buffer = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, raw, GL_STATIC_DRAW);

        Utility.checkGLError("Generating Vertex Buffer Object, v2");

        return getFunction(context, buffer, attributes, vertcount, type);
    }

    private static RenderFunction[] getFunction(RenderContext context, int buffer,
                                                ArrayList<Attribute> attributes, int vertcount, int type)
    {
        Object key = new Object();
        context.buffers.put(key, buffer);

        return new RenderFunction[] {
                context1 -> {
                    // bind
                    glBindBuffer(GL_ARRAY_BUFFER, buffer);
                    for (int i = 0; i < attributes.size(); i++)
                    {
                        glEnableVertexAttribArray(attributes.get(i).pointer);
                        glVertexAttribPointer(
                                attributes.get(i).pointer,
                                attributes.get(i).size,
                                GL_FLOAT,
                                false,
                                attributes.get(i).stride * 4,
                                attributes.get(i).offset * 4
                        );
                    }
                },
                context1 -> {
                    // draw
                    glDrawArrays(type, 0, vertcount);
                },
                context1 -> {
                    // unbind
                    for(int i = 0; i < attributes.size(); i++)
                    {
                        glDisableVertexAttribArray(attributes.get(i).pointer);
                    }
                },
                context1 -> {
                    context1.buffers.remove(key);
                    glDeleteBuffers(buffer);
                }
        };
    }

    public static class Attribute
    {
        public int pointer;
        public int size;
        public int stride;
        public int offset;

        public Attribute(int pointer, int size, int stride, int offset)
        {
            this.pointer = pointer;
            this.size = size;
            this.stride = stride;
            this.offset = offset;
        }

        public String toString()
        {
            return pointer + " " + size + " " + stride + " " + offset;
        }
    }
}
