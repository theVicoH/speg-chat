using System.Windows.Controls;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Collections.ObjectModel;

namespace wpf_dotnet
{
    public partial class AddRoomDialog : Window
    {
        public ObservableCollection<User> SelectedUsers { get; } = new ObservableCollection<User>();
        public string RoomName { get; private set; }
        public List<int> SelectedUserIds { get; private set; }
        private readonly int _roomType;

        public AddRoomDialog(int roomType)
        {
            InitializeComponent();
            _roomType = roomType;
            DataContext = Home.Instance.Users;

            if (_roomType == 2)
            {
                Title = "Nouvelle conversation privée";
                UsersListBox.SelectionMode = SelectionMode.Single;
            }
        }

        private void AddButton_Click(object sender, RoutedEventArgs e)
        {
            if (_roomType == 2 && SelectedUsers.Count >= 1)
            {
                MessageBox.Show("Vous ne pouvez sélectionner qu'un seul utilisateur");
                return;
            }

            var selected = UsersListBox.SelectedItems.Cast<User>().ToList();
            foreach (var user in selected.Where(u => !SelectedUsers.Contains(u)))
            {
                SelectedUsers.Add(user);
            }
        }

        private void RemoveButton_Click(object sender, RoutedEventArgs e)
        {
            var selected = SelectedUsersListBox.SelectedItems.Cast<User>().ToList();
            foreach (var user in selected)
            {
                SelectedUsers.Remove(user);
            }
        }

        private void CreateButton_Click(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(RoomNameTextBox.Text))
            {
                MessageBox.Show("Veuillez entrer un nom de salon");
                return;
            }

            if (_roomType == 2 && SelectedUsers.Count != 1)
            {
                MessageBox.Show("Veuillez sélectionner seulement un utilisateur");
                return;
            }

            RoomName = RoomNameTextBox.Text;
            SelectedUserIds = SelectedUsers.Select(u => u.Id).ToList();
            DialogResult = true;
        }

        private void CancelButton_Click(object sender, RoutedEventArgs e)
        {
            DialogResult = false;
        }
    }
}