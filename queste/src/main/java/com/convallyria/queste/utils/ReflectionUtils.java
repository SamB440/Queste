package com.convallyria.queste.utils;

import com.convallyria.queste.gui.GuiEditable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReflectionUtils {

    /**
     * Gets all fields from all super classes (up to Object.class) with the specified annotation.
     * @param target origin class
     * @param annotation annotation to find
     * @return {@link CompletableFuture} with a {@link List<Field>} of the annotated fields
     */
    public static CompletableFuture<List<Field>> getSuperFieldsFromAnnotationAsync(Class<?> target, Class<? extends Annotation> annotation) {
        System.out.println("aaaa");
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("bbbb");
            List<Field> fields = new ArrayList<>();
            Class<?> current = target;
            System.out.println(current);
            while (!current.equals(Object.class)) {
                System.out.println(current);
                for (Field declaredField : current.getDeclaredFields()) {
                    System.out.println(declaredField);
                    if (declaredField.isAnnotationPresent(GuiEditable.class)) {
                        fields.add(declaredField);
                    }
                }
                current = current.getSuperclass();
            }
            return fields;
        });
    }
}
