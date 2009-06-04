package model;

import java.io.*;
import java.util.HashMap;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;

public class StaticContent
{
	private final static HashMap<String, StaticContent> cachedFiles =
			new HashMap<String, StaticContent>();
	//private static HashMap<String, String> mimetypeMapping;
	
	private final static String staticContentDir = "webapps/ROOT/WEB-INF/classes/staticContent";
	//private final static String staticContentDir = "c:/code/NetBeans/disaster/build/web/WEB-INF/classes/staticContent";
	private final static int maxFileSize = 1024*1024; //1MB
	private static MimetypesFileTypeMap mimetypeMap = getMimeTypeMap();
	
	public final byte[] contents;
	public final String mimeType;
	
	private StaticContent(byte[] contents, String mimeType)
	{
		if (contents == null || mimeType == null)
			throw new NullPointerException();
		
		this.contents = contents;
		this.mimeType = mimeType;
	}
	
	private static MimetypesFileTypeMap getMimeTypeMap()
	{
		MimetypesFileTypeMap map = new MimetypesFileTypeMap();
		map.addMimeTypes("image/x-icon ico");
		map.addMimeTypes("text/css css");
		return map;
	}
	
	private static synchronized StaticContent loadContent(String name) throws ServletException
	{
		if (name == null)
			throw new NullPointerException();
		
		if (cachedFiles.containsKey(name)) //jeszcze raz sprawdzenie (w metodzie synchronicznej)
			return cachedFiles.get(name);
		
		File plik = new File(staticContentDir + "/" + name);

		if (!plik.isFile())
			return null;
		if (!plik.canRead())
			throw new ServletException("Nie mogę czytać pliku " + name);
		
		FileInputStream in;
		try
		{
			in = new FileInputStream(plik);
		}
		catch (FileNotFoundException e)
		{
			throw new ServletException("Nie mogę znaleźć pliku " + name);
		}
		
		long fileSizeL = plik.length();
		if (fileSizeL > maxFileSize)
			throw new ServletException("Próba odczytania za dużego pliku: " + name);
		int fileSize = (int)fileSizeL;
		
		byte[] fileContents = new byte[fileSize];
		int off = 0;
		
		try
		{
			int readC;
			
			while ((readC = in.read(fileContents, off, Math.min(fileSize - off, 10240))) > 0)
				off += readC;
		}
		catch (IOException e)
		{
			throw new ServletException("Nie mogę czytać z pliku: " + name);
		}
		
		if (off != fileSize)
			throw new ServletException("Ilość odczytanych z pliku danych nie " +
					"zgadza się z wpisem w tablicy plików: " + name);
		
		StaticContent plikO = new StaticContent(fileContents,
				mimetypeMap.getContentType(plik));
		
		cachedFiles.put(name, plikO);
		
		return plikO;
	}
	
	public static StaticContent getContent(String name) throws ServletException
	{
		if (name == null)
			throw new NullPointerException();
		
		if (cachedFiles.containsKey(name))
			return cachedFiles.get(name);
		
		if (name.contains(".."))
			return null;
		
		return loadContent(name);
	}
}
