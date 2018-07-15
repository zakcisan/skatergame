package render.window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Window
{
    private boolean mouseCallback[] = new boolean[10];
    private double mouseX, mouseY;
    private long window;
    private IMouse mouseFunction = new IMouse() {
        public void mouseClick(Window window, double x, double y, int button) {}
        public void mouseDrag(Window window, double x, double y, int button) {}
        public void mouseRelease(Window window, double mouseX, double mouseY, int button) {}
    };
    private IClose closeFunction = () -> {};
    private IKeyboard keyboardFunction = (window, key, scancode, action, mods) -> {};

    public void initialize(String name, int width, int height)
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

        // Create the window
        window = glfwCreateWindow(width, height, name, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
            {
                closeFunction.windowClose();
            }

            keyboardFunction.keyAction(this, key, scancode, action, mods);
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {

            if(action == GLFW_PRESS)
            {
                if(!mouseCallback[button])
                {
                    mouseFunction.mouseClick(this, mouseX, mouseY, button);
                }
                else
                {
                    mouseFunction.mouseDrag(this, mouseX, mouseY, button);
                }
                mouseCallback[button] = true;

            }
            else
            {
                mouseCallback[button] = false;
                mouseFunction.mouseRelease(this, mouseX, mouseY, button);
            }
        });

        glfwSetCursorPosCallback(window, (window, mouseX, mouseY) -> {
            this.mouseX = (double) mouseX;
            this.mouseY = (double) mouseY;
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        glfwSetWindowCloseCallback(window, (window) -> {

            closeFunction.windowClose();
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glViewport(0, 0, 800, 600);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


    }

    public boolean open()
    {
        return !glfwWindowShouldClose(window);
    }

    public void update()
    {
        glfwPollEvents();
        glfwSwapBuffers(window);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void terminate()
    {
        glfwTerminate();
    }

    public double getMouseX()
    {
        return mouseX;
    }

    public double getMouseY()
    {
        return mouseY;
    }

    public void setWindowFunctions(IMouse mouseFunction, IKeyboard keyboardFunction, IClose closeFunction)
    {
        this.mouseFunction = mouseFunction;
        this.keyboardFunction = keyboardFunction;
        this.closeFunction = closeFunction;
    }
}
