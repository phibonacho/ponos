package it.phibonachos.ponos.converters;

import it.phibonachos.utils.FunctionalWrapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Defines a {@link Converter} which elaborate parametric verdicts, it can be used to convert a single or multiple properties.
 */
public abstract class MultiValueConverter<T> implements Converter<T> {

    /**
     * Evaluate method collect all the properties needed by the validation class and retrieves them values in order to emit a verdict.
     * @param target Object from which props will be invoked
     * @param props properties bounded to validation class (1-n)
     * @param <Target> Type of target param
     * @return true if all clauses are met.
     * @throws Exception if at least one clause is not met or validate algorithm emit a negative verdict.
     */
    public <Target> T evaluate(Target target, Method... props) throws Exception {
        Supplier<Stream<Object>> sup = () -> Arrays.stream(props).map(FunctionalWrapper.tryCatch(m -> m.invoke(target), m -> null));
        if(sup.get().anyMatch(Objects::isNull))
            throw new NullPointerException();

        return convertAll(sup.get().toArray());
    }

    /**
     * @param objects properties bundled to validation.
     * @return true if validation algorithm emit a positive verdict.
     * @throws Exception if validation algorithm emit a negative verdict.
     */
    protected abstract T convertAll(Object ...objects) throws Exception;

    /**
     * @return the string specifying why the validation failed.
     */
    public String message() {
        return "fails conversion defined in " + this.getClass().getSimpleName();
    }
}
