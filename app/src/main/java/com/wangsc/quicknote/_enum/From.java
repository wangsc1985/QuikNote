package com.wangsc.quicknote._enum;

/**
 * Created by 阿弥陀佛 on 2015/11/15.
 */
public enum From {
    MainToAdd(1),
    MainToDetails(2),
    MainToEdit(3),
    RecycleToDetails(4),
    HiddenToDetails(5),
    HiddenToEdit(6);

    private int value = 0;

    private From(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static From valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 1:
                return MainToAdd;
            case 2:
                return MainToDetails;
            case 3:
                return MainToEdit;
            case 4:
                return RecycleToDetails;
            case 5:
                return HiddenToDetails;
            case 6:
                return HiddenToEdit;
            default:
                return null;
        }
    }
    public int value() {
        return this.value;
    }
}
