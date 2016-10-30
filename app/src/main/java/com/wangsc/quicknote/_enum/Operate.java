package com.wangsc.quicknote._enum;

/**
 * Created by 阿弥陀佛 on 2015/12/5.
 */
public enum Operate {
    ADD_NOTE(0),
    UPDATE_NOTE(1),
    DELETE_NOTE(2),
    DELETE_NOTE_BY_STATUS(3);

    private int value = 0;

    Operate(int value) {
        this.value = value;
    }

    public static Operate fromInt(int value) {
        switch (value) {
            case 0:
                return ADD_NOTE;
            case 1:
                return UPDATE_NOTE;
            case 2:
                return DELETE_NOTE;
            case 3:
                return DELETE_NOTE_BY_STATUS;
            default:
                return null;
        }
    }

    public int toInt() {
        return this.value;
    }

    public static int count(){
        return 4;
    }
}
