using System.Linq;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Drawing;
using System.Threading;

namespace dm_terminal
{
	class MessageListPanel : Panel
	{
		#region MessagePanel - klasa pomocnicza wyświetlająca wiadomości

		class MessagePanel : Panel
		{
			Message message;
			MessageListPanel list;

			public MessagePanel(Message message, MessageListPanel list)
				: base()
			{
				this.message = message;
				this.list = list;
				this.Size = new Size(213, 50); //226 - 13

				Label dateLabel = new Label();
				dateLabel.Text = message.getDate();
				dateLabel.ForeColor = SystemColors.GrayText;
				dateLabel.Location = new Point(0, 0);
				dateLabel.Size = new Size(150, 15);
				Controls.Add(dateLabel);

				Button markReadButton = new Button();
				markReadButton.Text = "OK";
				markReadButton.Location = new Point(163, 0);
				markReadButton.Size = new Size(50, 15);
				markReadButton.Click += delegate
				{
					if (MessageBox.Show("Na pewno usunąć tą wiadomość?",
						"Usunięcie wiadomości",
						MessageBoxButtons.OKCancel, MessageBoxIcon.Exclamation,
						MessageBoxDefaultButton.Button1)
						!= DialogResult.OK)
						return;

					Cursor.Current = Cursors.WaitCursor;
					try
					{
						message.markRead();
					}
					catch (QueryFailureException)
					{
						Cursor.Current = Cursors.Default;
						MessageBox.Show("Nie udało się oznaczyć wiadomości",
							"Błąd komunikacji z API", MessageBoxButtons.OK,
							MessageBoxIcon.Hand, MessageBoxDefaultButton.Button1);
						return;
					}
					if (list.messages.Remove(this.message))
						list.refreshControls();
					else
						list.doUpdate();
					Cursor.Current = Cursors.Default;
				};
				Controls.Add(markReadButton);

				TextBox messageBox = new TextBox();
				messageBox.Multiline = true;
				messageBox.ReadOnly = true;
				messageBox.WordWrap = true;
				messageBox.Size = new Size(213, 35);
				messageBox.Location = new Point(0, 15);
				messageBox.Text = message.getMessage();
				messageBox.ScrollBars = ScrollBars.Vertical;
				messageBox.BackColor = SystemColors.Window;
				messageBox.BorderStyle = BorderStyle.None;
				Controls.Add(messageBox);
			}
		}

		#endregion

		private List<Message> messages = new List<Message>();

		public MessageListPanel():base()
		{
			BackColor = Color.FromArgb(221, 255, 221);
			AutoScroll = true;

			updateThread = new Thread(new ThreadStart(updateThreadCallback));
			updateThread.IsBackground = true; //odpowiednik daemon z javy
			updateThread.Start();
		}

		private delegate void refreshControlsT();
		private void refreshControls()
		{
			this.SuspendLayout();
			Controls.Clear();

			for (int i = 0; i < messages.Count; i++)
			{
				Message m = messages[i];

				MessagePanel mp = new MessagePanel(m, this);
				mp.Location = new Point(0, (mp.Size.Height + 5) * i);
				Controls.Add(mp);
			}

			this.ResumeLayout(false);
		}

		public void doUpdate()
		{
			updateIntervalRemaining = 0;
		}

		#region Obsługa wątku odświeżającego

		private Thread updateThread = null;
		long updateIntervalRemaining = 0;
		const int updateIntervalResolution = 100;
		bool firstCheck = true;

		private void updateThreadCallback()
		{
			while (true)
			{
				Message[] newMessages = null;
				try
				{
					newMessages = Message.getMessages();
				}
				catch (QueryFailureException)
				{
					newMessages = null;
				}

				if (newMessages != null)
				{
					List<int> newMessageIDs = new List<int>();
					for (int i = 0; i < newMessages.Length; i++)
						newMessageIDs.Add(newMessages[i].getID());
					bool changed = false;
					for (int i = 0; i < messages.Count; i++)
					{
						int id = messages[i].getID();
						if (!newMessageIDs.Contains(id))
							changed = true;
						newMessageIDs.Remove(id);
					}
					if (newMessageIDs.Count > 0)
					{
						changed = true;
						if (!firstCheck)
							notifyObservers();
					}
					firstCheck = false;

					if (changed)
					{
						messages = newMessages.ToList();
						this.Invoke(new refreshControlsT(refreshControls));
					}
				}

				while (updateIntervalRemaining > 0)
				{
					Thread.Sleep(updateIntervalResolution);
					updateIntervalRemaining -= updateIntervalResolution;
				}
				updateIntervalRemaining = (int)Program.config.updateInterval * 1000;
			}
		}

		#endregion

		#region Zarządzanie obserwatorami

		private List<mainForm> observers = new List<mainForm>();

		public void addObserver(mainForm observer)
		{
			observers.Add(observer);
		}

		public void removeObserver(mainForm observer)
		{
			observers.Remove(observer);
		}

		protected void notifyObservers()
		{
			foreach (mainForm f in observers)
				f.notifyNewMessages();
		}

		#endregion
	}
}
