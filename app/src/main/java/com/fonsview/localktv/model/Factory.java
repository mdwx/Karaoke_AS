package com.fonsview.localktv.model;



public class Factory {
    
    public static ModelBase createObj(String className) throws Exception{
            return (ModelBase)Class.forName(className).newInstance();
    }
}