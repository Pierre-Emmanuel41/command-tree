package fr.pederobien.commandtree.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.IResult;

public class NodeHelper {

	/**
	 * Creates a IResult for a command.
	 * 
	 * @param success  True if the command ran successfully, false otherwise.
	 * @param feedback The first feedback to display to the user. If empty then not added.
	 * @return The result.
	 */
	public static IResult result(boolean success, String feedback) {
		return new CommandResult(success, feedback);
	}

	/**
	 * Creates a IResult for a command.
	 * 
	 * @param success True if the command ran successfully, false otherwise.
	 * @param format  The format to use for the result feedback.
	 * @param args    The arguments of the format for the result feedback.
	 * @return The result.
	 */
	public static IResult result(boolean success, String format, Object... args) {
		return result(success, String.format(format, args));
	}

	/**
	 * @param <U> The type of element in the empty list.
	 * @return An empty array list.
	 */
	public static <T> List<T> emptyList() {
		return new ArrayList<T>();
	}

	/**
	 * @param <U> The type of element in the empty stream.
	 * @return An empty stream.
	 */
	public static <T> Stream<T> emptyStream() {
		return Stream.of();
	}

	/**
	 * @return A stream that contains only available children.
	 */
	public static <T> Stream<INode<T>> getAvailableChildren(INode<T> source, T seed) {
		return source.getChildren().stream().filter(node -> node.isAvailable(seed));
	}

	/**
	 * Filter each element from the given stream using condition : <code>str.containsIgnoreCase(filter)</code>
	 * 
	 * @param stream A stream that contains strings to filter.
	 * @param filter The filter to apply on each stream element.
	 * 
	 * @return A list of string from the given stream that contains the filter.
	 */
	public static List<String> filter(Stream<String> stream, String filter) {
		return stream.filter(str -> containsIgnoreCase(str, filter)).collect(Collectors.toList());
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
	public static List<String> filter(Stream<String> stream, String... args) {
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
	public static String[] extract(String[] original, int from, int to) {
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
	public static String[] extract(String[] original, int from) {
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
	public static <T> Stream<T> concat(Stream<? extends T> stream1, Stream<? extends T> stream2) {
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
	public static List<String> check(String element, Predicate<String> predicate, List<String> listWhenVerify, List<String> listWhenNotVerify) {
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
	public static Stream<String> check(String element, Predicate<String> predicate, Stream<String> streamWhenVerify, Stream<String> streamWhenNotVerify) {
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
	public static List<String> check(String element, Predicate<String> predicate, List<String> returnedList) {
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
	public static Stream<String> check(String element, Predicate<String> predicate, Stream<String> returnedStream) {
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
	public static <T> List<T> asList(T... strings) {
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
	public static boolean isNotStrictInt(String number) {
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
	public static boolean isStrictInt(String number) {
		try {
			parseInt(number);
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
	public static boolean isNotStrictDouble(String number) {
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
	public static boolean isStrictDouble(String number) {
		try {
			parseDouble(number);
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
	public static boolean isNotStrictTime(String time) {
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
	public static boolean isStrictTime(String time) {
		try {
			parseTime(time);
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
	public static boolean isNotStrictTime(String time, DateTimeFormatter formatter) {
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
	public static boolean isStrictTime(String time, DateTimeFormatter formatter) {
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
	public static int parseInt(String number) {
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
	public static double parseDouble(String number) {
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
	public static LocalTime parseTime(String time) {
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
	 * @throws IllegalArgumentException If the the string argument is neither equal, ignoring case, to {@code "true"} nor
	 *                                  {@code "false"}.
	 */
	public static boolean parseBool(String bool) {
		if (bool.equalsIgnoreCase("true"))
			return true;
		if (bool.equalsIgnoreCase("false"))
			return false;
		throw new IllegalArgumentException(bool);
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
	public static boolean startWithIgnoreCase(String str, String beginning) {
		return str.length() < beginning.length() ? false : str.substring(0, beginning.length()).equalsIgnoreCase(beginning);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using ", " as delimiter.
	 * 
	 * @param elements The array that contains elements to concatenate.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(Object[], CharSequence, Function)
	 */
	public static String concat(String[] elements) {
		return concat(elements, ", ", elt -> elt);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using the specified delimiter.
	 * 
	 * @param elements  The array that contains elements to concatenate.
	 * @param delimiter The delimiter to use to separate elements.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(Object[], CharSequence, Function)
	 */
	public static String concat(String[] elements, CharSequence delimiter) {
		return concat(elements, delimiter, elt -> elt);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using ", " as delimiter.
	 * 
	 * @param elements The array that contains elements to concatenate.
	 * @param toString The function to apply on each element in order to get a String representation.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(Object[], CharSequence, Function)
	 */
	public static <U> String concat(U[] elements, Function<U, String> toString) {
		return concat(elements, ", ", toString);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using the specified delimiter as delimiter.
	 * 
	 * @param elements  The array that contains elements to concatenate.
	 * @param delimiter The delimiter to use to separate elements.
	 * @param toString  The function to apply on each element in order to get a String representation.
	 * 
	 * @return The concatenation of each element.
	 */
	public static <U> String concat(U[] elements, CharSequence delimiter, Function<U, String> toString) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (U arg : elements)
			joiner.add(toString.apply(arg));
		return joiner.toString();
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using ", " as delimiter.
	 * 
	 * @param elements The list that contains elements to concatenate.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(List, CharSequence, Function)
	 */
	public static String concat(List<String> elements) {
		return concat(elements, ", ", elt -> elt);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using the specified delimiter.
	 * 
	 * @param elements  The list that contains elements to concatenate.
	 * @param delimiter The delimiter to use to separate elements.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(List, CharSequence, Function)
	 */
	public static String concat(List<String> elements, CharSequence delimiter) {
		return concat(elements, delimiter, elt -> elt);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using ", " as delimiter.
	 * 
	 * @param elements The list that contains elements to concatenate.
	 * @param toString The function to apply on each element in order to get a String representation.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(List, CharSequence, Function)
	 */
	public static <U> String concat(List<U> elements, Function<U, String> toString) {
		return concat(elements, ", ", toString);
	}

	/**
	 * Concatenate each element present in the <code>elements</code> array using the specified delimiter.
	 * 
	 * @param elements  The list that contains elements to concatenate.
	 * @param delimiter The delimiter to use to separate elements.
	 * @param toString  The function to apply on each element in order to get a String representation.
	 * 
	 * @return The concatenation of each element.
	 * 
	 * @see #concat(List, CharSequence, Function)
	 */
	public static <U> String concat(List<U> elements, CharSequence delimiter, Function<U, String> toString) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (U arg : elements)
			joiner.add(toString.apply(arg));
		return joiner.toString();
	}

	/**
	 * Check if the content string contains the filter string ignoring the case.
	 * 
	 * @param content The content to check.
	 * @param filter  The filter to match.
	 * @return True if the content contains the filter, false otherwise.
	 */
	public static boolean containsIgnoreCase(String content, String filter) {
		String contentCopy = new String(content);
		String filterCopy = new String(filter);

		return contentCopy.toUpperCase().toLowerCase().contains(filterCopy.toUpperCase().toLowerCase());
	}
}
