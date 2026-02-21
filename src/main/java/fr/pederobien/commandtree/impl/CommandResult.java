package fr.pederobien.commandtree.impl;

import java.util.ArrayList;
import java.util.List;

import fr.pederobien.commandtree.interfaces.IResult;

public class CommandResult implements IResult {
	private final boolean success;
	private final List<String> feedbacks;

	/**
	 * Creates a command result.
	 * 
	 * @param success  The command state; True if it is a success, false otherwise.
	 * @param feedback The first feedback to display to the user. If empty then not added.
	 */
	public CommandResult(boolean success, String feedback) {
		this.success = success;

		feedbacks = new ArrayList<String>();
		if (!feedback.isEmpty())
			feedbacks.add(feedback);
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public List<String> getFeedbacks() {
		return feedbacks;
	}
}
