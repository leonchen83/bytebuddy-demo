package org.example.entity;

import org.example.ToString;
import org.example.agent.ToStringAgent;

import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * @author Baoyi Chen
 */
@ToString
public class Person {
	protected String name = "Alice";
	protected int age = 20;
	
	public static void main(String[] args) {
		ToStringAgent.premain("", ByteBuddyAgent.install());
		System.out.println(new Person());
	}
}
