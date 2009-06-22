using System;
using System.Linq;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using System.Xml;

namespace dm_terminal
{
	public class ProcedureList : List<Procedure>
	{
		private enum ProcMgmtStatus { READY, LOADING, FAIL, PROC_ACTION }
		private ProcMgmtStatus p_status = ProcMgmtStatus.READY;

		long updateIntervalRemaining = 0;
		const int updateIntervalResolution = 100;

		private ProcMgmtStatus status
		{
			get { return p_status; }
			set
			{
				p_status = value;
				notifyObservers();
			}
		}

		Thread updateThread;
		Queue<Procedure> switchStateThread = new Queue<Procedure>();

		public ProcedureList()
		{
			updateThread = new Thread(new ThreadStart(updateThreadCallback));
			updateThread.IsBackground = true; //odpowiednik daemon z javy
			updateThread.Start();
		}

		public void updateThreadCallback()
		{
			while (true)
			{
				status = ProcMgmtStatus.LOADING;
				notifyObservers();

				try
				{
					XmlNode s = Program.config.getAPI().doQuery("listProcedures", "procedure-list");

					this.Clear();

					foreach (XmlNode procNode in s.ChildNodes)
					{
						if (!procNode.Name.Equals("procedure"))
							throw new Exception("Nieprawidłowy węzeł");

						int id = int.Parse(procNode.Attributes.GetNamedItem("id").InnerText);
						string name = procNode.Attributes.GetNamedItem("name").InnerText;
						string stateStr = procNode.Attributes.GetNamedItem("state").InnerText;
						string desc = procNode.InnerText;
						Procedure.ProcedureState state;

						if (stateStr.Equals("ready"))
							state = Procedure.ProcedureState.READY;
						else if (stateStr.Equals("running"))
							state = Procedure.ProcedureState.RUNNING;
						else if (stateStr.Equals("shutting-down"))
							state = Procedure.ProcedureState.SHUTTING_DOWN;
						else
							throw new Exception("Nieznany status");

						this.Add(new Procedure(id, name, state));
					}

					status = ProcMgmtStatus.READY;
				}
				catch (QueryFailureException)
				{
					status = ProcMgmtStatus.FAIL;
				}

				notifyObservers();

				while (updateIntervalRemaining > 0)
				{
					while (switchStateThread.Count > 0)
					{
						switchProcedureStateCallback(
							switchStateThread.Dequeue());
						updateIntervalRemaining = 1000;
					}

					Thread.Sleep(updateIntervalResolution);
					updateIntervalRemaining -= updateIntervalResolution;
				}
				updateIntervalRemaining = (int)Program.config.updateInterval * 1000;
			}
		}

		#region Zarządzanie obserwatorami

		private List<mainForm> observers = new List<mainForm>();

		public void addObserver(mainForm observer)
		{
			observers.Add(observer);
			notifyObservers();
		}

		public void removeObserver(mainForm observer)
		{
			observers.Remove(observer);
		}

		protected void notifyObservers()
		{
			foreach (mainForm f in observers)
				switch (p_status)
				{
					case ProcMgmtStatus.READY:
						f.setProcedureListStatus("Gotowy");
						break;
					case ProcMgmtStatus.LOADING:
						f.setProcedureListStatus("Pobieranie listy...");
						break;
					case ProcMgmtStatus.FAIL:
						f.setProcedureListStatus("Błąd!");
						break;
					case ProcMgmtStatus.PROC_ACTION:
						f.setProcedureListStatus("Wykonywanie akcji...");
						break;
					default:
						throw new Exception("Nieznany status");
				}
		}

		#endregion

		//tak, wiem, że to powinny być dwie osobne metody
		public void switchProcedureState(Procedure proc)
		{
			if (proc.state == Procedure.ProcedureState.SHUTTING_DOWN)
				return;
			if (switchStateThread.Contains(proc))
				return;
			switchStateThread.Enqueue(proc);
		}

		private void switchProcedureStateCallback(Procedure proc)
		{
			bool ready;
			switch (proc.state)
			{
				case Procedure.ProcedureState.READY:
					ready = true;
					break;
				case Procedure.ProcedureState.RUNNING:
					ready = false;
					break;
				case Procedure.ProcedureState.SHUTTING_DOWN:
					MessageBox.Show("sd");
					return;
				default:
					throw new Exception("Nieznany stan");
			}

			status = ProcMgmtStatus.PROC_ACTION;
			notifyObservers();
			
			if (ready)
				proc.run();
			else
				proc.stop();

			status = ProcMgmtStatus.READY;
			notifyObservers();
		}

		public void refresh()
		{
			updateIntervalRemaining = 0;
		}

	}
}
