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
using wpf_dotnet.Utils;
using wpf_dotnet;


namespace wpf_dotnet
{
    public partial class Home : Page
    {
        private Grid _lastSelectedGroup;
        private Grid _lastSelectedPerson;
        private readonly HttpClient _client = new HttpClient();
        private ObservableCollection<Message> _messages = new ObservableCollection<Message>();
        public static Home Instance { get; private set; }
        public CurrentUser CurrentUser { get; private set; }
        private ObservableCollection<Room> _publicRooms = new ObservableCollection<Room>();
        private ObservableCollection<Room> _privateRooms = new ObservableCollection<Room>();
        public ObservableCollection<Room> PublicRooms => _publicRooms;
        public ObservableCollection<Room> PrivateRooms => _privateRooms;



        public Home()
        {
            InitializeComponent();
            //MessageBox.Show(SessionManager.Token);
            Instance = this;
            DataContext = this;
            Loaded += MainWindow_Loaded;
            MessagesList.ItemsSource = _messages;
        }

        private async void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            await LoadCurrentUser();
            await LoadPublicRooms();
            await LoadMessages();
        }

        private async Task LoadCurrentUser()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await _client.GetAsync("http://localhost:8080/users/me");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                CurrentUser = JsonConvert.DeserializeObject<CurrentUser>(json);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement utilisateur: {ex.Message}");
            }
        }
        private async Task LoadPublicRooms()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await _client.GetAsync("http://localhost:8080/rooms/type/1");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                var rooms = JsonConvert.DeserializeObject<List<Room>>(json);

                _publicRooms.Clear();
                foreach (var room in rooms)
                {
                    _publicRooms.Add(room);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement des salons publics: {ex.Message}");
            }
        }
        private async Task LoadPrivateRooms()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await _client.GetAsync("http://localhost:8080/rooms/type/2");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                var rooms = JsonConvert.DeserializeObject<List<Room>>(json);

                _privateRooms.Clear();
                foreach (var room in rooms)
                {
                    _privateRooms.Add(room);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement des messages privés: {ex.Message}");
            }
        }
        private async Task LoadMessages(int roomId = 2)
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await _client.GetAsync($"http://localhost:8080/messages/room/{roomId}");
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

        private async void ShipmentButton_Click(object sender, RoutedEventArgs e)
        {
            GroupsSection.Visibility = Visibility.Visible;
            PersonsSection.Visibility = Visibility.Collapsed;
            await LoadPublicRooms();
        }

        private async void UsersButton_Click(object sender, RoutedEventArgs e)
        {
            PersonsSection.Visibility = Visibility.Visible;
            GroupsSection.Visibility = Visibility.Collapsed;
            await LoadPrivateRooms();
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

        private async void GroupItem_PreviewMouseDown(object sender, MouseButtonEventArgs e)
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


                await LoadMessages(selectedRoom.Id);
            }
        }

        private async void PersonItem_PreviewMouseDown(object sender, MouseButtonEventArgs e)
        {
            var grid = sender as Grid;
            if (grid?.DataContext is Room selectedRoom)
            {
                if (_lastSelectedPerson != null)
                {
                    _lastSelectedPerson.Background = Brushes.Transparent;
                }
                grid.Background = new SolidColorBrush(Color.FromArgb(30, 0, 0, 0));
                _lastSelectedPerson = grid;

                await LoadMessages(selectedRoom.Id);
            }
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



        public class Message
        {
            public int Id { get; set; }
            public string Content { get; set; }
            public int UserId { get; set; }
            public string Username { get; set; }
            public DateTime CreatedAt { get; set; }
            public int RoomId { get; set; }
        }

        public class Room
        {
            public int Id { get; set; }
            public string Name { get; set; }
            public int TypeId { get; set; }
            public List<int> UserIds { get; set; }
            public int CreatorId { get; set; }
            public DateTime CreatedAt { get; set; }
            public DateTime UpdatedAt { get; set; }
        }
    }
}