using System;
using System.Linq;
using System.Text;
using System.Collections.Generic;
using WebSocketSharp;
using Newtonsoft.Json;
using wpf_dotnet.Utils;

namespace wpf_dotnet
{
    public class Notification
    {
        public int count { get; set; }
        public int roomId { get; set; }
        public int senderId { get; set; }
    }

    public class WebSocketService
    {
        private readonly Action<Message> _onMessageReceived;
        private readonly Action<string, int> _onNotificationReceived;
        private WebSocket _ws;
        private string _currentRoomId;
        private const string ServerUrl = "ws://localhost:8080/ws";

        private string AuthToken => SessionManager.Token?.Trim();

        public WebSocketService(Action<Message> onMessageReceived, Action<string, int> onNotificationReceived = null)
        {
            _onMessageReceived = onMessageReceived;
            _onNotificationReceived = onNotificationReceived;
        }

        /// <summary>
        /// Se connecte au serveur WebSocket pour la salle indiquée.
        /// Si une connexion existe déjà sur une autre salle, elle est fermée.
        /// </summary>
        public void Connect(string roomId)
        {
            if (_ws != null && _ws.IsAlive)
            {
                if (_currentRoomId != roomId)
                {
                    Disconnect();
                }
                else
                {
                    return;
                }
            }

            _currentRoomId = roomId;
            _ws = new WebSocket(ServerUrl);

            _ws.OnOpen += (sender, e) =>
            {
                Console.WriteLine("WebSocket connecté. Envoi de la trame CONNECT STOMP.");
                var connectFrame = $"CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\nAuthorization:Bearer {AuthToken}\n\n\0";
                _ws.Send(connectFrame);
            };

            _ws.OnMessage += (sender, e) =>
            {
                ProcessStompFrame(e.Data);
            };

            _ws.OnError += (sender, e) =>
            {
                Console.WriteLine("WebSocket error: " + e.Message);
            };

            _ws.OnClose += (sender, e) =>
            {
                Console.WriteLine("WebSocket closed: " + e.Reason);
            };

            _ws.Connect();
        }

        /// <summary>
        /// Envoie un message de chat à la salle spécifiée.
        /// </summary>
        public void SendMessage(string roomId, string messageContent)
        {
            if (_ws == null || !_ws.IsAlive || string.IsNullOrWhiteSpace(messageContent))
                return;

            var destination = $"/app/chat.send/{roomId}";
            var messageJson = JsonConvert.SerializeObject(new { content = messageContent });
            var sendFrame = $"SEND\ndestination:{destination}\nAuthorization:Bearer {AuthToken}\n\n{messageJson}\0";
            _ws.Send(sendFrame);
        }

        /// <summary>
        /// Ferme la connexion WebSocket en envoyant la trame DISCONNECT.
        /// </summary>
        public void Disconnect()
        {
            if (_ws != null)
            {
                if (_ws.IsAlive)
                {
                    var disconnectFrame = "DISCONNECT\n\n\0";
                    _ws.Send(disconnectFrame);
                    _ws.Close();
                }
                _ws = null;
            }
        }

        /// <summary>
        /// Analyse et traite la trame STOMP reçue.
        /// </summary>
        private void ProcessStompFrame(string frame)
        {
            var lines = frame.Split(new[] { "\n" }, StringSplitOptions.None);
            if (lines.Length == 0)
                return;

            var command = lines[0].Trim();

            if (command == "CONNECTED")
            {
                Console.WriteLine("STOMP connecté.");
                SubscribeToTopics();
            }
            else if (command == "MESSAGE")
            {
                int emptyLineIndex = Array.IndexOf(lines, "");
                if (emptyLineIndex >= 0 && emptyLineIndex < lines.Length - 1)
                {
                    var body = string.Join("\n", lines, emptyLineIndex + 1, lines.Length - emptyLineIndex - 1);
                    body = body.Trim('\0');

                    string destination = null;
                    foreach (var line in lines)
                    {
                        if (line.StartsWith("destination:"))
                        {
                            destination = line.Substring("destination:".Length).Trim();
                            break;
                        }
                    }

                    if (!string.IsNullOrEmpty(destination))
                    {
                        if (destination.StartsWith($"/topic/room."))
                        {
                            try
                            {
                                var msg = JsonConvert.DeserializeObject<Message>(body);
                                _onMessageReceived?.Invoke(msg);
                            }
                            catch (Exception ex)
                            {
                                Console.WriteLine("Erreur lors du parsing du message: " + ex.Message);
                            }
                        }
                        else if (destination.Contains("/queue/unreadCount"))
                        {
                            try
                            {
                                var notification = JsonConvert.DeserializeObject<Notification>(body);
                                Console.WriteLine("Nouveaux messages non lus : " + notification.count);
                                
                                int currentUserId = GetUserIdFromToken(AuthToken);
                                
                                if (notification.senderId != currentUserId)
                                {
                                    _onNotificationReceived?.Invoke(notification.roomId.ToString(), notification.count);
                                }
                                else
                                {
                                    Console.WriteLine("Message envoyé par l'utilisateur actuel, pas de notification");
                                }
                            }
                            catch (Exception ex)
                            {
                                Console.WriteLine("Erreur lors du parsing de la notification: " + ex.Message);
                            }
                        }
                    }
                }
            }
        }

