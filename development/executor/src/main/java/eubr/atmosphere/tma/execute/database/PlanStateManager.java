package eubr.atmosphere.tma.execute.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import eubr.atmosphere.tma.database.DatabaseManager;

public class PlanStateManager {

    public boolean setPlanStatus(int planId, int status) {

        String sql = "UPDATE Plan set status = ? WHERE planId = ?";
        
        try(PreparedStatement ps = DatabaseManager.getConnectionInstance().prepareStatement(sql)){
            ps.setInt(1, status);
            ps.setInt(2, planId);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public boolean setActionStatus(int planId, int actionId, int status) {

        String sql = "UPDATE ActionPlan set status = ? WHERE planId = ? AND actionId = ?";
        
        try(PreparedStatement ps = DatabaseManager.getConnectionInstance().prepareStatement(sql)){
            ps.setInt(1, status);
            ps.setInt(2, planId);
            ps.setInt(3, actionId);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
