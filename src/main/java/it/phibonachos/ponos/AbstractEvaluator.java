package it.phibonachos.ponos;

import it.phibonachos.ponos.converters.Converter;
import it.phibonachos.ponos.converters.SingleValueConverter;
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
                .filter(m -> getMainAnnotation(m)!=null)
                .filter(m -> m.getParameterCount() == 0)
                .filter(this::customFilter)
                .sorted(Comparator.comparing(sortPredicate()))
                .map(evaluateAlgorithm());
    }

    /**
     * <p>Expose {@link #evaluate(Stream)} preventing final user from manipulating directly validation stream</p>
     * @return the result of the evaluation operated over target properties
     */
    public Control evaluate(){
        return evaluate(validateStream());
    }

    protected Class<? extends Converter<Control>> fetchConverter(A annotation) throws Exception {
        throw new RuntimeException("fetchConverter is not implemented: provide a method in your custom annotation to retrieve a conversion class.");
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
    protected abstract Function<Method, Control> evaluateAlgorithm();

    /**
     * <p>Validate method with a generic validate interface<p/>
     * @param a annotation that must expose a method to retrieve a {@link Converter}
     * @param methods, method to validate
     * @return true if method return a valid value
     * @throws Exception if not valid
     */
    protected Control evaluateMethod(A a, Method ...methods) throws Exception {
        Converter<Control> validator = Converter.create(fetchConverter(a));

        if(validator instanceof SingleValueConverter)
            return validator.evaluate(this.t, methods[0]);

        return validator.evaluate(this.t, methods);
    }



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
     * @param throwingFunction wraps method invocation
     * @param fallback invoked when throwingFunction results null
     * @param <R> parametric return type
     * @return result of the invocation in throwingFunction or value provided from fallback function
     * @throws E on evaluation failure
     */
    protected  <R> Function<Method, R> invokeOnNull(FunctionalWrapper<Method, R, Exception> throwingFunction, Function<Method, R> fallback) {
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (NullPointerException npe) {
                return fallback.apply(i);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Same as {@link #invokeOnNull(FunctionalWrapper, Function)} but takes a supplier instead of a function (no params needed)
     * */
    protected  <R>Function<Method, R> invokeOnNull(FunctionalWrapper<Method, R, Exception> throwingFunction, Supplier<R> fallback) {
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
     * <p>Return getter method starting from property name</p>
     * @param throwingFunction algorithm to retrieve a getMethod using its name
     * @return the method of the given class
     * @throws RuntimeException if method is not found or other exceptions are catch
     */
    @Deprecated(since = "v0.1.1", forRemoval = true)
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