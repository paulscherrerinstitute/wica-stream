/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.controlsystem.epics.poller;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.controlsystem.epics.channel.EpicsChannelName;
import ch.psi.wica.controlsystem.epics.metadata.EpicsChannelMetadataRequest;
import ch.psi.wica.model.channel.WicaChannel;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.Validate;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

/**
 * Value object providing the specification for polling an EPICS channel.
 */
@Immutable
public class EpicsChannelPollerRequest
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final EpicsChannelName epicsChannelName;
   private final int pollingInterval;
   private final WicaChannel publicationChannel;


/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   /**
    * Creates a new instance which will poll the EPICS channel using the properties associated
    * with the supplied Wica Channel.
    *
    * @param wicaChannel the channel to poll.
    */
   public EpicsChannelPollerRequest( WicaChannel wicaChannel )
   {
      this( EpicsChannelName.of( Validate.notNull( wicaChannel.getName().getControlSystemName() ) ), wicaChannel.getProperties().getPollingIntervalInMillis(), wicaChannel );
   }

   /**
    * Creates a new instance that will poll the specified EPICS channel at the specified rate and publish the
    * obtained values on the specified Wica Channel.
    *
    * @param epicsChannelName the name of the EPICS channel to be polled.
    * @param pollingIntervalInMillis the polling interval.
    * @param wicaChannel the Wica channel on which publication will take place.
    */
   EpicsChannelPollerRequest( EpicsChannelName epicsChannelName, int pollingIntervalInMillis, WicaChannel wicaChannel )
   {
      this.epicsChannelName = Validate.notNull( epicsChannelName );
      this.pollingInterval = pollingIntervalInMillis;
      this.publicationChannel = Validate.notNull( wicaChannel );
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   @SuppressWarnings( "unused" )
   public EpicsChannelMetadataRequest getAsMetadataRequest()
   {
      return new EpicsChannelMetadataRequest( this.epicsChannelName, this.publicationChannel );
   }

   /**
    * Returns the channel name.
    *
    * @return the name.
    */
   public EpicsChannelName getEpicsChannelName()
   {
      return epicsChannelName;
   }

   /** Returns the polling interval.
    *
    * @return the polling interval
    */
   public int getPollingInterval()
   {
      return pollingInterval;
   }

   /**
    * Returns the Wica publication channel.
    *
    * @return the publication channel.
    */
   public WicaChannel getPublicationChannel()
   {
      return publicationChannel;
   }

   @Override
   public String toString()
   {
      return "Poller<" + publicationChannel.getName() + "," + pollingInterval + '>';
   }

   // Note on class identity implementation
   //
   // The EpicsChannelPollerService enforces the rule that it will not allow
   // multiple requests that are considered "the same".
   //
   // For polling channels requests are considered the same if they refer to
   // the same EPICS channel name operating at the same polling interval on
   // the same WicaChannel.
   //
   // The equals and hashcode implementations below were generated by IntelliJ
   // and give us what we need.

   @Override
   public boolean equals( Object o )
   {
      if ( this == o )
      {
         return true;
      }
      if ( o == null || getClass() != o.getClass() )
      {
         return false;
      }

      EpicsChannelPollerRequest that = (EpicsChannelPollerRequest) o;

      if ( pollingInterval != that.pollingInterval )
      {
         return false;
      }
      if ( !epicsChannelName.equals( that.epicsChannelName ) )
      {
         return false;
      }
      return publicationChannel.equals( that.publicationChannel );
   }

   @Override
   public int hashCode()
   {
      int result = epicsChannelName.hashCode();
      result = 31 * result + pollingInterval;
      result = 31 * result + publicationChannel.hashCode();
      return result;
   }


/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}