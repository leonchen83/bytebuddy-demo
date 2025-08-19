# Agent

### 反射的方式生成toString方法

```java
@ToString
public class Person {
	@Override
	public String toString() {
		return ToStringInterceptor.intercept(this);
	}
}
```

```shell
java -jar .\bytebuddy-demo-1.0-SNAPSHOT.jar ...
```

### 字节码的方式生成toString方法

```java
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
				String descriptor = field.getDescriptor();
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
				
			}
			mv.visitLdcInsn("]");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
			mv.visitInsn(Opcodes.ARETURN);
			
			return new Size(3, 1);
		}
	};
}
```

```java
@ToString
public class Person {
	@Override
	public String toString() {
		return new StringBuilder()
				.append("Person[")
				.append("name=").append(this.name)
				.append(", ")
				.append("age=").append(this.age)
				.append("]")
				.toString();
	}
}
```