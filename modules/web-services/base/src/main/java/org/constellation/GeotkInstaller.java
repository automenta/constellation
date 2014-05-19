/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constellation;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.lang.Setup;
import org.apache.sis.util.logging.Logging;

/**
 * Class responsible for starting and stopping geotoolkit.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GeotkInstaller implements ServletContextListener{

    private static final Logger LOGGER = Logging.getLogger(GeotkInstaller.class);

    @Override
    public synchronized void contextInitialized(ServletContextEvent sce) {

        LOGGER.log(Level.INFO, "=== Starting GeotoolKit ===");

        try{
            Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

            //Initialize geotoolkit
            Installation.allowSystemPreferences = false;
            ImageIO.scanForPlugins();
            Setup.initialize(null);

            try {
                Class.forName("javax.media.jai.JAI");
            } catch (ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "JAI libraries are not in the classpath. Please install it.\n "
                        + ex.getLocalizedMessage(), ex);
            }
            LOGGER.log(Level.INFO, "=== GeotoolKit successfully started ===");
        } catch(Exception ex) {
            LOGGER.log(Level.WARNING, "=== GeotoolKit failed to start ===\n"+ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public synchronized void contextDestroyed(ServletContextEvent sce) {

        LOGGER.log(Level.INFO, "=== Stopping GeotoolKit ===");
        try{
            Setup.shutdown();
            //wait for threads to die
            wait(2000);
            LOGGER.log(Level.INFO, "=== GeotoolKit successfully stopped ===");
        }catch(Exception ex){
            LOGGER.log(Level.WARNING, "=== GeotoolKit failed to stop ===\n"+ex.getLocalizedMessage(), ex);
        }
    }

}
