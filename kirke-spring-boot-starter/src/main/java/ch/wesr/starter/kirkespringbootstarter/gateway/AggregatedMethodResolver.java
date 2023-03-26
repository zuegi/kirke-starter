package ch.wesr.starter.kirkespringbootstarter.gateway;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AggregatedMethodResolver extends AbstractAggregatedBase {

    private boolean classFilter;
    private boolean methodFilter;
    private boolean methodParamFilter;
    private Object parameterObject;


    // FIXME Reihenfolge der Methode muss fixiert werden, welche Methode kann mit welcher Method kombiniert werden?
    public AggregatedMethodResolver() {
        scanResultCollector = ScanResultCollector.getInstance();
        result = scanResultCollector.getResult();
    }

    public AggregatedMethodResolver filterClassAnnotatedWith(Class<?> aggregateClass) {
        classInfoList = result.getClassesWithAnnotation((Class<? extends Annotation>) aggregateClass);
        methodInfoList = classInfoList.stream()
                .map(ClassInfo::getMethodInfo)
                .flatMap(Collection::stream)
                .toList();
        return this;
    }

    public AggregatedMethodResolver filterMethodAnnotatedWith(Class<?> aggregatedMethodClass) {
        if (classInfoList == null || classInfoList.isEmpty()) {
            classInfoList = result.getAllClasses();
        }
        // filter und gib die Klasse zurück, damit weitere Methoden aufgelistet werden können
        // muss also so eine Art Builder sein? In einem Singleton?
        // geht das überhaupt
        methodInfoList = classInfoList.stream()
                .map(ClassInfo::getMethodInfo)
                .flatMap(Collection::stream)
                .filter(methodInfo -> methodInfo.hasAnnotation((Class<? extends Annotation>) aggregatedMethodClass))
                .toList();

        return this;
    }

    public AggregatedMethodResolver filterMethodParameter(Object parameterObject) {
        this.parameterObject = parameterObject;
        methodParamFilter = true;
        return this;
    }


    //
    public List<Method> resolveExactlyOne() {
        return null;
    }

    public List<Method> resolve() {
        if ( methodParamFilter) {
            return methodInfoList.stream()
                    .map(MethodInfo::loadClassAndGetMethod)
                    .filter(m -> Arrays.stream(m.getParameterTypes())
//                            .peek(p -> System.out.println("ParameterType: " + p.getSimpleName()))
                            .anyMatch(parameterType -> parameterType.isInstance(parameterObject)))
                    .toList();
        }
        // else
        return methodInfoList.stream()
                .map(MethodInfo::loadClassAndGetMethod)
                .toList();
    }


}
