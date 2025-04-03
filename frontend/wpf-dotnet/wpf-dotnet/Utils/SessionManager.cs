namespace wpf_dotnet.Utils
{
    public static class SessionManager
    {
        public static string Token { get; private set; }

        public static void SetToken(string token)
        {
            Token = token;
        }

        public static bool IsAuthenticated()
        {
            return !string.IsNullOrEmpty(Token);
        }
    }
}
