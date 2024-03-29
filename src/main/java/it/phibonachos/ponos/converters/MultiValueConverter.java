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
        if(props.length != arity())
            throw new IllegalArgumentException(String.format("arity not respected: %s required over %s passed (%s)", arity(), props.length, this.getClass().getCanonicalName()));

        try {
            return convertAll(props);
        } catch (Exception e) {
            throw new ConverterException(this.message());
        }
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
