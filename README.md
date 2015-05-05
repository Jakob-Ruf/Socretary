# Socretary #

## General Information ##
The app SOCRETARY aims to connect you to your friends
Version 0.01

### Project Team ###

* Lucas Schlemm
* Jascha Krueper
* Daniel Mueller
* Jakob Ruf

# Benachrichtigungen #

Die Steuerung der Benachrichtigungen geschieht über die Klasse NotificationHelper.java.
Diese fungiert als BroadcastReciever und erlaubt so die zentrale Verwaltung der Benachrichtigungen.

## Auslösen von Notifications ##

Eine Benachrichtigung kann zur Anzeige gebracht werden, indem ein Broadcast losgeschickt wird.
Dieser muss den passenden Intent haben (**"de.lucasschlemm.CUSTOM_INTENT"**) und als Extra mindestens eine Aktion mitgeben.
Mögliche Typen sind:

* **"text"** zum Anzeigen von Benachrichtigungen nach dem Versand einer automatischen SMS. Hier wird zusätzlich das Extra **"recipient"** benötigt. Dann kann der Empfänger der SMS in der Benachrichtigung angezeigt werden.
* **"reminder"** zum Anzeigen von Benachrichtigungen, dass man sich mal wieder bei einem Freund melden könnte. Hier werden als Extra ebenfalls der Name des Freundes (**"contactName"**) und die Zeit seit dem letzten Kontakt (**"timePassed"**) benötigt.
* **"location"** zum Anzeigen von Benachrichtigungen, dass man sich in der Nähe eines Freundes befindet. Hier wird auch **"contactName"** als Name des Freundes benötigt.

Bespiel wie Benachrichtigungen aus einem Fragment ausgelöst werden
```
#!Java

        Intent intent = new Intent();
        intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
        intent.putExtra("type", "text");
        intent.putExtra("recipient", conName);
        getActivity().getBaseContext().sendBroadcast(intent);
```
## Entfernen von Benachrichtigungen ##

Sollen die Benachrichtigungen entfernt werden, so muss als Type ledigtlich **"cancel_Notification"** mitgegeben werden. Dieser Broadcast schließt alle Benachrichtigungen von Socretary

Notwendiger Codeschnippsel zum Beenden der Benachrichtigungen
```
#!Java
        Intent intent = new Intent();
        intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
        intent.putExtra("type", "cancel_Notification");
        sendBroadcast(intent);
```

### Contribution guidelines ###

* Writing tests: WRITING TESTS
* Code review
* Other guidelines