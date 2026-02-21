package fr.pederobien.commandtree.interfaces;

import java.util.List;

public interface IResult {

	/**
	 * @return True if the result state is a success, false otherwise.
	 */
	boolean isSuccess();

	/**
	 * @return The list of feedback to show to the user.
	 */
	List<String> getFeedbacks();
}
