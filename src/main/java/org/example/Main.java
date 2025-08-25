package org.example;

import org.example.agent.ToStringAgent;
import org.example.entity.Person;

import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * @author Baoyi Chen
 */
public class Main {
	public static void main(String[] args) {
		ToStringAgent.premain("", ByteBuddyAgent.install());
//		ToStringAgentEx.premain("", ByteBuddyAgent.install());
		System.out.println(new Person());
	}
}
