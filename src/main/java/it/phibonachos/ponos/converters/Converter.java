package it.phibonachos.ponos.converters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Converter<ControlType> {
    static <C, T extends Converter<C>> T create(Class<T> vti) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return vti.getConstructor().newInstance();
    }

    <Target> ControlType evaluate(Target target, Method... props) throws Exception;
}
