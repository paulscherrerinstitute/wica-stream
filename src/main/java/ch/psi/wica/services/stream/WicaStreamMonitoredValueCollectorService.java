/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.services.stream;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.controlsystem.event.wica.WicaChannelMonitoredValueUpdateEvent;
import ch.psi.wica.controlsystem.event.wica.WicaChannelPolledMonitorValueUpdateEvent;
import ch.psi.wica.controlsystem.event.wica.WicaChannelPolledValueUpdateEvent;
import ch.psi.wica.infrastructure.channel.WicaChannelValueTimestampRewriter;
import ch.psi.wica.infrastructure.stream.WicaStreamMonitoredValueDataBuffer;
import ch.psi.wica.model.app.WicaDataBufferStorageKey;
import ch.psi.wica.model.channel.WicaChannel;
import ch.psi.wica.model.channel.value.WicaChannelValue;
import ch.psi.wica.model.channel.value.WicaChannelValueConnected;
import ch.psi.wica.model.stream.WicaStream;
import ch.psi.wica.services.channel.WicaChannelValueFilteringService;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

@Service
@ThreadSafe
public class WicaStreamMonitoredValueCollectorService
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final Logger logger = LoggerFactory.getLogger( WicaStreamMonitoredValueCollectorService.class );

   private final WicaStreamMonitoredValueDataBuffer wicaStreamMonitoredValueDataBuffer;
   private final ApplicationEventPublisher applicationEventPublisher;
   private final WicaChannelValueTimestampRewriter wicaChannelValueTimestampRewriter;
   private final WicaChannelValueFilteringService wicaChannelValueFilteringService;

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   public WicaStreamMonitoredValueCollectorService( @Value( "${wica.channel-monitored-value-buffer-size}") int bufferSize,
                                                    @Autowired ApplicationEventPublisher applicationEventPublisher,
                                                    @Autowired WicaChannelValueTimestampRewriter wicaChannelValueTimestampRewriter,
                                                    @Autowired WicaChannelValueFilteringService wicaChannelValueFilteringService )
   {
      this.wicaStreamMonitoredValueDataBuffer = new WicaStreamMonitoredValueDataBuffer( bufferSize );
      this.applicationEventPublisher = applicationEventPublisher;
      this.wicaChannelValueTimestampRewriter = wicaChannelValueTimestampRewriter;
      this.wicaChannelValueFilteringService = wicaChannelValueFilteringService;
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   public Map<WicaChannel,List<WicaChannelValue>> get( WicaStream wicaStream, LocalDateTime since )
   {
      final var inputMap = wicaStreamMonitoredValueDataBuffer.getLaterThan( wicaStream.getWicaChannels(), since );
      return inputMap.entrySet()
                     .stream()
                     .filter( e -> e.getKey().getProperties().getDataAcquisitionMode().doesMonitorPublication() )
                     .map( e -> new AbstractMap.SimpleEntry<>( e.getKey(), wicaChannelValueFilteringService.filterValues( e.getKey(), e.getValue() ) ) )
                     .filter( e -> !e.getValue( ).isEmpty( ) )
                     .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, Map.Entry::getValue ) );
   }

   Map<WicaChannel,List<WicaChannelValue>> getLatest( WicaStream wicaStream )
   {
      final var inputMap = wicaStreamMonitoredValueDataBuffer.getLaterThan( wicaStream.getWicaChannels(), LocalDateTime.MIN );
      return inputMap.entrySet()
                     .stream()
                     .filter( e -> e.getKey().getProperties().getDataAcquisitionMode().doesMonitorPublication() )
                     .map( e -> new AbstractMap.SimpleEntry<>( e.getKey(), wicaChannelValueFilteringService.filterLastValues( e.getValue() ) ) )
                     .filter( e -> !e.getValue( ).isEmpty( ) )
                     .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, Map.Entry::getValue ) );
   }

   @EventListener
   public void handleWicaChannelMonitoredValueUpdateEvent( WicaChannelMonitoredValueUpdateEvent event )
   {
      Validate.notNull( event, "The 'event' argument was null." );
      final WicaChannel wicaChannel = event.getWicaChannel();
      final WicaDataBufferStorageKey wicaDataBufferStorageKey = WicaDataBufferStorageKey.getMonitoredValueStorageKey( wicaChannel );
      final WicaChannelValue wicaChannelValue = event.getWicaChannelValue();
      wicaStreamMonitoredValueDataBuffer.saveDataPoint( wicaDataBufferStorageKey, wicaChannelValue );
   }

   @EventListener
   public void handleWicaChannelPolledMonitorValueUpdateEvent( WicaChannelPolledMonitorValueUpdateEvent event)
   {
      Validate.notNull( event, "The 'event' argument was null." );
      final WicaChannel wicaChannel = event.getWicaChannel();
      final WicaDataBufferStorageKey wicaDataBufferStorageKey = WicaDataBufferStorageKey.getMonitoredValueStorageKey(wicaChannel );
      final WicaChannelValue wicaChannelValue = wicaStreamMonitoredValueDataBuffer.getLatest( wicaDataBufferStorageKey );
      final WicaChannelValue rewrittenChannelValue = wicaChannelValueTimestampRewriter.rewrite( wicaChannelValue, LocalDateTime.now() );
      applicationEventPublisher.publishEvent( new WicaChannelPolledValueUpdateEvent( wicaChannel, rewrittenChannelValue ) );
   }

   // Detect and log warning message when monitored value lags behind polled value by more than 1 second
   @EventListener
   public void handleUpdateEvent( WicaChannelPolledValueUpdateEvent event )
   {
      Validate.notNull( event, "The 'event' argument was null." );

      final WicaChannel wicaChannel = event.getWicaChannel();
      final WicaChannelValue latestPolledValue = event.getWicaChannelValue();

      if ( ! wicaChannel.getProperties().getDataAcquisitionMode().doesMonitoring() )
      {
         logger.trace( "Polled channel: '{}' does not support monitoring. No need to perform monitor validation.", wicaChannel.getNameAsString() );
         return;
      }

      logger.trace( "Validating latest monitored value for channel: '{}'.", wicaChannel.getNameAsString() );

      final WicaDataBufferStorageKey wicaDataBufferStorageKey = WicaDataBufferStorageKey.getMonitoredValueStorageKey( wicaChannel );
      final WicaChannelValue latestMonitoredValue = wicaStreamMonitoredValueDataBuffer.getLatest( wicaDataBufferStorageKey );

      if ( ! latestPolledValue.isConnected() )
      {
         logger.trace( "Latest polled value for channel: '{}' shows the channel is not online, monitor validation suppressed.", wicaChannel.getNameAsString() );
         return;
      }

      if ( ! latestMonitoredValue.isConnected() )
      {
         logger.trace( "Latest monitored value for channel: '{}' shows the channel is not online, monitor validation suppressed.", wicaChannel.getNameAsString() );
         return;
      }

      final LocalDateTime ts1 = ( (WicaChannelValueConnected) latestMonitoredValue).getDataSourceTimestamp();
      final String name1 =  wicaChannel.getNameAsString();
      final String val1 =  latestMonitoredValue.toString();
      logger.trace( "Latest monitored value for channel: '{}' has the value '{}' and timestamp '{}'.", name1, val1, ts1 );

      final LocalDateTime ts2 = ( (WicaChannelValueConnected) latestPolledValue).getDataSourceTimestamp();
      final String name2 =  wicaChannel.getNameAsString();
      final String val2 =  latestPolledValue.toString();
      logger.trace( "Latest polled value for channel: '{}' has the value '{}' and timestamp '{}'.", name2, val2, ts2 );

      final Duration lag =  Duration.between( ts1, ts2 );
      logger.trace( "The monitored value lag for channel: '{}' was '{}' seconds.", val2, lag.getSeconds() );

      if ( ( lag.getSeconds() > 5 )  && ( ! val1.equals( val2 ) ) )
      {
         logger.warn( "The monitored value for channel: '{}' does not match the polled value. The timestamp lag is {} seconds. ", name1, lag.getSeconds() );
         logger.warn( "Monitored value is '{}' but polled value is '{}'.", val1, val2 );
      }
   }

/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}
