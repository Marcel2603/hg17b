package db;

import java.util.HashMap;

public class Main {

    public static void main(final String[] args) {
        System.out.print("JO");
        PupilDB db1 = new PupilDB();
        //db1.addPupil(17, 530);
        System.out.print(db1.getScore(4));
        System.out.println(db1.isID(4));
        System.out.println(db1.isID(1234));
        //db1.makeRank();
        HashMap<Integer, Integer> ranking = db1.getToplist();
        for (int i = 1; i < 11; i++) {
            System.out.println(ranking.get(i));
        }
        System.out.println(db1.getRank(12));
    }
}
