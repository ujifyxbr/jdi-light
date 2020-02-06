package com.epam.jdi.mobile.driver.get;

import com.epam.jdi.mobile.settings.WebSettings;
import com.epam.jdi.tools.DataClass;
import com.epam.jdi.tools.func.JFunc1;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.List;

import static com.epam.jdi.mobile.common.Exceptions.exception;
import static com.epam.jdi.mobile.common.Exceptions.safeException;
import static com.epam.jdi.mobile.driver.WebDriverFactory.isRemote;
import static com.epam.jdi.mobile.driver.get.DownloadDriverManager.downloadDriver;
import static com.epam.jdi.mobile.driver.get.DownloadDriverManager.wdm;
import static com.epam.jdi.mobile.driver.get.DriverData.*;
import static com.epam.jdi.mobile.driver.get.RemoteDriver.getRemoteURL;
import static java.lang.Integer.parseInt;
import static java.lang.System.setProperty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by Roman Iovlev on 26.09.2019
 * Email: roman.iovlev.jdi@gmail.com; Skype: roman.iovlev
 */
public class DriverInfo extends DataClass<DriverInfo> {
    public DriverTypes type;
    public MutableCapabilities initCapabilities;
    public JFunc1<MutableCapabilities, Capabilities> capabilities;
    public String properties, path;
    public JFunc1<Object, WebDriver> getDriver;

    public WebDriver getDriver() {
        return isRemote()
                ? setupRemote()
                : setupLocal();
    }
    private WebDriver setupRemote() {
        try {
            return new RemoteWebDriver(new URL(RemoteDriver.getRemoteURL()), capabilities.execute(initCapabilities));
        } catch (Exception ex) {
            throw exception(ex, "Failed to setup remote "+type.name+" driver");
        }
    }
    private WebDriver setupLocal() {
        try {
            if (isNotBlank(DRIVERS_FOLDER)) {
                setProperty(properties, path);
                WebSettings.logger.info("Get local driver: " + path);
            }
            else {
                downloadDriver(type, PLATFORM, DRIVER_VERSION);
            }
            return getDriver.execute(capabilities.execute(initCapabilities));
        } catch (Exception ex) {
            try {
                if (isBlank(DRIVERS_FOLDER) && DRIVER_VERSION.equals(LATEST_VERSION)) {
                    WebSettings.logger.info("Failed to download driver (%s %s) of latest version:" +
                            "TRY TO GET DRIVER PREVIOUS VERSION", type, DRIVER_VERSION);
                    try {
                        downloadDriver(type, PLATFORM, getBelowVersion());
                        return getDriver.execute(capabilities.execute(initCapabilities));
                    } catch (Exception ex2) { throw exception(ex2, "Failed to download driver"); }
                }
                throw exception(safeException(ex));
            } catch (Exception ex2) {
                throw exception(ex2, "Failed to setup local driver");
            }
        }
    }
    public static String getBelowVersion() {
        String currentMajor = wdm.getDownloadedVersion().split("\\.")[0];
        List<String> allVersions = wdm.getVersions();
        for (int i = allVersions.size()-1; i>=0; i--)
             if (parseInt(currentMajor) > parseInt(allVersions.get(i).split("\\.")[0]))
                 return allVersions.get(i);
         throw exception("Can't find version below current(" + wdm.getDownloadedVersion()+")");
    }
}