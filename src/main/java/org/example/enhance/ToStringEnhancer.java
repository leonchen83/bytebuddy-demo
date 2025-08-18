package org.example.enhance;

import static net.bytebuddy.jar.asm.ClassWriter.COMPUTE_FRAMES;
import static net.bytebuddy.jar.asm.ClassWriter.COMPUTE_MAXS;
import static net.bytebuddy.matcher.ElementMatchers.named;

import java.lang.reflect.Field;

import org.example.ToStringGenerator;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;

/**
 * @author Baoyi Chen
 */
@SuppressWarnings("unchecked")
public class ToStringEnhancer {
	
	public static <T> T enhance(Class<T> clazz) {
		Class<?> r = new ByteBuddy()
				.subclass(clazz).name(clazz.getName() + "$ToString")
				.method(named("toString")).intercept(new ToStringGenerator(new TypeDescription.ForLoadedType(clazz)))
				.visit(new AsmVisitorWrapper.ForDeclaredMethods()
						.writerFlags(COMPUTE_FRAMES | COMPUTE_MAXS)).make()
				.load(clazz.getClassLoader()).getLoaded();
		try {
			var c = r.getConstructor();
			return (T) c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create instance of " + r.getName(), e);
		}
	}
}
