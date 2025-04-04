using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using Newtonsoft.Json;
using wpf_dotnet.Utils;

namespace wpf_dotnet
{
    public partial class ProfileModal : Window
    {
        private CurrentUser _currentUser;

        public ProfileModal(CurrentUser currentUser)
        {
            InitializeComponent();
            _currentUser = currentUser;
            UsernameTextBox.Text = _currentUser.Username;
        }

        private async void SaveButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string newUsername = UsernameTextBox.Text;

                using (HttpClient client = new HttpClient())
                {
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);

                    var url = $"http://localhost:8080/users/me";
                    var content = new StringContent(JsonConvert.SerializeObject(new { username = newUsername }), Encoding.UTF8, "application/json");

                    HttpResponseMessage response = await client.PutAsync(url, content);

                    if (response.IsSuccessStatusCode)
                    {
                        _currentUser.Username = newUsername;
                    }
                    else
                    {
                        string errorMessage = await response.Content.ReadAsStringAsync();
                        MessageBox.Show($"Erreur : {response.StatusCode}\n{errorMessage}");
                    }
                }

                DialogResult = true;
                Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur lors de la mise à jour : {ex.Message}");
            }
        }

    }
}
