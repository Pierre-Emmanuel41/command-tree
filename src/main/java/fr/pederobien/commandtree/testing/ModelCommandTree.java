package fr.pederobien.commandtree.testing;

import java.util.List;

import fr.pederobien.commandtree.impl.NodeHelper;
import fr.pederobien.commandtree.impl.Tree;
import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.INodeBuilder;
import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.commandtree.interfaces.ITree;

public class ModelCommandTree {
	private ITree<Model> tree;

	public ModelCommandTree() {
		tree = new Tree<Model>();

		// Init node ---------------------------------------------------------------
		INodeBuilder<Model> initBuilder = tree.getNodeBuilder("init", "Initialize the model");
		initBuilder.withCompletions((tree, args) -> initCompletion(tree, args));
		initBuilder.withExecution((tree, args) -> initExecution(tree, args));
		tree.add(initBuilder.build());

		// Modify node -------------------------------------------------------------
		INodeBuilder<Model> modifyBuilder = tree.getNodeBuilder("modify", "To modify the name, age or city of a Model");
		modifyBuilder.withAvailability(model -> model != null);
		INode<Model> modify = modifyBuilder.build();

		// Modify name node --------------------------------------------------------
		INodeBuilder<Model> modifyNameBuilder = tree.getNodeBuilder("name", "To modify the name of a Model");
		modifyNameBuilder.withAvailability(model -> model != null);
		modifyNameBuilder.withCompletions((tree, args) -> modifyNameCompletions(tree, args));
		modifyNameBuilder.withExecution((tree, args) -> modifyNameExecution(tree, args));
		modify.add(modifyNameBuilder.build());

		// Modify age node ---------------------------------------------------------
		INodeBuilder<Model> modifyAgeBuilder = tree.getNodeBuilder("age", "To modify the age of a Model");
		modifyAgeBuilder.withAvailability(model -> model != null);
		modifyAgeBuilder.withCompletions((tree, args) -> modifyAgeCompletions(tree, args));
		modifyAgeBuilder.withExecution((tree, args) -> modifyAgeExecution(tree, args));
		modify.add(modifyAgeBuilder.build());

		// Modify city node ---------------------------------------------------------
		INodeBuilder<Model> modifyCityBuilder = tree.getNodeBuilder("city", "To modify the city of a Model");
		modifyCityBuilder.withAvailability(model -> model != null);
		modifyCityBuilder.withCompletions((tree, args) -> modifyCityCompletions(tree, args));
		modifyCityBuilder.withExecution((tree, args) -> modifyCityExecution(tree, args));
		modify.add(modifyCityBuilder.build());

		tree.add(modify);
	}

	/**
	 * Extract a list of arguments from the given arguments parameter. Each argument shall be separated by a space character. Then
	 * dispatch the list of arguments to each node and get the possible completions.
	 * 
	 * @param arguments The arguments line to use to get the associated completion.
	 * 
	 * @return A List of possible completions for the final argument, or empty.
	 */
	public List<String> getCompletions(String argument) {
		return tree.getCompletions(argument);
	}

	/**
	 * Extract a list of arguments from the given arguments parameter. Each argument shall be separated by a space character. Then
	 * dispatch the list of arguments to each node and get the command result.
	 * 
	 * @param argument Passed command arguments, all separated by a space character.
	 * @return The command result composed of a success state and a feedback.
	 */
	public IResult execute(String argument) {
		return tree.execute(argument);
	}

	private List<String> initCompletion(ITree<Model> tree, String[] args) {
		if (args.length <= 1)
			return NodeHelper.asList("<name:string>");

		if (args.length <= 2)
			return NodeHelper.asList("<age:int>");

		if (args.length <= 3)
			return NodeHelper.asList("<city:string>");

		return NodeHelper.emptyList();
	}

	private IResult initExecution(ITree<Model> tree, String[] args) {
		if (args.length < 3)
			return NodeHelper.result(false, "At least one of the following argument is missing: <name> <age> <city>");

		// Model's name
		String name = args[0];

		// Model's age
		if (!NodeHelper.isStrictInt(args[1]))
			return NodeHelper.result(false, "The parameter \"%s\" is not an integer", args[1]);

		int age = NodeHelper.parseInt(args[1]);

		// Model's city
		String city = args[2];

		tree.setSeed(new Model(name, age, city));
		return NodeHelper.result(true, "Model initialized successfully: %s", tree.getSeed());
	}

	private List<String> modifyNameCompletions(ITree<Model> tree, String[] args) {
		if (args.length <= 1)
			return NodeHelper.asList("<newName:string>");

		return NodeHelper.emptyList();
	}

	private IResult modifyNameExecution(ITree<Model> tree, String[] args) {
		if (args.length == 0)
			return NodeHelper.result(false, "The model's new name is missing");

		String name = args[0];
		if (name.equals(tree.getSeed().getName()))
			return NodeHelper.result(false, "The model's name is already %s", name);

		String oldName = tree.getSeed().getName();
		tree.getSeed().setName(name);

		return NodeHelper.result(true, "%s has been renamed as %s", oldName, tree.getSeed().getName());
	}

	private List<String> modifyAgeCompletions(ITree<Model> tree, String[] args) {
		if (args.length <= 1)
			return NodeHelper.asList("<newAge:int>");

		return NodeHelper.emptyList();
	}

	private IResult modifyAgeExecution(ITree<Model> tree, String[] args) {
		if (args.length == 0)
			return NodeHelper.result(false, "The %s's age is missing", tree.getSeed().getName());

		if (!NodeHelper.isStrictInt(args[0]))
			return NodeHelper.result(false, "\"%s\" is not an integer", args[0]);

		int age = NodeHelper.parseInt(args[0]);

		if (age == tree.getSeed().getAge())
			return NodeHelper.result(false, "The %s's age is already %s", tree.getSeed().getName(), age);

		tree.getSeed().setAge(age);
		return NodeHelper.result(true, "%s's age is %s", tree.getSeed().getName(), tree.getSeed().getAge());
	}

	private List<String> modifyCityCompletions(ITree<Model> tree, String[] args) {
		if (args.length <= 1)
			return NodeHelper.asList("<newCity:string>");

		return NodeHelper.emptyList();
	}

	private IResult modifyCityExecution(ITree<Model> tree, String[] args) {
		if (args.length == 0)
			return NodeHelper.result(false, "The %s's new city is missing", tree.getSeed().getName());

		String city = args[0];
		if (city.equals(tree.getSeed().getCity()))
			return NodeHelper.result(false, "%s's city is already %s", tree.getSeed().getName(), city);

		tree.getSeed().setCity(city);
		return NodeHelper.result(true, "%s's city is %s", tree.getSeed().getName(), tree.getSeed().getCity());
	}
}
