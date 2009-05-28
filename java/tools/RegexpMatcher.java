package tools;

import java.util.regex.*;

public class RegexpMatcher
{
	final Pattern regexp;
	
	public RegexpMatcher(String regexp)
	{
		if (regexp == null)
			throw new NullPointerException();
		if (regexp.charAt(0) != '^')
			regexp = "^.*" + regexp;
		if (regexp.charAt(regexp.length() - 1) != '$')
			regexp = regexp + ".*$";
		this.regexp = Pattern.compile(regexp);
	}
	
	public String[] test(String phrase)
	{
		Matcher matcher = regexp.matcher(phrase);
		int groupCount = matcher.groupCount();
		
		if (!matcher.matches())
			return null;

		String[] ret = new String[groupCount];
		
		for (int i = 0; i < groupCount; i++)
			ret[i] = matcher.replaceFirst("$" + (i + 1)); //to można szybciej, ładniej itp

		return ret;
	}
}
