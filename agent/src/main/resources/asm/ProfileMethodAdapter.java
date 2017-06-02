package com.apec;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProfileMethodAdapter extends MethodAdapter {
	private String _className, _methodName;

	public ProfileMethodAdapter(MethodVisitor visitor, String className, String methodName) {
		super(visitor);
		_className = className;
		_methodName = methodName;
		System.out.println("Profiled " + methodName + " in class " + className + ".");
	}

	@Override
	public void visitLabel(Label arg0) {
		super.visitLabel(arg0);
	}

	@Override
	public void visitMultiANewArrayInsn(String arg0, int arg1) {
		System.out.println(arg0);
		super.visitMultiANewArrayInsn(arg0, arg1);
	}

	@Override
	public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
		System.out.println(arg0);
		System.out.println(arg1);
		System.out.println(arg2);
		System.out.println(arg3);
		System.out.println(arg4);
		System.out.println(arg5);
		super.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void visitCode() {
		this.visitLdcInsn(_className);
		this.visitLdcInsn(_methodName);
		this.visitMethodInsn(Opcodes.INVOKESTATIC, "pl/com/stream/server/Profile", "start",
				"(Ljava/lang/String;Ljava/lang/String;)V");
		super.visitCode();
	}

	@Override
	public void visitInsn(int inst) {
		switch (inst) {
		case Opcodes.ARETURN:
		case Opcodes.DRETURN:
		case Opcodes.FRETURN:
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.RETURN:
		case Opcodes.ATHROW:
			this.visitLdcInsn(_className);
			this.visitLdcInsn(_methodName);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, "pl/com/stream/server/Profile", "end",
					"(Ljava/lang/String;Ljava/lang/String;)V");

			break;
		default:
			break;
		}

		super.visitInsn(inst);
	}

}