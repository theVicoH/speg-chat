using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Http.Headers;
using System.Net.Http;
using System.Windows;
using System;
using System.Windows.Controls;
using Newtonsoft.Json;
using wpf_dotnet.Utils;
using System.Text;
namespace wpf_dotnet
{
    public partial class InteractRoleControl : UserControl
    {
        private ObservableCollection<RoomUser> _basicUsers = new ObservableCollection<RoomUser>();
        private ObservableCollection<RoomUser> _moderatorUsers = new ObservableCollection<RoomUser>();
        private int _currentRoomId;

        public InteractRoleControl()
        {
            InitializeComponent();
            BasicUsersList.ItemsSource = _basicUsers;
            ModeratorUsersList.ItemsSource = _moderatorUsers;
        }

        public async void LoadData(int roomId)
        {
            _currentRoomId = roomId;

            try
            {
                var client = new HttpClient();
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await client.GetAsync($"http://localhost:8080/user-rooms/room/{roomId}");

                var json = await response.Content.ReadAsStringAsync();
                var data = JsonConvert.DeserializeObject<dynamic>(json);

                _basicUsers.Clear();
                _moderatorUsers.Clear();

                foreach (var user in data.users)
                {
                    var roomUser = new RoomUser
                    {
                        Id = user.id,
                        Username = user.username,
                        RoleId = user.role == "administrator" ? 1 : user.role == "moderator" ? 2 : 3,
                        OriginalRoleId = user.role == "administrator" ? 1 : user.role == "moderator" ? 2 : 3 // Sauvegarde du rôle original
                    };

                    if (roomUser.RoleId == 2) _moderatorUsers.Add(roomUser);
                    else if (roomUser.RoleId == 3) _basicUsers.Add(roomUser);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur de chargement: {ex.Message}");
            }
        }

        private async void ConfirmChanges_Click(object sender, RoutedEventArgs e)
        {
            var client = new HttpClient();
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);

            bool hasErrors = false;
            var errorMessage = new StringBuilder();
            var successCount = 0;

            try
            {
                // Promotions
                foreach (RoomUser user in _moderatorUsers.Where(u => u.OriginalRoleId != 2))
                {
                    var url = $"http://localhost:8080/user-rooms/update-role/room/{_currentRoomId}/user/{user.Id}/role/2";

                    var response = await client.PutAsync(url, null);

                    if (response.IsSuccessStatusCode)
                    {
                        successCount++;
                    }
                    else
                    {
                        hasErrors = true;
                        var errorContent = await response.Content.ReadAsStringAsync();
                        errorMessage.AppendLine($"• Échec promotion de {user.Username}: {errorContent}");
                    }
                }

                // Rétrogradations
                foreach (RoomUser user in _basicUsers.Where(u => u.OriginalRoleId != 3))
                {
                    var url = $"http://localhost:8080/user-rooms/update-role/room/{_currentRoomId}/user/{user.Id}/role/3";

                    var response = await client.PutAsync(url, null);

                    if (response.IsSuccessStatusCode)
                    {
                        successCount++;
                    }
                    else
                    {
                        hasErrors = true;
                        var errorContent = await response.Content.ReadAsStringAsync();
                        errorMessage.AppendLine($"• Échec rétrogradation de {user.Username}: {errorContent}");
                    }
                }

                if (hasErrors)
                {
                    var fullMessage = new StringBuilder();

                    fullMessage.AppendLine("Vous n'avez pas les permissions nécessaires pour certains changements de rôle.");

                    MessageBox.Show(fullMessage.ToString(), "Erreur de permissions", MessageBoxButton.OK, MessageBoxImage.Warning);
                }

                if (successCount > 0)
                {
                    MessageBox.Show($"{successCount} changement(s) de rôle appliqué(s) avec succès !",
                                    "Succès",
                                    MessageBoxButton.OK,
                                    MessageBoxImage.Information);

                    // Recharger les données après modification
                    LoadData(_currentRoomId);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur lors de la communication avec le serveur : {ex.Message}",
                                "Erreur",
                                MessageBoxButton.OK,
                                MessageBoxImage.Error);
            }
        }

        private void PromoteButton_Click(object sender, RoutedEventArgs e)
        {
            foreach (RoomUser user in BasicUsersList.SelectedItems.Cast<RoomUser>().ToList())
            {
                _basicUsers.Remove(user);
                user.RoleId = 2;
                _moderatorUsers.Add(user);
            }
        }

        private void DemoteButton_Click(object sender, RoutedEventArgs e)
        {
            foreach (RoomUser user in ModeratorUsersList.SelectedItems.Cast<RoomUser>().ToList())
            {
                _moderatorUsers.Remove(user);
                user.RoleId = 3;
                _basicUsers.Add(user);
            }
        }
        public class RoomUser
        {
            public int Id { get; set; }
            public string Username { get; set; }
            public int RoleId { get; set; }
            public int OriginalRoleId { get; set; } // Nouvelle propriété
            public bool Blocked { get; set; }
        }
    }

}













