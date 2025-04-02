using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;

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

        private void HandleLoginButton(object sender, RoutedEventArgs e)
        {
            string username = UsernameTextBox.Text;
            string password = PasswordBox.Password;

            MessageBox.Show($"Login that guy\nNom d'utilisateur: {username}\nMot de passe: {password}");

            UsernameTextBox.Text = "";
            PasswordBox.Password = "";
            
            GoToHome(sender, e);
        }
    }
}
