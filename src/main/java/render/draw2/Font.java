package render.draw2;

import org.joml.Matrix4d;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Font
{
    public static final int CHAR_COUNT = 128;

    private RenderFunction drawFunction;
    double advance[] = new double[CHAR_COUNT];
    double lsb[] = new double[CHAR_COUNT]; // left side bearing
    private int size;

    private int version = 0;
    private int texture = 0;

    public Font(RenderContext context, RenderFunction shaderText, String path, int size, int bmpWidth, int bmpHeight)
    {
        this.size = size;
        try
        {
            RenderFunction[] vbo;
            RandomAccessFile file = new RandomAccessFile(new File(path), "r");
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = MemoryUtil.memAlloc((int) channel.size()); // plz be an int!
            channel.read(buffer);
            channel.close();
            file.close();
            buffer.flip();

            try (MemoryStack stack = MemoryStack.stackPush())
            {
                STBTTFontinfo info = STBTTFontinfo.mallocStack(stack);
                STBTruetype.stbtt_InitFont(info, buffer);
                IntBuffer adv = stack.ints(0);
                IntBuffer l = stack.ints(0);

                double scale = STBTruetype.stbtt_ScaleForPixelHeight(info, (float) size);
                for(int i = 0; i < CHAR_COUNT; i++)
                {
                    STBTruetype.stbtt_GetCodepointHMetrics(info, i, adv, l);
                    advance[i] = adv.get(0) * scale;
                    lsb[i] = l.get(0) * scale;
                }
            }

            float s0[] = new float[CHAR_COUNT];
            float s1[] = new float[CHAR_COUNT];
            float t0[] = new float[CHAR_COUNT];
            float t1[] = new float[CHAR_COUNT];
            float x0[] = new float[CHAR_COUNT];
            float x1[] = new float[CHAR_COUNT];
            float y0[] = new float[CHAR_COUNT];
            float y1[] = new float[CHAR_COUNT];

            try (MemoryStack stack = MemoryStack.stackPush())
            {
                ByteBuffer bitmap;
                bitmap = MemoryUtil.memAlloc(bmpWidth*bmpHeight);
                STBTTPackedchar.Buffer charData = STBTTPackedchar.mallocStack(CHAR_COUNT, stack);
                STBTTPackContext context2 = STBTTPackContext.mallocStack(stack);
                STBTruetype.stbtt_PackBegin(context2, bitmap, bmpWidth, bmpHeight, 0, 1);
                STBTruetype.stbtt_PackFontRange(context2, buffer, version, (float) size, 0, charData);
                STBTruetype.stbtt_PackEnd(context2);
                STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

                for(int codePoint = 0; codePoint < CHAR_COUNT; codePoint++)
                {
                    FloatBuffer xpos = stack.floats(0);
                    FloatBuffer ypos = stack.floats(0);
                    STBTruetype.stbtt_GetPackedQuad(charData, bmpWidth, bmpHeight, codePoint, xpos, ypos, q, false);
                    s0[codePoint] = q.s0();
                    s1[codePoint] = q.s1();
                    t0[codePoint] = q.t0();
                    t1[codePoint] = q.t1();
                    x0[codePoint] = q.x0();
                    x1[codePoint] = q.x1();
                    y0[codePoint] = q.y0();
                    y1[codePoint] = q.y1();
                }

                int tex = glGenTextures();
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, tex);
                glTexImage2D(
                        GL_TEXTURE_2D,
                        0,
                        GL_RED,
                        bmpWidth,
                        bmpHeight,
                        0,
                        GL_RED,
                        GL_UNSIGNED_BYTE,
                        bitmap
                );
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                Utility.checkGLError("Texture " + path);
                this.texture = tex;
            }

            try(MemoryStack stack = MemoryStack.stackPush())
            {
                FloatBuffer vertices = stack.mallocFloat(CHAR_COUNT * 6 * 4);
                for(int i = 0; i < CHAR_COUNT; i++)
                {
                    vertices.put(x0[i]).put(y0[i]).put(s0[i]).put(t0[i]);
                    vertices.put(x0[i]).put(y1[i]).put(s0[i]).put(t1[i]);
                    vertices.put(x1[i]).put(y1[i]).put(s1[i]).put(t1[i]);
                    vertices.put(x1[i]).put(y1[i]).put(s1[i]).put(t1[i]);
                    vertices.put(x1[i]).put(y0[i]).put(s1[i]).put(t0[i]);
                    vertices.put(x0[i]).put(y0[i]).put(s0[i]).put(t0[i]);
                }
                vertices.flip();
                ArrayList<VertexBufferObject.Attribute> attributes = new ArrayList<>();
                attributes.add(new VertexBufferObject.Attribute("position", 2, 4, 0));
                attributes.add(new VertexBufferObject.Attribute("textureCoordinate", 2, 4, 2));
                vbo = VertexBufferObject.initialize(context, vertices, CHAR_COUNT * 6, GL_TRIANGLES, attributes);
                Utility.checkGLError("Generating Vertex Buffer Object: " + path);
            }

            setRenderFunction(shaderText, new RenderFunction[] { vbo[0], vbo[2] });
        }
        catch(Exception e)
        {
            // a gem from my other code
            System.out.println(path + ": fuck you");
            e.printStackTrace();
        }

    }

    private void setRenderFunction(RenderFunction shaderText, RenderFunction[] vbo)
    {
        this.drawFunction = context1 -> {
            Matrix4d matrix = context1.matrix;
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                double bearingX = context1.fontX - this.lsb[context1.text.charAt(0)], bearingY = context1.fontY;

                for (int i = 0; i < context1.text.length(); i++)
                {
                    char c = context1.text.charAt(i);
                    if (c == '\n')
                    {
                        bearingX = context1.fontX - (i < context1.text.length() - 1 ? this.lsb[context1.text.charAt(i + 1)] : 0);
                        bearingY -= this.size;
                    } else if (c == ' ')
                    {
                        bearingX += this.advance[' '];
                    } else
                    {
                        FloatBuffer buffer = stack.mallocFloat(16);

                        Matrix4d mat = new Matrix4d();
                        mat.translate(bearingX, bearingY, 0);

                        context1.matrix = new Matrix4d(matrix).mul(mat);

                        shaderText.execute(context1);

                        glActiveTexture(context1.activeTexture + GL_TEXTURE0);
                        glBindTexture(GL_TEXTURE_2D, this.texture);

                        vbo[0].execute(context1);

                        glDrawArrays(GL_TRIANGLES, c * 6, 6);

                        vbo[1].execute(context1);

                        bearingX += this.advance[c];
                    }
                }
            }
        };
    }

    public RenderFunction getRenderFunction()
    {
        return this.drawFunction;
    }

    public double getWidth(String text)
    {
        double size = 0;
        for(int i = 0; i < text.length(); i++)
        {
            size += advance[text.charAt(i)];
        }
        return size;
    }
}
