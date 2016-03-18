# Socretary #

Readme für das Projekt Socretary der Vorlesung "Mobile Applications - Native Development"

## General Information ##
The app SOCRETARY aims to connect you to your friends
Version 0.01

### Project Team ###

* Lucas Schlemm
* Jascha Krueper
* Daniel Mueller
* Jakob Ruf

## Präsentation ##

[Link zur Präsentation](http://htmlpreview.github.io/?https://github.com/Jakob-Ruf/Socretary/blob/master/slides.html#/).


# Intents #

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

Sollen die Benachrichtigungen entfernt werden, so muss als Type lediglich **"cancel_Notification"** mitgegeben werden. Dieser Broadcast schließt alle Benachrichtigungen von Socretary

Notwendiger Codeschnippsel zum Beenden der Benachrichtigungen
```
#!Java
        Intent intent = new Intent();
        intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
        intent.putExtra("type", "cancel_Notification");
        sendBroadcast(intent);
```

## Location teilen ##

Das Teilen der eigenen Location wird über den Intent **"de.lucasschlemm.de.SHARELOCATION"** ausgelöst. Extras müssen keine hinzugefügt werden.
```
#!Java
        Intent intent = new Intent();
        intent.setAction("de.lucasschlemm.socretary.SHARELOCATION");
        ApplicationContext.getContext().sendBroadcast(intent);
```

# Application Context #

Damit nicht jeder Methode ein Context übergeben werden muss, wurde eine statische Klasse ApplicationContext erstellt. Diese wird in der onCreate der MainActivity initialisiert und liefert anschließend über die statischen Methoden getContext() und getActivity() die entsprechenden Context-Objekte zurück. Dadurch wurden viele Methoden um je einen Parameter reduziert
```
#!Java
        Context myContext = ApplicationContext.getContext();
```


# Datenbankzugriffe #

## Generell ##

Es wurde das Singleton-Muster verwendet, um eine mehrfache Instanziierung und somit mehrfachen Zugriff auf die Datenbank zu verhinden. Der Zugriff auf den DB-Helper erfolgt wie folgt:
Das "this" gibt hier den ApplicationContext an. Sollte diese Methode also in einem anderen Kontext (z.B. OnClickListener) aufgerufen werden, so muss hier "getApplicationContext()" verwendet werden.

```
#!Java

DatabaseHelper helper = DatabaseHelper.getInstance(this);
```

## Zugriffe auf die Datenbank ##

Alle Methoden verfügen auch über JavaDocs

* \+ long id: **insertContact**(Contact)
* \+ long id: **insertEncounter**(Encounter)
* \+ boolean success: **updateContact**(Contact) *nicht getestet*
* \+ boolean success: **updateEncounter**(Encounter) *noch nicht implementiert*
* \+ ArrayList<Contact> contacts: **getContactList**()
* \+ Contact contact: **getContact**(long id)
* \+ ArrayList<Encounter>: **getContactEncounterList**(long id)
* \+ boolean success: **deleteContact**(long id)
* \+ boolean success: **deleteEncounter**(long id)
* \+ void: **emptyTables**()




# Tutorial - Einstieg in socretary #

### Erste Schritte ###

Socretary bietet dir eine optimale Grundlage, um mit wichtigen Kontakten und Freunden in Verbindung zu bleiben. Dieses Tutorial soll dir einen Einstieg in socretary geben und dir den Umgang mit der App und den dazugehörigen Funktionen näher bringen.

Nach der Installation und dem ersten Starten der App, wird dich socretary nach deiner Handynummer fragen. Keine Angst, diese wird dazu benötigt, um beispielsweise deinen Freund, der dich in seiner socretary-Kontaktliste hat, 

![1-First.png](http://i.imgur.com/s44DeMl.png)

Nach der Eingabe der Handynummer bist du im Menü von socretary. Zu Beginn ist es am vorteilhaftesten, wenn du in die Übersicht navigierst.

![2-Second.png](http://i.imgur.com/dJtFvpp.png)

In der Übersicht angelangt, kannst über die blaue Schaltfläche einen Kontakt deiner Smartphone-Kontaktliste hinzufügen. Ist dies geschehen, kannst du nun die folgenden Attribute deines aufgenommenen Kontaktes bestimmen:

* Kontakthäufigkeit

* Geburtsdatum

* Adresse

Dir fällt spontan nicht die genaue Adresse deines Kontaktes ein? - Nicht schlimm, du kannst in socretary die Adressen deiner Kontakte ganz einfach später nachpflegen.

![3-.png](http://i.imgur.com/4e9bLXd.png)
![4-.png](http://i.imgur.com/NuIjcH3.png)
![5-Fifth-Address.png](http://i.imgur.com/9k8Y4QV.pn)g

Ist der Kontakt erstellt, wird dieser in deiner socretary-Kontaktliste aufgeführt.

![6-ÜbersichtKontakt.png](http://i.imgur.com/xD9sLRD.png)

Der erste Kontakt ist in deiner socretary-Kontaktliste. Du kannst nun beliebig viele folgen lassen. Der erste Teil des Einstiegs ist damit abgeschlossen, im weiteren Verlauf erhältst du einen Überblick über die Funktionen und die Möglichkeiten von socretary.




### Einstellungen: ###

Über das Menü gelangst du in die Einstellungen. Hier kannst du an verschiedenen Stellschrauben drehen. So ist es dir möglich, die Benachrichtigungen zu (de-)aktivieren, sowie den Umkreis um deine Freunde einzustellen. Dieser Umkreis hat die Bedeutung, dass wenn sich einer deiner Kontakte in diesem befindet, du eine Pushbenachrichtigung erhältst.

![8-Settings.png](http://i.imgur.com/LfgHrLL.png)

Außerdem hast du die Wahl, welche Kommunikation von socretary eingelesen werden soll. So werden durch die Betätigung von "Anrufe einlesen" und "Nachrichten einlesen" die Verläufe der jeweiligen Dinge ausgelesen und Einträge, die von deinen socretary-Kontakten stammen, verwendet und den socretary-Kontakten zugeordnet.




### SMS-Vorlagen: ###

Über das Menü lässt sich über 'SMS-Vorlagen' ein Manager aufrufen, in dem du SMS-Vorlagen verwalten kannst, die du später einem socretary-Kontakt zuordnen kannst.

![9-SmsVorlagen.png](http://i.imgur.com/Z2jeMbK.png)

Über die (+)-Schaltfläche kannst du eine neue Nachricht hinzufügen.

![10-AddSms.png](http://i.imgur.com/pQAn5Cl.png)

Es erscheint ein Fenster, in dem du einen Nachrichtentext formulieren kannst. Dieser Text ist an keine Zeichenlänge gebunden. Wenn du den Text eingegeben hast, kannst du ihn durch die Fensterbestätigung zu deiner SMS-Vorlagen-Liste hinzufügen. Auch hier kannst du beliebig viele SMS-Vorlagen folgen lassen.

Möchtest du SMS-Vorlagen löschen? - Dies ist durch die Betätigung des Mülltonnensymbols möglich. 

![11-SMSentfernen.png](http://i.imgur.com/e9g9CGm.png)

Beim Löschen der Vorlagen ist es möglich, eine Mehrfachauswahl zu treffen, das heißt du musst nicht jede Nachricht einzeln löschen.


### Kontaktansicht ###

Socretary bietet zwei Arten der Kontaktansicht, zum einen eine Art Schnellübersicht und eine Detailansicht.






**Schnellübersicht:**


Zur Schnellübersicht gelangst du durch das kurze antippen eines Kontaktes in deiner socretary-Kontaktliste. Darauf hin wird dir ein Fenster angezeigt, welches dir eine kurze Übersicht über den Kontakt anzeigt.

![7-KontaktkurzPress.png](http://i.imgur.com/5eOmP1d.png)

Im Fokus dieser Ansicht steht aber die Möglichkeit direkt  mit dem Kontakt zu kommunizieren. Mit der Betätigung von "Anruf", rufst du den Kontakt direkt an. Ähnliches geschieht bei der Auswahl von "Nachricht". Du gelangst in deine Standard-SMS-App mit dem Kontakt als Empfänger.



**Detailansicht:**

Durch das lange Drücken eines Kontaktes gelangst du in die jeweilige Detailansicht. In der Detailansicht werden verschiedene Informationen zu dem Kontakt dargestellt. Unter dem Reiter 'Details' werden die Informationen über das Geburtsdatum, die Adresse, die SMS-Vorlagen und die Telefonnummer aufgeführt.

![12-KontaktansichtDetails.png](http://i.imgur.com/QQyjUSa.png)

Die Detailseite dient aber nicht nur der reinen Darstellung von Informationen. Hier hast du die Möglichkeit deinem Kontakt SMS-Vorlagen aus deiner SMS-Vorlagen-Liste zuzuordnen und zu verwalten. Außerdem bietet dir socretary in der Detailansicht die Möglichkeit, die Adresse des Kontaktes nachträglich einzutragen. Die App zeigt automatisch die Eingabemaske an, wenn man den Detail-Reiter aufruft und die Adresse noch nicht vorhanden ist.

Der Reiter 'Verlauf' zeigt dir die Kommunikation mit dem ausgewählten Kontakt an. Hier siehst du (je nach dem ob Anrufe und/oder SMS eingelesen worden sind), in wie weit du mit dem Kontakt in der Vergangenheit kommuniziert hast. Anrufe und SMS werden angezeigt und unterschieden ob diese eingehend oder ausgehend waren.

![13-Kontaktansicht-Verlauf.png](http://i.imgur.com/KVC217h.png)

Du hast aber hier die Möglichkeit auch einen Eintrag manuell hinzuzufügen. Dies ist möglich indem du das Kontaktmenü oben rechts öffnest und 'Eintrag hinzufügen' auswählst.

![15-Kontaktmenü.png](http://i.imgur.com/59KqJPT.png)

Über die Auswahl gelangst du zu folgendem Fenster. Hier hast du die Möglichkeit, die Art, das Medium, die Uhrzeit und das Datum der Kommunikation zu bestimmen.

![14-Kontakte-Eintraghinzu.png](http://i.imgur.com/O7JT3mG.png)

Mit der Bestätigung wird der Eintrag nun im Verlauf gelistet.
Über das Kontaktmenü hast du außerdem die Möglichkeit den ausgewählten Kontakt aus deiner socretary-Kontaktliste zu löschen.
