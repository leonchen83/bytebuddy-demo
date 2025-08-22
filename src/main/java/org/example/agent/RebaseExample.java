/*
 * Copyright 2016-2017 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.agent;

import static net.bytebuddy.matcher.ElementMatchers.named;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.example.ToStringGenerator;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.pool.TypePool;

public class RebaseExample {
	
	public static void main(String[] args) throws Exception {
		TypePool pool = TypePool.Default.of(Thread.currentThread().getContextClassLoader());
		TypeDescription description = pool.describe("org.example.entity.Manager").resolve();
		Class<?> clazz = new ByteBuddy()
				.rebase(pool.describe("org.example.entity.Manager").resolve(), 
						ClassFileLocator.ForClassLoader.of(Thread.currentThread().getContextClassLoader()))
				.method(named("toString"))
				.intercept(new ToStringGenerator(description))
				.make().load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

		System.out.println(clazz.getConstructor().newInstance());

		// 保留原方法并改名
		System.out.println(Arrays.stream(clazz.getDeclaredMethods()).map(e -> e.toString()).collect(Collectors.joining("\n")));
		// public java.lang.String org.example.entity.Manager.toString()
		// private java.lang.String org.example.entity.Manager.toString$original$bmVVQFi6()
	}
	
	public static void rebase() throws Exception {
		TypePool pool = TypePool.Default.of(Thread.currentThread().getContextClassLoader());
		Class<?> clazz = new ByteBuddy()
				.rebase(pool.describe("org.example.entity.Manager").resolve(), 
						ClassFileLocator.ForClassLoader.of(Thread.currentThread().getContextClassLoader()))
				.method(named("toString"))
				.intercept(MethodDelegation.to(new ToStringIntercept()))
				.make().load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
		
		System.out.println(clazz.getConstructor().newInstance());
	}
	
	public static class ToStringIntercept {
		public String intercept(@SuperCall Callable<String> origin) throws Exception {
			String original = origin.call();
			return "enhance:" + original;
		}
	}
}
