package render.draw2;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RenderBundle implements RenderFunction
{
    public RenderFunction[] functions;

    private static ArrayList<RenderFunction> simplify(RenderFunction func)
    {
        ArrayList<RenderFunction> f = new ArrayList<>();
        if(func instanceof RenderBundle)
        {
            RenderBundle bundle = (RenderBundle) func;
            for (int i = 0; i < bundle.functions.length; i++)
            {
                f.addAll(simplify(bundle.functions[i]));
            }
            return f;
        }

        f.add(func);
        return f;
    }

    public RenderBundle(RenderFunction[] func)
    {
        ArrayList<RenderFunction> f = new ArrayList<>();
        for(int i = 0; i < func.length; i++)
        {
            f.addAll(simplify(func[i]));
        }
        this.functions = f.toArray(new RenderFunction[f.size()]);
    }

    public void execute(RenderContext context)
    {
        for(int i = 0; i < functions.length; i++)
        {
            functions[i].execute(context);
        }
    }
}
