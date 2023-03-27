package ch.wesr.starter.kirkesampleapp.feature.food.shared;


import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedFieldResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

class AggregatedFieldResolverUnitTest {


    @Test
    void field_and_method_resolve() throws IllegalAccessException {
        UUID fieldId = UUID.randomUUID();
        UUID selectedProductFieldId = UUID.randomUUID();
        FieldSelectedProductEvent fieldSelectedProductEvent = new FieldSelectedProductEvent(fieldId, selectedProductFieldId);
        List<Field> fieldList = new AggregatedFieldResolver()
                .filterClasses(fieldSelectedProductEvent.getClass())
                .filterFieldAnnotationWith(FieldResolverFieldAnnotation.class)
                .resolve();

        Assertions.assertThat(fieldList).hasSize(1);
        Field field = fieldList.get(0);
        field.setAccessible(true);
        Object resolvedSelectedProductFieldId = field.get(fieldSelectedProductEvent);

        Assertions.assertThat(selectedProductFieldId).isEqualTo(resolvedSelectedProductFieldId);

    }

    @Test
    void field_resolve() throws IllegalAccessException {

        UUID fieldId = UUID.randomUUID();
        FieldResolverEvent fieldResolverEvent = new FieldResolverEvent(fieldId);

        List<Field> fieldList = new AggregatedFieldResolver()
                .filterClasses(fieldResolverEvent.getClass())
                .filterFieldAnnotationWith(FieldResolverFieldAnnotation.class)
                .resolve();

        Assertions.assertThat(fieldList).hasSize(1);
        Field field = fieldList.get(0);

        field.setAccessible(true);
        Object resolvedFieldId = field.get(fieldResolverEvent);

        Assertions.assertThat(fieldId).isEqualTo(resolvedFieldId);
    }
}

record FieldResolverEvent(@FieldResolverFieldAnnotation UUID fieldId) {
}

record FieldSelectedProductEvent(UUID fieldId, @FieldResolverFieldAnnotation UUID selectedProductId) {
}

@interface FieldResolverFieldAnnotation {
}
