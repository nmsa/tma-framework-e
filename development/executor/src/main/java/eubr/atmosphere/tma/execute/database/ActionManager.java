package eubr.atmosphere.tma.execute.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.utils.DatabaseManager;

public class ActionManager {

    public static Action obtainActionById(int actionId) {
        // TODO: This method can go to TMA-K component
        Action action = null;

        String sql = "select actionName, resourceId, actuatorId from Action "
                + "where "
                + "actionId = ? ;";
        
        try {
            PreparedStatement ps = DatabaseManager.getConnectionInstance().prepareStatement(sql);
            ps.setInt(1, actionId);
            ResultSet rs = DatabaseManager.executeQuery(ps);

            if (rs.next()) {
                String actionName = (String) rs.getObject("actionName");
                int resourceId = (int) rs.getObject("resourceId");
                int actuatorId = (int) rs.getObject("actuatorId");
                action = new Action(actionId, actionName, resourceId, actuatorId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return action;
    }
}
