/*- Package Declaration ------------------------------------------------------*/

package ch.psi.wica.controllers;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.controlsystem.epics.channel.EpicsChannelManager;
import ch.psi.wica.controlsystem.epics.monitor.EpicsChannelMonitorPublisher;
import ch.psi.wica.controlsystem.epics.poller.EpicsChannelPollerPublisher;
import ch.psi.wica.model.app.StatisticsCollectable;
import ch.psi.wica.model.app.StatisticsCollectionService;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

/**
 * Returns an HTML Page which provides general information together with links
 * to the other administration pages.
 */
@ThreadSafe
@Controller
class WicaAdminPageController
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final Logger logger = LoggerFactory.getLogger(WicaAdminPageController.class );

   private final StatisticsCollectionService statisticsCollectionService;
   private final EpicsChannelMonitorPublisher epicsChannelMonitorPublisher;
   private final EpicsChannelPollerPublisher epicsChannelPollerPublisher;
   private final EpicsChannelManager epicsMonitoredChannelManager;
   private final EpicsChannelManager epicsPolledChannelManager;

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   /**
    * Constructs a new instance.
    *
    * @param statisticsCollectionService the statistics collection service.
    * @param epicsChannelMonitorPublisher the EPICS channel monitor publisher.
    * @param epicsChannelPollerPublisher the EPICS channel poller publisher.
    * @param epicsMonitoredChannelManager the EPICS monitored channel manager.
    * @param epicsPolledChannelManager the EPICS polled channel manager.
    */
   public WicaAdminPageController( @Autowired StatisticsCollectionService statisticsCollectionService,
                                   @Autowired EpicsChannelMonitorPublisher epicsChannelMonitorPublisher,
                                   @Autowired EpicsChannelPollerPublisher epicsChannelPollerPublisher,
                                   @Autowired EpicsChannelManager.EpicsMonitoredChannelManagerService epicsMonitoredChannelManager,
                                   @Autowired EpicsChannelManager.EpicsPolledChannelManagerService epicsPolledChannelManager)
   {
      this.statisticsCollectionService = statisticsCollectionService;
      this.epicsChannelMonitorPublisher = epicsChannelMonitorPublisher;
      this.epicsChannelPollerPublisher= epicsChannelPollerPublisher;
      this.epicsMonitoredChannelManager = epicsMonitoredChannelManager;
      this.epicsPolledChannelManager = epicsPolledChannelManager;
   }

/*- Class methods ------------------------------------------------------------*/
/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   /**
    * Returns the admin page as HTML.
    * <p>
    * Leaves the default MVC handling for this method. This means that the returned value will
    * be interpreted as a reference to a thymeleaf template.
    *
    * @param viewModel the view model.
    * @return the HTML page.
    */
   @GetMapping( value="/admin", produces = MediaType.TEXT_HTML_VALUE )
   public String getAdminPageAsHtml( Model viewModel )
   {
      logger.trace("Received status GET /admin request" );

      final List<StatisticsCollectable.Statistics> statisticsList = statisticsCollectionService.collect();

      viewModel.addAttribute("statisticsList", statisticsList );

      // Return reference to the template. Spring Boot will do the rest !
      final String templateFileName = "AdminPage";
      logger.trace( "Returning reference to thymeleaf template: '{}'", templateFileName );
      return templateFileName;
   }

   /**
    * Returns a list of all channel monitors.
    *
    * @return the list.
    */
   @GetMapping( value="/channel-monitors/all", produces = MediaType.APPLICATION_JSON_VALUE )
   public ResponseEntity<List<String>> getAllMonitoredChannels()
   {
      final List<String> channelNames = epicsChannelMonitorPublisher.getStatistics().getChannelNames();
      return new ResponseEntity<>( channelNames, HttpStatus.OK );
   }

   /**
    * Returns a list of unconnected channel monitors.
    *
    * @return the list.
    */
   @GetMapping( value="/channel-monitors/unconn" )
   public ResponseEntity<List<String>> getUnconnectedChannelMonitors()
   {
      final List<String> unconnectedChannelNames = epicsMonitoredChannelManager.getStatistics().getUnconnectedChannels();
      return new ResponseEntity<>( unconnectedChannelNames, HttpStatus.OK );
   }

   /**
    * Returns a list of all channel pollers.
    *
    * @return the list.
    */
   @GetMapping( value="/channel-pollers/all", produces = MediaType.APPLICATION_JSON_VALUE )
   public ResponseEntity<List<String>> getAllPolledChannels()
   {
      final List<String> channelNames = epicsChannelPollerPublisher.getStatistics().getChannelNames();
      return new ResponseEntity<>( channelNames, HttpStatus.OK );
   }

   /**
    * Returns a list of unconnected channel pollers.
    *
    * @return the list.
    */
   @GetMapping( value="/channel-pollers/unconn" )
   public ResponseEntity<List<String>> getUnconnectedChannelPollers()
   {
      final List<String> unconnectedChannelNames = epicsPolledChannelManager.getStatistics().getUnconnectedChannels();
      return new ResponseEntity<>( unconnectedChannelNames, HttpStatus.OK );
   }

   /**
    * Resets the statistics.
    */
   @PutMapping( value="/statistics" )
   public void resetStatistics()
   {
      statisticsCollectionService.resetStatistics();
   }


/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}

