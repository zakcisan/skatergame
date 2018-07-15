package render.draw2;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TextureLibrary
{
    private Map<String, Texture> map = new HashMap<String, Texture>();

    public TextureLibrary()
    {
        addTexture("res/test.png");
    }

    public void bind(int activeTexture, String name)
    {
        glActiveTexture(activeTexture);
        glBindTexture(GL_TEXTURE_2D, map.get(name).identity);
    }

    public RenderFunction getBindFunction(String path)
    {
        return getBindFunction(getTexture(path), 0);
    }

    public RenderFunction getBindFunction(int texture)
    {
        return getBindFunction(texture, GL_TEXTURE0);
    }

    public RenderFunction getBindFunction(int texture, int activeTexture)
    {
        return (context) -> {
            glActiveTexture(activeTexture + GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
        };
    }

    public int getTexture(String path)
    {
        if(map.containsKey(path))
        {
            return map.get(path).identity;
        }
        else
        {
            System.out.println(path + " not found!");
            return map.get("res/test.png").identity;
        }
    }

    public void addTexture(String path)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.ints(0);
            IntBuffer h = stack.ints(0);
            IntBuffer bpp = stack.ints(0);
            ByteBuffer bitmap = STBImage.stbi_load(path, w, h, bpp, 4);

            int width = w.get(0);
            int height = h.get(0);

            if(width == 0 && height == 0)
            {
                bitmap = stack.malloc(4 * 4);
                bitmap.put((byte) 0b11111111).put((byte) 0b11111111).put((byte) 0b11111111).put((byte) 0b11111111);
                bitmap.put((byte) 0b00000000).put((byte) 0b00000000).put((byte) 0b00000000).put((byte) 0b11111111);
                bitmap.put((byte) 0b00000000).put((byte) 0b00000000).put((byte) 0b00000000).put((byte) 0b11111111);
                bitmap.put((byte) 0b11111111).put((byte) 0b11111111).put((byte) 0b11111111).put((byte) 0b11111111);
                bitmap.flip();
                width = 2;
                height = 2;
                if(!path.equals("res/test.png"))
                    System.out.println("Could not load texture " + path);
            }

            int tex = glGenTextures();
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, tex);
            glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RGBA,
                    width,
                    height,
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    bitmap
            );
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            Utility.checkGLError("Texture " + path);

            map.put(path, new Texture(tex, width, height));
        }
    }

    public void addTexture(String name, int identity, int width, int height)
    {
        map.put(name, new Texture(identity, width, height));
    }

    public void addTexture(ByteBuffer bitmap, int width, int height, String name, int color)
    {
        int tex = glGenTextures();
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                color,
                width,
                height,
                0,
                color,
                GL_UNSIGNED_BYTE,
                bitmap
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        Utility.checkGLError("Texture " + name);

        map.put(name, new Texture(tex, width, height));
    }

    public void destroy()
    {
        map.forEach((str, i) -> {
            glDeleteTextures(i.identity);
        });
    }

    private static class Texture
    {
        int identity;
        int width;
        int height;

        public Texture(int id, int w, int h)
        {
            this.identity = id;
            this.width = w;
            this.height = h;
        }
    }
}
