package com.docler.lamp.lampgocapplication.matrix;

interface IVector4f {
    float getX();

    float getY();

    float getZ();

    float getW();

    boolean compareTo(Vector4f rhs);
}
