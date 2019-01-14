import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RunQuery {

	public static String getQuery(int num) {
		//This method contains the code for five different SQL queries.
		//It returns a specific line of SQL code, based on the number passed to the method.
		System.out.println("Executing Query " + num);
		System.out.println("###");
		switch (num) {
		case 1: 
			System.out.println("The following routes have been affected by disruptions:");
			return "select RouteName from Route join (select RouteID from StopOnRoute join (select StopID from Stop join (select RoadID from Road join Disruption using (RoadID)) using (RoadID)) using (StopID)) using (RouteID) group by RouteID";
		case 2: 
			System.out.println("The following routes have NEVER been affected by disruptions:");
			return "select RouteName from Route R1 left join (select RouteID from Route join (select RouteID from StopOnRoute join (select StopID from Stop join (select RoadID	from Road join Disruption using (RoadID)) using (RoadID)) using (StopID)) using (RouteID) group by RouteID) R2 using (RouteID) where R2.RouteID isnull";
		case 3:
			System.out.println("The top 10 routes MOST affected by disruptions are:");
			return "select RouteName from Route join (select RouteID, count (*) as \"Frequency\" from StopOnRoute join (select StopID from Stop join (select RoadID from Road join Disruption using (RoadID)) using (RoadID)) using (StopID) group by RouteID order by Frequency desc) using (RouteID) limit 10";
		case 4:
			System.out.println("The top 10 routes LEAST affected by disruptions are:");
			return "select RouteName from Route join (select RouteID, count (*) as \"Frequency\" from StopOnRoute join (select StopID from Stop join (select RoadID from Road join Disruption using (RoadID)) using (RoadID)) using (StopID) group by RouteID order by Frequency) using (RouteID) limit 10";
		case 5:
			System.out.println("The routes that have been affected in the last 5 years are:");
			return "select RouteName from Route join (select RouteID, EndDate from StopOnRoute join (select StopID, EndDate from Stop join (select RoadID, EndDate from Disruption join Road using (RoadID)) using (RoadID)) using (StopID)) using (RouteID) where EndDate > date('now', '-5 years') group by RouteID";
		}
		return "";
	}
	
	public static void main(String[] args) {	
		int queryNum;
		
		//This program contains 5 SQL queries.
		//If the program has not been passed a number between 1-5, it will advise the user.
		try {
			queryNum = Integer.parseInt(args[0]);
			if (queryNum < 1 || queryNum > 5) throw new NumberFormatException();
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.out.println("Please pass a number between 1-5.");
			System.out.println("For example, type: java -jar \"RunQuery.jar\" 1");
			return;
		}

		String url = "jdbc:sqlite:PublicTransport.db"; //The program requires the database file to be in the same directory.
		try {
			Connection connection = DriverManager.getConnection(url); //Connect to the database via JDBC	
			System.out.println("Connection to SQLite successful");
			System.out.println("###");
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(getQuery(queryNum)); //Execute the query chosen by the user.		
			while (result.next()) { //Output all results.
				String routeName = result.getString("RouteName"); //Each query only returns route names.
				System.out.printf("%s%n", routeName);
			}
		} catch (SQLException e) {e.printStackTrace();}

	}

}
