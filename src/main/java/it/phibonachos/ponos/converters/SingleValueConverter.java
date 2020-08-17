package it.phibonachos.ponos.converters;

public abstract class SingleValueConverter<C,T> extends MultiValueConverter<C> {

    @Override
    @SuppressWarnings("unchecked")
    protected C convertAll(Object... objects) throws Exception {
        return convert((T) objects[0]);
    }

    public abstract C convert(T guard) throws Exception;

}
