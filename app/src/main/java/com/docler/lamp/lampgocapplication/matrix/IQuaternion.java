package com.docler.lamp.lampgocapplication.matrix;

public interface IQuaternion extends IVector4f {
    void toAxisAngle(Vector4f output);

    double[] toEulerAngles();

    MatrixF4x4 getMatrix4x4();
}
