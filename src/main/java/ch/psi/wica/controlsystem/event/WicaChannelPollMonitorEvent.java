/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.controlsystem.event;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.model.channel.WicaChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

public class WicaChannelPollMonitorEvent
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final WicaChannel wicaChannel;

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   public WicaChannelPollMonitorEvent( WicaChannel wicaChannel )
   {
      final Logger logger = LoggerFactory.getLogger( WicaChannelPollMonitorEvent.class);
      this.wicaChannel = wicaChannel;
      logger.trace("Event created: '{}'.", this );

   }

   /*- Class methods ------------------------------------------------------------*/
   /*- Public methods -----------------------------------------------------------*/

   public WicaChannel getWicaChannel()
   {
      return wicaChannel;
   }

   @Override
   public String toString()
   {
      return "WicaChannelPollMonitorEvent{" +
         "wicaChannel=" + wicaChannel +
         '}';
   }

/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}