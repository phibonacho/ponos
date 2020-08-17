package it.phibonachos.ponos.converters;

public abstract class CoupleConverter<C,F,S> extends MultiValueConverter<C> {

    @Override
    @SuppressWarnings("unchecked")
    protected C convertAll(Object... objects) throws Exception {
        return convert((F) objects[0], (S)objects[1]);
    }

    public abstract C convert(F guard, S boundGuard);
}
