package it.phibonachos.utils;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface FunctionalWrapper <I, R, E extends Exception> {
    R accept(I s) throws E;

    public static <I,R> Function<I, R> tryCatch(FunctionalWrapper<I, R, Exception> throwingFunction) throws RuntimeException {
        return i -> {
            try {
                return throwingFunction.accept(i);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <I,R> Function<I, R> tryCatch(FunctionalWrapper<I, R, Exception> throwingFunction, Function<I,R> fallback) {
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (Exception e) {
                return fallback.apply(i);
            }
        };
    }

    public static <I,R> Function<I, R> tryCatch(FunctionalWrapper<I, R, Exception> throwingFunction, Supplier<R> fallback) {
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (Exception e) {
                return fallback.get();
            }
        };
    }
}
