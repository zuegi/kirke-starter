package ch.wesr.starter.kirkespringbootstarter.gateway;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public abstract class AbstractAggregatedBase {


    protected ScanResultCollector scanResultCollector;
    protected ScanResult result;
    protected ClassInfoList classInfoList;
    protected List<MethodInfo> methodInfoList;

    public AbstractAggregatedBase() {
        scanResultCollector = ScanResultCollector.getInstance();
        result = scanResultCollector.getResult();
    }

    AbstractAggregatedBase filterClassAnnotatedWithHelper(Class<?> aggregateClass) {
        classInfoList = result.getClassesWithAnnotation((Class<? extends Annotation>) aggregateClass);
        return this;
    }

    protected AbstractAggregatedBase filterMethodAnnotatedWithHelper(Class<?> aggregatedMethodClass) {
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

    protected AbstractAggregatedBase filterClassesHelper(Class<?> aggregatedClassClass) {
        classInfoList = result.getAllClasses().filter(classInfo -> classInfo.getName().equals(aggregatedClassClass.getName()));
        return this;
    }
}
