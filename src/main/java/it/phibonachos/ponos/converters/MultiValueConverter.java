package it.phibonachos.ponos.converters;

/**
 * Defines a {@link Converter} which elaborate parametric verdicts, it can be used to convert a single or multiple properties.
 */
public abstract class MultiValueConverter<T> implements Converter<T> {

    /**
     * Evaluate method collect all the properties needed by the validation class and retrieves them values in order to emit a verdict.
     * @param props properties to convert (1-n)
     * @return the result of conversion.
     * @throws Exception if conversion fails.
     */
    @Override
    public T evaluate(Object... props) throws Exception {
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
