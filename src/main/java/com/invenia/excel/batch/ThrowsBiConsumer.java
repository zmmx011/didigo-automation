package com.invenia.excel.batch;

import java.util.Objects;

@FunctionalInterface
public interface ThrowsBiConsumer<T, U> {
	void accept(T t, U u) throws Exception;

	default ThrowsBiConsumer<T, U> andThen(ThrowsBiConsumer<? super T, ? super U> after) {
		Objects.requireNonNull(after);
		return (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}
