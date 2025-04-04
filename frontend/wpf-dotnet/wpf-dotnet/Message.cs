using System;

namespace wpf_dotnet
{
    public class Message
    {
        public int Id { get; set; }
        public string Content { get; set; }
        public int UserId { get; set; }
        public string Username { get; set; }
        public DateTime CreatedAt { get; set; }
        public int RoomId { get; set; }
    }
}