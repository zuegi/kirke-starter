package ch.wesr.starter.kirkesampleapp.feature.food;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.FoodCartCreatedEvent;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

class AggregatedMethodResolverUnitTest {


    @Test
    void filter_for_all_method_with_class_annotation() {
        // given
        AggregatedMethodResolver aggregatedMethodResolver = new AggregatedMethodResolver();
        // when
        List<Method> methodList = aggregatedMethodResolver.filterClassAnnotatedWith(Pillepalle.class)
                .resolve();
        // then
        Assertions.assertThat(methodList).hasSize(6);
    }

    @Test
    void filter_for_all_annotated_method_with_class_annotation() {
        // given
        AggregatedMethodResolver aggregatedMethodResolver = new AggregatedMethodResolver();
        // when
        List<Method> methodList = aggregatedMethodResolver.filterClassAnnotatedWith(Pillepalle.class)
                .filterMethodAnnotatedWith(TestCommandHandler.class)
                .resolve();
        // then
        Assertions.assertThat(methodList).hasSize(2);
    }

    @Test
    void filter_for_all_annotated_method_with_param_with_class_annotation() {
        // given
        CreateFoodCartCommand command = new CreateFoodCartCommand(UUID.randomUUID());
        AggregatedMethodResolver aggregatedMethodResolver = new AggregatedMethodResolver();
        // when
        List<Method> methodList = aggregatedMethodResolver.filterClassAnnotatedWith(Pillepalle.class)
                .filterMethodAnnotatedWith(TestCommandHandler.class)
                .filterMethodParameter(command)
                .resolve();
        // then
        Assertions.assertThat(methodList).hasSize(1);
    }

    @Test
    void filter_all_annotated_method_but_no_class_annotation() {
        List<Method> methodList = new AggregatedMethodResolver()
                .filterMethodAnnotatedWith(MethodPillepalle.class)
                .resolve();

        Assertions.assertThat(methodList).hasSize(2);
    }

    @Test
    void filter_when_no_method_annotation_found() {
        List<Method> methodList = new AggregatedMethodResolver()
                .filterMethodAnnotatedWith(DoesNotExist.class)
                .resolve();

        Assertions.assertThat(methodList)
                .hasSize(0);
    }

    /*
        Inner TestClass
     */
    @Pillepalle
    public static class TestPillepalle {

        public static void method1() {
        }

        @MethodPillepalle
        public static void method2() {
        }

        @MethodPillepalle
        public static void method3() {
        }

        @TestCommandHandler
        public static void createFoodCartCommand(CreateFoodCartCommand commmand) {
        }

        @TestCommandHandler
        public static void addSelectedProduct(AddSelectedProduct commmand) {
        }

        @TestEventHandler
        public static void methodWithEvent(FoodCartCreatedEvent event) {
        }

    }


    // Inner Annotation for testing purpose
    public @interface Pillepalle {
    }

    public @interface MethodPillepalle {
    }

    public @interface DoesNotExist {

    }

    public @interface TestCommandHandler {
    }

    public @interface TestEventHandler {
    }


    private record AddSelectedProduct() {
    }

}

