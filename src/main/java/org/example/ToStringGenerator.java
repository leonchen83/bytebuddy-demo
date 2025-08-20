package org.example;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

/**
 * @author Baoyi Chen
 */
public class ToStringGenerator implements Implementation {
	private final TypeDescription type;
	
	public ToStringGenerator(TypeDescription type) {
		this.type = type;
	}
	
	@Override
	public ByteCodeAppender appender(Target target) {
		return new ByteCodeAppender() {
			@Override
			public Size apply(MethodVisitor mv, Context context, MethodDescription method) {
				mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
				mv.visitInsn(Opcodes.DUP);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
				
				mv.visitLdcInsn(type.getSimpleName() + "[");
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
				
				boolean first = true;
				for (FieldDescription field : type.getDeclaredFields()) {
					if (!first) {
						mv.visitLdcInsn(", ");
						mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
					}
					first = false;
					mv.visitLdcInsn(field.getName() + "=");
					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitFieldInsn(Opcodes.GETFIELD, type.getInternalName(), field.getName(), field.getDescriptor());
					
					String desc = field.getDescriptor();
					switch (desc) {
						case "I":
						case "B":
						case "S":
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
							break;
						case "J":
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
							break;
						case "D":
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false);
							break;
						case "F":
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;", false);
							break;
						case "Z":
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
							break;
						case "C":
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
							break;
						default:
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
							break;
					}
				}
				
				mv.visitLdcInsn("]");
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
				
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
				mv.visitInsn(Opcodes.ARETURN);
				
				return new Size(3, 1);
			}
		};
	}
	
	@Override
	public InstrumentedType prepare(InstrumentedType type) {
		return type;
	}
}