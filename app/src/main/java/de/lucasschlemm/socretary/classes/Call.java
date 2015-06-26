package de.lucasschlemm.socretary.classes;

/**
 * Created by Daniel on 12.06.15.
 */
public class Call {

    private Contact contact;
    private String subject;

    public Call(Contact contact, String subject){
        this.contact = contact;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }



    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }


}
