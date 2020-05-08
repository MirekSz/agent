
package com.apec;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProfileClassAdapter extends ClassVisitor {

	private final String className;

	public ProfileClassAdapter(final ClassVisitor cw, final String className) {
		super(Opcodes.ASM4, cw);
		this.className = className;
	}

	@Override
	public void visitAttribute(final Attribute attr) {
		// TODO Auto-generated method stub
		super.visitAttribute(attr);
	}

	@Override
	public void visit(final int version, final int access, final String name, final String signature, final String superName,
			final String[] interfaces) {
		// TODO Auto-generated method stub
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
			final String[] exceptions) {

		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

		return new ProfileMethodAdapter(mv, className, name);
	}

}
