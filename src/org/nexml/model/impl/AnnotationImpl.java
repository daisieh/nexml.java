package org.nexml.model.impl;

import org.nexml.model.Annotation;

public class AnnotationImpl extends NexmlWritableImpl implements Annotation {
    
    private String mProperty;
    private Object mValue;
    
    public AnnotationImpl(String property, Object value) {
        
    }
    
    public AnnotationImpl() {}

    @Override
    String getTagName() {
        return "meta";
    }

    public String getProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getValue() {
        // TODO Auto-generated method stub
        return mValue;
    }

    public void setProperty(String property) {
        getElement().setAttribute("property", property);
    }

    public void setValue(Object value) {
        // TODO Auto-generated method stub

    }

}