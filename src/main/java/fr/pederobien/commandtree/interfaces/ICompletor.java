package fr.pederobien.commandtree.interfaces;

import java.util.List;

@FunctionalInterface
public interface ICompletor {

	/**
	 * Requests a list of possible completions for a command argument.
	 * 
	 * @param args The arguments passed to the command, including final partial argument to be completed and command alias.
	 * 
	 * @return A List of possible completions for the final argument, or empty.
	 */
	public List<String> onTabComplete(String[] args);
}
