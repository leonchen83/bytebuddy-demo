package org.example.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.example.ToString;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

public class ToStringAgent {
	public static void premain(String agentArgs, Instrumentation inst) {
		new AgentBuilder.Default()
				.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
				.type(ElementMatchers.isAnnotatedWith(ToString.class))
				.transform((builder, desc, loader, module, domain) -> 
					builder.defineMethod("toString", String.class, Modifier.PUBLIC)
							.intercept(MethodDelegation.to(ToStringInterceptor.class)))
				.installOn(inst);
	}
	
	public static class ToStringInterceptor {
		@RuntimeType
		public static String intercept(@This Object obj) {
			return Arrays.stream(obj.getClass().getDeclaredFields())
					.peek(f -> f.setAccessible(true))
					.map(f -> {
						try {
							return f.getName() + "=" + f.get(obj);
						} catch (IllegalAccessException e) {
							return f.getName() + "=<inaccessible>";
						}
					}).collect(Collectors.joining(", ", obj.getClass().getSimpleName() + "[", "]"));
		}
	}
}