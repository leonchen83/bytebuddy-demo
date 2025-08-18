package org.example.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;

import org.example.ToString;
import org.example.ToStringGenerator;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author Baoyi Chen
 */
public class ToStringAgentEx {
	public static void premain(String agentArgs, Instrumentation inst) {
		new AgentBuilder.Default()
				.type(ElementMatchers.isAnnotatedWith(ToString.class))
				.transform((builder, desc, loader, module, domain) ->
						builder
								.defineMethod("toString", String.class, Modifier.PUBLIC)
								.intercept(new ToStringGenerator(desc)))
				.installOn(inst);
	}
}
