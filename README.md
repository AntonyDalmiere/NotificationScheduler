# Notification Scheduler
![Android](https://img.shields.io/badge/platform-Android-green)

Une librairie Android simple qui permet de planifier des notifications même après un redémarrage. 

## Introduction
Quand on veux envoyer une notifications a une heures spécifiques cela demande beaucoup de code inutile, la création d'un broadcastReveiver , le stockage de la notification etc ... Cela résulte en l'a création de souvent 3 ou 4 classes pour juste envoyer une notification a une heure précise. Grâce à cette librairie cela ce fait en moins de 5lignes.👍
## Fonctionnalités
- Planification a une heure précise 
- Précis même si l'appareil est en doze
- Choisir la couleur de la notification 
- Choisir l'icône de la notification 
- Actions au click de la notification
- Sous action 
- Planifications persistente même après un redémarrage 
- Compatible NotificationChannel
- Possibilité de définir des icônes larges pour les notifications 
## Setup with Gradle
Add this to your build.gradle file for your app.
```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```	

Add this to your dependencies in build.gradle for your project.
```java
	dependencies {
	        implementation 'com.github.AntonyDalmiere:NotificationScheduler:master-SNAPSHOT'
	}
```

## Example

Create a NotificationScheduler Builder Object

```java

NotificationScheduler.Builder notifScheduler = new NotificationScheduler.Builder(getApplicationContext());

```

Then set the fields you want.

```java
  
  notifScheduler.title(String title);
  notifScheduler.content(String content);
  notifScheduler.color(Int red,Int green,Int blue,Int alpha);//Color of notification header
  notifScheduler.time(Calendar time);//The time to popup notification
  notifScheduler.large_icon(Int resource);//Icon resource by ID
  notifScheduler.addAction(Intent intent,String text); //The action will call the intent when pressed
  
```

After all the fields that you want are set just call build()!

```java

  notifScheduler.build();

```
## Documentation 
Voir sur le [wiki](URLWiki).
## Credits

Made by [Antony Dalmiere](https://github.com/AntonyDalmiere).
