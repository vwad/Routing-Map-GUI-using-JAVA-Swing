package SQLDatabase.Query;

import Closure.Distributed.DataDistribution;
import Closure.Distributed.GaussianDistribution;
import Routing.Graph.*;
import Utils.Location.Location;
import Utils.Time.DateCalculator;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.*;

public class QueryExecutor {

    public List<Node> getNodes(Connection c, Location city) {
        try (Statement statement = c.createStatement()) {
            double[] leftCorner = city.getLeftGroundCornerCoordinates();
            double[] rightCorner = city.getRightUpperCornerCoordinates();
            List<Node> nodes = new ArrayList<>();
            String query = "SELECT " +
                    "stop_id as stop, " +
                    "stop_name as name, " +
                    "stop_lat as 'lat'," +
                    "stop_lon as 'lon'" +
                    "FROM Stops " +
                    "WHERE " +
                    "stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String id = resultSet.getString("stop");
                String name = resultSet.getString("name");
                double lat = resultSet.getDouble("lat");
                double lon = resultSet.getDouble("lon");
                Node node = new StopNode(id, name, lat, lon);
                nodes.add(node);
            }
            resultSet.close();
            return nodes;
        } catch (SQLException e) {

        }
        return null;
    }

    public static DataDistribution buildDataDistribution(Connection c, Location city, String startDate, int startTime, int endTime) throws SQLiteException {
        try (Statement statement = c.createStatement()) {
            double[] leftCorner = city.getLeftGroundCornerCoordinates();
            double[] rightCorner = city.getRightUpperCornerCoordinates();
            startDate = startDate.replaceAll("-", "");
            String day = DateCalculator.getDayFromDateString(startDate).toLowerCase();
            String query = "SELECT " +
                    "s1.departure_time_seconds as departure_time " +
                    "FROM Stop_times s1 " +
                    "join Stop_times s2 on s1.trip_id = s2.trip_id and s1.stop_sequence = s2.stop_sequence-1 " +
                    "join Stops s on s2.stop_id  = s.stop_id " +
                    "join Stops st on s1.stop_id = st.stop_id " +
                    "join Trips t on s2.trip_id = t.trip_id " +
                    "join Routes r on t.route_id = r.route_id " +
                    "join Agency a on r.agency_id = a.agency_id " +
                    "left join Calendar c on t.service_id = c.service_id " +
                    "left join Calendar_Dates cd on t.service_id = cd.service_id and cd.service_date = '" + startDate + "' " +
                    "where (c." + day + " is null or c." + day + " = 1) " +
                    "and (cd.exception_type is NULL or cd.exception_type = 1) " +
                    "and s1.departure_time_seconds between '" + startTime + "' and '" + endTime + "' " +
                    "and s.stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and s.stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "' " +
                    "and st.stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and st.stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "' " +
                    "ORDER BY s1.departure_time_seconds ";
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.setFetchSize(4500);
            List<Integer> data = new ArrayList<>();
            while (resultSet.next()) {
                int departure_time = resultSet.getInt(1);
                data.add(departure_time);
            }
            DataDistribution dataDistribution = new DataDistribution(data);
            resultSet.close();
            return dataDistribution;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GaussianDistribution buildGaussianDistribution(Connection c, Location city, String startDate, int startTime, int endTime) throws SQLiteException {
        try (Statement statement = c.createStatement()) {
            double[] leftCorner = city.getLeftGroundCornerCoordinates();
            double[] rightCorner = city.getRightUpperCornerCoordinates();
            startDate = startDate.replaceAll("-", "");
            String day = DateCalculator.getDayFromDateString(startDate).toLowerCase();
            String query = "SELECT " +
                    "s1.departure_time_seconds as departure_time " +
                    "FROM Stop_times s1 " +
                    "join Stop_times s2 on s1.trip_id = s2.trip_id and s1.stop_sequence = s2.stop_sequence-1 " +
                    "join Stops s on s2.stop_id  = s.stop_id " +
                    "join Stops st on s1.stop_id = st.stop_id " +
                    "join Trips t on s2.trip_id = t.trip_id " +
                    "join Routes r on t.route_id = r.route_id " +
                    "join Agency a on r.agency_id = a.agency_id " +
                    "left join Calendar c on t.service_id = c.service_id " +
                    "left join Calendar_Dates cd on t.service_id = cd.service_id and cd.service_date = '" + startDate + "' " +
                    "where (c." + day + " is null or c." + day + " = 1) " +
                    "and (cd.exception_type is NULL or cd.exception_type = 1) " +
                    "and s1.departure_time_seconds between '" + startTime + "' and '" + endTime + "' " +
                    "and s.stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and s.stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "' " +
                    "and st.stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and st.stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "' " +
                    "ORDER BY s1.departure_time_seconds ";
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.setFetchSize(4500);
            List<Integer> data = new ArrayList<>();
            double mean = 0;
            while (resultSet.next()) {
                int departure_time = resultSet.getInt(1);
                data.add(departure_time);
                mean += departure_time;
            }
            mean/=data.size();
            GaussianDistribution histogram = new GaussianDistribution(mean,data);
            resultSet.close();
            return histogram;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addStopTravels(Connection c, StopGraph graph, Location city, String startDate, int startTime, int endTime) throws SQLiteException {
        try (Statement statement = c.createStatement()) {
            double[] leftCorner = city.getLeftGroundCornerCoordinates();
            double[] rightCorner = city.getRightUpperCornerCoordinates();
            startDate = startDate.replaceAll("-", "");
            String day = DateCalculator.getDayFromDateString(startDate).toLowerCase();
            String query = "SELECT " +
                    "a.agency_name as agency_name, " +
                    "s1.stop_id as 'from', " +
                    "s2.stop_id as 'to', " +
                    "s1.departure_time_seconds as departure_time, " +
                    "s2.arrival_time_seconds as arrival_time, " +
                    "r.route_long_name as route_long_name, " +
                    "r.route_short_name as route_short_name, " +
                    "t.trip_headsign as trip_headsign, " +
                    "t.trip_id as trip_id " +
                    "FROM Stop_times s1 " +
                    "join Stop_times s2 on s1.trip_id = s2.trip_id and s1.stop_sequence = s2.stop_sequence-1 " +
                    "join Stops s on s2.stop_id  = s.stop_id " +
                    "join Stops st on s1.stop_id = st.stop_id " +
                    "join Trips t on s2.trip_id = t.trip_id " +
                    "join Routes r on t.route_id = r.route_id " +
                    "join Agency a on r.agency_id = a.agency_id " +
                    "left join Calendar c on t.service_id = c.service_id " +
                    "left join Calendar_Dates cd on t.service_id = cd.service_id and cd.service_date = '" + startDate + "' " +
                    "where (c." + day + " is null or c." + day + " = 1) " +
                    "and (cd.exception_type is NULL or cd.exception_type = 1) " +
                    "and s1.departure_time_seconds between '" + startTime + "' and '" + endTime + "' " +
                    "and s.stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and s.stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "' " +
                    "and st.stop_lat between '" + leftCorner[0] + "' and '" + rightCorner[0] + "' " +
                    "and st.stop_lon between '" + leftCorner[1] + "' and '" + rightCorner[1] + "' " +
                    "ORDER BY s1.departure_time_seconds ";
            ResultSet resultSet = statement.executeQuery(query);

            resultSet.setFetchSize(4500);
            while (resultSet.next()) {
                String agency_name = resultSet.getString(1);
                String from = resultSet.getString(2);
                String to = resultSet.getString(3);
                int departure_time = resultSet.getInt(4);
                int arrival_time = resultSet.getInt(5);
                String route_long_name = resultSet.getString(6);
                String route_short_name = resultSet.getString(7);
                String trip_headsign = resultSet.getString(8);
                String trip_id = resultSet.getString(9);
                Node fromNode = graph.getStop(from);
                Node toNode = graph.getStop(to);
                TransportTravelInformation transport = new TransportTravelInformation(departure_time, arrival_time,
                        fromNode, toNode, agency_name, route_short_name, route_long_name, trip_headsign,trip_id);
                graph.addTravel(from, to, transport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
