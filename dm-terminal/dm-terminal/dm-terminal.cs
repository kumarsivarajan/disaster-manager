using System;
using System.Linq;
using System.Collections.Generic;
using System.Windows.Forms;

namespace dm_terminal
{
	static class Program
	{
		public static Config config = new Config();

		[MTAThread]
		static void Main()
		{
			//TODO: może jakiś splash screen?

			Application.Run(new mainForm());

			config.save();
		}
	}
}