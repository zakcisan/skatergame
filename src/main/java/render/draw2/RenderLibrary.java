package render.draw2;

public class RenderLibrary
{
    // btw TextureLibrary is inside of this object
    /** this object holds all of the important OpenGL context variables we need to store */
    public RenderContext context;

    // these are more "core" openGL type functions--don't worry about them
    public RenderFunction shaderSimple;
    public RenderFunction shaderTexture;
    public RenderFunction shaderTextureFramed;
    public RenderFunction shaderText;
    public RenderFunction vboSimpleSquareBind, vboSimpleSquareDraw, vboSimpleSquareUnbind;
    public RenderFunction vboTextureSquareBind, vboTextureSquareDraw, vboTextureSquareUnbind;
    public Font arial16;
    public Font arial32;
    public Font arial64;

    // these functions call the above functions in the correct orders
    // each function will have a description of possible parameters of RenderContext to modify

    /**Matrix4d matrix
     * Vector4f color
     * */
    public RenderFunction drawSquareSimple;

    /**activeTexture refers to the currently bound texture in OpenGL's context that should be rendered
     * if unsure or not working, setting to be zero is safe
     *
     * Matrix4d matrix
     * Vector4f color
     * int activeTexture
     */

    public RenderFunction drawSquareTextured;

    /**this square's texture can be 'framed', meaning that you can render just a section of it
     * this is commonly used for any sort of spritesheet work (ie sprite animations)
     * see drawSquareTextured for activeTexture
     * frameStart is the top left position (from 0.0 to 1.0 for top left to bottom right)
     * frameSize is the size starting from frameStart and moving down-right
     *
     * Matrix4d matrix
     * Vector4f color
     * int activeTexture
     * Vector2f frameStart
     * Vector2f frameSize
     */
    public RenderFunction drawSquareTexturedFramed;

    /*
    all fonts will have the same parameters
    the above "core" font object can be used for getting the absolute width--in pixels--of a string rendered in a font
    IMPORTANT: when rendering a font, use a model matrix with width/height of 1.0. the scaling is done by the function
    this means that the matrix used only has to be a projection matrix multiplied by a position (and not scaling) matrix

    parameters:
    Matrix4d matrix
    Vector4f color
    int activeTexture
    double fontX
    double fontY
     */
    public RenderFunction drawFontArial16;
    public RenderFunction drawFontArial32;
    public RenderFunction drawFontArial64;

    /** creates all normal and useful objects to rendering */
    public RenderLibrary(RenderContext context)
    {
        this.context = context;

        shaderSimple = ShaderSimple.initialize(context);
        shaderTexture = ShaderTexture.initialize(context);
        shaderTextureFramed = ShaderTextureFramed.initialize(context);
        shaderText = ShaderText.initialize(context);

        RenderFunction[] vss = VertexBufferObject.initializeDefault(context);
        vboSimpleSquareBind = vss[0];
        vboSimpleSquareDraw = vss[1];
        vboSimpleSquareUnbind = vss[2];

        RenderFunction[] vts = VertexBufferObject.initializeDefaultTextured(context);
        vboTextureSquareBind = vts[0];
        vboTextureSquareDraw = vts[1];
        vboTextureSquareUnbind = vts[2];

        arial16 = new Font(context, shaderText, "res/font/arial.ttf", 16, 512, 256);
        arial32 = new Font(context, shaderText, "res/font/arial.ttf", 32, 512, 512);
        arial64 = new Font(context, shaderText, "res/font/arial.ttf", 64, 1024, 512);

        drawSquareSimple = new RenderBundle(new RenderFunction[] {
                shaderSimple, vboSimpleSquareBind, vboSimpleSquareDraw, vboSimpleSquareUnbind
        });
        drawSquareTextured = new RenderBundle(new RenderFunction[] {
                shaderTexture, vboTextureSquareBind, vboTextureSquareDraw, vboTextureSquareUnbind
        });
        drawSquareTexturedFramed = new RenderBundle(new RenderFunction[] {
                shaderTextureFramed, vboTextureSquareBind, vboTextureSquareDraw, vboTextureSquareUnbind
        });
        drawFontArial16 = arial16.getRenderFunction();
        drawFontArial32 = arial32.getRenderFunction();
        drawFontArial64 = arial64.getRenderFunction();

        //context.textures.addTexture("res/textures/block.png");
    }
}
