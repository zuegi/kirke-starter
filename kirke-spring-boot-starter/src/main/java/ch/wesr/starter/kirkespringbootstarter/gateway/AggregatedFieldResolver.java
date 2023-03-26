package ch.wesr.starter.kirkespringbootstarter.gateway;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AggregatedFieldResolver extends AbstractAggregatedBase {

    private List<FieldInfo> fieldInfos;

    public AggregatedFieldResolver filterMethodAnnotatedWith(Class<?> aggregatedMethodClass) {
        return (AggregatedFieldResolver) filterMethodAnnotatedWithHelper(aggregatedMethodClass);
    }

    public AggregatedFieldResolver filterClasses(Class<?> aggregatedClassClass) {
        return (AggregatedFieldResolver) filterClassesHelper(aggregatedClassClass);
    }

    public AggregatedFieldResolver filterFieldAnnotationWith(Class<?> aggregatedFieldResolverClass) {
        if (classInfoList == null || classInfoList.isEmpty()) {
            classInfoList = result.getAllClasses();
        }

        fieldInfos = classInfoList.stream()
                .filter(classInfo -> classInfo.hasDeclaredFieldAnnotation((Class<? extends Annotation>) aggregatedFieldResolverClass))
                .map(ClassInfo::getDeclaredFieldInfo)
                .flatMap(Collection::stream)
                .filter(fieldInfo -> fieldInfo.hasAnnotation((Class<? extends Annotation>) aggregatedFieldResolverClass))
                .toList();
        return this;
    }

    public List<Field> resolve() {
        if (fieldInfos == null || fieldInfos.isEmpty()) {
            return new ArrayList<>();
        }
        return fieldInfos.stream()
                .map(FieldInfo::loadClassAndGetField)
                .toList();
    }

}
