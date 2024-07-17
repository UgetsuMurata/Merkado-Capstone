package com.capstone.merkado.DataManager.ValueReturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Generic interface for returning values in a callback.
 * This interface allows an object to return a value of type {@code T} asynchronously or in response to some event.
 *
 * @param <T> The type of the value to be returned.
 */
public interface ValueReturnWithStatus<T> {
    /**
     * Callback for returning a value.
     * This method is called to provide a value of type {@code T}, which can be {@code null}.
     *
     * @param t            The value to return, which can be {@code null}.
     * @param returnStatus Returned status.
     */
    void valueReturn(@Nullable T t, @NonNull ReturnStatus returnStatus);
}
