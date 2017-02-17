package agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ClassLogger implements ClassFileTransformer {
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (!(className.startsWith("pl") || className.startsWith("hello"))) {
			return classfileBuffer;
		}
		if (className.contains("asen") && className.contains("RemoteBusinessHandler")) {
			return transform(classfileBuffer, "doPost");
		}
		if (className.contains("CGLIB") || className.contains("$") || className.contains("javassist")
				|| className.contains("asen")) {
			return classfileBuffer;
		}

		if (!className.contains("server")) {
			return classfileBuffer;
		}
		// System.out.println("TRANSFORM " + className);
		return transform(classfileBuffer, null);
	}

	private byte[] transform(byte[] classfileBuffer, String onlyMethod) {
		CtClass cl = null;
		byte[] b = null;
		try {
			ClassPool pool = ClassPool.getDefault();
			LoaderClassPath loaderClassPath = new LoaderClassPath(Thread.currentThread().getContextClassLoader());
			pool.appendClassPath(loaderClassPath);
			cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			pool.importPackage("agent.ProfileSession");
			CtBehavior[] methods = cl.getDeclaredBehaviors();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].isEmpty() == false) {
					if (onlyMethod != null) {
						if (onlyMethod.equals(methods[i].getName())) {
							changeMethod(methods[i]);
						}
					} else {
						changeMethod(methods[i]);
					}
					// System.out.println("TRANSFORM " + methods[i]);
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

	private void changeMethod(CtBehavior method) throws NotFoundException, CannotCompileException {
		// method.addLocalVariable("__start", CtClass.longType);
		// method.addLocalVariable("__stop", CtClass.longType);
		// method.insertBefore(" __start = System.currentTimeMillis();");
		// method.insertAfter(" __stop = System.currentTimeMillis() -
		// __start;");
		// method.addLocalVariable("__start", CtClass.longType);
		// method.addLocalVariable("__stop", CtClass.longType);
		String longName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
		method.insertBefore("ProfileSession.opStart(\"" + longName + "\");");
		method.insertAfter("ProfileSession.opStop(\"" + longName + "\");");
	}
}
