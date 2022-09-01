package com.ejjiu.image.interfaces;

import com.google.common.base.Preconditions;

import com.ejjiu.image.spring.utils.SpringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/06 20:06
 */
public interface AutowireInterface extends Initializable {
    default void initialize()
    {
        this.initialize(null,null);
    }
    @Override
    default void initialize(URL location, ResourceBundle resources) {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();

        Field[] fields = getClass().getDeclaredFields();
        fillAutowireField(applicationContext, fields);
        Class<?> superclass = getClass().getSuperclass();
        
        if (superclass != null) {
            fields = superclass.getDeclaredFields();
            fillAutowireField(applicationContext,fields);
        }
    }

    default void fillAutowireField(ApplicationContext applicationContext, Field[] fields) {
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                try {

                    Object bean = applicationContext.getBean(field.getName());
                    if (annotation.required()) {
                        Preconditions
                                .checkNotNull(bean, "不能注入,field.getName:%s,this.getClass.getName:%s", field.getName(), this.getClass().getName());
                    }
                    field.setAccessible(true);
                    field.set(this, bean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ;
}
