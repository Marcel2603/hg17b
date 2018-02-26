package de.zukunftsdiplom.server;
import java.util.HashMap;

public class App {

    public static void main(final String[] args) {

        PupilDB db1 = new PupilDB();
        /*
        System.out.print(db1.getScore(4));
        System.out.println(db1.isID(4));
        System.out.println(db1.isID(1234));
        db1.makeRank();
        HashMap<Integer, Integer> ranking = db1.getToplist();
        for (int i = 1; i < 11; i++) {
            System.out.println(ranking.get(i));
        }
        System.out.println(db1.getRank(12));
        //db1.readTTL();*/
        db1.isOrganizer("Daniel.Shade@ost-passage-theater.de");
        System.out.println(db1.isOrganizer("Daniel.Schade@ost-passage-theater.de"));
        db1.getEventsStudents(true);
    }

}
