using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using Newtonsoft.Json;
using wpf_dotnet.Utils;
using wpf_dotnet;
using System.Text;

using System;

namespace wpf_dotnet
{
    public partial class MembersListControl : UserControl
    {
        private readonly HttpClient _client = new HttpClient();
       
        public MembersListControl()
        {
            InitializeComponent();
        }


        public async void MembersListControl_Loaded()
        {
            int roomId = Home.CurrentRoomId;

            var data = await LoadUserRoomInfo(roomId);
            if (data != null)
            {
                RoomTitle.Text = $"Membres du salon : {data.RoomName}";
                MembersList.ItemsSource = data.Users;
            }
        }   

        private async Task<UserRoomResponse> LoadUserRoomInfo(int _currentRoomId)
        {
            try
            {
                _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SessionManager.Token);
                var response = await _client.GetAsync($"http://localhost:8080/user-rooms/room/{_currentRoomId}");
                response.EnsureSuccessStatusCode();

                var json = await response.Content.ReadAsStringAsync();
                var result = JsonConvert.DeserializeObject<UserRoomResponse>(json);

                return result;
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Erreur lors du chargement des infos du salon : {ex.Message}");
                return null;
            }
        }

        public class UserRoomResponse
        {
            public string RoomName { get; set; }
            public List<UserRoom> Users { get; set; }
        }

        public class UserRoom
        {
            public int Id { get; set; }
            public string Username { get; set; }
            public string Role { get; set; }
            public bool Blocked { get; set; }

            public string BlockedText => Blocked ? "Oui" : "Non";
        }
    }
}
