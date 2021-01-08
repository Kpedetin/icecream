package com.blackbox.dao.enumeration;

public enum Timeslot {
	H1("8H-9H"),
	H2("9H-10H"),
	H3("10H-11H"),
	H4("11H-12H"),
	H5("12H-13H"),
	H6("13H-14H"),
	H7("14H-15H"),
	H8("15H-16H"),
	H9("16H-17H"),
	H10("17H-18H"),
	H11("18H-19H"),
	H12("19H-20H");

	private final String timeslot;

	Timeslot(String timeslot) {
		this.timeslot = timeslot;
	}

	public static Timeslot getPreviousTimeslot(Timeslot timeslot) throws Exception {
		Timeslot nextTimeslot = null;
		switch (timeslot) {
			case H2:
				nextTimeslot = H1;
				break;
			case H3:
				nextTimeslot = H2;
				break;
			case H4:
				nextTimeslot = H3;
				break;
			case H5:
				nextTimeslot = H4;
				break;
			case H6:
				nextTimeslot = H5;
				break;
			case H7:
				nextTimeslot = H6;
				break;
			case H8:
				nextTimeslot = H7;
				break;
			case H9:
				nextTimeslot = H8;
				break;
			case H10:
				nextTimeslot = H9;
				break;
			case H11:
				nextTimeslot = H10;
				break;
			case H12:
				nextTimeslot = H11;
				break;
			default:
				throw new Exception("Unknown timeslot");
		}
		return nextTimeslot;
	}
}
