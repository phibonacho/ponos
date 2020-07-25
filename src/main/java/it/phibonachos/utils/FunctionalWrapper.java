package it.phibonachos.utils;

@FunctionalInterface
public interface FunctionalWrapper <I, R, E extends Exception> {
    R accept(I s) throws E;
}
