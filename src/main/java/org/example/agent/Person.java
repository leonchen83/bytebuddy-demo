package org.example.agent;

/**
 * @author Baoyi Chen
 */
@ToString
public class Person {
	private String name = "Alice";
	private int age = 20;
	
	public static void main(String[] args) {
		System.out.println(new Person());
	}
}
