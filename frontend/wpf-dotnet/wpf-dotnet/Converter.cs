using System;
using System.Globalization;
using System.Windows;
using System.Windows.Data;
using static System.Net.Mime.MediaTypeNames;

namespace wpf_dotnet
{
    public class UserIdToAlignmentConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is int userId && System.Windows.Application.Current.MainWindow is MainWindow mainWindow)
            {
                return userId == mainWindow._currentUser?.Id
                    ? HorizontalAlignment.Right
                    : HorizontalAlignment.Left;
            }
            return HorizontalAlignment.Left;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    public class UserIdToVisibilityConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is int userId && System.Windows.Application.Current.MainWindow is MainWindow mainWindow)
            {
                return userId != mainWindow._currentUser?.Id
                    ? Visibility.Visible
                    : Visibility.Collapsed;
            }
            return Visibility.Collapsed;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    public class UserIdToInverseVisibilityConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is int userId && System.Windows.Application.Current.MainWindow is MainWindow mainWindow)
            {
                return userId == mainWindow._currentUser?.Id
                    ? Visibility.Visible
                    : Visibility.Collapsed;
            }
            return Visibility.Collapsed;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    public class InitialsConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is string username && !string.IsNullOrWhiteSpace(username))
            {
                var parts = username.Split(new[] { ' ' }, StringSplitOptions.RemoveEmptyEntries);
                if (parts.Length >= 2)
                {
                    return $"{char.ToUpper(parts[0][0])}{char.ToUpper(parts[1][0])}";
                }
                return username.Length >= 2
                    ? username.Substring(0, 2).ToUpper()
                    : username.ToUpper();
            }
            return "??";
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}