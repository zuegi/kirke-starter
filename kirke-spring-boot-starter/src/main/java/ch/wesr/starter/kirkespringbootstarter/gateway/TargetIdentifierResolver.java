package ch.wesr.starter.kirkespringbootstarter.gateway;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class TargetIdentifierResolver {

    public static UUID resolve(Object object, Class<?> aggregateIdentifierClass) {
        UUID targetIdentifier = null;
        try {
            Field field = getField(object, aggregateIdentifierClass);
            if (field != null) {
                field.setAccessible(true);
                targetIdentifier = (UUID) field.get(object);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return targetIdentifier;
    }

    private static Field getField(Object object, Class<?> aggregateIdentifierClass) {
        List<Field> fieldList = new AggregatedFieldResolver()
                .filterClasses(object.getClass())
                .filterFieldAnnotationWith(aggregateIdentifierClass)
                .resolve();
        if (fieldList == null || fieldList.isEmpty()) {
            return null;
        }
        Field field = fieldList.get(0);
        assert fieldList.size() == 1;
        return field;
    }
}
