package org.example;

import org.example.enhance.ToStringEnhancer;

/**
 * @author Baoyi Chen
 */
@ToString
public class Employee {
	protected String id = "E001";
	protected String name = "Bob";
	protected double salary = 5000.0;
	protected String department = "IT";
	
	public static void main(String[] args) {
		System.out.println(ToStringEnhancer.enhance(Employee.class));
	}
}
