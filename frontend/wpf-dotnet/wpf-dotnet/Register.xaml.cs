using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;

namespace wpf_dotnet
{
    public partial class Register: Page
    {
        public Register()
        {
            InitializeComponent();
        }

        private void GoToHome(object sender, RoutedEventArgs e)
        {
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

        private void GoToLogin(object sender, RoutedEventArgs e)
        {
            if (NavigationService != null)
            {
                NavigationService.Navigate(new Login());
            }
            else
            {
                // Trouver MainWindow et naviguer manuellement
                var mainWindow = Application.Current.MainWindow as MainWindow;
                mainWindow?.MainFrame.Navigate(new Login());
            }
        }

        private void HandleRegisterButton(object sender, RoutedEventArgs e)
        {
            string username = UsernameTextBox.Text;
            string password = PasswordBox.Password;

            MessageBox.Show($"Register that guy\nNom d'utilisateur: {username}\nMot de passe: {password}");

            UsernameTextBox.Text = "";
            PasswordBox.Password = "";

            //GoToHome(sender, e);
        }
    }
}
