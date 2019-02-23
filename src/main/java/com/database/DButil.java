package main.java.com.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.com.Airplane;
import main.java.com.Flight;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class DButil
{
    private static DButil instance;

    private DButil()
    {}

    public static synchronized DButil getCurrentInstance()
    {
        if (instance == null)
        {
            instance = new DButil();
        }

        return instance;
    }



    public static Connection getConnection()
    {
        Connection conn =null;
        String URL = "jdbc:mysql://localhost/rft";
        try
        {
            conn = DriverManager.getConnection(URL,"root","dnylm404");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return conn;
    }


    public void addFlight(Flight f)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        try
        {
            conn = DButil.getConnection();
            String sql = "insert into flight (flight_number, departure_location, destination, dep_date, arr_date, airplane_number) values (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1,f.getFlightNumber());
            ps.setString(2,f.getDepartureLocation());
            ps.setString(3,f.getDestination());
            ps.setString(4,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(f.getDepDate()));
            ps.setString(5,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(f.getArrDate()));
            ps.setString(6,f.getAirplaneNumber());
            ps.execute();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }
    }


    public ObservableList<Flight> getAllFlights()
    {

        Connection conn ;
        PreparedStatement ps = null;
        ResultSet rs= null;
        ObservableList<Flight> allFlights = FXCollections.observableArrayList();
        conn = DButil.getConnection();
        String sql = "select * from flight";
        try
        {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next())
            {
                Flight f = new Flight(rs.getString("flight_number"),
                        rs.getString("departure_location"),
                        rs.getString("destination"),
                        rs.getTimestamp("dep_date"),
                        rs.getTimestamp("arr_date"),
                        rs.getString("airplane_number"));
                allFlights.add(f);
            }

        }
        catch (SQLException e){e.printStackTrace();}

        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return allFlights;
    }


    public void cleanFlights()
    {
        Connection conn ;
        PreparedStatement ps = null;
        conn = DButil.getConnection();
        String sql1 = "update airplane set status = 'inactive' where airplane_number not in (select airplane_number from flight where dep_date <= current_timestamp() and arr_date > current_timestamp())";
        String sql2 = "update airplane set status = 'active' where airplane_number in (select airplane_number from flight where dep_date <= current_timestamp() and arr_date > current_timestamp())";
        try
        {
            ps = conn.prepareStatement(sql1);
            ps.execute();
            ps = conn.prepareStatement(sql2);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanFlights1(Connection conn)
    {

        PreparedStatement ps ;
        String sql1 = "update airplane set status = 'inactive' where airplane_number not in (select airplane_number from flight where dep_date <= current_timestamp() and arr_date > current_timestamp())";
        String sql2 = "update airplane set status = 'active' where airplane_number in (select airplane_number from flight where dep_date <= current_timestamp() and arr_date > current_timestamp())";
        try
        {
            ps = conn.prepareStatement(sql1);
            ps.execute();
            ps = conn.prepareStatement(sql2);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Airplane> getAllAirplanes()
    {
        ObservableList<Airplane> o = FXCollections.observableArrayList();
        Connection conn;
        PreparedStatement ps = null;
        ResultSet rs= null;
        conn = DButil.getConnection();
        String sql = "select * from airplane";
        try
        {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next())
            {
                Airplane airplane = new Airplane(rs.getString("airplane_number"),
                        rs.getString("type"),
                        rs.getDouble("size"),
                        rs.getInt("capacity"),
                        rs.getDate("date_of_purchase"),
                        rs.getString("status"));
                o.add(airplane);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public void addAirplane(Airplane airplane)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        try
        {
            conn = DButil.getConnection();
            String sql = "insert into airplane (airplane_number,type,size,capacity,date_of_purchase,status) values (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1,airplane.getAirplaneNumber());

            if (airplane.getType() == null)
                ps.setNull(2, Types.VARCHAR);
            else
                ps.setString(2,airplane.getType());

            if (airplane.getSize() == -1.0)
                ps.setNull(3, Types.DOUBLE);
            else
                ps.setDouble(3,airplane.getSize());

            if (airplane.getCapacity() == -1)
                ps.setNull(4, Types.INTEGER);
            else
                ps.setInt(4, airplane.getCapacity());

            if (airplane.getDateOfPurchase() == null)
                ps.setNull(5, Types.DATE);
            else
                ps.setString(5,new SimpleDateFormat("yyyy-MM-dd").format(airplane.getDateOfPurchase()));

            ps.setString(6,airplane.getStatus());

            ps.execute();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }
    }

    public void editAirplane(Airplane airplaneToEdit , Airplane newAirplane)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        try
        {
            conn = DButil.getConnection();
            String sql = "update airplane set airplane_number = ? ,type = ? ,size = ?, capacity = ? ,date_of_purchase = ? where airplane_number = ?; " ;
            ps = conn.prepareStatement(sql);

            ps.setString(1,newAirplane.getAirplaneNumber());

            if (newAirplane.getType() == null)
                ps.setNull(2, Types.VARCHAR);
            else
                ps.setString(2,newAirplane.getType());

            if (newAirplane.getSize() == -1.0)
                ps.setNull(3, Types.DOUBLE);
            else
                ps.setDouble(3,newAirplane.getSize());

            if (newAirplane.getCapacity() == -1)
                ps.setNull(4, Types.INTEGER);
            else
                ps.setInt(4, newAirplane.getCapacity());

            if (newAirplane.getDateOfPurchase() == null)
                ps.setNull(5, Types.DATE);
            else
                ps.setString(5,new SimpleDateFormat("yyyy-MM-dd").format(newAirplane.getDateOfPurchase()));

            ps.setString(6,airplaneToEdit.getAirplaneNumber());

            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }
    }

    public void deleteAirplane(Airplane airplane)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        try
        {
            conn = DButil.getConnection();
            String sql = "delete from airplane where airplane_number = ?;" ;
            ps = conn.prepareStatement(sql);
            ps.setString(1 , airplane.getAirplaneNumber());
            ps.execute();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }
    }

    public void updateAirplaneStatus(String airplaneNumber)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        try
        {
            conn = DButil.getConnection();
            String sql = "update airplane set status = 'active' where airplane_number = ?" ;
            ps = conn.prepareStatement(sql);
            ps.setString(1 , airplaneNumber);
            ps.execute();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }
    }

    public Airplane getAirplaneById(String airplaneNumber)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        Airplane airplane = null;
        try
        {
            conn = DButil.getConnection();
            String sql = "select * from airplane where airplane_number = ?" ;
            ps = conn.prepareStatement(sql);
            ps.setString(1 , airplaneNumber);
            rs = ps.executeQuery();
            while (rs.next())
            {
                airplane = new Airplane(rs.getString("airplane_number"),rs.getString("type"),rs.getDouble("size"),rs.getInt("capacity"),rs.getDate("date_of_purchase"),rs.getString("status"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }

        return airplane;
    }

    public Flight getFlightById(String flightNumber)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        Flight flight = null;
        try
        {
            conn = DButil.getConnection();
            String sql = "select * from flight where flight_number = ?" ;
            ps = conn.prepareStatement(sql);
            ps.setString(1 , flightNumber);
            rs = ps.executeQuery();
            while (rs.next())
            {
                flight = new Flight(rs.getString("flight_number"),
                        rs.getString("departure_location"),
                        rs.getString("destination"),
                        rs.getTimestamp("dep_date"),
                        rs.getTimestamp("arr_date"),
                        rs.getString("airplane_number"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }

        return flight;
    }

    public void deleteFlight(Flight flight)
    {
        Connection conn = null;
        PreparedStatement ps =null;
        try
        {
            conn = DButil.getConnection();
            String sql = "delete from flight where flight_number = ?;" ;
            ps = conn.prepareStatement(sql);
            ps.setString(1 , flight.getFlightNumber());
            ps.execute();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;
            }
            catch (SQLException e)
            {e.printStackTrace();}

        }
    }

    public ArrayList<Flight> getAirplaneFutureFlights(String airplaneNumber)
    {
        Connection conn ;
        PreparedStatement ps = null;
        ResultSet rs= null;
        ArrayList<Flight> flights = new ArrayList<>();
        conn = DButil.getConnection();
        String sql = "select * from flight where airplane_number = ? and dep_date > current_timestamp() ";
        try
        {
            ps = conn.prepareStatement(sql);
            ps.setString(1,airplaneNumber);
            rs = ps.executeQuery();
            while (rs.next())
            {
                Flight f = new Flight(rs.getString("flight_number"),
                        rs.getString("departure_location"),
                        rs.getString("destination"),
                        rs.getTimestamp("dep_date"),
                        rs.getTimestamp("arr_date"),
                        rs.getString("airplane_number"));
                flights.add(f);
            }

        }
        catch (SQLException e){e.printStackTrace();}

        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return flights;
    }

    public Flight getAirplaneCurrentFlight(String airplaneNumber)
    {
        Connection conn ;
        PreparedStatement ps = null;
        ResultSet rs= null;
        Flight flight = null;


        conn = DButil.getConnection();
        String sql = "select * from flight where airplane_number = ? and dep_date <= current_timestamp() and arr_date >= current_timestamp()";

        try
        {
            ps = conn.prepareStatement(sql);
            ps.setString(1 , airplaneNumber);
            rs = ps.executeQuery();
            while (rs.next())
            {
                flight = new Flight(rs.getString("flight_number"),
                        rs.getString("departure_location"),
                        rs.getString("destination"),
                        rs.getTimestamp("dep_date"),
                        rs.getTimestamp("arr_date"),
                        rs.getString("airplane_number"));
            }

        }
        catch (SQLException e){e.printStackTrace();}

        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return flight;
    }

    public List<Flight> getAirplaneFlights(Airplane airplane)
    {
        Connection conn ;
        PreparedStatement ps = null;
        ResultSet rs= null;
        ArrayList<Flight> flights = new ArrayList<>();
        conn = DButil.getConnection();
        String sql = "select * from flight where airplane_number = ? ";
        try
        {
            ps = conn.prepareStatement(sql);
            ps.setString(1,airplane.getAirplaneNumber());
            rs = ps.executeQuery();
            while (rs.next())
            {
                Flight f = new Flight(rs.getString("flight_number"),
                        rs.getString("departure_location"),
                        rs.getString("destination"),
                        rs.getTimestamp("dep_date"),
                        rs.getTimestamp("arr_date"),
                        rs.getString("airplane_number"));
                flights.add(f);
            }

        }
        catch (SQLException e){e.printStackTrace();}

        finally
        {
            try
            {
                conn.close();conn= null;ps.close();ps=null;rs.close();rs=null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return flights;
    }
}

