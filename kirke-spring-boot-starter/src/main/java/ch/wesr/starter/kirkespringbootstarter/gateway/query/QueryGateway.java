package ch.wesr.starter.kirkespringbootstarter.gateway.query;


import ch.wesr.starter.kirkespringbootstarter.annotation.QueryHandler;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Component
public class QueryGateway {


    public <T> T query(Object query, Class<T> type) {

        List<Method> methods = getMethods(query);

        assert methods.size() == 1;

        Method method = methods.get(0);
        Class<?> declaringClass = method.getDeclaringClass();

        Object bean = SpringContext.getBean(declaringClass);
        Object viewObject = null;

        try {
           viewObject =  method.invoke(bean, query);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }


        return (T) viewObject;
    }

    private static List<Method> getMethods(Object query) {
        return new AggregatedMethodResolver()
                .filterMethodAnnotatedWith(QueryHandler.class)
                .filterMethodParameter(query)
                .resolve();
    }
}
