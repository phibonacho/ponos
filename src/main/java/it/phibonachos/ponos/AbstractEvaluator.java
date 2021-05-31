package it.phibonachos.ponos;

import it.phibonachos.ponos.converters.Converter;
import it.phibonachos.ponos.converters.ConverterException;
import it.phibonachos.utils.FunctionalWrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractEvaluator<Target, Control, A extends Annotation, E extends Exception> {
    protected Target t;
    protected Class<A> annotationClass;
    protected Map<String,Object> cache;

    public AbstractEvaluator(Target t) {
        this.t = t;
        this.cache = new HashMap<>();
    }

    /**
     * <p>Wraps {@link #processAll()} preventing final user from manipulating directly validation stream</p>
     *
     * @return the result of the evaluation operated over target properties
     */
    public Control evaluate() throws Exception {
        return processAll();
    }

    /**
     * @param annotation the annotation from which retrieve converter class information
     * @return an instance of the converter class
     */
    protected abstract Class<? extends Converter<Control>> fetchConverter(A annotation);

    /**
     * @return the evaluation based on properties concatenation
     */
    protected abstract BinaryOperator<Control> evaluationReductor();

    /**
     * <p>Defines a procedure to sort properties to validate.</p>
     *
     * @return a comparator for sorting properties in validation stream
     */
    public abstract Comparator<Method> comparingPredicate();

    /**
     * @param m A property getter to filter
     * @return true if the property should be included in validation stream
     */
    protected Boolean customFilter(Method m) {
        return true;
    }

    /**
     * <p>Handy way to provide a fallback in functional java</p>
     * <p>Invoke method against a class and uses fallback in case of null property</p>
     *
     * @param throwingFunction wraps method invocation
     * @param fallback         invoked when throwingFunction results null
     * @param <R>              parametric return type
     * @return result of the invocation in throwingFunction or value provided from fallback function
     */
    protected <R> Function<Method, R> invokeOnNull(FunctionalWrapper<Method, R, Exception> throwingFunction, Function<Method, R> fallback) {
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (NullPointerException npe) {
                return fallback.apply(i);
            } catch (ConverterException ce) {
                throw ce;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Same as {@link #invokeOnNull(FunctionalWrapper, Function)} but takes a supplier instead of a function (no params needed)
     */
    protected <R> Function<Method, R> invokeOnNull(FunctionalWrapper<Method, R, Exception> throwingFunction, Supplier<R> fallback) {
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (NullPointerException npe) {
                return fallback.get();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        };
    }

    /**
     * @param m Method from which retrieve target annotation
     * @return target annotation
     */
    protected A getMainAnnotation(Method m) {
        return m.getAnnotation(annotationClass);
    }

    /**
     * This method provides an handy way to developer to alter property evaluation/process avoiding evaluator preparing phase to be exposed.
     *
     * @param annotation the target property custom annotation, carrying all the information useful to evaluation
     * @param converter the converter class stated in annotation
     * @param property the property about to be evaluated
     * @return the evaluation of the property
     * @throws Exception if evaluation fails
     */
    protected Control process(A annotation, Converter<Control> converter, Object property, Method method) throws Exception {
        return converter.evaluate(property);
    }

    /**
     * @param properties are the properties needed from target object
     * @return a list of objects representing the required properties
     */
    protected Object[] fetchValues(String ...properties) {
        List<Object> result = new ArrayList<>();
        for(String property : properties)
            if(!cache.containsKey(property))
                try {
                    result.add(fetchValue(property));
                } catch (Exception ignored) {
                    // mmmh
                }
            else
                result.add(cache.get(property));

        return result.toArray();
    }

    protected Object fetchValue(String propName) {
        try {
            return fetchValue(new PropertyDescriptor(propName, this.t.getClass()).getReadMethod());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected Object fetchValue(Method getter) {
        try {
            Object aux = getter.invoke(this.t);
            cache.put(getter.getName(), aux);
            return aux;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param target the property getter being evaluated
     * @return the result of {@link #process(Annotation, Converter, Object, Method)}
     * @throws Exception if converter class cannot be instantiated
     */
    private Control prepare(Method target) throws Exception {
        A annotation = getMainAnnotation(target);
        Converter<Control> converter = Converter.create(fetchConverter(annotation));
        Object property = fetchValue(target);

        return process(annotation, converter, property, target);
    }

    private Control processAll() throws Exception {
        return Arrays.stream(this.t.getClass().getDeclaredMethods())
                .filter(m -> getMainAnnotation(m) != null)
                .filter(m -> m.getParameterCount() == 0)
                .filter(this::customFilter)
                .sorted(comparingPredicate())
                .map(FunctionalWrapper.tryCatch(this::prepare))
                .reduce(evaluationReductor()).orElseThrow(RuntimeException::new);
    }

}