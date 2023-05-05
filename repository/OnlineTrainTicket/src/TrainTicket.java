import java.io.*;
import java.util.*;

// 1.Classes, 2.Inheritance, 3.Try / catch, 4.Input & Output, or 5.Collections.
// Station, Kilometer (to next station)
/* <PassengerSeats> Data-Structure
 {
	[TIME] = {
		[BOGIE] = {
			{..., Seats}
		},
		...
	}
}
*/

public class TrainTicket extends Utilitys { // 2) Inheritance
	public ArrayList<String[]> TrainStations = new ArrayList<String[]>();
	public HashMap<Integer, String[][]> PassengerSeats = new HashMap<Integer, String[][]>(); // 5) Collections
	public ArrayList<Ticket> PassengerTickets = new ArrayList<Ticket>(); //Unconfirm - Ticket
	private Terminal Console = new Terminal();
	private iEmail EmailReporter = new iEmail("rutchanonbass@gmail.com", "rgmrxgpzwekmxhde");
	private int TrainSpeed = 90; // Unit is km/h
	private int TrainBogie = 8;
	private int TrainSeatsPerBogie = 20; // recommand not more 30
	private double PricePerKM; // this based on how many distance do you go and multiple by PricePerKM
	private int RoundsPerHour;
	private int RoundsPerDay;
	private int StartHour;
	private int MapDistance;
	public double GetPricePerKM() {
		return PricePerKM;
	}
	public int GetTrainSpeed() {
		return TrainSpeed;
	}
	public TrainTicket() {
		File MyCafeMenuFile = new File("MapStations.bak");
		try (BufferedReader BuffReader = new BufferedReader(new FileReader(MyCafeMenuFile))) { // 4.1) Input
			String Line = null;
			while ((Line = BuffReader.readLine()) != null) {
				String[] Station = Line.split(", ");
				Double.parseDouble(SafeGet(Station, 1)); // Unit is Kilometer **pcall check
				String Name = SafeGet(Station, 0);
				String Distance = SafeGet(Station, 1);
				TrainStations.add(new String[] {Name, Distance});
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			Console.Pause();
		}
		Random Randomizer = new Random();
		this.PricePerKM = Math.min(Randomizer.nextDouble() + 0.5, 1.1); // 0.5 - 1.1 
		for (int i = 0; i < TrainStations.size(); i++) {
			String[] StationInfo = TrainStations.get(i);
			double DistanceBetween = Double.parseDouble(StationInfo[1]);
			if (DistanceBetween > 0) {
				this.MapDistance += DistanceBetween;
			}
		}
		// (double) TrainSpeed / 3600: km/hr -> km/s
		this.RoundsPerHour = (int) Math.ceil( ((double) this.MapDistance) / ((double) TrainSpeed) ); // base on total distance from first station to last station
		this.RoundsPerDay = (24 / RoundsPerHour);
		this.StartHour = Randomizer.nextInt(24);
		for (int Round = 0; Round < this.RoundsPerDay; Round++) {
			int Start = (this.StartHour + (this.RoundsPerHour * Round)) % 24;
			int Finish = (this.StartHour + (this.RoundsPerHour * (Round + 1))) % 24;
			System.out.println(String.format("%d. %02d:00 - %02d:00", Round + 1, Start, Finish));
			PassengerSeats.put(Round + 1, new String[TrainBogie][TrainSeatsPerBogie]); // Time -> Train:PassengerSeats per round
		}
	}
	public void StationsPage() {
		Console.Clear();
		System.out.println(":::===== Online Train Ticket =====::: (Stations)");
		for (int i = 0; i < this.TrainStations.size(); i++) {
			String[] StationInfo = this.TrainStations.get(i);
			System.out.println(String.format(" [%d: %s]", i + 1, StationInfo[0]));
			double DistanceBetween = Double.parseDouble(StationInfo[1]);
			if (DistanceBetween > 0) {
				System.out.println(String.format("   | -> (%s km)", Comma(DistanceBetween)));
			}
		}
		System.out.println(String.format("Distance of joureny map: %s km", Comma(this.MapDistance)));
		System.out.println(":::===== ====== ===== ====== =====:::");
		int StartLocation = Console.PromptInteger("// Choose your start location", 1, TrainStations.size());
		int Destination;
		do {
			Destination = Console.PromptInteger("// Choose your destination <*not same as start location>", 1, TrainStations.size());
			if (Destination == StartLocation) { System.out.println("**Destination must not be same as start location."); }
		} while (Destination == StartLocation);
		System.out.println(":::===== ====== ===== ====== =====:::");
		for (int Round = 0; Round < this.RoundsPerDay; Round++) {
			int Start = (this.StartHour + (this.RoundsPerHour * Round)) % 24;
			int Finish = (this.StartHour + (this.RoundsPerHour * (Round + 1))) % 24;
			System.out.println(String.format("%d. %02d:00 - %02d:00", Round + 1, Start, Finish));
		}
		System.out.println(":::===== ====== ===== ====== =====:::");
		int Round = Console.PromptInteger("// Choose your train schedule", 1, RoundsPerDay);
		String[][] CurrentPassengerSeats = this.PassengerSeats.getOrDefault(Round, null);
		System.out.println(":::===== ====== ===== ====== =====:::");
		if (CurrentPassengerSeats != null) {
			for (int Bogie = 0; Bogie < CurrentPassengerSeats.length; Bogie++) {
				String[] Seats = CurrentPassengerSeats[Bogie];
				System.out.print((Bogie + 1) + ". /");
				for (int Seat = 0; Seat < Seats.length / 2; Seat++) {
					System.out.print(String.format(" [%s] ", (Seats[Seat] == null ? String.format("%02d", Seat + 1) : "--")));
				}
				System.out.println("\\");
				String SpaceLineUp = "";
				for (int i = 0; i < (int) Math.log10(Bogie + 1); i++) { SpaceLineUp += " "; }
				System.out.print(SpaceLineUp + "   \\"); // why Math.log10(x) because we will know digits of number
				for (int Seat = Seats.length / 2; Seat < Seats.length; Seat++) {
					System.out.print(String.format(" [%s] ", (Seats[Seat] == null ? String.format("%02d", Seat + 1) : "--")));
				}
				System.out.println("/");
			}
			System.out.println(":::===== ====== ===== ====== =====:::");
			try { // 3) Try / catch
				int BogieIdx = Console.PromptInteger("// Choose your bogie", 1, CurrentPassengerSeats.length) - 1;
				int SeatIdx = Console.PromptInteger("// Choose your seat", 1, CurrentPassengerSeats[BogieIdx].length) - 1;
				if (CurrentPassengerSeats[BogieIdx][SeatIdx] != null) {
					System.out.println("**Someone took your seat, please look for another. (Skipped Process)");
					Console.Pause();
				} else {
					String PassengerName = Console.PromptString("// Passenger name", "any");
					String PassengerEmail = Console.PromptString("// Passenger email", "any");
					Ticket UncomfirmTicket = new Ticket(PassengerName, PassengerEmail, Round, BogieIdx, SeatIdx);
					UncomfirmTicket.From = StartLocation - 1; // array index start at 0
					UncomfirmTicket.To = Destination - 1;
					UncomfirmTicket.RoundStart = (this.StartHour + (this.RoundsPerHour * (Round - 1))) % 24;
					UncomfirmTicket.RoundEnd = (this.StartHour + (this.RoundsPerHour * Round)) % 24;
					CurrentPassengerSeats[BogieIdx][SeatIdx] = UncomfirmTicket.UUID();
					this.PassengerSeats.put(Round, CurrentPassengerSeats); // update to HashMap
					this.PassengerTickets.add(UncomfirmTicket);
				}
			} catch (IndexOutOfBoundsException e) {} // not happen for sure, since using PromptInteger which is limit itself already.
		}
	}
	public void ConfimPage() {
		Console.Clear();
		if (this.PassengerTickets.size() > 0) {
			System.out.println(":::===== Online Train Ticket =====::: (Confirm)");
			for (int i = 0; i < this.PassengerTickets.size(); i++) {
				Ticket v = this.PassengerTickets.get(i);
				double TotalDistance = v.JourneyDistance();
				System.out.println(String.format("%d. Passenger: %s, Ticket: [%s] (%s km, %s baht)", i + 1, v.GetPassengerName(), v.UUID(), Comma(TotalDistance), Comma((int)(TotalDistance * this.PricePerKM))));
			}
			System.out.println(":::===== ====== ===== ====== =====:::");
			System.out.println("1. See detail");
			System.out.println("2. Remove out of cart");
			System.out.println("3. Pay & confirm");
			System.out.println("4. BACK");
			switch (Console.PromptInteger("// Select your action", 1, 4)) {
				case 1:
					int TicketIdx1 = Console.PromptInteger("Choose your ticket by number order", 1, this.PassengerTickets.size()) - 1;
					Console.Clear();
					Ticket v1 = this.PassengerTickets.get(TicketIdx1);
					System.out.println("Your railway station map:");
					System.out.println(v1.JourneyMap()); // print JourneyMap (each of station to station)
					double TotalDistance = v1.JourneyDistance();
					System.out.println(String.format("Total distance: %s", Comma(TotalDistance)));
					System.out.println(String.format("Total price: %s (1 baht / %.2f km)", Comma((int) (TotalDistance * this.PricePerKM)), this.PricePerKM));
					double ApproxDuration = (((double) TotalDistance) / ((double) this.TrainSpeed));
					System.out.println(String.format("Estimate time destination: %s, (%02d:00 - %02d:00)", SecondToClock( (int) (ApproxDuration * 60 * 60) ), v1.RoundStart, v1.RoundStart + (int) Math.ceil(ApproxDuration)));
					Console.Pause();
					break;
				case 2:
					int TicketIdx2 = Console.PromptInteger("Choose your ticket by number order", 1, this.PassengerTickets.size()) - 1;
					Ticket v2 = this.PassengerTickets.remove(TicketIdx2);
					String[][] CurrentPassengerSeats = this.PassengerSeats.getOrDefault(v2.GetRound(), null);
					if (CurrentPassengerSeats != null) {
						if (CurrentPassengerSeats[v2.GetBogie()][v2.GetSeat()] != null) {
							CurrentPassengerSeats[v2.GetBogie()][v2.GetSeat()] = null;
							this.PassengerSeats.put(v2.GetRound(), CurrentPassengerSeats);
						}
					}
					Console.Pause();
					break;
				case 3:
					for (int i = 0; i < this.PassengerTickets.size(); i++) {
						Ticket v3 = this.PassengerTickets.get(i);
						String[][] _CurrentPassengerSeats_ = this.PassengerSeats.getOrDefault(v3.GetRound(), null);
						if (_CurrentPassengerSeats_ != null) {
							if (_CurrentPassengerSeats_[v3.GetBogie()][v3.GetSeat()] != null) {
								_CurrentPassengerSeats_[v3.GetBogie()][v3.GetSeat()] = null;
								this.PassengerSeats.put(v3.GetRound(), _CurrentPassengerSeats_);	
								String PassengerEmail = v3.GetPassengerEmail();
								String FileContent = v3.toString();
								if (EmailReporter.Send(PassengerEmail, String.format("Train Trick #%s", v3.UUID()), FileContent)) {
									System.out.println(String.format("The system sent ticket to passenger's email successfully. (id: %s)", v3.UUID()));
								} else {
									System.out.println(String.format("There is a problem to passenger's email (id: %s), so then we keep as a receipt file instead when the passenger come and get it in station", v3.UUID()));
									File ReceiptFile = new File(String.format("%s.txt", v3.UUID()));
									try (BufferedWriter BuffWriter = new BufferedWriter(new FileWriter(ReceiptFile))) { // 4.2) Input
										BuffWriter.write(FileContent);
									} catch (IOException e) {
										e.printStackTrace();
										Console.Pause();
									}
								}
							}
						}
					}
					this.PassengerTickets = new ArrayList<Ticket>(); // set to empty. // no remove seat since this is final confirm.
					Console.Pause();
					break;
				case 4:
				default:
					break;
			}
		} else {
			System.out.println(">> System was not found any ticket in a cart.");
			Console.Pause();
		}	
	}
	public void Start() {
		do {
			Console.Clear();
			System.out.println(":::===== Online Train Ticket =====:::");
			if (this.PassengerTickets.size() > 0) {
				for (int i = 0; i < this.PassengerTickets.size(); i++) {
					Ticket v = this.PassengerTickets.get(i);
					double TotalDistance = v.JourneyDistance();
					System.out.println(String.format("%d. Passenger: %s, Ticket: [%s] (%s km, %s baht)", i + 1, v.GetPassengerName(), v.UUID(), Comma(TotalDistance), Comma((int)(TotalDistance * this.PricePerKM))));
				}
				System.out.println(":::===== ====== ===== ====== =====:::");
			}
			System.out.println("1. Stations");
			System.out.println("2. Confirm");
			System.out.println("3. EXIT");
			switch (Console.PromptInteger("// Select an action", 1, 3)) {
				case 1:
					StationsPage();
					break;
				case 2:
					ConfimPage();
					break;
				case 3: // lead to default case: (which is exit btw.)
				default:
					Console.Close();
					break;
			}
		} while (!Console.Suspended);
	}
}
