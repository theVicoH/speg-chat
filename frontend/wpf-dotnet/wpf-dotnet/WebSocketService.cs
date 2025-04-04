using Apache.NMS;
using Apache.NMS.Stomp;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Threading;
using wpf_dotnet;
using Application = System.Windows.Application;

namespace wpf_dotnet
{
    public class WebSocketService
    {
        private IConnection _connection;
        private ISession _session;
        private IMessageConsumer _consumer;
        private string _currentRoomId;
        private readonly Action<Message> _onMessageReceived;

        public WebSocketService(Action<Message> onMessageReceived)
        {
            _onMessageReceived = onMessageReceived;
        }

        public void Connect(string roomId)
        {
            if (_connection != null && _currentRoomId == roomId)
                return;

            Disconnect();

            try
            {
                // Configuration de la connexion
                var uri = new Uri("stomp://localhost:8080/ws");
                var factory = new NMSConnectionFactory(uri);

                _connection = factory.CreateConnection();
                _connection.Start();

                _session = _connection.CreateSession();
                var destination = _session.GetTopic($"/topic/room.{roomId}");

                _consumer = _session.CreateConsumer(destination);
                _consumer.Listener += message =>
                {
                    var textMessage = message as ITextMessage;
                    var msg = JsonConvert.DeserializeObject<Message>(textMessage.Text);
                    Application.Current.Dispatcher.Invoke(() => _onMessageReceived(msg));
                };

                _currentRoomId = roomId;
                Console.WriteLine("✅ Connecté au WebSocket");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Erreur de connexion: {ex.Message}");
            }
        }

        public void Disconnect()
        {
            try
            {
                _consumer?.Close();
                _session?.Close();
                _connection?.Close();
                Console.WriteLine("🔌 Déconnecté");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Erreur de déconnexion: {ex.Message}");
            }
        }
        public void SendMessage(string roomId, string content)
        {
            try
            {
                if (_connection == null || !_connection.IsStarted)
                {
                    Console.WriteLine("⚠️ Tentative de reconnexion...");
                    Connect(roomId); // On essaie de se reconnecter
                    Thread.Sleep(500); // Temps pour la connexion
                }
                // Vérifie que la session existe
                if (_session == null)
                {
                    Console.WriteLine("❌ Session non initialisée");
                    return;
                }

                // Crée la destination et le producteur
                var destination = _session.GetTopic($"/app/chat.send/{roomId}");
                var producer = _session.CreateProducer(destination);

                // Crée et envoie le message
                var message = _session.CreateTextMessage(JsonConvert.SerializeObject(new { content }));
                producer.Send(message);
                Console.WriteLine($"📤 Message envoyé dans la salle {roomId}: {content}");
                Console.WriteLine("📤 Message envoyé");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Erreur d'envoi: {ex.Message}");
            }
        }
    }
}