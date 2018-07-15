package render.draw2;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;

public final class MatrixMath
{
    private MatrixMath() {} // ensures that this class cannot be instantiated

    public static Matrix4d getMatrix(double x, double y, double w, double h)
    {
        return new Matrix4d().translationRotateScale(x, y, 0, 0, 0, 0, 0, w, h, 0);
    }

    public static Matrix4d getMatrix(double x, double y, double w, double h, double r)
    {
        return new Matrix4d().translationRotateScale(new Vector3d(x, y, 0), new Quaterniond().rotate(0, 0, r), new Vector3d(w, h, 0));
    }

    public static Matrix4d getMatrixDeg(double x, double y, double w, double h, double r)
    {
        return getMatrix(x, y, w, h, r * (double) Math.PI / 180.0f);
    }

    public static Matrix4d getMatrix(Matrix4d projection, Matrix4d multiply)
    {
        return new Matrix4d(projection).mul(multiply);
    }

    public static Matrix4d getMatrix(Vector2d position, Vector2d scale)
    {
        return getMatrix(position.x, position.y, scale.x, scale.y);
    }

    public static Matrix4d getMatrix(Vector2d position, Vector2d scale, double rotation)
    {
        return getMatrix(position.x, position.y, scale.x, scale.y, rotation);
    }
}