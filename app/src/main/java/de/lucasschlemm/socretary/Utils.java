package de.lucasschlemm.socretary;

import java.util.Date;

public class Utils {

    /**
     * Methode für das Einfügen eines Zeitstempels in der Datenbank
     * @return aktuelle Zeit in ms
     */
    public static long getCurrentTime(){

        //TODO @Jakob: Ich weiß nicht ob das besser ist:
        //System.currentTimeMillis();
        return (new Date()).getTime();
    }




}
