using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Newtonsoft.Json;

namespace wpf_dotnet
{
    public partial class MainWindow : Window
    {
        private Grid _lastSelectedGroup;
        private Grid _lastSelectedPerson;
        private readonly HttpClient _client = new HttpClient();
        private ObservableCollection<Message> _messages = new ObservableCollection<Message>();
        public CurrentUser _currentUser;

        public MainWindow()
        {
            InitializeComponent();
            Loaded += MainWindow_Loaded;
            MessagesList.ItemsSource = _messages;
        }

        private async void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            await LoadCurrentUser();
            await LoadMessages();
        }

        private async Task LoadCurrentUser()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzdHJvb25nIiwiaWF0IjoxNzQzNjM5MDQxLCJleHAiOjE3NDM3MjU0NDF9.u_hMBchf2ZdPCefz6666H-qSpboJK_0idCPuPd9UVudhZproI9KjHSsFmBnwtukL");
                var response = await _client.GetAsync("http://localhost:8080/users/me");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                _currentUser = JsonConvert.DeserializeObject<CurrentUser>(json);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement utilisateur: {ex.Message}");
            }
        }

        private async Task LoadMessages()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzdHJvb25nIiwiaWF0IjoxNzQzNjM5MDQxLCJleHAiOjE3NDM3MjU0NDF9.u_hMBchf2ZdPCefz6666H-qSpboJK_0idCPuPd9UVudhZproI9KjHSsFmBnwtukL");
                var response = await _client.GetAsync("http://localhost:8080/messages/room/2");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                var messages = JsonConvert.DeserializeObject<List<Message>>(json);
                _messages.Clear();
                foreach (var msg in messages)
                {
                    _messages.Add(msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement messages: {ex.Message}");
            }
        }

        private void ShipmentButton_Click(object sender, RoutedEventArgs e)
        {
            GroupsSection.Visibility = Visibility.Visible;
            PersonsSection.Visibility = Visibility.Collapsed;
        }

        private void UsersButton_Click(object sender, RoutedEventArgs e)
        {
            PersonsSection.Visibility = Visibility.Visible;
            GroupsSection.Visibility = Visibility.Collapsed;
        }

        private void SettingsButton_Click(object sender, RoutedEventArgs e)
        {
            var button = sender as Button;
            if (button != null && button.ContextMenu != null)
            {
                button.ContextMenu.PlacementTarget = button;
                button.ContextMenu.Placement = System.Windows.Controls.Primitives.PlacementMode.Relative;
                button.ContextMenu.VerticalOffset = -button.ActualHeight - 5;
                button.ContextMenu.HorizontalOffset = 0;
                button.ContextMenu.IsOpen = true;
            }
        }

        private void ProfileMenuItem_Click(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("Ouverture du profil utilisateur", "Profil", MessageBoxButton.OK, MessageBoxImage.Information);
        }

        private void LogoutMenuItem_Click(object sender, RoutedEventArgs e)
        {
            var result = MessageBox.Show("Êtes-vous sûr de vouloir vous déconnecter ?", "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question);
            if (result == MessageBoxResult.Yes)
            {
                Application.Current.Shutdown();
            }
        }

        private void GroupItem_PreviewMouseDown(object sender, MouseButtonEventArgs e)
        {
            var grid = sender as Grid;
            if (grid == null) return;
            if (_lastSelectedGroup != null)
            {
                _lastSelectedGroup.Background = Brushes.Transparent;
            }
            grid.Background = new SolidColorBrush(Color.FromArgb(30, 0, 0, 0));
            _lastSelectedGroup = grid;
            string groupName = grid.Tag?.ToString() ?? "Unknown group";
            MessageBox.Show($"Group selected: {groupName}");
        }

        private void PersonItem_PreviewMouseDown(object sender, MouseButtonEventArgs e)
        {
            var grid = sender as Grid;
            if (grid == null) return;
            if (_lastSelectedPerson != null)
            {
                _lastSelectedPerson.Background = Brushes.Transparent;
            }
            grid.Background = new SolidColorBrush(Color.FromArgb(30, 0, 0, 0));
            _lastSelectedPerson = grid;
            string personName = grid.Tag?.ToString() ?? "Unknown person";
            MessageBox.Show($"Person selected: {personName}");
        }

        private void ShowAllGroups_Click(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("Affichage de tous les salons");
        }

        private void ShowAllPersons_Click(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("Affichage de tous les contacts");
        }

        private void ChatMenuButton_Click(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("Menu du chat ouvert");
        }

        private void SendMessageButton_Click(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("Message envoyé");
        }
    }

    public class Message
    {
        public int Id { get; set; }
        public string Content { get; set; }
        public int UserId { get; set; }
        public string Username { get; set; }
        public DateTime CreatedAt { get; set; }
        public int RoomId { get; set; }
    }

    public class CurrentUser
    {
        public int Id { get; set; }
        public string Username { get; set; }
    }
}