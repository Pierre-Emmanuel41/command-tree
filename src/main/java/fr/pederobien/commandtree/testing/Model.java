package fr.pederobien.commandtree.testing;

import java.util.StringJoiner;

public class Model {
	private String name;
	private int age;
	private String city;

	/**
	 * Creates a dummy model with properties.
	 * 
	 * @param name The model's name.
	 * @param age  The model's age.
	 * @param city The model's city.
	 */
	public Model(String name, int age, String city) {
		this.name = name;
		this.age = age;
		this.city = city;
	}

	/**
	 * @return The model's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the model's name.
	 * 
	 * @param name The new model's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The model's age.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Set the model's age.
	 * 
	 * @param age The new model's age.
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return The model's city.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Set the model's city.
	 * 
	 * @param city The new model's city.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("name=" + getName());
		joiner.add("age=" + getAge());
		joiner.add("city=" + getCity());
		return joiner.toString();
	}
}
