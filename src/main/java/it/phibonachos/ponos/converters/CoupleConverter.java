package it.phibonachos.ponos.converters;

/**
 * SingleValueConverter is a sample class that shows how fixed arity converter can be implemented starting from {@link MultiValueConverter}
 * @param <C> Is the return type of the converter
 * @param <F> Is the first input type of the converter
 * @param <S> Is the second input type of the converter
 */

public abstract class CoupleConverter<C,F,S> extends MultiValueConverter<C> {

    @Override
    @SuppressWarnings("unchecked")
    protected C convertAll(Object... objects) throws Exception {
        return convert((F) objects[0], (S)objects[1]);
    }

    public abstract C convert(F guard, S boundGuard) throws Exception;
}
