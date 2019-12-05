package eubr.atmosphere.tma.execute.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.data.Actuator;
import eubr.atmosphere.tma.database.DatabaseManager;

public class ActuatorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorManager.class);
    
    public static Actuator obtainActuatorByAction(Action action) {
        // TODO: This method can go to TMA-K component
        Actuator actuator = null;

        String sql = "select address, pubKey from Actuator "
                + "where "
                + "actuatorId = ? ;";
        
        try {
            PreparedStatement ps = DatabaseManager.getConnectionInstance().prepareStatement(sql);
            ps.setInt(1, action.getActuatorId());
            ResultSet rs = DatabaseManager.executeQuery(ps);

            if (rs.next()) {
                String address = (String) rs.getObject("address");
                String pubKey = (String) rs.getObject("pubKey");
                LOGGER.info(address);
                //LOGGER.info(pubKey.toString());
                actuator = new Actuator(action.getActuatorId(), address, pubKey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actuator;
    }
}
