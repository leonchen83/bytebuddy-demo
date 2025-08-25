package org.example.dynamicproxy;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isDefaultMethod;
import static net.bytebuddy.matcher.ElementMatchers.isFinalizer;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author Baoyi Chen
 */
@SuppressWarnings("unchecked")
public class ProxyFactory {
	
	public <T> T getProxy(Class<T> clazz, InvocationHandler handler) {
		try {
			if (clazz.isInterface()) {
				return proxyInterface(clazz, handler).getConstructor().newInstance();
			} else {
				return proxyConcreate(clazz, handler).getConstructor().newInstance();
			}
		} catch (Throwable t) {
			throw new InternalError(t);
		}
	}
	
	<T> Class<? extends T> proxyInterface(Class<T> c, InvocationHandler h) {
		return (Class<? extends T>) new ByteBuddy().subclass((Object.class)).implement(c)
				.method(isDeclaredBy(c).and(ElementMatchers.not(isDefaultMethod())))
				.intercept(InvocationHandlerAdapter.of(h)).make().load(c.getClassLoader(), WRAPPER).getLoaded();
	}
	
	<T> Class<? extends T> proxyConcreate(Class<T> c, InvocationHandler h) {
		return new ByteBuddy().subclass(c).method(not(isFinalizer())
						.and(not(isDeclaredBy((Object.class)))).and(not(isDefaultMethod())))
				.intercept(InvocationHandlerAdapter.of(h)).make().load(c.getClassLoader(), WRAPPER).getLoaded();
	}
	
	public static interface ITest {
		void run();
	}
	
	public static class Test implements ITest {
		@Override
		public void run() {
			System.out.println("Test run method");
		}
	}
	
	public static class TestProxy extends Test {
		private InvocationHandler handler;
		
		@Override
		public void run() {
			try {
				handler.invoke(this, Test.class.getMethod("run"), null);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class XInvocationHandler implements InvocationHandler {
		Object target;
		
		public XInvocationHandler(Object target) {
			this.target = target;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("Running: " + method.getName());
			return method.invoke(target, args);
		}
	}
	
	public static void main(String[] args) {
		
		Test target = new Test();
		ProxyFactory factory = new ProxyFactory();
		ITest test = factory.getProxy(ITest.class, new XInvocationHandler(target));
		test.run();
		
		System.out.println("#".repeat(32));
		
		TestProxy proxy = new TestProxy();
		proxy.handler = new XInvocationHandler(target);
		proxy.run();
	}
}
