package com.dhemery.runtimesuite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.InitializationError;

public class RuntimeSuiteAdapter {
	private final Object suite;

	public RuntimeSuiteAdapter(Class<?> suiteClass) throws InitializationError {
		suite = instantiate(suiteClass);
	}

	private Object instantiate(Class<?> suiteClass) throws InitializationError {
		try {
			return suiteClass.newInstance();
		} catch (Throwable cause) {
			throw new InitializationError(cause);
		}
	}

	public List<ClassFinder> finders() throws InitializationError {
		return matchingMembers(Finder.class, ClassFinder.class);
	}

	public List<ClassFilter> filters() throws InitializationError {
		return matchingMembers(Filter.class, ClassFilter.class);
	}

	@SuppressWarnings("unchecked")
	private <T> T member(Field memberField) throws InitializationError {
		try {
			return (T) memberField.get(suite);
		} catch (Throwable cause) {
			throw new InitializationError(cause);
		}
	}

	private <T> List<T> matchingMembers(Class<? extends Annotation> annotation, Class<T> type) throws InitializationError {
		List<T> result = new ArrayList<T>();
		for(Field field : suite.getClass().getFields()) {
			if(matches(field, annotation, type)) {
				result.add(this.<T>member(field));
			}
		}
		return result;
	}

	private <T> boolean matches(Field field, Class<? extends Annotation> annotation, Class<T> type) {
		return hasAnnotation(field, annotation) && hasType(field, type);
	}

	private boolean hasAnnotation(Field field, Class<? extends Annotation> annotation) {
		return field.isAnnotationPresent(annotation);
	}

	private boolean hasType(Field field, Class<?> type) {
		return type.isAssignableFrom(field.getType());
	}

}
