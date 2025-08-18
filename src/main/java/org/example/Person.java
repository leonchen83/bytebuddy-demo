package org.example;

/**
 * @author Baoyi Chen
 */
@ToString
public class Person {
	protected String name = "Alice";
	protected int age = 20;
	
	public static void main(String[] args) {
		System.out.println(new Person());
	}
}
