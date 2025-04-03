using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using System;
using System.Configuration;
using System.Net.Http;
using System.Text;
using wpf_dotnet.Utils;

namespace wpf_dotnet
{
    public partial class Login : Page
    {
        public Login()
        {
            InitializeComponent();
        }

        private void GoToHome(object sender, RoutedEventArgs e)
        {
            if (!SessionManager.IsAuthenticated())
            {
                MessageBox.Show("Vous devez être connecté pour accéder à cette page !");
                return;
            }
            if (NavigationService != null)
            {
                NavigationService.Navigate(new Home());
            }
            else
            {
                // Trouver MainWindow et naviguer manuellement
                var mainWindow = Application.Current.MainWindow as MainWindow;
                mainWindow?.MainFrame.Navigate(new Home());
            }
        }

        private void GoToRegister(object sender, RoutedEventArgs e)
        {
            if (NavigationService != null)
            {
                NavigationService.Navigate(new Register());
            }
            else
            {
                // Trouver MainWindow et naviguer manuellement
                var mainWindow = Application.Current.MainWindow as MainWindow;
                mainWindow?.MainFrame.Navigate(new Register());
            }
        }

        private async void HandleLoginButton(object sender, RoutedEventArgs e)
        {
            string username = UsernameTextBox.Text;
            string password = PasswordBox.Password;

            string json = $"{{\"username\":\"{username}\",\"password\":\"{password}\"}}";

            string url = "http://localhost:8080/auth/login";

            using (HttpClient client = new HttpClient())
            {
                try
                {
                    StringContent content = new StringContent(json, Encoding.UTF8, "application/json");

                    HttpResponseMessage response = await client.PostAsync(url, content);

                    if (response.IsSuccessStatusCode)
                    {
                        string responseContent = await response.Content.ReadAsStringAsync();

                        // Extraire le token depuis la réponse JSON
                        string token = ExtractToken(responseContent);

                        // Stocker le token dans SessionManager
                        SessionManager.SetToken(token);

                        GoToHome(sender, e);
                    }
                    else
                    {
                        MessageBox.Show("Nom d'utilisateur ou mot de passe incorect");
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
        }

        // Fonction pour extraire le token depuis la réponse JSON
        private string ExtractToken(string jsonResponse)
        {
            int tokenStart = jsonResponse.IndexOf("\"token\":\"") + 9;
            if (tokenStart == 8) return "";

            int tokenEnd = jsonResponse.IndexOf("\"", tokenStart);
            return jsonResponse.Substring(tokenStart, tokenEnd - tokenStart);
        }
    }
}
