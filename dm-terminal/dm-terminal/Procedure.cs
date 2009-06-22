using System;
using System.Linq;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Xml;

namespace dm_terminal
{
	public class Procedure
	{
		public enum ProcedureState { READY, RUNNING, SHUTTING_DOWN };

		private int p_id;
		private string p_name;
		private ProcedureState p_state;

		public int id { get { return p_id; } }
		public string name { get { return p_name; } }
		public ProcedureState state { get { return p_state; } }
		public string stateStr
		{
			get
			{
				switch (p_state)
				{
					case ProcedureState.READY:
						return "gotowa";
					case ProcedureState.RUNNING:
						return "uruchomiona";
					case ProcedureState.SHUTTING_DOWN:
						return "zatrzymywana";
					default:
						throw new Exception("Nie sprawdzono stanu");
				}
			}
		}

		public Procedure(int id, string name, ProcedureState state)
		{
			p_id = id;
			p_name = name;
			p_state = state;
		}

		public void run()
		{
			XmlNode result =
				Program.config.getAPI().doQuery("runProcedure/" + id, "procedure-execution");
			if (result.Attributes.GetNamedItem("success").
				InnerText.Trim().Equals("1"))
				p_state = ProcedureState.RUNNING;
		}

		public void stop()
		{
			XmlNode result =
				Program.config.getAPI().doQuery("shutdownProcedure/" + id, "procedure-execution");
			if (result.Attributes.GetNamedItem("success").
				InnerText.Trim().Equals("1"))
				p_state = ProcedureState.SHUTTING_DOWN;
		}
	}
}
