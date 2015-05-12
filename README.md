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

* + long id: **insertContact**(Contact)
* + long id: **insertEncounter**(Encounter)
* + boolean success: **updateContact**(Contact) *nicht getestet*
* + boolean success: **updateEncounter**(Encounter) *noch nicht implementiert*
* + ArrayList<Contact> contacts: **getContactList**()
* + Contact contact: **getContact**(long id)
* + ArrayList<Encounter>: **getContactEncounterList**(long id)
* + boolean success: **deleteContact**(long id)
* + boolean success: **deleteEncounter**(long id)
* + void: **emptyTables**()




### Contribution guidelines ###

* Writing tests: WRITING TESTS
* Code review
* Other guidelines