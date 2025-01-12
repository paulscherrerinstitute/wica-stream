/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.infrastructure.stream;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.model.stream.WicaStreamId;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.Validate;
import org.springframework.http.codec.ServerSentEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

/**
 * Provides the functionality to build Server-Sent-Events (SSE) for the WICA
 * streaming service.
 */
@Immutable
public enum WicaStreamServerSentEventBuilder
{

   /**
    * Defines the event type and comment associated with HEARTBEAT Server-Sent-Events.
    */
   EV_WICA_SERVER_HEARTBEAT         ("ev-wica-server-heartbeat", "server heartbeat"         ),

   /**
    * Defines the event type and comment associated with CHANNEL METADATA Server-Sent-Events.
    */
   EV_WICA_CHANNEL_METADATA         ("ev-wica-channel-metadata", "channel metadata"         ),

   /**
    * Defines the event type and comment associated with CHANNEL POLLED VALUE Server-Sent-Events.
    */
   EV_WICA_CHANNEL_POLLED_VALUES    ("ev-wica-channel-value",    "channel polled values"    ),

   /**
    * Defines the event type and comment associated with CHANNEL MONITORED VALUE Server-Sent-Events.
    */
   EV_WICA_CHANNEL_MONITORED_VALUES ("ev-wica-channel-value",    "channel monitored values" );

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   /**
    * The format which will be used when making String representations
    * of the times/dates in this class as used in the SSE comment field.
    */
   private static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
   private final String event;
   private final String comment;

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   /**
    * Create a new WicaStreamServerSentEventBuilder with the specified event
    * type and comment.
    *
    * @param event the event type.
    * @param comment the comment.
    */
   WicaStreamServerSentEventBuilder( String event, String comment )
   {
      this.event = Validate.notBlank( event );
      this.comment = Validate.notBlank( comment );
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   /**
    * Returns a Wica ServerSent Event customised with the supplied WicaStream id
    * and String data payload.
    *
    * @param id the WicaStreamId
    * @param dataString the String data
    * @return the generated SSE.
    */
   public ServerSentEvent<String> build( WicaStreamId id, String dataString )
   {
      Validate.notNull( id, "The id field was null" );
      Validate.notNull( dataString,"The valueMap field was null ");

      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( DATETIME_FORMAT_PATTERN );
      final String formattedTimeAndDateNow =  LocalDateTime.now().format(formatter );
      return ServerSentEvent.builder( dataString )
            .id( id.asString() )
            .comment( formattedTimeAndDateNow + " - " + this.comment )
            .event( this.event )
            .build();
   }


/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}
