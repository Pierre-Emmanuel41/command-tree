package fr.pederobien.commandtree.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.RandomAccess;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.pederobien.commandtree.events.NodeAddPostEvent;
import fr.pederobien.commandtree.events.NodeAvailableChangePostEvent;
import fr.pederobien.commandtree.events.NodeRemovePostEvent;
import fr.pederobien.commandtree.exceptions.BooleanParseException;
import fr.pederobien.commandtree.exceptions.NodeRegisterException;
import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.utils.event.EventManager;

public class Node<T> implements INode<T> {
	private static final String ALL_CHILDREN = "*";

	private String label;
	private T explanation;
	private INode<T> parent;
	private Supplier<Boolean> isAvailable;
	private Map<String, INode<T>> nodes;
	private boolean availableValue;

	/**
	 * Creates a node specified by the given parameters.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 */
	protected Node(String label, T explanation, Supplier<Boolean> isAvailable) {
		this.label = label;
		this.explanation = explanation;
		this.nodes = new LinkedHashMap<String, INode<T>>();
		this.isAvailable = isAvailable;
		availableValue = isAvailable.get();
	}

	/**
	 * Creates a node specified by the given parameters.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 */
	protected Node(String label, T explanation) {
		this(label, explanation, () -> false);
	}

	@Override
	public Iterator<Entry<String, INode<T>>> iterator() {
		return nodes.entrySet().iterator();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public T getExplanation() {
		return explanation;
	}

	@Override
	public void setParent(INode<T> parent) {
		this.parent = parent;
	}

	@Override
	public INode<T> getParent() {
		return parent == null ? this : parent;
	}

	@Override
	public INode<T> getRoot() {
		return parent == null ? this : parent.getRoot();
	}

	@Override
	public void add(INode<T> node) {
		INode<T> register = nodes.get(node.getLabel());
		if (register != null)
			throw new NodeRegisterException(register);

		nodes.put(node.getLabel(), node);
		node.setParent(this);
		EventManager.callEvent(new NodeAddPostEvent(node, this));
	}

	@Override
	public void remove(String label) {
		INode<T> remove = nodes.remove(label);
		if (remove != null) {
			remove.setParent(null);
			EventManager.callEvent(new NodeRemovePostEvent(remove, this));
		}
	}

	@Override
	public Map<String, ? extends INode<T>> getChildren() {
		return Collections.unmodifiableMap(nodes);
	}

	@Override
	public List<? extends INode<T>> getChildrenByLabel(String label) {
		return getDescendants(this, label);
	}

	@Override
	public boolean isAvailable() {
		boolean available = isAvailable.get();
		if (availableValue != available)
			EventManager.callEvent(new NodeAvailableChangePostEvent(this));
		return availableValue = isAvailable.get();
	}

	@Override
	public void setAvailable(Supplier<Boolean> isAvailable) {
		this.isAvailable = isAvailable;
	}

	/**
	 * @param <U> The type of element in the empty list.
	 * @return An empty array list.
	 */
	protected <U> List<U> emptyList() {
		return new ArrayList<U>();
	}

	/**
	 * @param <U> The type of element in the empty stream.
	 * @return An empty stream.
	 */
	protected <U> Stream<U> emptyStream() {
		return Stream.of();
	}

	/**
	 * @return A stream that contains only available children.
	 */
	protected Stream<INode<T>> getAvailableChildren() {
		return nodes.values().stream().filter(node -> node.isAvailable());
	}

	/**
	 * Filter each string from the given stream using condition : <code>str.contains(filter)</code>
	 * 
	 * @param stream A stream that contains string to filter.
	 * @param filter The condition used to filter the previous stream.
	 * 
	 * @return A list of string from the given stream that contains the filter.
	 */
	protected List<String> filter(Stream<String> stream, String filter) {
		return stream.filter(str -> str.contains(filter)).collect(Collectors.toList());
	}

	/**
	 * Filter each string from the given stream using condition : <code>str.contains(args[args.length - 1])</code>. This method is
	 * equivalent to : <code>filter(stream, args[args.length - 1])</code>. In other words, this method filter the given stream using
	 * the last argument from the array <code>args</code>.
	 * 
	 * @param stream A stream that contains string to filter.
	 * @param args   The array that contains arguments coming from method <code>onTabComplete</code>.
	 * 
	 * @return A list of string from the given stream that contains the filter.
	 * 
	 * @see #filter(Stream, String)
	 * @see #onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, String, String[])
	 */
	protected List<String> filter(Stream<String> stream, String... args) {
		return filter(stream, args[args.length - 1]);
	}

	/**
	 * Copies the specified range of the specified array into a new array. The initial index of the range (<tt>from</tt>) must lie
	 * between zero and <tt>original.length</tt>, inclusive. The value at <tt>original[from]</tt> is placed into the initial element
	 * of the copy (unless <tt>from == original.length</tt> or <tt>from == to</tt>). Values from subsequent elements in the original
	 * array are placed into subsequent elements in the copy. The final index of the range (<tt>to</tt>), which must be greater than
	 * or equal to <tt>from</tt>, may be greater than <tt>original.length</tt>, in which case <tt>null</tt> is placed in all elements
	 * of the copy whose index is greater than or equal to <tt>original.length - from</tt>. The length of the returned array will be
	 * <tt>to - from</tt>.
	 * <p>
	 * The resulting array is of exactly the same class as the original array.
	 *
	 * @param original the array from which a range is to be copied.
	 * @param from     the initial index of the range to be copied, inclusive.
	 * @param to       the final index of the range to be copied, exclusive. (This index may lie outside the array.)
	 * @return a new array containing the specified range from the original array, truncated or padded with nulls to obtain the
	 *         required length
	 * @throws ArrayIndexOutOfBoundsException if {@code from < 0} or {@code from > original.length}
	 * @throws IllegalArgumentException       if <tt>from &gt; to</tt>
	 * @throws NullPointerException           if <tt>original</tt> is null
	 */
	protected String[] extract(String[] original, int from, int to) {
		return Arrays.copyOfRange(original, from, to);
	}

	/**
	 * Copy the specified array into a new array. This method is equivalent to call {@link #extract(String[], int, int)} with
	 * parameter "to" equals args.length.
	 * 
	 * @param original the array from which a range is to be copied.
	 * @param from     the initial index of the range to be copied, inclusive.
	 * 
	 * @return a new array containing the specified range from the original array, truncated or padded with nulls to obtain the
	 *         required length
	 */
	protected String[] extract(String[] original, int from) {
		return extract(original, from, original.length);
	}

	/**
	 * Creates a lazily concatenated stream whose elements are all the elements of the first stream followed by all the elements of
	 * the second stream. The resulting stream is ordered if both of the input streams are ordered, and parallel if either of the
	 * input streams is parallel. When the resulting stream is closed, the close handlers for both input streams are invoked.
	 *
	 * @implNote Use caution when constructing streams from repeated concatenation. Accessing an element of a deeply concatenated
	 *           stream can result in deep call chains, or even {@code StackOverflowException}.
	 *
	 * @param <T>     The type of stream elements.
	 * @param stream1 the first stream.
	 * @param stream2 the second stream.
	 * 
	 * @return the concatenation of the two input streams.
	 */
	protected <U> Stream<U> concat(Stream<? extends U> stream1, Stream<? extends U> stream2) {
		return Stream.concat(stream1, stream2);
	}

	/**
	 * Check if the element verify the rules coming from the given predicate. If the element verify the rules, then it returns the
	 * specified <code>listWhenVerify</code>. Otherwise, it return the specified <code>listWhenNotVerify</code>
	 * 
	 * @param element           The element to check.
	 * @param predicate         The predicate that contains the rules.
	 * @param listWhenVerify    The list to return if the element verify the rules.
	 * @param listWhenNotVerify The list to return if the element does not verify the rules.
	 * 
	 * @return A List of String.
	 */
	protected List<String> check(String element, Predicate<String> predicate, List<String> listWhenVerify, List<String> listWhenNotVerify) {
		return predicate.test(element) ? listWhenVerify : listWhenNotVerify;
	}

	/**
	 * Check if the element verify the rules coming from the given predicate. If the element verify the rules, then it returns the
	 * specified <code>listWhenVerify</code>. Otherwise, it return the specified <code>listWhenNotVerify</code>
	 * 
	 * @param element             The element to check.
	 * @param predicate           The predicate that contains the rules.
	 * @param streamWhenVerify    The stream to return if the element verify the rules.
	 * @param streamWhenNotVerify The stream to return if the element does not verify the rules.
	 * 
	 * @return A List of String.
	 */
	protected Stream<String> check(String element, Predicate<String> predicate, Stream<String> streamWhenVerify, Stream<String> streamWhenNotVerify) {
		return predicate.test(element) ? streamWhenVerify : streamWhenNotVerify;
	}

	/**
	 * Check if the element verify the rules coming from the given predicate. If the element verify the rules, then it returns the
	 * specified list of String. Otherwise, it return an empty list of String.
	 * 
	 * @param element      The element to check.
	 * @param predicate    The predicate that contains the rules.
	 * @param returnedList The list to return if the element verify the rules.
	 * 
	 * @return A List of String.
	 */
	protected List<String> check(String element, Predicate<String> predicate, List<String> returnedList) {
		return check(element, predicate, returnedList, emptyList());
	}

	/**
	 * Check if the element verify the rules coming from the given predicate. If the element verify the rules, then it returns the
	 * specified stream of String. Otherwise, it return an empty stream of String.
	 * 
	 * @param element        The element to check.
	 * @param predicate      The predicate that contains the rules.
	 * @param returnedStream The stream to return if the element verify the rules.
	 * 
	 * @return A stream of String.
	 */
	protected Stream<String> check(String element, Predicate<String> predicate, Stream<String> returnedStream) {
		return check(element, predicate, returnedStream, emptyStream());
	}

	/**
	 * Returns a fixed-size list backed by the specified array. (Changes to the returned list "write through" to the array.) This
	 * method acts as bridge between array-based and collection-based APIs, in combination with {@link Collection#toArray}. The
	 * returned list is serializable and implements {@link RandomAccess}.
	 *
	 * <p>
	 * This method also provides a convenient way to create a fixed-size list initialized to contain several elements:
	 * 
	 * <pre>
	 * List&lt;String&gt; stooges = Arrays.asList("Larry", "Moe", "Curly");
	 * </pre>
	 *
	 * @param strings the array by which the list will be backed.
	 * @return A list view of the specified array.
	 */
	@SuppressWarnings("unchecked")
	protected <U> List<U> asList(U... strings) {
		return Arrays.asList(strings);
	}

	/**
	 * Parses the string argument as a signed decimal integer. The characters in the string must all be decimal digits, except that
	 * the first character may be an ASCII minus sign {@code '-'} ({@code '\u005Cu002D'}) to indicate a negative value or an ASCII
	 * plus sign {@code '+'} ({@code '\u005Cu002B'}) to indicate a positive value. The resulting integer value is returned, exactly as
	 * if the argument and the radix 10 were given as arguments to the {@link #parseInt(java.lang.String, int)} method.
	 *
	 * @param number a {@code String} containing the {@code int} representation to be parsed
	 * @return True if the given string contains a parsable integer OR is empty, false otherwise.
	 */
	protected boolean isNotStrictInt(String number) {
		return number.equals("") || number.equals("-") || isStrictInt(number);
	}

	/**
	 * Parses the string argument as a signed decimal integer. The characters in the string must all be decimal digits, except that
	 * the first character may be an ASCII minus sign {@code '-'} ({@code '\u005Cu002D'}) to indicate a negative value or an ASCII
	 * plus sign {@code '+'} ({@code '\u005Cu002B'}) to indicate a positive value. The resulting integer value is returned, exactly as
	 * if the argument and the radix 10 were given as arguments to the {@link #parseInt(java.lang.String, int)} method.
	 *
	 * @param number a {@code String} containing the {@code int} representation to be parsed
	 * @return True if the given string contains a parsable integer false otherwise.
	 */
	protected boolean isStrictInt(String number) {
		try {
			getInt(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Parses the string argument as a signed decimal double.
	 *
	 * @param number the string to be parsed.
	 * 
	 * @return True if the given string contains a parsable double OR is empty, false otherwise.
	 * 
	 * @throws NullPointerException  if the string is null.
	 * @throws NumberFormatException if the string does not contain a parsable {@code double}.
	 * 
	 * @see java.lang.Double#valueOf(String)
	 */
	protected boolean isNotStrictDouble(String number) {
		return number.equals("") || number.equals("-") || isStrictDouble(number);
	}

	/**
	 * Parses the string argument as a signed decimal double.
	 *
	 * @param number the string to be parsed.
	 * 
	 * @return True if the given string contains a parsable double false otherwise.
	 * 
	 * @throws NullPointerException  if the string is null.
	 * @throws NumberFormatException if the string does not contain a parsable {@code double}.
	 * 
	 * @see java.lang.Double#valueOf(String)
	 */
	protected boolean isStrictDouble(String number) {
		try {
			getDouble(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Parses the string argument as a {@link LocalTime}.
	 * <p>
	 * The string must represent a valid time and is parsed using {@link java.time.format.DateTimeFormatter#ISO_LOCAL_TIME}.
	 *
	 * @param text the text to parse such as "10:15:30", not null.
	 * @return True if the given string contains a parsable time OR is empty, false otherwise.
	 */
	protected boolean isNotStrictTime(String time) {
		return time.equals("") || isStrictTime(time);
	}

	/**
	 * Parses the string argument as a {@link LocalTime}.
	 * <p>
	 * The string must represent a valid time and is parsed using {@link java.time.format.DateTimeFormatter#ISO_LOCAL_TIME}.
	 *
	 * @param text the text to parse such as "10:15:30", not null.
	 * @return True if the given string contains a parsable time, false otherwise.
	 */
	protected boolean isStrictTime(String time) {
		try {
			getTime(time);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	/**
	 * Parses the string argument as a {@link LocalTime}.
	 * <p>
	 * The text is parsed using the formatter, returning a time.
	 *
	 * @param time      the text to parse, not null.
	 * @param formatter the formatter to use, not null.
	 * @return True if the given string contains a parsable time OR is empty, false otherwise.
	 */
	protected boolean isNotStrictTime(String time, DateTimeFormatter formatter) {
		return time.equals("") || isStrictTime(time, formatter);
	}

	/**
	 * Parses the string argument as a {@link LocalTime}.
	 * <p>
	 * The text is parsed using the formatter, returning a time.
	 *
	 * @param time      the text to parse, not null.
	 * @param formatter the formatter to use, not null.
	 * @return True if the given string contains a parsable time, false otherwise.
	 */
	protected boolean isStrictTime(String time, DateTimeFormatter formatter) {
		try {
			LocalTime.parse(time, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	/**
	 * Parses the string argument as a signed decimal integer. The characters in the string must all be decimal digits, except that
	 * the first character may be an ASCII minus sign {@code '-'} ({@code '\u005Cu002D'}) to indicate a negative value or an ASCII
	 * plus sign {@code '+'} ({@code '\u005Cu002B'}) to indicate a positive value. The resulting integer value is returned, exactly as
	 * if the argument and the radix 10 were given as arguments to the {@link #parseInt(java.lang.String, int)} method.
	 *
	 * @param s a {@code String} containing the {@code int} representation to be parsed.
	 * 
	 * @return the integer value represented by the argument in decimal.
	 * 
	 * @exception NumberFormatException if the string does not contain a parsable integer.
	 */
	protected int getInt(String number) {
		return Integer.parseInt(number);
	}

	/**
	 * Returns a new {@code double} initialized to the value represented by the specified {@code String}, as performed by the
	 * {@code valueOf} method of class {@code Double}.
	 *
	 * @param number The string to be parsed.
	 * 
	 * @return The {@code double} value represented by the string argument.
	 * 
	 * @throws NullPointerException  If the string is null.
	 * @throws NumberFormatException If the string does not contain a parsable {@code double}.
	 * @see java.lang.Double#valueOf(String)
	 */
	protected double getDouble(String number) {
		return Double.parseDouble(number);
	}

	/**
	 * Obtains an instance of {@code LocalTime} from a text string such as {@code 10:15}.
	 * <p>
	 * The string must represent a valid time and is parsed using {@link java.time.format.DateTimeFormatter#ISO_LOCAL_TIME}.
	 *
	 * @param time The time to parse such as "10:15:30", not null.
	 * 
	 * @return The parsed local time, not null.
	 * 
	 * @throws DateTimeParseException If the text cannot be parsed.
	 */
	protected LocalTime getTime(String time) {
		return LocalTime.parse(time);
	}

	/**
	 * Parses the string argument as a boolean. The {@code boolean} returned represents the value {@code true} if and only if the
	 * string argument equals, ignoring case, to the string {@code "true"} or represents the value {@code false} if and only if the
	 * string argument equals, ignoring case, to the string {@code "false"}.
	 * <p>
	 * Example: {@code Boolean.parseBoolean("True")} returns {@code true}.<br>
	 *
	 * @param bool the {@code String} containing the boolean representation to be parsed
	 * @return the boolean represented by the string argument
	 * 
	 * @throws BooleanParseException If the the string argument is neither equal, ignoring case, to {@code "true"} nor
	 *                               {@code "false"}.
	 */
	protected boolean getBoolean(String bool) {
		if (bool.equalsIgnoreCase("true"))
			return true;
		if (bool.equalsIgnoreCase("false"))
			return false;
		throw new BooleanParseException(bool);
	}

	/**
	 * Verify the given string start with the specified beginning ignoring case. For example : <br>
	 * <code>str = "IBeGinLIkeThis";<br>
	 * beginning = "ibEginli";<br></code> The method return true.
	 * 
	 * @param str       The string to check.
	 * @param beginning The beginning used as reference.
	 * @return True if the string begin with the given beginning, false otherwise.
	 */
	protected boolean startWithIgnoreCase(String str, String beginning) {
		return str.length() < beginning.length() ? false : str.substring(0, beginning.length()).equalsIgnoreCase(beginning);
	}

	/**
	 * Concatenate each argument present into the given array like : elt1 + ", " + elt2 + ", " + elt3 +...
	 * 
	 * @param args The array that contains arguments.
	 * 
	 * @return The concatenation of each argument.
	 * 
	 * @see #concat(String[], CharSequence)
	 */
	protected String concat(String[] args) {
		return concat(args, ", ");
	}

	/**
	 * Concatenate each string in the <code>strings</code> array.
	 * 
	 * @param strings   An array that contains string to concatenate.
	 * @param delimiter the sequence of characters to be used between each element added to the concatenation value.
	 * @return The concatenation of each string.
	 * 
	 * @see StringJoiner
	 */
	protected String concat(String[] strings, CharSequence delimiter) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (String string : strings)
			joiner.add(string);
		return joiner.toString();
	}

	/**
	 * Concatenate each string in the list <code>strings</code> using the given delimiter.
	 * 
	 * @param strings   The list that contains string to concatenate
	 * @param delimiter the sequence of characters to be used between each element added to the concatenation value.
	 * @return The concatenation of each string.
	 */
	protected String concat(List<String> strings, CharSequence delimiter) {
		return concat(strings.toArray(new String[] {}), delimiter);
	}

	/**
	 * Concatenate each string in the list <code>strings</code> using the delimiter ", ".
	 * 
	 * @param strings   The list that contains string to concatenate
	 * @param delimiter the sequence of characters to be used between each element added to the concatenation value.
	 * @return The concatenation of each string.
	 * 
	 * @see #concat(List, CharSequence)
	 */
	protected String concat(List<String> strings) {
		return concat(strings, ", ");
	}

	/**
	 * Iterate over the children of this node in order to get all children that matches with the given label.
	 * 
	 * @param node  The node used to get its children.
	 * @param label The label to match on.
	 * 
	 * @return A list that contains all descendants
	 */
	private List<? extends INode<T>> getDescendants(INode<T> node, String label) {
		List<INode<T>> children = new ArrayList<INode<T>>();
		if (label.equals(ALL_CHILDREN) || node.getLabel().equals(label))
			children.add(node);

		for (INode<T> child : node.getChildren().values())
			if (label.equals(ALL_CHILDREN) || node.getLabel().equals(label))
				children.add(child);

		for (INode<T> child : node.getChildren().values())
			children.addAll(getDescendants(child, label));
		return children;
	}
}
