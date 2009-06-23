namespace dm_terminal
{
    partial class mainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;
        private System.Windows.Forms.MainMenu mainMenu;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
			this.components = new System.ComponentModel.Container();
			System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(mainForm));
			this.mainMenu = new System.Windows.Forms.MainMenu();
			this.menuZamknijBtn = new System.Windows.Forms.MenuItem();
			this.menuMinimizeBtn = new System.Windows.Forms.MenuItem();
			this.mainFormTabs = new System.Windows.Forms.TabControl();
			this.tabMessages = new System.Windows.Forms.TabPage();
			this.messageListRefreshButton = new System.Windows.Forms.Button();
			this.tabProcedures = new System.Windows.Forms.TabPage();
			this.procedureListStatus = new System.Windows.Forms.Label();
			this.procedureListRefreshButton = new System.Windows.Forms.Button();
			this.procedureListGrid = new System.Windows.Forms.DataGrid();
			this.ProcedureListStyle = new System.Windows.Forms.DataGridTableStyle();
			this.idColumn = new System.Windows.Forms.DataGridTextBoxColumn();
			this.nameColumn = new System.Windows.Forms.DataGridTextBoxColumn();
			this.stateColumn = new System.Windows.Forms.DataGridTextBoxColumn();
			this.tabConfig = new System.Windows.Forms.TabPage();
			this.newMessageNotifyBox = new System.Windows.Forms.CheckBox();
			this.updateIntervalBox = new System.Windows.Forms.NumericUpDown();
			this.updateIntervalLabel = new System.Windows.Forms.Label();
			this.saveConfigButton = new System.Windows.Forms.Button();
			this.apiURLBox = new System.Windows.Forms.TextBox();
			this.apiURLLabel = new System.Windows.Forms.Label();
			this.notification = new Microsoft.WindowsCE.Forms.Notification();
			this.inputPanel = new Microsoft.WindowsCE.Forms.InputPanel(this.components);
			this.mainFormTabs.SuspendLayout();
			this.tabMessages.SuspendLayout();
			this.tabProcedures.SuspendLayout();
			this.tabConfig.SuspendLayout();
			this.SuspendLayout();
			// 
			// mainMenu
			// 
			this.mainMenu.MenuItems.Add(this.menuZamknijBtn);
			this.mainMenu.MenuItems.Add(this.menuMinimizeBtn);
			// 
			// menuZamknijBtn
			// 
			this.menuZamknijBtn.Text = "Zamknij";
			this.menuZamknijBtn.Click += new System.EventHandler(this.menuZamknijBtn_Click);
			// 
			// menuMinimizeBtn
			// 
			this.menuMinimizeBtn.Text = "Minimalizuj";
			this.menuMinimizeBtn.Click += new System.EventHandler(this.menuMinimizeBtn_Click);
			// 
			// mainFormTabs
			// 
			this.mainFormTabs.Controls.Add(this.tabMessages);
			this.mainFormTabs.Controls.Add(this.tabProcedures);
			this.mainFormTabs.Controls.Add(this.tabConfig);
			this.mainFormTabs.Dock = System.Windows.Forms.DockStyle.Fill;
			this.mainFormTabs.Location = new System.Drawing.Point(0, 0);
			this.mainFormTabs.Name = "mainFormTabs";
			this.mainFormTabs.SelectedIndex = 0;
			this.mainFormTabs.Size = new System.Drawing.Size(240, 268);
			this.mainFormTabs.TabIndex = 0;
			// 
			// tabMessages
			// 
			this.tabMessages.AutoScroll = true;
			this.tabMessages.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(221)))), ((int)(((byte)(255)))), ((int)(((byte)(221)))));
			this.tabMessages.Controls.Add(this.messageListRefreshButton);
			this.tabMessages.Location = new System.Drawing.Point(0, 0);
			this.tabMessages.Name = "tabMessages";
			this.tabMessages.Size = new System.Drawing.Size(240, 245);
			this.tabMessages.Text = "Komunikaty";
			// 
			// messageListRefreshButton
			// 
			this.messageListRefreshButton.Location = new System.Drawing.Point(7, 222);
			this.messageListRefreshButton.Name = "messageListRefreshButton";
			this.messageListRefreshButton.Size = new System.Drawing.Size(226, 20);
			this.messageListRefreshButton.TabIndex = 1;
			this.messageListRefreshButton.Text = "Odśwież";
			this.messageListRefreshButton.Click += new System.EventHandler(this.messageListRefreshButton_Click);
			// 
			// tabProcedures
			// 
			this.tabProcedures.AutoScroll = true;
			this.tabProcedures.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(221)))), ((int)(((byte)(255)))), ((int)(((byte)(221)))));
			this.tabProcedures.Controls.Add(this.procedureListStatus);
			this.tabProcedures.Controls.Add(this.procedureListRefreshButton);
			this.tabProcedures.Controls.Add(this.procedureListGrid);
			this.tabProcedures.Location = new System.Drawing.Point(0, 0);
			this.tabProcedures.Name = "tabProcedures";
			this.tabProcedures.Size = new System.Drawing.Size(232, 242);
			this.tabProcedures.Text = "Procedury";
			// 
			// procedureListStatus
			// 
			this.procedureListStatus.Location = new System.Drawing.Point(7, 202);
			this.procedureListStatus.Name = "procedureListStatus";
			this.procedureListStatus.Size = new System.Drawing.Size(226, 20);
			this.procedureListStatus.Text = "procedureListStatus";
			// 
			// procedureListRefreshButton
			// 
			this.procedureListRefreshButton.Location = new System.Drawing.Point(7, 222);
			this.procedureListRefreshButton.Name = "procedureListRefreshButton";
			this.procedureListRefreshButton.Size = new System.Drawing.Size(226, 20);
			this.procedureListRefreshButton.TabIndex = 1;
			this.procedureListRefreshButton.Text = "Odśwież";
			this.procedureListRefreshButton.Click += new System.EventHandler(this.procedureListRefreshButton_Click);
			// 
			// procedureListGrid
			// 
			this.procedureListGrid.BackgroundColor = System.Drawing.Color.FromArgb(((int)(((byte)(221)))), ((int)(((byte)(255)))), ((int)(((byte)(221)))));
			this.procedureListGrid.Location = new System.Drawing.Point(7, 7);
			this.procedureListGrid.Name = "procedureListGrid";
			this.procedureListGrid.RowHeadersVisible = false;
			this.procedureListGrid.Size = new System.Drawing.Size(226, 190);
			this.procedureListGrid.TabIndex = 0;
			this.procedureListGrid.TableStyles.Add(this.ProcedureListStyle);
			this.procedureListGrid.DoubleClick += new System.EventHandler(this.dataGrid1_DoubleClick);
			this.procedureListGrid.CurrentCellChanged += new System.EventHandler(this.procedureListGrid_CurrentCellChanged);
			// 
			// ProcedureListStyle
			// 
			this.ProcedureListStyle.GridColumnStyles.Add(this.idColumn);
			this.ProcedureListStyle.GridColumnStyles.Add(this.nameColumn);
			this.ProcedureListStyle.GridColumnStyles.Add(this.stateColumn);
			this.ProcedureListStyle.MappingName = "ProcedureList";
			// 
			// idColumn
			// 
			this.idColumn.Format = "";
			this.idColumn.FormatInfo = null;
			this.idColumn.HeaderText = "ID";
			this.idColumn.MappingName = "id";
			this.idColumn.Width = 30;
			// 
			// nameColumn
			// 
			this.nameColumn.Format = "";
			this.nameColumn.FormatInfo = null;
			this.nameColumn.HeaderText = "Nazwa";
			this.nameColumn.MappingName = "name";
			this.nameColumn.Width = 110;
			// 
			// stateColumn
			// 
			this.stateColumn.Format = "";
			this.stateColumn.FormatInfo = null;
			this.stateColumn.HeaderText = "Stan";
			this.stateColumn.MappingName = "stateStr";
			this.stateColumn.Width = 80;
			// 
			// tabConfig
			// 
			this.tabConfig.AutoScroll = true;
			this.tabConfig.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(221)))), ((int)(((byte)(255)))), ((int)(((byte)(221)))));
			this.tabConfig.Controls.Add(this.newMessageNotifyBox);
			this.tabConfig.Controls.Add(this.updateIntervalBox);
			this.tabConfig.Controls.Add(this.updateIntervalLabel);
			this.tabConfig.Controls.Add(this.saveConfigButton);
			this.tabConfig.Controls.Add(this.apiURLBox);
			this.tabConfig.Controls.Add(this.apiURLLabel);
			this.tabConfig.Location = new System.Drawing.Point(0, 0);
			this.tabConfig.Name = "tabConfig";
			this.tabConfig.Size = new System.Drawing.Size(232, 242);
			this.tabConfig.Text = "Konfiguracja";
			// 
			// newMessageNotifyBox
			// 
			this.newMessageNotifyBox.Location = new System.Drawing.Point(7, 119);
			this.newMessageNotifyBox.Name = "newMessageNotifyBox";
			this.newMessageNotifyBox.Size = new System.Drawing.Size(226, 20);
			this.newMessageNotifyBox.TabIndex = 6;
			this.newMessageNotifyBox.Text = "Powiadamiaj o nowych zdarzeniach";
			this.newMessageNotifyBox.CheckStateChanged += new System.EventHandler(this.configBindingChanged);
			// 
			// updateIntervalBox
			// 
			this.updateIntervalBox.Location = new System.Drawing.Point(7, 81);
			this.updateIntervalBox.Maximum = new decimal(new int[] {
            32767,
            0,
            0,
            0});
			this.updateIntervalBox.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
			this.updateIntervalBox.Name = "updateIntervalBox";
			this.updateIntervalBox.Size = new System.Drawing.Size(226, 22);
			this.updateIntervalBox.TabIndex = 5;
			this.updateIntervalBox.Value = new decimal(new int[] {
            5,
            0,
            0,
            0});
			this.updateIntervalBox.ValueChanged += new System.EventHandler(this.configBindingChanged);
			// 
			// updateIntervalLabel
			// 
			this.updateIntervalLabel.Location = new System.Drawing.Point(7, 61);
			this.updateIntervalLabel.Name = "updateIntervalLabel";
			this.updateIntervalLabel.Size = new System.Drawing.Size(152, 20);
			this.updateIntervalLabel.Text = "Częstotliwość aktualizacji";
			// 
			// saveConfigButton
			// 
			this.saveConfigButton.Location = new System.Drawing.Point(7, 222);
			this.saveConfigButton.Name = "saveConfigButton";
			this.saveConfigButton.Size = new System.Drawing.Size(226, 20);
			this.saveConfigButton.TabIndex = 2;
			this.saveConfigButton.Text = "Zapisz";
			this.saveConfigButton.Click += new System.EventHandler(this.saveConfigButton_Click);
			// 
			// apiURLBox
			// 
			this.apiURLBox.Location = new System.Drawing.Point(7, 27);
			this.apiURLBox.MaxLength = 255;
			this.apiURLBox.Name = "apiURLBox";
			this.apiURLBox.Size = new System.Drawing.Size(226, 21);
			this.apiURLBox.TabIndex = 1;
			this.apiURLBox.GotFocus += new System.EventHandler(this.apiURLBox_GotFocus);
			this.apiURLBox.LostFocus += new System.EventHandler(this.apiURLBox_LostFocus);
			// 
			// apiURLLabel
			// 
			this.apiURLLabel.Location = new System.Drawing.Point(7, 4);
			this.apiURLLabel.Name = "apiURLLabel";
			this.apiURLLabel.Size = new System.Drawing.Size(100, 20);
			this.apiURLLabel.Text = "URL do API";
			// 
			// notification
			// 
			this.notification.Caption = "Disaster Manager Terminal";
			this.notification.Critical = true;
			this.notification.Icon = ((System.Drawing.Icon)(resources.GetObject("notification.Icon")));
			this.notification.InitialDuration = 60;
			this.notification.Text = "notification";
			this.notification.ResponseSubmitted += new Microsoft.WindowsCE.Forms.ResponseSubmittedEventHandler(this.notification_ResponseSubmitted);
			// 
			// mainForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
			this.AutoScroll = true;
			this.ClientSize = new System.Drawing.Size(240, 268);
			this.ControlBox = false;
			this.Controls.Add(this.mainFormTabs);
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.Menu = this.mainMenu;
			this.Name = "mainForm";
			this.Text = "Disaster Manager Terminal";
			this.mainFormTabs.ResumeLayout(false);
			this.tabMessages.ResumeLayout(false);
			this.tabProcedures.ResumeLayout(false);
			this.tabConfig.ResumeLayout(false);
			this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabControl mainFormTabs;
        private System.Windows.Forms.TabPage tabMessages;
        private System.Windows.Forms.TabPage tabProcedures;
        private System.Windows.Forms.MenuItem menuZamknijBtn;
        private System.Windows.Forms.TabPage tabConfig;
		private System.Windows.Forms.TextBox apiURLBox;
        private System.Windows.Forms.Button saveConfigButton;
        private System.Windows.Forms.Label updateIntervalLabel;
        private System.Windows.Forms.NumericUpDown updateIntervalBox;
        private System.Windows.Forms.CheckBox newMessageNotifyBox;
        private Microsoft.WindowsCE.Forms.Notification notification;
        private System.Windows.Forms.MenuItem menuMinimizeBtn;
        private System.Windows.Forms.DataGridTableStyle ProcedureListStyle;
        private System.Windows.Forms.DataGridTextBoxColumn idColumn;
        private System.Windows.Forms.Button procedureListRefreshButton;
        private System.Windows.Forms.DataGridTextBoxColumn nameColumn;
		private System.Windows.Forms.DataGridTextBoxColumn stateColumn;
        private System.Windows.Forms.Label procedureListStatus;
        private System.Windows.Forms.DataGrid procedureListGrid;
		private Microsoft.WindowsCE.Forms.InputPanel inputPanel;
		private System.Windows.Forms.Button messageListRefreshButton;
		private System.Windows.Forms.Label apiURLLabel;

    }
}

