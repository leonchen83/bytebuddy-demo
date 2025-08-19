package org.example.entity;

import org.example.ToString;
import org.example.agent.ToStringAgentEx;

import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * @author Baoyi Chen
 */
@ToString
public class Manager {
	protected String id = "M001";
	protected String name = "Alice";
	protected double bonus = 12000.0;
	protected String department = "Finance";
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	public static void main(String[] args) {
		ToStringAgentEx.premain("", ByteBuddyAgent.install());
		System.out.println(new Manager());
	}
}
