package de.lucasschlemm.socretary;

import java.io.Serializable;

/**
 * Created by Daniel on 12.06.15.
 */
public class CallsTuple implements Serializable {

    private String contact;
    private String subject;

    public CallsTuple(String contact, String subject){
        this.contact = contact;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }



    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