        /// <summary>
        /// S'abonne aux différents topics STOMP (chat et notifications privées).
        /// </summary>
        private void SubscribeToTopics()
        {
            var subscribeChat = $"SUBSCRIBE\nid:sub-{_currentRoomId}\ndestination:/topic/room.{_currentRoomId}\nAuthorization:Bearer {AuthToken}\n\n\0";
            _ws.Send(subscribeChat);

            var username = GetUsernameFromToken(AuthToken);
            if (!string.IsNullOrEmpty(username))
            {
                var subscribeNotif = $"SUBSCRIBE\nid:sub-notif-{username}\ndestination:/user/{username}/queue/unreadCount\nAuthorization:Bearer {AuthToken}\n\n\0";
                _ws.Send(subscribeNotif);
                Console.WriteLine($"Abonné aux notifications pour l'utilisateur: {username}");
            }
            else
            {
                Console.WriteLine("Impossible d'extraire le nom d'utilisateur du token JWT");
            }
        }

        /// <summary>
        /// Extrait le nom d'utilisateur du token JWT
        /// </summary>
        private string GetUsernameFromToken(string token)
        {
            try
            {
                if (token.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
                {
                    token = token.Substring("Bearer ".Length);
                }

                // Divise le token en ses trois parties (header, payload, signature)
                string[] parts = token.Split('.');
                if (parts.Length != 3)
                {
                    Console.WriteLine("Format de token JWT invalide");
                    return null;
                }

                // Décode la partie payload (Base64Url)
                string payload = DecodeBase64Url(parts[1]);
                
                // Parse le JSON du payload
                var payloadData = JsonConvert.DeserializeObject<Dictionary<string, object>>(payload);
                
                // Cherche les claims qui pourraient contenir le nom d'utilisateur
                foreach (var claimType in new[] { "name", "sub", "preferred_username", "username" })
                {
                    if (payloadData.TryGetValue(claimType, out object value) && value != null)
                    {
                        return value.ToString();
                    }
                }
                
                Console.WriteLine("Aucun claim de nom d'utilisateur trouvé dans le token");
                return null;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur lors du décodage du token JWT: {ex.Message}");
                return null;
            }
        }

        /// <summary>
        /// Décode une chaîne Base64Url en texte
        /// </summary>
        private string DecodeBase64Url(string base64Url)
        {
            // Remplace les caractères spécifiques à Base64Url par ceux de Base64 standard
            string base64 = base64Url.Replace('-', '+').Replace('_', '/');
            
            // Ajoute le padding si nécessaire
            switch (base64.Length % 4)
            {
                case 2: base64 += "=="; break;
                case 3: base64 += "="; break;
            }
            
            // Décode la chaîne Base64
            byte[] bytes = Convert.FromBase64String(base64);
            return Encoding.UTF8.GetString(bytes);
        }

        /// <summary>
        /// Extrait l'ID de l'utilisateur du token JWT
        /// </summary>
        private int GetUserIdFromToken(string token)
        {
            try
            {
                if (token.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
                {
                    token = token.Substring("Bearer ".Length);
                }

                // Divise le token en ses trois parties
                string[] parts = token.Split('.');
                if (parts.Length != 3)
                {
                    Console.WriteLine("Format de token JWT invalide");
                    return -1;
                }

                // Décode la partie payload
                string payload = DecodeBase64Url(parts[1]);
                
                // Parse le JSON du payload
                var payloadData = JsonConvert.DeserializeObject<Dictionary<string, object>>(payload);
                
                // Cherche les claims qui pourraient contenir l'ID utilisateur
                foreach (var claimType in new[] { "id", "sub", "user_id" })
                {
                    if (payloadData.TryGetValue(claimType, out object value) && value != null)
                    {
                        if (int.TryParse(value.ToString(), out int userId))
                        {
                            return userId;
                        }
                    }
                }
                
                Console.WriteLine("Aucun claim d'ID utilisateur trouvé dans le token");
                return -1;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur lors du décodage du token JWT: {ex.Message}");
                return -1;
            }
        }
    }
}
