using System;
using System.Linq;
using System.Drawing;
using System.Windows.Forms;
using Microsoft.WindowsCE.Forms;
using System.Runtime.InteropServices;

namespace dm_terminal
{
	public partial class mainForm : Form
	{
		public static ProcedureList procedures = new ProcedureList();
		private int selectedIndex = -1;
		private MessageListPanel messageListPanel;

		public mainForm()
		{
			InitializeComponent();

			messageListPanel = new MessageListPanel();
			messageListPanel.Location = new Point(7, 7);
			messageListPanel.Size = new Size(226, 209);
			tabMessages.Controls.Add(messageListPanel);

			apiURLBox.DataBindings.Add("Text", Program.config, "apiURL");
			updateIntervalBox.DataBindings.Add("Value", Program.config, "updateInterval");
			newMessageNotifyBox.DataBindings.Add("Checked", Program.config, "newMessageNotify");
			procedureListGrid.DataSource = procedures;
			
			notification.Text = "<html><body>" +
				"Nowe zdarzenie" +
				"<form>" +
				"<input type=\"submit\" name=\"open\" value=\"Otwórz\">" +
				"<input type=\"submit\" name=\"ignore\" value=\"Zignoruj\">" +
				"</form></body></html>";

			procedures.addObserver(this);
			messageListPanel.addObserver(this);
		}

		[DllImport("coredll.dll")]
			static extern int ShowWindow(IntPtr hWnd, int nCmdShow);

		public void Minimize()
		{
			ShowWindow(this.Handle, 6); //const int SW_MINIMIZED = 6;
		}

		#region Obsługa listy procedur i kontrolki statusu

		private delegate void setProcedureListStatusT(string status);
		private void setProcedureListStatusM(string status)
		{
			procedureListStatus.Text = status;
			procedureListGrid.DataSource = procedures;

			procedureListGrid.DataSource = null;
			procedureListGrid.DataSource = procedures;
			if (selectedIndex >= 0)
				procedureListGrid.Select(selectedIndex);
		}

		public void setProcedureListStatus(string status)
		{
			procedureListStatus.Invoke(
				new setProcedureListStatusT(setProcedureListStatusM),
				new Object[] { status });
		}

		#endregion

		#region Obsługa powiadomień o nowych komunikatach

		private delegate void notifyNewMessagesT();
		private void notifyNewMessagesM()
		{
			if (Program.config.newMessageNotify)
				notification.Visible = true;
		}

		public void notifyNewMessages()
		{
			Invoke(new notifyNewMessagesT(notifyNewMessagesM));
		}

		#endregion

		#region Obsługa zdarzeń od kontrolek

		private void menuZamknijBtn_Click(object sender, EventArgs e)
		{
			Application.Exit();
		}

		private void saveConfigButton_Click(object sender, EventArgs e)
		{
			Program.config.save();
		}

		private void configBindingChanged(object sender, EventArgs e)
		{
			Control c = (Control)sender;
			foreach (Binding binding in c.DataBindings)
				binding.WriteValue();
		}

		private void menuMinimizeBtn_Click(object sender, EventArgs e)
		{
			this.Minimize();
		}

		private void dataGrid1_DoubleClick(object sender, EventArgs e)
		{
			if (sender != procedureListGrid)
				throw new Exception("Nieprawidłowe wywołanie zdarzenia");

			if (selectedIndex < 0 || selectedIndex >= procedures.Count)
				return;

			Procedure proc = procedures.ElementAt(selectedIndex);
			procedures.switchProcedureState(proc);
		}

		private void procedureListRefreshButton_Click(object sender, EventArgs e)
		{
			procedures.refresh();
		}

		private void procedureListGrid_CurrentCellChanged(object sender, EventArgs e)
		{
			if (sender != procedureListGrid)
				throw new Exception("Nieprawidłowe wywołanie zdarzenia");
			selectedIndex = procedureListGrid.CurrentCell.RowNumber;
			procedureListGrid.Select(selectedIndex);
		}

		private void apiURLBox_GotFocus(object sender, EventArgs e)
		{
			inputPanel.Enabled = true;
		}

		private void apiURLBox_LostFocus(object sender, EventArgs e)
		{
			inputPanel.Enabled = false;
		}

		private void messageListRefreshButton_Click(object sender, EventArgs e)
		{
			messageListPanel.doUpdate();
		}

		private void notification_ResponseSubmitted(object sender, ResponseSubmittedEventArgs e)
		{
			notification.Visible = false;
			if (e.Response.Equals("open"))
			{
				mainFormTabs.SelectedIndex = mainFormTabs.TabPages.IndexOf(tabMessages);
				this.BringToFront();
			}
		}

		#endregion

	}
}