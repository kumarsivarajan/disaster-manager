using System;
using System.Text;
using System.Net;
using System.IO;
using System.Xml;

namespace dm_terminal
{
	class QueryFailureException : Exception
	{
		public QueryFailureException(string message):base(message)
		{
		}
	}

	class API
	{
		const long maxResponseLength = 1024 * 500;
		private string url;
		const int queryTimeout = 5000;

		public API(string url)
		{
			this.url = url.TrimEnd('/') + "/api/";
			//tu jakieś sprawdzanie, czy URL jest ok
		}

		public bool isConnected()
		{
			return true;
		}

		private String doRAWQuery(string queryUrl)
		{
			string queryURLParsed = url + queryUrl.Trim('/') + "/";
			HttpWebRequest webRequest;
			try
			{
				webRequest = (HttpWebRequest)HttpWebRequest.Create(queryURLParsed);
				//webRequest.KeepAlive = false;
				webRequest.Timeout = queryTimeout;
			}
			catch (UriFormatException)
			{
				throw new QueryFailureException("Niepoprawny URL");
			}
			WebResponse webResponse;
			try
			{
				webResponse = webRequest.GetResponse();
			}
			catch (WebException e)
			{
				throw new QueryFailureException("Błąd wykonania zapytania: " + e.Message);
			}
			Stream responseStream = webResponse.GetResponseStream();
			long length = webResponse.ContentLength;

			if (length > maxResponseLength)
			{
				webRequest.Abort();
				throw new QueryFailureException("Za duża odpowiedź");
			}

			StringBuilder response = new StringBuilder();
			byte[] buffer = new byte[1024];

			while ((length = responseStream.Read(buffer, 0, buffer.Length)) > 0)
				response.Append(Encoding.UTF8.GetString(buffer, 0, (int)length));

			responseStream.Close();

			return response.ToString();
		}

		public XmlNode doQuery(string queryUrl)
		{
			string rawResults = doRAWQuery(queryUrl);

			XmlDocument xmlDoc = new XmlDocument();
			xmlDoc.LoadXml(rawResults);

			//TODO: walidacja przez xsd

			XmlElement root = xmlDoc.DocumentElement;
			if (root.Name != "disaster-manager-api")
				throw new QueryFailureException("Nieprawidłowa odpowiedź API");

			return root;
		}

		public XmlNode doQuery(string queryUrl, string mainNodeName)
		{
			XmlNode root = doQuery(queryUrl);

			foreach (XmlNode mainNode in root.ChildNodes)
				if (mainNode.Name.Equals(mainNodeName))
					return mainNode;

			throw new QueryFailureException("Odpowiedź API jest złego typu");
		}
	}
}
