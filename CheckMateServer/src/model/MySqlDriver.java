package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import facebook4j.api.FavoriteMethods;
import sun.security.jca.GetInstance;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class MySqlDriver {

	Connection conn = null;
	static MySqlDriver instance;

	public static MySqlDriver getInstance() {
		if (instance == null) {
			instance = new MySqlDriver();
		}
		return instance;
	}

	private MySqlDriver() {
	}

	public void connect() {
		String url = "jdbc:mysql://127.0.0.1:3306/";
		
		String dbName = "checkmate";

		String driver = "com.mysql.jdbc.Driver";
		String userName = "admin7RStHHh";
		String password = "AKjhkEUcpZBj";

		try {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			conn = DriverManager.getConnection(url + dbName, userName, password);
			// Do something with the Connection

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	public Type getFacebookType(String facebookType){
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Type t = null;
		try {
			String s = " SELECT f.fb_type_id, f.fb_type_name "
					+ " FROM facebook_types f "
					+ " WHERE f.fb_type_name = ? ";                                               
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setString(1, facebookType);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Type();
				t.setId(rs.getInt("fb_type_id"));
				t.setName(rs.getString("fb_type_name"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return t;

	}

	public Type getGoogleType(String facebookType) {
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Type t = null;
		try {
			String s = " SELECT g.goog_type_id, g.goog_type_name							   "
					+ " FROM google_types g                                                    "
					+ " JOIN google_to_facebook fg ON g.goog_type_id = fg.goog_type_id   "
					+ " JOIN facebook_types f ON f.fb_type_id = fg.fb_type_id                  "
					+ " WHERE f.fb_type_name = ?                                               ";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setString(1, facebookType);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Type();
				t.setId(rs.getInt("goog_type_id"));
				t.setName(rs.getString("goog_type_name"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return t;
	}

	public int getDislikeCountByGoogType(int googTypeId, int userId) {

		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			String s = "SELECT COUNT(*) FROM emotions WHERE user_id = ? and goog_type_id = ? and  'like_ind' = 1 and date > (CURRENT_TIMESTAMP - 30)";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setInt(1, googTypeId);
			preparedStatement.setInt(2, userId);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return count;
	}

	public int getDislikeCountByPlace(String placeId, int userId) {

		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			String s = "SELECT COUNT(*) FROM emotions WHERE user_id = ? and goog_place_id = ? and 'like_ind' = 0 and date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, placeId);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return count;
	}
	
	public List<Type> getGoogleTypesByInterest(int interest) {
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Type t = null;
		List<Type> googleTypes = new ArrayList<Type>();
		try {
 			String s = " SELECT t.goog_type_id, t.goog_type_name"
					 + " FROM google_to_interest i"
					 + " JOIN google_types t on t.goog_type_id = i.goog_type_id"
					 + " WHERE interest_type = ?";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setInt(1, interest);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Type();
				t.setId(rs.getInt("goog_type_id"));
				t.setName(rs.getString("goog_type_name"));
				googleTypes.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return googleTypes;
	}
	
	public List<Type> getFacebookTypesByInterest(int interest) {
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Type t = null;
		List<Type> facebookTypes = new ArrayList<Type>();
		try {
 			String s = " SELECT t.fb_type_id, t.fb_type_name"
					 + " FROM facebook_to_interest i"
					 + " JOIN facebook_types t on t.fb_type_id = i.fb_type_id"
					 + " WHERE interest_type = ?";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setInt(1, interest);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Type();
				t.setId(rs.getInt("fb_type_id"));
				t.setName(rs.getString("fb_type_name"));
				facebookTypes.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return facebookTypes;
	}
			
	public List<Interest> getInterestsByGoogleType(int googleType) {
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Interest t = null;
		List<Interest> interestTypes = new ArrayList<Interest>();
		try {
 			String s = " SELECT i.interest_type,t.interest_name"
					 + " FROM google_to_interest i"
					 + " JOIN interests t ON t.interest_id = i.interest_type"
					 + " WHERE goog_type_id = ?";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setInt(1, googleType);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Interest();
				t.setId(rs.getInt("interest_type"));
				t.setName(rs.getString("interest_name"));
				interestTypes.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return interestTypes;
	}
	
	public List<Interest> getInterestsByFacebookType(String facebookType) {
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Interest t = null;
		List<Interest> interestTypes = new ArrayList<Interest>();
		try {
 			String s = " SELECT i.interest_type,t.interest_name"
					 + " FROM facebook_to_interest i"
					 + " JOIN interests t ON t.interest_id = i.interest_type"
					 + " JOIN facebook_types f ON f.fb_type_id = i.fb_type_id"
					 + " WHERE fb_type_name = ?";
			preparedStatement = conn.prepareStatement(s);
			preparedStatement.setString(1, facebookType);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Interest();
				t.setId(rs.getInt("interest_type"));
				t.setName(rs.getString("interest_name"));
				interestTypes.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return interestTypes;
	}
	
    private static String createGoogleTypesByInterestWithRateQuery(int facebookTypeLen,int googleTypeLen) {
		String s = 
		"select gi.goog_type_id,gt.goog_type_name, ((interests.count +1) * power(1.2,e.like) * power(1/1.2,e.dislike)) as 'rate' " +
		"from google_to_interest gi JOIN " +
		"	 google_types gt USING (goog_type_id) " +
		"	 JOIN " +
		"     (select fi.interest_type,count(*) count " +
		"     from facebook_to_interest fi " +
		"     join facebook_types ft on ft.fb_type_id = fi.fb_type_id ";
		StringBuilder queryBuilder = new StringBuilder("where ft.fb_type_name in (");
        for( int i = 0; i< facebookTypeLen; i++){
            queryBuilder.append(" ?");
            if(i != facebookTypeLen -1) queryBuilder.append(",");
        }
        queryBuilder.append(") ");
        s += queryBuilder.toString();
        
		String s2 = "     group by fi.interest_type) interests USING (interest_type) " +
		"     JOIN " +
		"     (select goog_type_id, COUNT(CASE WHEN like_ind = 1 then 1 ELSE NULL END) as 'like', COUNT(CASE WHEN like_ind = 0 then 1 ELSE NULL END) as 'dislike' " +
		"     from emotions e " +
		"     where e.user_id = ? " +
		"     group by goog_type_id) e USING (goog_type_id) " ;
			
        queryBuilder = new StringBuilder("where gi.goog_type_id not in (");
        for( int i = 0; i< googleTypeLen; i++){
            queryBuilder.append(" ?");
            if(i != googleTypeLen -1) queryBuilder.append(",");
        }
        queryBuilder.append(") ");
        s2 += queryBuilder.toString();
        
        return s+s2;
    }
    
	public List<Type> getGoogleTypesByInterestWithRate(List<String> facebookTypes,List<Integer> googleTypes,int userID) {
		connect();

		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Type t = null;
		List<Type> types = new ArrayList<Type>();
		try {
 			String s = createGoogleTypesByInterestWithRateQuery(facebookTypes.size(),googleTypes.size());
			preparedStatement = conn.prepareStatement(s);
			
			for(int i = 1; i <= facebookTypes.size(); i++){
				preparedStatement.setString(i, facebookTypes.get(i- 1));
			}
			
			preparedStatement.setInt(facebookTypes.size() + 1, userID);
			
			for(int i = facebookTypes.size() + 2,j=0; j < googleTypes.size(); i++,j++){
				preparedStatement.setInt(i, googleTypes.get(j));
			}
			
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				t = new Type();
				t.setId(rs.getInt("goog_type_id"));
				t.setName(rs.getString("goog_type_name"));
				t.setRate(rs.getInt("rate"));
				types.add(t);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return types;
	}
	
	public List<Type> getTypesByFacebookInterests(Iterable<String> inSubSelect, Iterable<String> notIn){
		
		return null;
	}
}