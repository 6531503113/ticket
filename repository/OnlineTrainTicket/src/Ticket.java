import java.util.*;

public class Ticket extends TrainTicket { // 1) Classes
	private UUID TicketId;
	public int From;
	public int To;
	public int RoundStart; // Hour
	public int RoundEnd; 
	private int Round;
	private int Bogie;
	private int Seat;
	private String PassengerName;
	private String PassengerEmail;
	public Ticket(String Name, String Email, int iRound, int iBogie, int iSeat) {
		this.PassengerName = Name;
		this.PassengerEmail = Email;
		this.TicketId = UUID.randomUUID();
		this.Round = iRound;
		this.Bogie = iBogie;
		this.Seat = iSeat;
	}
	public String UUID() {
		return this.TicketId.toString();
	}
	public String GetPassengerName() {
		return this.PassengerName;
	}
	public String GetPassengerEmail() {
		return this.PassengerEmail;
	}
	public double JourneyDistance() {
		double TotalDistance = 0;
		for (int Index = From; ((From < To) ? Index <= To : Index >= To); Index = (From < To) ? (Index + 1) : (Index - 1)) {
			String[] StationInfo = TrainStations.get(Index); // extends TrainTicket
			double DistanceBetween = (From < To) ? DistanceBetween = Double.parseDouble(StationInfo[1]) : Double.parseDouble(TrainStations.get(Math.max(0, Index - 1))[1]);
			if ((Index != To) && (DistanceBetween > 0)) {
				TotalDistance += DistanceBetween;
			}
		}
		return TotalDistance;
	}
	public String JourneyMap() {
		String map = "";
		for (int Index = From; ((From < To) ? Index <= To : Index >= To); Index = (From < To) ? (Index + 1) : (Index - 1)) {
			String[] StationInfo = TrainStations.get(Index); // extends TrainTicket
			map += String.format(" [%s]", StationInfo[0]) + "\n";
			//System.out.println(String.format(" [%s]", StationInfo[0]));
			double DistanceBetween = (From < To) ? DistanceBetween = Double.parseDouble(StationInfo[1]) : Double.parseDouble(TrainStations.get(Math.max(0, Index - 1))[1]);
			// why math.max(0, ...), because prevent indexoutofbound from incase travel in back way
			if ((Index != To) && (DistanceBetween > 0)) { // not printing last distance line
				map += String.format("   | -> (%s km)", Comma(DistanceBetween)) + "\n";
				//System.out.println(String.format("   | -> (%s km)", Comma(DistanceBetween)));
			}
		}
		return map;
	}
	public int GetSeat() {
		return Seat;
	}
	public int GetBogie() {
		return Bogie;
	}
	public int GetRound() {
		return Round;
	}
	@Override
	public String toString() {
		String[] Start = TrainStations.get(this.From);
		String[] Destination = TrainStations.get(this.To);
		double TotalDistance = JourneyDistance();
		double ApproxDuration = (((double) TotalDistance) / ((double) this.GetTrainSpeed()));
		return
			"Passenger: " + this.PassengerName + "\n" +
			"Round: #" + this.Round + String.format(" (%02d:00 - %02d:00)", RoundStart, RoundEnd) + "\n" +
			"Bogie: " + (this.Bogie + 1) + "\n" +
			"Seat: " + (this.Seat + 1) + "\n" +
			"Total price: " + String.format("%s (1 baht / %.2f km)", Comma((int) (TotalDistance * this.GetPricePerKM())), this.GetPricePerKM()) + "\n" +
			"===== Stations ===== " + "\n" +
			"From: " + Start[0] + "\n" +
			"To: " + Destination[0] + "\n" +
			"Total Distance: " + Comma(TotalDistance) + " km" + "\n" +
			"Estimate time destination: " + String.format("%s, (%02d:00 - %02d:00)", SecondToClock( (int) (ApproxDuration * 60 * 60) ), RoundStart, RoundStart + (int) Math.ceil(ApproxDuration)) + "\n" +
			"\n" +
			JourneyMap();
	}
}
