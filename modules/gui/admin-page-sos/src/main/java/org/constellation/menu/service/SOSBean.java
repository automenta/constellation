/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.constellation.menu.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.faces.model.SelectItem;
import javax.sql.DataSource;
import org.constellation.ServiceDef.Specification;
import org.constellation.configuration.DataSourceType;
import org.constellation.configuration.SOSConfiguration;
import org.constellation.generic.database.Automatic;
import org.constellation.observation.sql.ObservationDatabaseCreator;
import org.mdweb.sql.DatabaseCreator;
import org.mdweb.sql.DatabaseUpdater;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SOSBean extends AbstractServiceBean{

    private String omConfigType;
    
    private String omDataDirectory;
    
    private String omDriverClass = "org.postgresql.Driver";
    
    private String omConnectURL;
    
    private String omUserName;
    
    private String omUserPass;
    
    private String omPostgisDir;
    
    private String smlConfigType;
    
    private String smlDataDirectory;
    
    private String smlDriverClass = "org.postgresql.Driver";
    
    private String smlConnectURL;
    
    private String smlUserName;
    
    private String smlUserPass;
    
    private String profile;
    
    private String obsIdBase;
    
    private String obstmpIdBase;
    
    private String sensorIdBase;
    
    private String phenIdBase;
    
    private String maxObs;
    
    private String tmpTime;
    
    
    
    public SOSBean() {
        super(Specification.SOS,
                "/service/sos.xhtml",
                "/service/sosConfig.xhtml");
        addBundle("service.sos");
    }
    
    public List<SelectItem> getOmConfigTypes() {
        final List<SelectItem> selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem("postgrid"));
        selectItems.add(new SelectItem("filesystem"));
        return selectItems;
    }
    
    public List<SelectItem> getSmlConfigTypes() {
        final List<SelectItem> selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem("mdweb"));
        selectItems.add(new SelectItem("filesystem"));
        return selectItems;
    }
    
    public List<SelectItem> getProfiles() {
        final List<SelectItem> selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem("discovery"));
        selectItems.add(new SelectItem("transactional"));
        return selectItems;
    }

    /**
     * @return the omConfigType
     */
    public String getOmConfigType() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.omConfigType = config.getObservationReaderType().getName().toLowerCase();
        }
        return omConfigType;
    }

    /**
     * @param omConfigType the omConfigType to set
     */
    public void setOmConfigType(String omConfigType) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            final DataSourceType rType = DataSourceType.fromName(omConfigType);
            config.setObservationReaderType(rType);
            if (rType == DataSourceType.POSTGRID) {
                config.setObservationFilterType(DataSourceType.POSTGRID);
                config.setObservationWriterType(DataSourceType.POSTGRID);
            } else if (rType == DataSourceType.FILESYSTEM) {
                config.setObservationFilterType(DataSourceType.LUCENE);
                config.setObservationWriterType(DataSourceType.FILESYSTEM);
            }
        }
        this.omConfigType = omConfigType;
    }

    /**
     * @return the omDataDirectory
     */
    public String getOmDataDirectory() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.omDataDirectory = config.getOMConfiguration().getDataDirectoryValue();
        }
        return omDataDirectory;
    }

    /**
     * @param omDataDirectory the omDataDirectory to set
     */
    public void setOmDataDirectory(String omDataDirectory) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getOMConfiguration().setDataDirectory(omDataDirectory);
        }
        this.omDataDirectory = omDataDirectory;
    }

    /**
     * @return the omDriverClass
     */
    public String getOmDriverClass() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.omDriverClass = config.getOMConfiguration().getBdd().getClassName();
        }
        return omDriverClass;
    }

    /**
     * @param omDriverClass the omDriverClass to set
     */
    public void setOmDriverClass(String omDriverClass) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getOMConfiguration().getBdd().setClassName(omDriverClass);
        }
        this.omDriverClass = omDriverClass;
    }

    /**
     * @return the omConnectURL
     */
    public String getOmConnectURL() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.omConnectURL = config.getOMConfiguration().getBdd().getConnectURL();
        }
        return omConnectURL;
    }

    /**
     * @param omConnectURL the omConnectURL to set
     */
    public void setOmConnectURL(String omConnectURL) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getOMConfiguration().getBdd().setConnectURL(omConnectURL);
        }
        this.omConnectURL = omConnectURL;
    }

    /**
     * @return the omUserName
     */
    public String getOmUserName() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.omUserName = config.getOMConfiguration().getBdd().getUser();
        }
        return omUserName;
    }

    /**
     * @param omUserName the omUserName to set
     */
    public void setOmUserName(String omUserName) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getOMConfiguration().getBdd().setUser(omUserName);
        }
        this.omUserName = omUserName;
    }

    /**
     * @return the omUserPass
     */
    public String getOmUserPass() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.omUserPass = config.getOMConfiguration().getBdd().getPassword();
        }
        return omUserPass;
    }

    /**
     * @param omUserPass the omUserPass to set
     */
    public void setOmUserPass(String omUserPass) {
        if (!omUserPass.isEmpty()) {
            if (configurationObject instanceof SOSConfiguration) {
                final SOSConfiguration config = (SOSConfiguration) configurationObject;
                config.getOMConfiguration().getBdd().setPassword(omUserPass);
            }
            this.omUserPass = omUserPass;
        }
    }

    /**
     * @return the smlConfigType
     */
    public String getSmlConfigType() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.smlConfigType = config.getSMLType().getName();
        }
        return smlConfigType;
    }

    /**
     * @param smlConfigType the smlConfigType to set
     */
    public void setSmlConfigType(String smlConfigType) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setSMLType(DataSourceType.fromName(smlConfigType));
        }
        this.smlConfigType = smlConfigType;
    }

    /**
     * @return the smlDataDirectory
     */
    public String getSmlDataDirectory() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.smlDataDirectory = config.getSMLConfiguration().getDataDirectoryValue();
        }
        return smlDataDirectory;
    }

    /**
     * @param smlDataDirectory the smlDataDirectory to set
     */
    public void setSmlDataDirectory(String smlDataDirectory) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getSMLConfiguration().setDataDirectory(smlDataDirectory);
        }
        this.smlDataDirectory = smlDataDirectory;
    }

    /**
     * @return the smlDriverClass
     */
    public String getSmlDriverClass() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.smlDriverClass = config.getSMLConfiguration().getBdd().getClassName();
        }
        return smlDriverClass;
    }

    /**
     * @param smlDriverClass the smlDriverClass to set
     */
    public void setSmlDriverClass(String smlDriverClass) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getSMLConfiguration().getBdd().setClassName(smlDriverClass);
        }
        this.smlDriverClass = smlDriverClass;
    }

    /**
     * @return the smlConnectURL
     */
    public String getSmlConnectURL() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.smlConnectURL= config.getSMLConfiguration().getBdd().getConnectURL();
        }
        return smlConnectURL;
    }

    /**
     * @param smlConnectURL the smlConnectURL to set
     */
    public void setSmlConnectURL(String smlConnectURL) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getSMLConfiguration().getBdd().setConnectURL(smlConnectURL);
        }
        this.smlConnectURL = smlConnectURL;
    }

    /**
     * @return the smlUserName
     */
    public String getSmlUserName() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.smlUserName = config.getSMLConfiguration().getBdd().getUser();
        }
        return smlUserName;
    }

    /**
     * @param smlUserName the smlUserName to set
     */
    public void setSmlUserName(String smlUserName) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.getSMLConfiguration().getBdd().setUser(smlUserName);
        }
        this.smlUserName = smlUserName;
    }

    /**
     * @return the smlUserPass
     */
    public String getSmlUserPass() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.smlUserPass = config.getOMConfiguration().getBdd().getPassword();
        }
        return smlUserPass;
    }

    /**
     * @param smlUserPass the smlUserPass to set
     */
    public void setSmlUserPass(final String smlUserPass) {
        if (!smlUserPass.isEmpty())
            if (configurationObject instanceof SOSConfiguration) {
                final SOSConfiguration config = (SOSConfiguration) configurationObject;
                config.getSMLConfiguration().getBdd().setPassword(smlUserPass);
            }
            this.smlUserPass = smlUserPass;
        }
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.profile = config.getProfileValue();
        }
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setProfile(profile);
        }
        this.profile = profile;
    }
    
    /**
     * @return the omPostgisDir
     */
    public String getOmPostgisDir() {
        return omPostgisDir;
    }

    /**
     * @param omPostgisDir the omPostgisDir to set
     */
    public void setOmPostgisDir(String omPostgisDir) {
        this.omPostgisDir = omPostgisDir;
    }
    
    /**
     * @return the obsIdBase
     */
    public String getObsIdBase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.obsIdBase = config.getObservationIdBase();
        }
        return obsIdBase;
    }

    /**
     * @param obsIdBase the obsIdBase to set
     */
    public void setObsIdBase(String obsIdBase) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setObservationIdBase(obsIdBase);
        }
        this.obsIdBase = obsIdBase;
    }

    /**
     * @return the obstmpIdBase
     */
    public String getObstmpIdBase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.obstmpIdBase = config.getObservationTemplateIdBase();
        }
        return obstmpIdBase;
    }

    /**
     * @param obstmpIdBase the obstmpIdBase to set
     */
    public void setObstmpIdBase(String obstmpIdBase) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setObservationTemplateIdBase(obstmpIdBase);
        }
        this.obstmpIdBase = obstmpIdBase;
    }

    /**
     * @return the sensorIdBase
     */
    public String getSensorIdBase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.sensorIdBase = config.getSensorIdBase();
        }
        return sensorIdBase;
    }

    /**
     * @param sensorIdBase the sensorIdBase to set
     */
    public void setSensorIdBase(String sensorIdBase) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setSensorIdBase(sensorIdBase);
        }
        this.sensorIdBase = sensorIdBase;
    }

    /**
     * @return the phenIdBase
     */
    public String getPhenIdBase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.phenIdBase = config.getPhenomenonIdBase();
        }
        return phenIdBase;
    }

    /**
     * @param phenIdBase the phenIdBase to set
     */
    public void setPhenIdBase(String phenIdBase) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setPhenomenonIdBase(phenIdBase);
        }
        this.phenIdBase = phenIdBase;
    }

    /**
     * @return the maxObs
     */
    public String getMaxObs() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.maxObs = config.getMaxObservationByRequest() + "";
        }
        return maxObs;
    }

    /**
     * @param maxObs the maxObs to set
     */
    public void setMaxObs(String maxObs) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            try {
                int n = Integer.parseInt(maxObs);
                config.setMaxObservationByRequest(n);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "unable to parse the number of maxObservation:{0}", maxObs);
            }
        }
        this.maxObs = maxObs;
    }

    /**
     * @return the tmpTime
     */
    public String getTmpTime() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            this.tmpTime = config.getTemplateValidTime();
        }
        return tmpTime;
    }

    /**
     * @param tmpTime the tmpTime to set
     */
    public void setTmpTime(String tmpTime) {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            config.setTemplateValidTime(tmpTime);
        }
        this.tmpTime = tmpTime;
    }
    

    public void buildMDWDatabase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            final Automatic smlConfig = config.getSMLConfiguration();
            if (smlConfig != null) {
                if (smlConfig.getBdd() != null) {
                    try {
                        final DataSource ds = smlConfig.getBdd().getDataSource();
                        if (ds != null) {
                            final DatabaseCreator dbCreator = new DatabaseCreator(ds, true);
                            dbCreator.createMetadataDatabase();
                        }
                    } catch (SQLException ex) {
                        LOGGER.log(Level.WARNING, "Error while creating the mdweb database", ex);
                    }
                }
            } else {
                LOGGER.warning("no SML configuration in sos config");
            }
        }
    }
    
    public void buildOMDatabase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            final Automatic omConfig = config.getSMLConfiguration();
            try {
                if (omConfig != null) {
                    if (omConfig.getBdd() != null) {
                        final DataSource ds = omConfig.getBdd().getDataSource();
                        if (ds != null) {
                            final File postgisInstall = new File(omPostgisDir);
                            ObservationDatabaseCreator.createObservationDatabase(ds, postgisInstall);
                        }
                    }
                } else {
                    LOGGER.warning("no OM configuration in sos config");
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Error while creating the database", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Error while creating the database", ex);
            }
        }
    }
    
    public boolean getNeedBuildMDWDatabase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            final Automatic smlConfig = config.getSMLConfiguration();
            if (smlConfig != null) {
                if (smlConfig.getBdd() != null) {
                    try {
                        final DataSource ds = smlConfig.getBdd().getDataSource();
                        if (ds != null) {
                            final DatabaseCreator dbCreator = new DatabaseCreator(ds, true);
                            return dbCreator.validConnection() && !dbCreator.structurePresent();
                        } else {
                            LOGGER.finer("No datasource available for build");
                            return false;
                        }
                    } catch (SQLException ex) {
                        LOGGER.log(Level.WARNING, "Error while looking for database structure presence", ex);
                    }
                }
            }
        }
        return false;
    }
    
    public void updateMDWDatabase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            final Automatic smlConfig = config.getSMLConfiguration();
            if (smlConfig != null) {
                if (smlConfig.getBdd() != null) {
                    try {
                        final DataSource ds = smlConfig.getBdd().getDataSource();
                        if (ds != null) {
                            final DatabaseUpdater dbUpdater = new DatabaseUpdater(ds, true);
                            if (dbUpdater.isToUpgradeDatabase()) {
                                dbUpdater.upgradeDatabase();
                            }
                        }
                    } catch (SQLException ex) {
                        LOGGER.log(Level.WARNING, "Error while updating the SML mdweb database", ex);
                    }
                }
            } else {
                LOGGER.warning("no SML configuration in sos config");
            }
        }
    }
    
    public boolean getNeedUpdateMDWDatabase() {
        if (configurationObject instanceof SOSConfiguration) {
            final SOSConfiguration config = (SOSConfiguration) configurationObject;
            final Automatic smlConfig = config.getSMLConfiguration();
            if (smlConfig != null) {
                if (smlConfig.getBdd() != null) {
                    try {
                        final DataSource ds = smlConfig.getBdd().getDataSource();
                        if (ds != null) {
                            final DatabaseUpdater dbUpdater = new DatabaseUpdater(ds, true);
                            if (dbUpdater.validConnection() && dbUpdater.structurePresent()) {
                                return dbUpdater.isToUpgradeDatabase();
                            }
                        } else {
                            LOGGER.finer("No datasource available for update");
                        }
                    } catch (SQLException ex) {
                        LOGGER.log(Level.WARNING, "Error while looking for database upgrade", ex);
                    }
                }
            } else {
                LOGGER.warning("no SML configuration in sos config");
            }
        }
        return false;
    }
}
