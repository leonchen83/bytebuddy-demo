package org.example.agent;

import static net.bytebuddy.matcher.ElementMatchers.named;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.example.ToStringGenerator;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool;

/**
 * @author Baoyi Chen
 */
public class RedefineExample {
	
	public static void main(String[] args) throws Exception {
		TypePool pool = TypePool.Default.of(Thread.currentThread().getContextClassLoader());
		TypeDescription description = pool.describe("org.example.entity.Manager").resolve();
		Class<?> clazz = new ByteBuddy()
				.redefine(pool.describe("org.example.entity.Manager").resolve(), 
						ClassFileLocator.ForClassLoader.of(Thread.currentThread().getContextClassLoader()))
				.method(named("toString"))
				.intercept(new ToStringGenerator(description))
				.make().load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
		
		System.out.println(clazz.getConstructor().newInstance());
		
		// 不保留原方法
		System.out.println(Arrays.stream(clazz.getDeclaredMethods()).map(e -> e.toString()).collect(Collectors.joining("\n")));
		// public java.lang.String org.example.entity.Manager.toString()
	}
}
