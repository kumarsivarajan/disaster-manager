using Microsoft.Win32;

namespace dm_terminal
{
	class Config
	{
		const string keyStr = "Software\\dm-terminal";
		RegistryKey key;
		API api;

		public decimal updateInterval { get; set; }
		public bool newMessageNotify { get; set; }

		private string apiURL_p;
		public string apiURL
		{
			get
			{
				return apiURL_p;
			}
			set
			{
				apiURL_p = value;
				api = null;
			}
		}

		public Config()
		{
			key = Registry.CurrentUser.OpenSubKey(keyStr, true);
			if (key == null)
				key = Registry.CurrentUser.CreateSubKey(keyStr);

			apiURL = (string)key.GetValue("api-url", "");
			updateInterval = (int)key.GetValue("update-interval", 60);
			newMessageNotify = ((int)key.GetValue("new-message-notify", 1) > 0);
		}

		public void save()
		{
			key.SetValue("api-url", apiURL, RegistryValueKind.String);
			key.SetValue("update-interval", updateInterval, RegistryValueKind.DWord);
			if (newMessageNotify)
				key.SetValue("new-message-notify", 1, RegistryValueKind.DWord);
			else
				key.SetValue("new-message-notify", 0, RegistryValueKind.DWord);
		}

		public API getAPI()
		{
			if (api == null)
				api = new API(apiURL);
			return api;
		}
	}
}
