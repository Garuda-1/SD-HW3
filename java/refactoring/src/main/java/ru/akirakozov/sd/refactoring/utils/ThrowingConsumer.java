package ru.akirakozov.sd.refactoring.utils;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}
