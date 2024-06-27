package org.example.ex6.step3;

import org.reflections.Reflections;

import java.lang.reflect.Modifier;

import static org.reflections.ReflectionUtils.Fields;
import static org.reflections.ReflectionUtils.get;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;
import static org.reflections.util.ReflectionUtilsPredicates.withModifier;

public class Run {

    public static void main(String[] args) {
        Reflections reflections = new Reflections("org.example.ex6.step3");
        reflections.getTypesAnnotatedWith(ProductClass.class, false).forEach(
                clazz -> {
                    System.out.println(">" + clazz.getSimpleName());
                    get(
                            Fields.of(clazz)
                                    .filter(withModifier(Modifier.PUBLIC))
                    ).forEach(field -> System.out.println("---" + field.getName()));
                }
        );

        Kayak kayak = new Kayak();
        kayak.height = 40;
        kayak.width = 60;
        kayak.length = 210;

        creerEtiquette(kayak);
    }

    public static void creerEtiquette(Object product) {

        Class<?> clazz = product.getClass();
        if(!clazz.isAnnotationPresent(ProductClass.class)) {
            throw new RuntimeException("Impossible de générer une étiquette pour cet objet");
        };

        System.out.println("["+clazz.getSimpleName()+"]");

        get(
                Fields.of(clazz)
                        .filter(withAnnotation(LabelField.class))
        ).forEach(field -> {
            try {
                System.out.println(field.getName() + "=" + field.get(product));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
