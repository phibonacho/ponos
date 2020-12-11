package it.phibonachos.ponos.converters;

import java.util.Arrays;
import java.util.Objects;

/**
 * Defines a {@link Converter} which elaborate parametric verdicts, it can be used to convert a single or multiple properties.
 */
public abstract class MultiValueConverter<T> implements Converter<T> {

    /**
     * Evaluate method collect all the properties needed by the validation class and retrieves them values in order to emit a verdict.
     * @param props properties bounded to validation class (1-n)
     * @return true if all clauses are met.
     * @throws Exception if at least one clause is not met or validate algorithm emit a negative verdict.
     */
    @Override
    public T evaluate(Object... props) throws Exception {
        if(Objects.isNull(props[0]))
            throw new NullPointerException();

        if(Arrays.stream(props).allMatch(Objects::isNull))
            throw new RuntimeException("All bounded properties are not instantiated");

        return convertAll(props);
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
