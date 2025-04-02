using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace wpf_dotnet
{
    public partial class MainWindow : Window
    {
        private Grid _lastSelectedGroup;
        private Grid _lastSelectedPerson;

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
            // Ouvrir une fenêtre de profil ou autre action
            MessageBox.Show("Ouverture du profil utilisateur", "Profil", MessageBoxButton.OK, MessageBoxImage.Information);
        }

        private void LogoutMenuItem_Click(object sender, RoutedEventArgs e)
        {
            var result = MessageBox.Show("Êtes-vous sûr de vouloir vous déconnecter ?",
                                      "Confirmation",
                                      MessageBoxButton.YesNo,
                                      MessageBoxImage.Question);

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
}