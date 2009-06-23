using System;
using System.Collections.Generic;
using System.Xml;

namespace dm_terminal
{
	class Message
	{
		private int id;
		private string date;
		private string message;

		private Message()
		{
		}

		public static Message[] getMessages()
		{
			List<Message> messages = new List<Message>();

			XmlNode xmlMessages = Program.config.getAPI().doQuery("listMessages", "message-list");

			foreach (XmlNode xmlMessage in xmlMessages)
			{
				Message message = new Message();
				message.id = int.Parse(xmlMessage.Attributes.GetNamedItem("id").InnerText);
				message.date = xmlMessage.Attributes.GetNamedItem("date").InnerText;
				message.message = xmlMessage.InnerText.Trim();

				messages.Add(message);
			}

			return messages.ToArray();
		}

		public int getID()
		{
			return id;
		}

		public String getDate()
		{
			return date;
		}

		public String getMessage()
		{
			return message;
		}

		public void markRead()
		{
			Program.config.getAPI().doQuery("markMessageRead/" + getID(), "mark-message-read-confirm");
		}
	}
}
