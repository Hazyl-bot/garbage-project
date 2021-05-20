package com.garbage.project.util;

public enum GARBAGE_TYPE {

    RECYCLABLE("可回收垃圾","RECYCLABLE"), WASTE("厨余垃圾","WASTE"),DRY("干垃圾","DRY"),
    WET("湿垃圾","WET"),HARMFUL("有害垃圾","HARMFUL"), OTHER("其他垃圾","OTHER");
    private String name;
    private String value;

    GARBAGE_TYPE(String name,String value){
        this.name=name;
        this.value=value;
    }

    public String getName() {
        return name;
    }
    public String getValue(){
        return this.value;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
