package com.apec;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;

public class ClassTransformer implements ClassFileTransformer {
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (!className.startsWith("pl/com/stream")) {
			return classfileBuffer;
		}
		if (className.contains("asen") && className.contains("RemoteBusinessHandler")) {
			return transform(classfileBuffer, "doPost", className);
		}
		if (className.contains("CGLIB") || className.contains("$") || className.contains("javassist")
				|| className.contains("asen") || className.contains("Profile")) {
			return classfileBuffer;
		}

		if (className.contains("server") || className.contains("webservice")) {
			return transform(classfileBuffer, null, className);
		}

		return classfileBuffer;
	}

	private byte[] transform(byte[] classfileBuffer, String onlyMethod, String className) {
		// System.out.println("TRANSFORM " + className);
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		ClassAdapter profiler = new ProfileClassAdapter(cw, className);
		// ClassVisitor cv = new LogMethodClassVisitor(cw, className);
		cr.accept(profiler, 0);
		return cw.toByteArray();
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
		method.insertBefore("ProfileSession.opStart(\"" + longName + "\", $args);");
		method.insertAfter("ProfileSession.opStop(\"" + longName + "\");", true);
	}
}
