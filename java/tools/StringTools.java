package tools;

public class StringTools
{
	public static String join(String c, Object[] arr)
	{
		String out = "";
		for (Object elem : arr)
			out += elem.toString() + c;
		if (c.length() == 0 || out.length() == 0)
			return out;
		return out.substring(0, out.length() - c.length());
	}
}
