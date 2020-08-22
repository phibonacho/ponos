package it.phibonachos.ponos.converters;

/**
 * SingleValueConverter is a sample class that shows how fixed arity converter can be implemented starting from {@link MultiValueConverter}
 * @param <C> Is the return type of the converter
 * @param <T> Is the input type of the converter
 */
public abstract class SingleValueConverter<C,T> extends MultiValueConverter<C> {

    @Override
    @SuppressWarnings("unchecked")
    protected C convertAll(Object... objects) throws Exception {
        return convert((T) objects[0]);
    }

    public abstract C convert(T guard) throws Exception;

}
