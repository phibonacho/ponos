package it.phibonachos.ponos;

import it.phibonachos.utils.FunctionalWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractEvaluator<Target, Control, A extends Annotation, E extends Exception> {
    protected Target t;
    protected Class<A> annotationClass;

    public AbstractEvaluator(Target t){
        this.t = t;
    }

    private Stream<Control> validateStream() {
        return Arrays.stream(this.t.getClass().getDeclaredMethods())
                .filter(m -> m.getAnnotation(annotationClass)!=null)
                .filter(m -> m.getParameterCount() == 0)
                .filter(this::customFilter)
                .sorted(Comparator.comparing(sortPredicate()))
                .map(validateAlgorithm());
    }

    /**
     * <p>Expose {@link #evaluate(Stream)} preventing final user from manipulating directly validation stream</p>
     * @return the result of the evaluation operated over target properties
     */
    public Control validate(){
        return evaluate(validateStream());
    }

    /**
     * @param s Stream of properties already converted to Control type
     * @return the evaluation based on properties concatenation
     */
    protected abstract Control evaluate(Stream<Control> s);

    /**
     * @return a function for sorting properties in validation stream
     */
    protected abstract Function<Method, Boolean> sortPredicate();

    /**
     * @return Conversion function from property type to Control type
     */
    protected abstract Function<Method, Control> validateAlgorithm();

    /**
     * @param m A property getter to filter
     * @return true if the property should be included in validation stream
     */
    protected Boolean customFilter(Method m) {
        return true;
    }

    /**
     * <p>Invoke method against a class and uses fallback in case of null property</p>
     * @param throwingFunction wraps method invocation
     * @param fallback invoked when throwingFunction results null
     * @param <R> parametric return type
     * @return result of the invocation in throwingFunction or value provided from fallback function
     * @throws E on evaluation failure
     */
    protected abstract  <R> Function<Method, R> invokeOnNull(FunctionalWrapper<Method, R, Exception> throwingFunction, Function<Method, R> fallback) throws E;

    /**
     * Same as {@link #invokeOnNull(FunctionalWrapper, Function)} but takes a supplier instead of a function (no params needed)
     * */
    protected abstract  <R>Function<Method, R> invokeOnNull(FunctionalWrapper<Method, R, Exception> throwingFunction, Supplier<R> fallback) throws E;


    /**
     * <p>Return getter method starting from property name</p>
     * @param throwingFunction algorithm to retrieve a getMethod using its name
     * @return the method of the given class
     * @throws RuntimeException if method is not found or other exceptions are catch
     */
    @Deprecated
    protected Function<? super String, Method> fetchMethod(FunctionalWrapper<String, Method, NoSuchMethodException> throwingFunction) throws RuntimeException{
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (NoSuchMethodException nsme) {
                throw new RuntimeException("cannot find method: " + nsme.getMessage());
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
}