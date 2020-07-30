
package com.apec;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ClassTransformer implements ClassFileTransformer {

	private static final List<String> SKIP = new ArrayList();
	static {
		SKIP.add("ImportServiceImpl");
	}

	private static boolean INITIALIZED = false;
	private ClassLoader contextClassLoader;
	private boolean performance;
	private boolean tracking;

	public ClassTransformer(final String args) {
		if (args == null) {
			this.performance = true;
			this.tracking = true;
		} else if ("p".equals(args)) {
			this.performance = true;
		} else if ("e".equals(args)) {
			this.tracking = true;
		} else if ("ep".equals(args) || "pe".equals(args)) {
			this.performance = true;
			this.tracking = true;
		}

	}

	private boolean skip(final String className) {
		for (String toSkip : SKIP) {
			if (className.contains(toSkip)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
		if (loader.toString().contains("next-url-module-loader")) {
			return classfileBuffer;
		}
		ClassPool pool = ClassPool.getDefault();
		if (!INITIALIZED) {
			contextClassLoader = Thread.currentThread().getContextClassLoader();
			LoaderClassPath loaderClassPath = new LoaderClassPath(contextClassLoader);
			pool.appendClassPath(loaderClassPath);
			pool.importPackage("agent.ProfileSession");
			INITIALIZED = true;
		}
		if (contextClassLoader != Thread.currentThread().getContextClassLoader()) {
			contextClassLoader = Thread.currentThread().getContextClassLoader();
			LoaderClassPath loaderClassPath = new LoaderClassPath(contextClassLoader);
			pool.appendClassPath(loaderClassPath);
		}
		if (!className.startsWith("pl")) {
			return classfileBuffer;
		}
		if (className.contains("/client/")) {
			return classfileBuffer;
		}
		if (skip(className)) {
			return classfileBuffer;
		}
		if (className.contains("asen") && className.contains("FrontControllerImpl")) {
			return transform(classfileBuffer, "executeImpl", className, false);
		}
		if (className.contains("asen") && className.contains("GroovyScriptExecutor")) {
			return transform(classfileBuffer, "execute", className, false);
		}
		if (className.contains("asen") && className.contains("DatabaseAccessServiceImpl")) {
			return transform(classfileBuffer, "executeQuery", className, true);
		}
		if (className.contains("asen") && className.contains("QueryExecutorServiceImpl")) {
			return transform(classfileBuffer, Arrays.asList("executeQuery", "executeSQLQuery"), className, false);
		}
		if (className.contains("domain/entity")) {
			return transformEntity(classfileBuffer, className);
		}
		if (className.contains("asen") && className.contains("DetachedQueryExecutorImpl")) {
			return transform(classfileBuffer, Arrays.asList("execute", "executeOnQueryPool"), className, true);
		}
		if (className.contains("CGLIB") || className.contains("$") || className.contains("javassist") || className.contains("asen")) {
			return classfileBuffer;
		}

		if (className.contains("server") || className.contains("webservice")) {
			return transform(classfileBuffer, className, false);
		}

		return classfileBuffer;
	}

	private byte[] transformEntity(final byte[] classfileBuffer, final String className) {
		if (!this.tracking) {
			return classfileBuffer;
		}
		ClassPool pool = ClassPool.getDefault();
		// System.out.println("TRANSFORM " + className);
		CtClass cl = null;
		byte[] b = null;
		try {
			cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			if ("AbstractEntity".equals(cl.getSuperclass().getSimpleName())) {
				CtBehavior[] methods = cl.getDeclaredBehaviors();
				List<String> fieldsNames =
						Arrays.asList(cl.getDeclaredFields()).stream().map(CtField::getName).collect(Collectors.toList());
				for (CtBehavior method : methods) {
					if (method.getModifiers() != Modifier.PUBLIC) {
						continue;
					}
					if (method.getName().equals("setId" + cl.getSimpleName())) {
						continue;
					}
					if (method.getName().startsWith("set")) {
						String fieldName = method.getName().replaceFirst("set", "");
						fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
						if (!fieldsNames.contains(fieldName)) {
							continue;
						}
						String longName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
						if (longName.contains("$")) {
							continue;
						}
						try {
							method.insertBefore("firePropertyChange(\"" + fieldName + "\", this." + fieldName + ", $1);");
						} catch (CannotCompileException e) {
							throw new CannotCompileException(longName, e);
						}
					}
				}
			}
			b = cl.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cl != null) {
				cl.detach();
			}
		}
		return b;
	}

	private byte[] transform(final byte[] classfileBuffer, final String onlyMethod, final String className, final boolean onlyFirstParam) {
		return transform(classfileBuffer, Arrays.asList(onlyMethod), className, onlyFirstParam);
	}

	private byte[] transform(final byte[] classfileBuffer, final String className, final boolean onlyFirstParam) {
		return transform(classfileBuffer, Collections.EMPTY_LIST, className, onlyFirstParam);
	}

	private byte[] transform(final byte[] classfileBuffer, final List<String> onlyMethod, final String className,
			final boolean onlyFirstParam) {
		if (!this.performance) {
			return classfileBuffer;
		}
		ClassPool pool = ClassPool.getDefault();
		// System.out.println("TRANSFORM " + className);
		CtClass cl = null;
		byte[] b = null;
		try {
			cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			CtBehavior[] methods = cl.getDeclaredBehaviors();
			for (CtBehavior method : methods) {
				if (method.isEmpty() == false) {
					if (!onlyMethod.isEmpty()) {
						if (onlyMethod.contains(method.getName())) {
							changeMethod(method, onlyFirstParam);
						}
					} else {
						changeMethod(method, onlyFirstParam);
					}
					// System.out.println("TRANSFORM " + methods[i]);
				}
			}
			b = cl.toBytecode();

			// cl.setName("Main");

			// cl.writeFile("c:\\");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cl != null) {
				cl.detach();
			}
		}
		return b;
	}

	private void changeMethod(final CtBehavior method, final boolean onlyFirstParam) throws NotFoundException, CannotCompileException {

		if (method.getModifiers() == Modifier.PROTECTED) {
			// return;
		}
		// method.addLocalVariable("__start", CtClass.longType);
		// method.addLocalVariable("__stop", CtClass.longType);
		// method.insertBefore(" __start = System.currentTimeMillis();");
		// method.insertAfter(" __stop = System.currentTimeMillis() -
		// __start;");
		// method.addLocalVariable("__start", CtClass.longType);
		// method.addLocalVariable("__stop", CtClass.longType);
		String longName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
		if (longName.contains("$")) {
			return;
		}
		try {
			if (onlyFirstParam) {
				method.insertBefore("ProfileSession.opStart(\"" + longName + "\", $1);");
			} else {
				method.insertBefore("ProfileSession.opStart(\"" + longName + "\", $args);");

			}
			method.insertAfter("ProfileSession.opStop(\"" + longName + "\");", true);
		} catch (CannotCompileException e) {
			throw new CannotCompileException(longName, e);
		}
	}
}
