package it.phibonachos.ponos.converters;

import java.lang.reflect.InvocationTargetException;

public interface Converter<ControlType> {
    static <C, T extends Converter<C>> T create(Class<T> vti) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return vti.getConstructor().newInstance();
    }

    ControlType evaluate(Object... props) throws Exception;

    /**
     * <p>Defines converter arity, how many arguments are needed to have a meaningful conversion.</p>
     *
     * @return arity of given converter
     */
    int arity();
}
