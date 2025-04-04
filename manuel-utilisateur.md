# **Manuel Utilisateur - Speg-Chat**

## **Installation**

### **Prérequis**
Avant de commencer, assurez-vous d'avoir les éléments suivants :
- Un ordinateur sous Windows.
- Une connexion Internet active.
- Les logiciels nécessaires installés :
  - **Backend** : Java 21, Maven, MariaDB.
  - **Frontend** : .NET SDK 5.0, Visual Studio.

### **Étapes d'installation**
1. Téléchargez le projet depuis le dépôt GitHub ou recevez les fichiers d'installation.
2. Suivez les instructions d'installation fournies dans la section précédente pour configurer le backend et le frontend.

---

## **Connexion et Inscription**

### **1. Inscription**
1. Lancez l'application Speg-Chat.
2. Cliquez sur le bouton **S'inscrire**.
3. Remplissez les champs suivants :
   - **Nom d'utilisateur** : Votre pseudonyme.
   - **Mot de passe** : Un mot de passe sécurisé.
4. Cliquez sur **S'identifier**.
5. Une fois l'inscription réussie, vous serez redirigé vers la page de connexion.

### **2. Connexion**
1. Sur la page de connexion, entrez vos identifiants :
   - **Nom d'utilisateur** : Votre pseudonyme utilisée lors de l'inscription.
   - **Mot de passe** : Le mot de passe associé.
2. Cliquez sur **Se connecter**.
3. Si les informations sont correctes, vous serez redirigé vers la page d'accueil.

---

## **Navigation dans l'application**

### **Page d'accueil**
- Une fois connecté, vous accédez à la page d'accueil.
- Vous pouvez voir les options disponibles, comme accéder à vos messages ou modifier votre profil.

### **Menu principal**
- **Messages Privés** : Consultez vos conversations privées.
- **Salons publiques** : Consultez les salons publiques.
- **Profil** : Modifiez vos informations personnelles ou vos préférences.
- **Déconnexion** : Quittez votre session en toute sécurité.

---

## **Fonctionnalités principales**

### **1. Gestion des utilisateurs**
- Inscription et connexion sécurisées.
- Gestion des sessions avec authentification via JWT.

### **2. Messagerie**
- Envoyez et recevez des messages en temps réel.
- Consultez l'historique de vos conversations.
- Participez à des conversations privées avec d'autres utilisateurs.

### **3. Salons publics**
- Rejoignez des salons de discussion existants.
- Créez vos propres salons publics.
- Invitez d'autres utilisateurs à rejoindre vos salons.

### **4. Gestion des salons**
- **Création d'un salon**:
  1. Accédez à la section "Salons publiques".
  2. Cliquez sur "Créer un salon".
  3. Donnez un nom à votre salon et définissez les paramètres.
  4. Cliquez sur "Confirmer" pour créer le salon.
- **Administration d'un salon**:
  - En tant que créateur, vous devenez automatiquement l'administrateur du salon.
  - Vous pouvez nommer des modérateurs parmi les membres du salon.
  - Vous avez le pouvoir de supprimer définitivement le salon.

### **5. Modération**
- **Pouvoirs des administrateurs**:
  - Supprimer des messages inappropriés.
  - Bloquer temporairement des utilisateurs.
  - Bannir définitivement des utilisateurs du salon.
  - Nommer et révoquer des modérateurs.
- **Pouvoirs des modérateurs**:
  - Supprimer des messages inappropriés.
  - Bloquer temporairement des utilisateurs.
  - Signaler des comportements abusifs à l'administrateur.

### **6. Profil**
- Modifiez vos informations personnelles (nom).

---

## **Dépannage**

### **Problèmes courants**
1. **Impossible de se connecter** :
   - Vérifiez vos identifiants.
   - Assurez-vous que le backend est en cours d'exécution.
2. **Erreur de connexion à la base de données** :
   - Vérifiez que MariaDB est correctement configuré.
   - Assurez-vous que les informations dans le fichier `application.properties` sont correctes.
3. **L'application ne répond pas** :
   - Redémarrez l'application.
   - Vérifiez que le backend et le frontend sont correctement configurés.
4. **Messages non délivrés** :
   - Vérifiez votre connexion internet.
   - Assurez-vous que vous n'avez pas été bloqué ou banni du salon.
5. **Impossible de créer un salon** :
   - Vérifiez que vous respectez les règles de nommage.
   - Assurez-vous que le nom du salon n'est pas déjà utilisé.

---

Merci d'utiliser Speg-Chat ! Nous espérons que cette application répondra à vos attentes.
