/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.services.stream;

/*- Imported packages --------------------------------------------------------*/
/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

import ch.psi.wica.infrastructure.stream.WicaStreamConfigurationDecoder;
import ch.psi.wica.model.stream.WicaStream;
import ch.psi.wica.model.stream.WicaStreamId;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ThreadSafe
public class WicaStreamLifecycleService
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final Logger logger = LoggerFactory.getLogger(WicaStreamLifecycleService.class );

   private final Map<WicaStreamId, WicaStreamServerSentEventPublisher> wicaStreamPublisherMap = Collections.synchronizedMap( new HashMap<>() );

   private final WicaStreamDataRequesterService wicaStreamDataRequesterService;
   private final WicaStreamDataCollectorService wicaStreamDataCollectorService;

   private final AtomicInteger streamsCreated;
   private final AtomicInteger streamsDeleted;


/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   /**
    * Creates a new instance that may be accessed concurrently by multiple
    * threads.
    *
    * @param wicaStreamDataRequesterService reference to the service which
    *     configures the underlying control system to start monitoring
    *     the channels in the stream.
    *
    * @param wicaStreamDataCollectorService reference to the service
    *     which provides up to date channel metadata and value
    *     information for the channels in the stream.
    */
   public WicaStreamLifecycleService( @Autowired WicaStreamDataRequesterService wicaStreamDataRequesterService,
                                      @Autowired WicaStreamDataCollectorService wicaStreamDataCollectorService
   )
   {
      this.wicaStreamDataRequesterService = Validate.notNull(wicaStreamDataRequesterService);
      this.wicaStreamDataCollectorService = wicaStreamDataCollectorService;
      this.streamsCreated = new AtomicInteger( 0 );
      this.streamsDeleted = new AtomicInteger( 0 );
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   /**
    * Creates and returns an active wica stream based on the supplied JSON
    * stream configuration string.
    *
    * @param jsonStreamConfiguration the configuration string.
    * @return the returned stream.
    */
   public WicaStream create( String jsonStreamConfiguration )
   {
      Validate.notEmpty( jsonStreamConfiguration, "The 'jsonStreamConfiguration' argument was null." );

      // To ensure consistency the following operations are performed as a single atomic operation.
      synchronized( this )
      {
         // Attempt to decode the stream configuration.
         final StopWatch streamDecodeTimer = StopWatch.createStarted();
         final WicaStreamConfigurationDecoder decoder;
         try
         {
            decoder = new WicaStreamConfigurationDecoder( jsonStreamConfiguration);
         }
         catch ( Exception ex )
         {
            throw new IllegalArgumentException("The JSON configuration string '" + jsonStreamConfiguration + "' was invalid.", ex);
         }

         if ( decoder.getWicaChannels().size() == 0 )
         {
            throw new IllegalArgumentException("The JSON configuration string did not define any channels.");
         }

         final long streamDecodeTimeInMillis = streamDecodeTimer.getTime();
         logger.info("STREAM DECODING TOOK {} ms,", streamDecodeTimeInMillis );

         // Allocate a new stream ID.
         final WicaStreamId wicaStreamId = WicaStreamId.createNext();

         // Create a stream based on the supplied configuration.
         final WicaStream wicaStream = new WicaStream ( wicaStreamId, decoder.getWicaStreamProperties(), decoder.getWicaChannels());

         // Tell the control system monitoring service to start monitoring the
         // control system channels in this stream.
         final StopWatch startMonitoringTimer = StopWatch.createStarted();
         wicaStreamDataRequesterService.startMonitoring(wicaStream );
         final long startMonitoringTimeInMicroseconds = startMonitoringTimer.getTime(TimeUnit.MICROSECONDS );
         logger.info("START MONITORING TOOK {} us,", startMonitoringTimeInMicroseconds);

         // Create a new publisher and add it to the map of recognized publishers.
         final WicaStreamServerSentEventPublisher wicaStreamServerSentEventPublisher = new WicaStreamServerSentEventPublisher(wicaStream, wicaStreamDataCollectorService);
         wicaStreamPublisherMap.put( wicaStreamId, wicaStreamServerSentEventPublisher );

         // Lastly increase the count of created streams.
         streamsCreated.incrementAndGet();

         // Return a reference to the newly created stream.
         return wicaStream;
      }
   }

   /**
    * Deletes the wica stream with the specified id.
    *
    * @param wicaStreamId the Id of the stream to delete.
    *
    * @throws NullPointerException if the stream ID was null.
    * @throws IllegalStateException if the stream ID was not recognised.
    */
   public void delete( WicaStreamId wicaStreamId )
   {
      Validate.notNull( wicaStreamId, "The 'wicaStreamId' argument was null." );
      Validate.isTrue(( isKnown(wicaStreamId ) ), "The 'wicaStreamId' argument was not recognised."  );

      // To ensure consistency the following operations are performed as a single atomic operation.
      synchronized( this )
      {
         // Shutdown the information publisher associated with this stream.
         final WicaStreamServerSentEventPublisher wicaStreamServerSentEventPublisher = wicaStreamPublisherMap.get( wicaStreamId );
         wicaStreamServerSentEventPublisher.shutdown();

         // Tell the control system monitoring service that we are no longer
         // interested in this stream.
         final WicaStream wicaStream = wicaStreamServerSentEventPublisher.getStream();
         wicaStreamDataRequesterService.stopMonitoring(wicaStream ) ;

         // Remove the stream's ID from the list of recognised publishers.
         wicaStreamPublisherMap.remove( wicaStreamId );

         // Lastly increase the count of deleted streams.
         streamsDeleted.incrementAndGet();
      }
   }

   /**
    * Gets the publication flux for the stream with the specified id.
    *
    * @param wicaStreamId the id of the flux to fetch.
    * @return the combined flux.
    */
   public Flux<ServerSentEvent<String>> getFlux( WicaStreamId wicaStreamId  )
   {
      Validate.notNull( wicaStreamId, "The 'wicaStreamId' argument was null." );
      return wicaStreamPublisherMap.get( wicaStreamId ).getFlux();
   }

   /**
    * Returns an flag saying whether the specified id is recognised within the system.
    *
    * @param wicaStreamId the id of the stream to check.
    * @return the result.
    */
   public boolean isKnown( WicaStreamId wicaStreamId )
   {
      Validate.notNull( wicaStreamId, "The 'wicaStreamId' argument was null." );
      return wicaStreamPublisherMap.containsKey( wicaStreamId );
   }

   /**
    * Returns a count of the number of streams which have been created
    * since the server started running.
    *
    * @return the count.
    */
   public int getStreamsCreated()
   {
      return streamsCreated.get();
   }

   /**
    * Returns a count of the number of streams which have been deleted
    * since the server started running.
    *
    * @return the count.
    */
   public int getStreamsDeleted()
   {
      return streamsDeleted.get();
   }

/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}
