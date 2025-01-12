/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.controlsystem.event.channel;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.controlsystem.epics.channel.EpicsChannelName;
import org.apache.commons.lang3.Validate;
import org.epics.ca.Channel;


/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

public record EpicsChannelFirstConnectedEvent(String scope, Channel<Object> caChannel)
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/
/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   public EpicsChannelFirstConnectedEvent( String scope, Channel<Object> caChannel )
   {
      this.scope = Validate.notNull( scope, "The 'scope' argument is null." );
      this.caChannel = Validate.notNull( caChannel, "The 'caChannel' argument is null." );
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   public EpicsChannelName getEpicsChannelName()
   {
      //noinspection resource
      return EpicsChannelName.of( caChannel().getName() );
   }

/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}