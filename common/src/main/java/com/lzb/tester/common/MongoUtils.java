package com.lzb.tester.common;

import com.lzb.tester.dto.MongoPageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Update;
import java.lang.reflect.Field;

public class MongoUtils {

    public static void filedsCopy(Object source, Update target){
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            name = name.equals("id") ? "_id" : name;
            try {
                Object o = field.get(source);
                target.set(name,o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T>MongoPageInfo<T> pageInfoCopy(Page<T> page){
        return MongoPageInfo.<T>builder().totalPage(page.getTotalPages())
                .totalSize(page.getTotalElements())
                .content(page.getContent())
                .size(page.getSize()).build();

    }
}
