package org.example;

import java.lang.reflect.Method;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * @author Baoyi Chen
 */
public class ToStringGeneratorEx implements Implementation {
	private final TypeDescription type;
	
	public ToStringGeneratorEx(TypeDescription type) {
		this.type = type;
	}
	
	@Override
	public ByteCodeAppender appender(Target target) {
		return new ByteCodeAppender() {
			@Override
			public Size apply(MethodVisitor mv, Context context, MethodDescription method) {
				StackManipulation sm = new StackManipulation.Compound(
						TypeCreation.of(TypeDescription.ForLoadedType.of(StringBuilder.class)),
						Duplication.SINGLE,
						MethodInvocation.invoke(findConstructor(StringBuilder.class)),
						new TextConstant(type.getSimpleName() + "["),
						MethodInvocation.invoke(findMethod(StringBuilder.class, "append", String.class))
				);
				
				boolean first = true;
				for (FieldDescription field : type.getDeclaredFields()) {
					StackManipulation fsm = new StackManipulation.Compound();
					if (!first) {
						fsm = new StackManipulation.Compound(
								new TextConstant(", "),
								MethodInvocation.invoke(findMethod(StringBuilder.class, "append", String.class))
						);
					}
					first = false;
					
					MethodDescription appendMethod;
					TypeDescription fieldType = field.getType().asErasure();
					if (fieldType.represents(int.class) || fieldType.represents(byte.class) || fieldType.represents(short.class)) {
						appendMethod = findMethod(StringBuilder.class, "append", int.class);
					} else if (fieldType.represents(long.class)) {
						appendMethod = findMethod(StringBuilder.class, "append", long.class);
					} else if (fieldType.represents(double.class)) {
						appendMethod = findMethod(StringBuilder.class, "append", double.class);
					} else if (fieldType.represents(float.class)) {
						appendMethod = findMethod(StringBuilder.class, "append", float.class);
					} else if (fieldType.represents(boolean.class)) {
						appendMethod = findMethod(StringBuilder.class, "append", boolean.class);
					} else if (fieldType.represents(char.class)) {
						appendMethod = findMethod(StringBuilder.class, "append", char.class);
					} else {
						appendMethod = findMethod(StringBuilder.class, "append", Object.class);
					}
					
					StackManipulation appendField = new StackManipulation.Compound(
							new TextConstant(field.getName() + "="),
							MethodInvocation.invoke(findMethod(StringBuilder.class, "append", String.class)),
							MethodVariableAccess.loadThis(),
							FieldAccess.forField(field).read(),
							MethodInvocation.invoke(appendMethod)
					);
					
					sm = new StackManipulation.Compound(sm, fsm, appendField);
				}
				
				sm = new StackManipulation.Compound(
						sm,
						new TextConstant("]"),
						MethodInvocation.invoke(findMethod(StringBuilder.class, "append", String.class)),
						MethodInvocation.invoke(findMethod(StringBuilder.class, "toString")),
						MethodReturn.REFERENCE
				);
				
				StackManipulation.Size size = sm.apply(mv, context);
				return new Size(size.getMaximalSize(), method.getStackSize());
			}
		};
	}
	
	@Override
	public InstrumentedType prepare(InstrumentedType type) {
		return type;
	}
	
	public static MethodDescription findMethod(Class<?> type, String name, Class<?>... parameterTypes) {
		try {
			Method method = type.getMethod(name, parameterTypes);
			return new MethodDescription.ForLoadedMethod(method);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Method not found: " + type.getName() + "." + name, e);
		}
	}
	
	public static MethodDescription findConstructor(Class<?> type, Class<?>... parameterTypes) {
		try {
			return new MethodDescription.ForLoadedConstructor(type.getConstructor(parameterTypes));
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Constructor not found: " + type.getName(), e);
		}
	}
}
