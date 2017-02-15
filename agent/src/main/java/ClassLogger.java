import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

public class ClassLogger implements ClassFileTransformer {
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		// System.out.println(className);
		if (!(className.startsWith("pl") || className.startsWith("hello"))) {
			return classfileBuffer;
		}
		if (className.contains("CGLIB")) {
			return classfileBuffer;
		}
		System.out.println("TRANSFORM " + className);
		ClassPool pool = ClassPool.getDefault();
		CtClass cl = null;
		byte[] b = null;
		try {
			cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			CtBehavior[] methods = cl.getDeclaredBehaviors();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].isEmpty() == false) {
					changeMethod(methods[i]);
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
		// CtClass sp = pool.get("SomeProcess");
		// for (CtMethod m : sp.getDeclaredMethods()) {
		// if (m.hasAnnotation(ToBeTransformed.class)) {
		// String body = // create new body
		// m.setBody(body);
		// }
		// }

		// return classfileBuffer;
	}

	private void changeMethod(CtBehavior method) throws NotFoundException, CannotCompileException {
		method.addLocalVariable("__start", CtClass.longType);
		method.addLocalVariable("__stop", CtClass.longType);
		method.insertBefore(" __start = System.currentTimeMillis();");
		method.insertAfter(" __stop = System.currentTimeMillis() - __start;Informer.info(\"" + method.getLongName()
				+ "\",__stop);");
	}
}
