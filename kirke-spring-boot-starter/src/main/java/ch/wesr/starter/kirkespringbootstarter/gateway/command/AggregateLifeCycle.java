package ch.wesr.starter.kirkespringbootstarter.gateway.command;




import ch.wesr.starter.kirkespringbootstarter.annotation.EventHandler;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * NOTE: This class should never be made a spring bean as its method are static for using in pojos
 */
public class AggregateLifeCycle {

    // mit dieser Methode wollen wir ein existierendes Bean aufrufen, dazu hilft uns die Klasse SpringContext
    public static void apply(Object event) {

        List<Method> methods = new AggregatedMethodResolver()
                .filterMethodAnnotatedWith(EventHandler.class)
                .filterMethodParameter(event)
                .resolve();

        methods.forEach(method -> {
            Class<?> declaringClass = method.getDeclaringClass();

            Object bean = SpringContext.getBean(declaringClass);

            try {
                method.invoke(bean, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        });


    }

}
