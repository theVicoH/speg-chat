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
        private ObservableCollection<Room> _rooms = new ObservableCollection<Room>();
        public ObservableCollection<Room> Rooms => _rooms;

        public MainWindow()
        {
            InitializeComponent();
            MainFrame.Navigate(new Login());
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
            if (grid?.DataContext is Room selectedRoom) 
            {
                if (_lastSelectedGroup != null)
                {
                    _lastSelectedGroup.Background = Brushes.Transparent;
                }
                grid.Background = new SolidColorBrush(Color.FromArgb(30, 0, 0, 0));
                _lastSelectedGroup = grid;

             
                LoadMessages(selectedRoom.Id); 
            }
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
        private async Task LoadRooms()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzdHJpbmciLCJpYXQiOjE3NDM2NDc2MDUsImV4cCI6MTc0MzczNDAwNX0.i7-7IbmGehq0f-wTfRZdTwQwQP1VqHe6q8SYtqnqVxS4sWnAPsDjwvd2-AmZXKfW");
                var response = await _client.GetAsync("http://localhost:8080/rooms");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                var rooms = JsonConvert.DeserializeObject<List<Room>>(json);

                _rooms.Clear();
                foreach (var room in rooms)
                {
                    _rooms.Add(room);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement des salons: {ex.Message}");
            }
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
    public class Room
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public int TypeId { get; set; }
        public int CreatorId { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime UpdatedAt { get; set; }
    }
}