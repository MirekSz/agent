
package com.apec;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

public class SisClassTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

		if (className.contains("Customer")) {

			ClassPool pool = ClassPool.getDefault();
			System.out.println("TRANSFORM " + className);
			CtClass cl = null;
			byte[] b = null;
			try {
				CtClass makeClass = pool.makeClass("Yeb");
				makeClass.addInterface(pool.get(Runnable.class.getName()));
				makeClass.addField(CtField.make("Object o;", makeClass));
				makeClass
						.addConstructor(CtNewConstructor.make("public " + makeClass.getSimpleName() + "(Object o) {this.o=o;}", makeClass));
				makeClass.addMethod(CtMethod.make("public void run(){System.out.println(\"hello\"+this+\" \"+this.o);}", makeClass));
				makeClass.toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());

				cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
				CtBehavior[] methods = cl.getDeclaredBehaviors();

				cl.addMethod(CtMethod.make("public void hello(){	new java.lang.Thread(new Yeb(this)).start();}", cl));
				for (CtBehavior method : methods) {
					if (method.getName().contains("Name")) {
						method.insertBefore("hello();");
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

		} else if (className.contains("Child")) {

			ClassPool pool = ClassPool.getDefault();
			System.out.println("TRANSFORM " + className);
			CtClass cl = null;
			byte[] b = null;
			try {
				CtClass ctClass = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
				ctClass.setSuperclass(pool.get("pl.com.Base"));
				ctClass.addInterface(pool.get(Runnable.class.getName()));
				ctClass.addMethod(CtNewMethod.make("public void hello2() {System.out.println(\"runtime\");}", ctClass));
				b = ctClass.toBytecode();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cl != null) {
					cl.detach();
				}
			}
			return b;
		}
		return classfileBuffer;
	}

}
