package me.coley.cafedude;

public interface Access {
    int ACC_PUBLIC = 0x0001;
    int ACC_PRIVATE = 0x0002;
    int ACC_PROTECTED = 0x0004;
    int ACC_STATIC = 0x0008;
    int ACC_FINAL = 0x0010;
    int ACC_SYNCHRONIZED = 0x0020;
    int ACC_BRIDGE = 0x0040;
    int ACC_VARARGS = 0x0080;
    int ACC_NATIVE = 0x100;
    int ACC_ABSTRACT = 0x0400;
    int ACC_STRICT = 0x0800;
    int ACC_SYNTHETIC = 0x1000;
}
