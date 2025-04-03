using System.Windows;
using System.Windows.Controls;
using System;
using System.Net.Http;
using System.Text;
using wpf_dotnet.Utils;

namespace wpf_dotnet
{
    public partial class Register: Page
    {
        public Register()
        {
            InitializeComponent();
        }

        private void GoToLogin(object sender, RoutedEventArgs e)
        {
            if (NavigationService != null)
            {
                NavigationService.Navigate(new Login());
            }
            else
            {
                var mainWindow = Application.Current.MainWindow as MainWindow;
                mainWindow?.MainFrame.Navigate(new Login());
            }
        }

        private async void HandleRegisterButton(object sender, RoutedEventArgs e)
        {
            string username = UsernameTextBox.Text;
            string password = PasswordBox.Password;

            string json = $"{{\"username\":\"{username}\",\"password\":\"{password}\"}}";

            string url = "http://localhost:8080/auth/signup";

            using (HttpClient client = new HttpClient())
            {
                try
                {
                    StringContent content = new StringContent(json, Encoding.UTF8, "application/json");

                    HttpResponseMessage response = await client.PostAsync(url, content);

                    if (response.IsSuccessStatusCode)
                    {
                        GoToLogin(sender, e);
                    }
                    else
                    {
                        string responseContent = await response.Content.ReadAsStringAsync();
                        MessageBox.Show("Erreur lors de la création d'un compte: " + response.StatusCode + "\n" + responseContent);
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
        }
    }
}
