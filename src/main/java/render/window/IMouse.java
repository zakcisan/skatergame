package render.window;

public interface IMouse
{
    public void mouseClick(Window window, double mouseX, double mouseY, int button);
    public void mouseDrag(Window window, double mouseX, double mouseY, int button);
    public void mouseRelease(Window window, double mouseX, double mouseY, int button);
}
