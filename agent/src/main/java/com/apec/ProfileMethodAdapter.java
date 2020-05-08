
package com.apec;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProfileMethodAdapter extends MethodVisitor {

	Map<String, String> variables = new HashMap();

	public ProfileMethodAdapter(final MethodVisitor mv, final String className, final String methodName) {
		super(Opcodes.ASM4, mv);
		_className = className;
		_methodName = methodName;
		System.out.println("Profiled " + methodName + " in class " + className + ".");
	}

	private final String _className, _methodName;

	@Override
	public void visitLabel(final Label arg0) {
		super.visitLabel(arg0);
	}

	// @Override
	// public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
	// if (opcode == Opcodes.PUTFIELD) {
	// final Type fieldType = Type.getType(desc);
	// super.swap(Type.getObjectType(owner), fieldType);
	// if (fieldType.getSize() == 1) {
	// super.dupX1();
	// } else {
	// super.dupX2();
	// }
	// super.visitLdcInsn(name);
	// super.visitMethodInsn(Opcodes.INVOKESTATIC, "Guard", "fieldWrite", "(Ljava/lang/Object;Ljava/lang/String;)V");
	// }
	// super.visitFieldInsn(opcode, owner, name, desc);
	// }

	@Override
	public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
		// TODO Auto-generated method stub
		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}

	@Override
	public void visitFieldInsn(final int arg0, final String arg1, final String arg2, final String arg3) {
		if (arg0 == Opcodes.PUTFIELD) {
			this.visitLdcInsn(arg1);
			this.visitLdcInsn(arg2 + " " + arg3);
			// this.visitLdcInsn(arg3 + " " + arg0);
			this.visitVarInsn(Opcodes.ALOAD, 1);
			// this.visitFieldInsn(Opcodes.GETFIELD, arg1, arg2, arg3);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, "Guard", "check", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
					false);
		}
		if (arg0 == Opcodes.GETFIELD) {
			System.out.println(arg1 + " " + arg2);
		}
		super.visitFieldInsn(arg0, arg1, arg2, arg3);

	}

	@Override
	public void visitMultiANewArrayInsn(final String arg0, final int arg1) {
		super.visitMultiANewArrayInsn(arg0, arg1);
	}

	@Override
	public void visitLdcInsn(final Object cst) {
		// TODO Auto-generated method stub
		super.visitLdcInsn(cst);
	}

	@Override
	public void visitLocalVariable(final String arg0, final String arg1, final String arg2, final Label arg3, final Label arg4,
			final int arg5) {
		System.out.println("var " + arg0 + " " + arg1 + " ");

		super.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void visitCode() {
		super.visitCode();
	}

	@Override
	public void visitInsn(final int inst) {
		switch (inst) {
			case Opcodes.ARETURN:
			case Opcodes.DRETURN:
			case Opcodes.FRETURN:
			case Opcodes.IRETURN:
			case Opcodes.LRETURN:
			case Opcodes.RETURN:
			case Opcodes.ATHROW:
				break;
			default:
				break;
		}

		super.visitInsn(inst);
	}

}
