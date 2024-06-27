package org.example.ex6.step2;

import org.reflections.Reflections;

import java.lang.reflect.Modifier;

import static org.reflections.ReflectionUtils.*;
import static org.reflections.util.ReflectionUtilsPredicates.withModifier;

public class Run {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("org.example.ex6.step2");
        reflections.getTypesAnnotatedWith(ProductClass.class, false).forEach(
                clazz -> {
                    System.out.println(">" + clazz.getSimpleName());
                    get(
                            Fields.of(clazz)
//                                    .filter(withModifier(Modifier.PRIVATE))
                    ).forEach(field -> System.out.println("---" + field.getName()));
                }
        );
    }
}
