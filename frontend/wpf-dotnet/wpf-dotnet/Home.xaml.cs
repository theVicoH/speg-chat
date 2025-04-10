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
using System.Text;
using System.ComponentModel;
using System.Windows.Controls.Primitives;
using System.Linq;



namespace wpf_dotnet
{
    public partial class Home : Page, INotifyPropertyChanged

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
        private ObservableCollection<User> _users = new ObservableCollection<User>();
        public ObservableCollection<User> Users => _users;
        public event PropertyChangedEventHandler PropertyChanged;
        private WebSocketService _webSocketService;

        private static int _currentRoomId;  // Propriété statique
        public static int CurrentRoomId  // Propriété statique pour accéder à CurrentRoomId
        {
            get => _currentRoomId;
            set
            {
                _currentRoomId = value;
                // Tu pourrais aussi appeler OnPropertyChanged ici si tu veux que les autres éléments soient notifiés.
            }
        }


        protected virtual void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }



        public Home()
        {
            InitializeComponent();
            _webSocketService = new WebSocketService(OnWebSocketMessageReceived, OnNotificationReceived);
            Instance = this;
            DataContext = this;
            Loaded += MainWindow_Loaded;
            MessagesList.ItemsSource = _messages;
            this.Unloaded += Home_Unloaded;
        }

        private void OnNotificationReceived(string roomName, int count)
        {
            Application.Current.Dispatcher.Invoke(() =>
            {
                var room = PrivateRooms.FirstOrDefault(r => r.Name == roomName);
                if (room != null)
                {
                    room.UnreadCount = count;
                }
            });
        }


        private async void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            await LoadCurrentUser();
            await LoadPublicRooms();
            await LoadUsers();
            if (_publicRooms.Any())
            {
                CurrentRoomId = _publicRooms.First().Id;
                CurrentRoomName = _publicRooms.First().Name;
                await LoadMessages(CurrentRoomId);
            }
            else
            {
                _messages.Clear();
                CurrentRoomName = "Aucun salon disponible";
            }
        }

        private void OnWebSocketMessageReceived(Message message)
        {
            Application.Current.Dispatcher.Invoke(() =>
            {
                _messages.Add(message);
            });
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
                MessageBox.Show($"Erreur chargement des messages priv�s: {ex.Message}");
            }
        }





        private async Task LoadMessages(int roomId)
        {
            if (roomId <= 0) return;
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
        private async Task CreateRoom(string name, int typeId, List<int> userIds)
        {
            try
            {
                var roomData = new
                {
                    name = name,
                    typeId = typeId,
                    userIds = userIds
                };

                var content = new StringContent(JsonConvert.SerializeObject(roomData), Encoding.UTF8, "application/json");

                var response = await _client.PostAsync("http://localhost:8080/rooms", content);
                response.EnsureSuccessStatusCode();

                // Recharger la liste appropri�e
                if (typeId == 1) await LoadPublicRooms();
                else await LoadPrivateRooms();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur cr�ation salon: {ex.Message}");
            }
        }
        private async Task LoadUsers()
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await _client.GetAsync("http://localhost:8080/users");
                response.EnsureSuccessStatusCode();
                var json = await response.Content.ReadAsStringAsync();
                var users = JsonConvert.DeserializeObject<List<User>>(json);

                _users.Clear();
                foreach (var user in users)
                {
                    if (user.Id != CurrentUser?.Id) // Exclure l'utilisateur actuel
                        _users.Add(user);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur chargement utilisateurs: {ex.Message}");
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

        private async void ProfileMenuItem_Click(object sender, RoutedEventArgs e)
        {
            ProfileModal profileModal = new ProfileModal(CurrentUser);
            
            bool? result = profileModal.ShowDialog();
            
            if (result == true)
            {
                await LoadCurrentUser();
            }
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

        private void LogoutMenuItem_Click(object sender, RoutedEventArgs e)
        {
            var result = MessageBox.Show("Êtes-vous sûr de vouloir vous déconnecter ?", "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question);
            if (result == MessageBoxResult.Yes)
            {
                SessionManager.SetToken("");
                GoToLogin(sender, e);
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

          
                CurrentRoomName = selectedRoom.Name;
                CurrentRoomId = selectedRoom.Id;
                CurrentRoomType = 1;
                _webSocketService.Connect(selectedRoom.Id.ToString());

                await LoadMessages(selectedRoom.Id);
            }
        }

        private void Home_Unloaded(object sender, RoutedEventArgs e)
        {
            _webSocketService?.Disconnect();
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

                CurrentRoomName = selectedRoom.Name;
                CurrentRoomId = selectedRoom.Id;
                CurrentRoomType = 2;
                _webSocketService.Connect(selectedRoom.Id.ToString());

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
            var button = sender as Button;
            if (button?.ContextMenu != null)
            {
                button.ContextMenu.PlacementTarget = button;
                button.ContextMenu.Placement = PlacementMode.Bottom;
                button.ContextMenu.IsOpen = true;
            }
        }

        private void InteractRoleMenuItem_Click(object sender, RoutedEventArgs e)
        {
            MessagesScrollViewer.Visibility = Visibility.Collapsed;
            MessageInputGrid.Visibility = Visibility.Collapsed;
            OverlayContentGrid.Visibility = Visibility.Visible;
            InteractRoleView.Visibility = Visibility.Visible;
            MembersListView.Visibility = Visibility.Collapsed;
            InteractRoleView.LoadData(CurrentRoomId);
        }

        private void ListMembersMenuItem_Click(object sender, RoutedEventArgs e)
        {
            MessagesScrollViewer.Visibility = Visibility.Collapsed;
            MessageInputGrid.Visibility = Visibility.Collapsed;
            OverlayContentGrid.Visibility = Visibility.Visible;

            // Crée une nouvelle instance de MembersListControl
            MembersListControl membersListControl = new MembersListControl();

            // Ajoute MembersListControl à un conteneur (par exemple, un Grid) dans la vue principale
            OverlayContentGrid.Children.Clear();  // Efface les autres contrôles si nécessaire
            OverlayContentGrid.Children.Add(membersListControl);

            membersListControl.MembersListControl_Loaded();
        }


        private void BackButton_Click(object sender, RoutedEventArgs e)
        {
            OverlayContentGrid.Visibility = Visibility.Collapsed;
            MessagesScrollViewer.Visibility = Visibility.Visible;
            MessageInputGrid.Visibility = Visibility.Visible;
        }



        private async void DeleteRoomMenuItem_Click(object sender, RoutedEventArgs e)
        {
            if (CurrentRoomId == 0)
            {
                MessageBox.Show("Aucun salon sélectionné");
                return;
            }

            var result = MessageBox.Show($"Êtes-vous sûr de vouloir supprimer le salon {CurrentRoomName} ?",
                                        "Confirmation",
                                        MessageBoxButton.YesNo,
                                        MessageBoxImage.Question);

            if (result == MessageBoxResult.Yes)
            {
                try
                {
                    _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                    var response = await _client.DeleteAsync($"http://localhost:8080/rooms/{CurrentRoomId}");

                    if (response.IsSuccessStatusCode)
                    {
                        // Recharger les salons
                        if (_currentRoomType == 1) await LoadPublicRooms();
                        else await LoadPrivateRooms();

                        // Réinitialiser l'affichage
                        CurrentRoomId = 0;
                        CurrentRoomName = string.Empty;
                        _messages.Clear();

                        MessageBox.Show("Salon supprimé avec succès");
                    }
                    else if (response.StatusCode == System.Net.HttpStatusCode.Forbidden)
                    {
                        MessageBox.Show("Vous n'êtes pas autorisé à supprimer ce salon. Demandez à un administrateur.",
                                      "Permission refusée",
                                      MessageBoxButton.OK,
                                      MessageBoxImage.Warning);
                    }
                    else
                    {
                        MessageBox.Show($"Erreur lors de la suppression (HTTP {(int)response.StatusCode})",
                                      "Erreur",
                                      MessageBoxButton.OK,
                                      MessageBoxImage.Error);
                    }
                }
                catch (Exception ex)
                {
                    // Pour les versions avant .NET 5, on ne peut pas accéder au StatusCode directement
                    // On affiche donc un message générique
                    MessageBox.Show($"Erreur lors de la suppression : {ex.Message}",
                                  "Erreur",
                                  MessageBoxButton.OK,
                                  MessageBoxImage.Error);
                }
            }
        }
        private int _currentRoomType;
        public int CurrentRoomType
        {
            get => _currentRoomType;
            set => _currentRoomType = value;
        }

        private void SendMessageButton_Click(object sender, RoutedEventArgs e)
        {
            var messageContent = MessageInputTextBox.Text.Trim();

            // Validation du message
            if (string.IsNullOrEmpty(messageContent))
            {
                MessageBox.Show("Veuillez saisir un message");
                return;
            }

            // Validation de la salle
            if (_currentRoomId == 0)
            {
                MessageBox.Show("Veuillez sélectionner une salle d'abord");
                return;
            }

            try
            {
                // Envoi via WebSocket
                _webSocketService.SendMessage(_currentRoomId.ToString(), messageContent);

                // Réinitialisation du champ
                MessageInputTextBox.Text = string.Empty;
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur création salon: {ex.Message}");
            }
        }

        private async void AddPublicRoom_Click(object sender, RoutedEventArgs e)
        {
            await ShowAddRoomDialog(1);
        }

        private async void AddPrivateRoom_Click(object sender, RoutedEventArgs e)
        {
            await ShowAddRoomDialog(2);
        }

        private string _currentRoomName;
        public string CurrentRoomName
        {
            get => _currentRoomName;
            set
            {
                _currentRoomName = value;
                OnPropertyChanged(nameof(CurrentRoomName));
            }
        }

        private async Task ShowAddRoomDialog(int roomType)
        {
            await LoadUsers(); // Charger les utilisateurs � chaque ouverture

            var dialog = new AddRoomDialog(roomType)
            {
                Owner = Application.Current.MainWindow
            };

            if (dialog.ShowDialog() == true)
            {
                await CreateRoom(dialog.RoomName, roomType, dialog.SelectedUserIds);
            }
        }


        // private void OnNotificationReceived(string roomName, int count)
        // {
        //     Application.Current.Dispatcher.Invoke(() =>
        //     {
        //         // On recherche dans la collection PrivateRooms le salon correspondant (ici, par exemple, via le nom)
        //         var room = PrivateRooms.FirstOrDefault(r => r.Name == roomName);
        //         if (room != null)
        //         {
        //             room.UnreadCount = count;
        //         }
        //     });
        // }

        public class Room : INotifyPropertyChanged
        {
            public int Id { get; set; }
            public string Name { get; set; }
            public int TypeId { get; set; }
            public List<int> UserIds { get; set; }
            public int CreatorId { get; set; }
            public DateTime CreatedAt { get; set; }
            public DateTime UpdatedAt { get; set; }

            private int _unreadCount;
            public int UnreadCount
            {
                get => _unreadCount;
                set
                {
                    if (_unreadCount != value)
                    {
                        _unreadCount = value;
                        OnPropertyChanged(nameof(UnreadCount));
                    }
                }
            }

            public event PropertyChangedEventHandler PropertyChanged;
            protected void OnPropertyChanged(string propertyName) =>
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }



    }
}