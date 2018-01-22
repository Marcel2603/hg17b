package db;

public class Main {

	public static void main(String[] args) {
		PupilDB db1 = new PupilDB();
		db1.addPupil(8, 8);
		System.out.print(db1.getScore(3));
		System.out.println(db1.isID(9));
		System.out.println(db1.isID(4));
	
	}

}
