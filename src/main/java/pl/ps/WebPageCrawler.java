package pl.ps;

import pl.ps.properties.PageProperties;
import pl.ps.utils.Assert;
import pl.ps.utils.Constants;
import pl.ps.web.ListPageScanner;

import java.io.File;
import java.sql.SQLException;

public class WebPageCrawler {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        File[] propertiesFiles = getPropertiesFiles();
        for(File propertiesFile : propertiesFiles) {
            PageProperties pageProperties = new PageProperties(propertiesFile);
            if(pageProperties.getActive()) {
                ListPageScanner pageScanner = new ListPageScanner(pageProperties);
                pageScanner.load();
                pageScanner.openPages();
            }
        }
    }

    private static File[] getPropertiesFiles() {
        File configDir = new File(Constants.CONFIG_DIRECTORY_RELATIVE_PATH);
        Assert.directoryExists(configDir);
        return configDir.listFiles();
    }
}
