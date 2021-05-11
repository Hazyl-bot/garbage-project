package com.garbage.project.util;

public enum GARBAGE_TYPE {

    RECYCLABLE("可回收垃圾","RECYCLABLE"), OTHER("其他垃圾","OTHER"),HARMFUL("有害垃圾","HARMFUL"),
    DRY("干垃圾","DRY"), WET("湿垃圾","WET");
    private String name;
    private String value;

    private GARBAGE_TYPE(String name,String value){
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
