package eubr.atmosphere.tma.execute.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import eubr.atmosphere.tma.data.Configuration;
import eubr.atmosphere.tma.database.DatabaseManager;

public class ConfigurationManager {

    public static List<Configuration> obtainConfiguration(int planId, int actionId) {
        List<Configuration> configurationList = new ArrayList<Configuration>();
        
        
        String sql = "select c.configurationId, c.keyName, cd.value "
                + "from Plan p "
                + "inner join ActionPlan ap on p.planId = ap.planId "
                + "inner join ConfigurationData cd on ap.planId = cd.planId "
                + "inner join Configuration c on cd.configurationId = c.configurationId "
                + "where p.planId = ? "
                + "and c.actionId = ? ;";
        
        try {
            PreparedStatement ps = DatabaseManager.getConnectionInstance().prepareStatement(sql);
            ps.setInt(1, planId);
            ps.setInt(2, actionId);
            ResultSet rs = DatabaseManager.executeQuery(ps);

            while (rs.next()) {
                int configurationId = (int) rs.getObject("configurationId");
                String keyName = (String) rs.getObject("keyName");
                String value = (String) rs.getObject("value");
                Configuration config = new Configuration(configurationId, keyName, value);
                
                configurationList.add(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return configurationList;
    }
}
