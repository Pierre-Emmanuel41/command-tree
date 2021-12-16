package fr.pederobien.commandtree.exceptions;

public class BooleanParseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String bool;

	public BooleanParseException(String bool) {
		super("The value" + bool + " cannot be parsed as boolean");
		this.bool = bool;
	}

	/**
	 * @return The value that cannot be parsed as boolean.
	 */
	public String getBool() {
		return bool;
	}
}
