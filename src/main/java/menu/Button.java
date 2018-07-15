package menu;

import org.joml.Matrix4d;
import org.joml.Vector4d;
import org.joml.Vector4f;
import render.draw2.MatrixMath;
import render.draw2.RenderLibrary;

public class Button
{
    private Matrix4d matrix;
    private Vector4f color;
    private String text;
    private double x, y, width, height;

    public Button(double cx, double cy, double width, double height, Vector4f color, String text)
    {
        this.x = cx;
        this.y = cy;
        this.width = width;
        this.height = height;
        this.matrix = MatrixMath.getMatrix(cx, cy, width, height);
        this.text = text;
        this.color = color;
    }

    public void update(RenderLibrary library, Matrix4d ortho)
    {
        library.context.matrix = new Matrix4d(ortho).mul(this.matrix);
        library.context.color = color;
        library.drawSquareSimple.execute(library.context);

        library.context.matrix = new Matrix4d(ortho);
        library.context.color = new Vector4f(1, 1, 1, 1);
        library.context.fontX = x - 1 / 2.0 * library.arial32.getWidth(text);
        library.context.fontY = y + 32 / 4.0;
        library.context.text = text;
        library.drawFontArial32.execute(library.context);
        /*
        renderer.function(RenderFunction.SHADER_SIMPLE, new RenderParametersShaderSimple(new Matrix4d(ortho).mul(this.matrix), color));
        renderer.function(RenderFunction.VBO_SIMPLE, ZERO);
        renderer.function(RenderFunction.FUNC_DRAWARRAYS, ZERO);
        renderer.function(RenderFunction.VBO_SIMPLE, ONE);

        renderer.function(RenderFunction.OBJ_FONT_ARIAL_32, new RenderParametersFont(text,
                new Matrix4d(ortho), x - 1 / 2.0 * Font.getFontWidth(renderer, RenderFunction.OBJ_FONT_ARIAL_32, text), y - 8, new Vector4d(1, 1, 1, 1)));
                */
    }

    public boolean within(double x, double y)
    {
        if(this.x - width / 2 < x && x < this.x + width / 2)
        {
            if(this.y - height / 2 < y && y < this.y + height / 2)
            {
                return true;
            }
        }
        return false;
    }
}
