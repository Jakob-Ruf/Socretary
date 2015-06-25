# Socretary #

## General Information ##
The app SOCRETARY aims to connect you to your friends
Version 0.01

### Project Team ###

* Lucas Schlemm
* Jascha Krueper
* Daniel Mueller
* Jakob Ruf

# Temporär #

aktuell kann über folgendes Statement ein Kontakt mit Beispieldaten erzeugt werden:

```
#!Java

Contact contact = new Contact();
```
Dieser besitzt ID , Namen, Nummer, Geburtstag, das letzte Kontaktdatum, die Kontakthäufigkeit und die Adresse (alles als String)


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

Sollen die Benachrichtigungen entfernt werden, so muss als Type lediglich **"cancel_Notification"** mitgegeben werden. Dieser Broadcast schließt alle Benachrichtigungen von Socretary

Notwendiger Codeschnippsel zum Beenden der Benachrichtigungen
```
#!Java
        Intent intent = new Intent();
        intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
        intent.putExtra("type", "cancel_Notification");
        sendBroadcast(intent);
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




### Contribution guidelines ###

* Writing tests: WRITING TESTS
* Code review
* Other guidelines


## Tutorial - Einstieg in socretary ##

Socretary bietet dir eine optimale Grundlage, um mit wichtigen Kontakten und Freunden in Verbindung zu bleiben. Dieses Tutorial soll dir einen Einstieg in socretary geben und dir den Umgang mit der App und den dazugehörigen Funktionen näher bringen.

Nach der Installation und dem ersten Starten der App, wird dich socretary nach deiner Handynummer fragen. Keine Angst, diese wird dazu benötigt, um beispielsweise deinen Freund, der dich in seiner socretary-Kontaktliste hat, darauf aufmerksam zu machen, dass du in seinem Umkreis bist.ö
![1-First.png](https://bitbucket.org/repo/aAzoKb/images/1271786039-1-First.png)
Nach der Eingabe der Handynummer bist du im Menü von socretary. Zu Beginn ist es am vorteilhaftesten, wenn du in die Übersicht navigierst.
![2-Second.png](https://bitbucket.org/repo/aAzoKb/images/2783151772-2-Second.png)
In der Übersicht angelangt, kannst über die blaue Schaltfläche einen Kontakt deiner Smartphone-Kontaktliste hinzufügen. Ist dies geschehen, kannst du nun die folgenden Attribute deines aufgenommenen Kontaktes bestimmen:
* Kontakthäufigkeit
* Geburtsdatum
* Adresse
Dir fällt spontan nicht die genaue Adresse deines Kontaktes ein? - Nicht schlimm, du kannst in socretary die Adressen deiner Kontakte ganz einfach nachpflegen.
![3-.png](https://bitbucket.org/repo/aAzoKb/images/214797380-3-.png)
![4-.png](https://bitbucket.org/repo/aAzoKb/images/4103426109-4-.png)
![5-Fifth-Address.png](https://bitbucket.org/repo/aAzoKb/images/358176076-5-Fifth-Address.png)
Ist der Kontakt erstellt, wird dieser in deiner socretary-Kontaktliste aufgeführt.
![6-ÜbersichtKontakt.png](https://bitbucket.org/repo/aAzoKb/images/1473694768-6-%C3%9CbersichtKontakt.png)

![7-KontaktkurzPress.png](https://bitbucket.org/repo/aAzoKb/images/441216587-7-KontaktkurzPress.png)

![8-Settings.png](https://bitbucket.org/repo/aAzoKb/images/4084048922-8-Settings.png)

![9-SmsVorlagen.png](https://bitbucket.org/repo/aAzoKb/images/2577379485-9-SmsVorlagen.png)

![10-AddSms.png](https://bitbucket.org/repo/aAzoKb/images/271390194-10-AddSms.png)

![11-SMSentfernen.png](https://bitbucket.org/repo/aAzoKb/images/2490902126-11-SMSentfernen.png)

![12-KontaktansichtDetails.png](https://bitbucket.org/repo/aAzoKb/images/430645938-12-KontaktansichtDetails.png)
![13-Kontaktansicht-Verlauf.png](https://bitbucket.org/repo/aAzoKb/images/3537996818-13-Kontaktansicht-Verlauf.png)
![14-Kontakte-Eintraghinzu.png](https://bitbucket.org/repo/aAzoKb/images/2027024406-14-Kontakte-Eintraghinzu.png)
![15-Kontaktmenü.png](https://bitbucket.org/repo/aAzoKb/images/959416431-15-Kontaktmen%C3%BC.png)